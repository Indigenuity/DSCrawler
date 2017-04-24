package crawling.discovery.execution;

import play.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import async.monitoring.Lobby;
import async.monitoring.WaitingRoom;
import crawling.discovery.entities.Resource;
import crawling.discovery.planning.CrawlPlan;
import crawling.discovery.planning.CrawlTool;
import crawling.discovery.planning.DiscoveryPlan;
import crawling.discovery.planning.ResourcePlan;
import crawling.discovery.planning.ResourcePreOrder;
import crawling.discovery.results.CrawlReport;
import newwork.StartWork;
import newwork.WorkStatus;

public class Crawler extends UntypedActor {

	protected final CrawlContext crawlContext;
	protected final CrawlTool crawlTool;
	
	protected final Map<PlanId, ActorRef> supervisors = new HashMap<PlanId, ActorRef>();
	private WaitingRoom waitingRoom;
	
	protected boolean endWhenReady = false;
	
	public Crawler(CrawlContext crawlContext){
		this.crawlContext = crawlContext;
		this.crawlTool = crawlContext.getCrawlTool();
	}
	
	private void preCrawl() {
		populateSupervisors();
		startWaitingRoom();
		crawlTool.preCrawl(crawlContext);
	}
	
	private void populateSupervisors(){
		for(ResourceContext resourceContext : crawlContext.getResourceContexts()){
			 ActorRef supervisor = getContext().actorOf(Props.create(ResourceSupervisor.class,resourceContext)
					 .withDispatcher("akka.master-dispatcher"));
			 supervisors.put(resourceContext.getPlanId(), supervisor);
//			 System.out.println("supervisor : " + resourceContext.getPlanId());
		}
	}
	
	private void startWaitingRoom() {
		waitingRoom = new WaitingRoom("Crawl : " + crawlContext);
		Lobby.add(waitingRoom);
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
//		System.out.println("Received message in Crawler : " + message);
		if(message instanceof StartWork){
			startCrawl();
		} else if (message instanceof EndWhenReady){
			endWhenReady = true;
		} else if(message instanceof SeedWorkOrder){
			processSeed((SeedWorkOrder)message);
		} else if(message instanceof ResourceWorkResult){
			processResourceWorkResult((ResourceWorkResult) message);
		}
		
		contemplateTheEnd();
	}
	
	protected void contemplateTheEnd(){
		if(waitingRoom.isEmpty() && endWhenReady){
			endItAll();
		}
	}
	
	protected void endItAll(){
//		System.out.println("Ending it softly");
		CrawlReport report = new CrawlReport(crawlContext);
		crawlTool.postCrawl(crawlContext, report);
		context().stop(getSelf());
	}
	
	protected void processResourceWorkResult(ResourceWorkResult workResult) {
//		System.out.println("Processing work result in crawler");
		if(workResult.getWorkStatus() == WorkStatus.ERROR){
			System.out.println("Exception while fetching source(" + workResult.getSource() + ") : " + workResult.getException());
			StringWriter sw = new StringWriter();
			workResult.getException().printStackTrace(new PrintWriter(sw));
			String exceptionAsString = sw.toString();
			Logger.error("Failed Work Order for source : " + workResult.getException().getMessage() + "\n" + exceptionAsString);
		} else if(workResult.getWorkStatus() == WorkStatus.NOT_STARTED){
//			System.out.println("Work not started : " + workResult.getSource());
			//TODO Do anything when a source didn't make the cut?
		} else {
//			System.out.println("Work successfule, processing discoveries : " + workResult.getSource());
			for(DiscoveredSource discoveredSource : workResult.getDiscoveredSources()){
				processDiscoveredSource(discoveredSource);
			}
		}
		waitingRoom.remove(workResult.getUuid());
	}
	
//	protected  ResourceWorkOrder generateWorkOrder(Set<DiscoveryPlan> discoveryPlans, Object source){
//		return new ResourceWorkOrder(source, discoveryPlans);
//		
//	}
	
	protected void processDiscoveredSource(DiscoveredSource discoveredSource){
//		System.out.println("processing discoveredsource : " + discoveredSource.getSource());
		ResourceWorkOrder workOrder = new ResourceWorkOrder(discoveredSource.getSource(),
				discoveredSource.getParent(),
				discoveredSource.getDestinationPlan());
		assignWork(workOrder);
	}
	
	public void startCrawl(){
//		System.out.println("Starting crawl");
		preCrawl();
		processPreOrders();
//		ResourceWorkOrder workOrder = new ResourceWorkOrder(crawlPlan.getSeed(),
//				null,
//				crawlPlan.getDiscoveryPlans(crawlPlan.getSeedPlan().getUuid()));
//
//		assignWork(workOrder, crawlPlan.getSeedPlan().getUuid());
	}
	
	protected void processPreOrders(){
		for(ResourceContext context : crawlContext.getResourceContexts()){
			for(ResourcePreOrder preOrder : context.getPreOrders()){
				processPreOrder(preOrder, context.getPlanId());
			}
		}
	}
	
	protected void processPreOrder(ResourcePreOrder preOrder, PlanId planId){
		ResourceWorkOrder workOrder = new ResourceWorkOrder(preOrder.getSource(),
				preOrder.getParent(),
				planId);
		assignWork(workOrder);
	}
	
	protected void processSeed(SeedWorkOrder seedWorkOrder){
		crawlTool.preProcessSeed(crawlContext, seedWorkOrder);
		ResourceWorkOrder workOrder = new ResourceWorkOrder(seedWorkOrder.getSource(),
				null,
				seedWorkOrder.getPlanId());
		assignWork(workOrder);
	}
	
	protected void assignWork(ResourceWorkOrder workOrder){
//		System.out.println("Assigning work to context : " + workOrder.getPlanId() + " (" + supervisors.get(workOrder.getPlanId()) +")");
		waitingRoom.add(workOrder.getUuid(), ActorRef.noSender());
		supervisors.get(workOrder.getPlanId()).tell(workOrder, getSelf());
	}

}
