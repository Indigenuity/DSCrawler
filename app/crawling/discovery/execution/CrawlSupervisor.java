package crawling.discovery.execution;

import play.Logger;
import utilities.DSFormatter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import async.monitoring.Lobby;
import async.monitoring.WaitingRoom;
import crawling.discovery.control.CrawlUtil;
import crawling.discovery.entities.Resource;
import crawling.discovery.local.SiteCrawlPlan;
import crawling.discovery.planning.CrawlPlan;
import crawling.discovery.planning.CrawlTool;
import crawling.discovery.planning.DiscoveryPlan;
import crawling.discovery.planning.DiscoveryPoolPlan;
import crawling.discovery.planning.ResourcePlan;
import crawling.discovery.planning.ResourcePreOrder;
import crawling.discovery.results.CrawlReport;
import newwork.StartWork;
import newwork.WorkResult;
import newwork.WorkStatus;

public class CrawlSupervisor extends UntypedActor {

	protected ActorRef customer;
	protected long workOrderUuid;
	protected CrawlContext crawlContext;
	protected CrawlTool crawlTool;
	
	protected final Map<PlanId, ActorRef> supervisors = new HashMap<PlanId, ActorRef>();
	private WaitingRoom waitingRoom;
	
	protected boolean endWhenReady = true;
	
	public CrawlSupervisor() throws Exception{
//		System.out.println("CrawlSupervisor fired up and ready to go");
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
//		System.out.println("Received message in CrawlSupervisor : " + message);
		try{
			if(message instanceof CrawlOrder){
				customer = getSender();
				workOrderUuid = ((CrawlOrder)message).getUuid();
				startCrawl((CrawlOrder)message);
			} else if(message instanceof ResourceWorkResult){
				processResourceWorkResult((ResourceWorkResult) message);
			} else if(message instanceof ResourceWorkOrder){
				assignWork((ResourceWorkOrder) message);
			}
			
			contemplateTheEnd();
		} catch (Exception e) {
			Logger.error("Error while handling messages in CrawlSupervisor : " + e.getClass().getSimpleName() + " : " + e.getMessage() + " : " + DSFormatter.toString(e));
			System.out.println("Error while handling messages in CrawlSupervisor : " + e.getClass().getSimpleName() + " : " + e.getMessage() + " : " + DSFormatter.toString(e));
			endInShame();
		}
	}
	
	protected void startCrawl(CrawlOrder crawlOrder) throws Exception{
		CrawlPlan crawlPlan = crawlOrder.getCrawlPlan();
		
		generateContext(crawlPlan);
		resourcePopulation(crawlPlan);
		populateSupervisors();
		generateWaitingRoom();
		crawlTool.preCrawl(crawlContext);
		generateWorkOrdersFromAllResources();
	}
	
	private void generateContext(CrawlPlan crawlPlan) throws Exception {
		this.crawlContext = crawlPlan.generateContext();
		this.crawlTool = crawlPlan.getCrawlTool();
	}
	
	private void resourcePopulation(CrawlPlan crawlPlan) throws Exception{
		crawlTool.preResourcePopulation(crawlContext);
		crawlPlan.processPreResources(crawlContext);
		System.out.println("Starting crawl : " + crawlPlan.getName() + " with " + crawlContext.getResources().size() + " preresources");
	}
	
	private void populateSupervisors(){
		for(ResourceContext resourceContext : crawlContext.getResourceContexts()){
			 ActorRef supervisor = getContext().actorOf(Props.create(ResourceSupervisor.class,resourceContext)
					 .withDispatcher("akka.master-dispatcher"));
			 supervisors.put(resourceContext.getPlanId(), supervisor);
//			 System.out.println("supervisor : " + resourceContext.getPlanId());
		}
	}
	
	private void generateWaitingRoom() {
		waitingRoom = new WaitingRoom("Crawl : " + crawlContext.getPlanId());
//		Lobby.add(waitingRoom);
	}
	
	
	
	protected void processResourceWorkResult(ResourceWorkResult workResult) {
//		System.out.println("Processing work result in crawler");
		waitingRoom.remove(workResult.getUuid());
		if(workResult.getWorkStatus() == WorkStatus.ERROR){
			System.out.println("Exception while fetching from source (" + crawlContext.getResource(workResult.getResourceId()).getSource() + ") : " + workResult.getException());
			StringWriter sw = new StringWriter();
			workResult.getException().printStackTrace(new PrintWriter(sw));
			String exceptionAsString = sw.toString();
			Logger.error("Failed Work Order for source : " + workResult.getException().getMessage() + "\n" + exceptionAsString);
		} else if(workResult.getWorkStatus() == WorkStatus.COMPLETE){
			Resource resource = crawlContext.getResource(workResult.getResourceId());
		} else if(workResult.getWorkStatus() == WorkStatus.ABORTED){
			//TODO Do anything when a source didn't make the cut?
		} else {
			Logger.error("Unaccounted for work result status in CrawlSupervisor : " + workResult.getWorkStatus());
		}
	}
	
	protected void generateWorkOrdersFromAllResources(){
		for(Resource resource : crawlContext.getResources()){
			generateWorkOrder(resource);
		}
	}
	
	protected void generateWorkOrder(Resource resource) {
		boolean shouldAssign = false;
		
		if(resource.getFetchStatus() == WorkStatus.UNASSIGNED) {
			resource.setFetchStatus(WorkStatus.ASSIGNED);
			shouldAssign = true;
		}
		if(resource.getDiscoveryStatus() == WorkStatus.UNASSIGNED) {
			resource.setDiscoveryStatus(WorkStatus.ASSIGNED);
			shouldAssign = true;
		}
		
		if(shouldAssign){
			assignWork(new ResourceWorkOrder(resource.getResourceId()));
		}
	}
	
	protected void assignWork(ResourceWorkOrder workOrder){
		Resource resource = crawlContext.getResource(workOrder.getResourceId());
		ActorRef supervisor = supervisors.get(crawlContext.getResourcePlanId(resource.getResourceId()));
		//System.out.println("Assigning work to context : " + resource.getPlanId());
		supervisor.tell(workOrder, getSelf());
		waitingRoom.add(workOrder.getUuid(), ActorRef.noSender());
	}

	protected void contemplateTheEnd(){
//		System.out.println("Contemplating the void");
		if(waitingRoom.isEmpty()){
			generateWorkOrdersFromAllResources();
		}
		if(waitingRoom.isEmpty()){
			crawlTool.beforeFinish(crawlContext);
			generateWorkOrdersFromAllResources();
		}
		if(waitingRoom.isEmpty() && endWhenReady){
			endItAll();
		}
	}
	
	protected void endItAll(){
//		System.out.println("Ending it softly");
		CrawlReport report = new CrawlReport(crawlContext);
		crawlTool.postCrawl(crawlContext, report);
		customer.tell(new WorkResult(workOrderUuid), getSelf());
	}
	
	protected void endInShame() {
		context().stop(getSelf());
	}
}
