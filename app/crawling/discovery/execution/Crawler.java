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
import crawling.discovery.planning.DiscoveryPlan;
import crawling.discovery.planning.ResourcePlan;
import crawling.discovery.planning.ResourceSupervisor;
import newwork.StartWork;
import newwork.WorkStatus;

public class Crawler extends UntypedActor {

	protected final CrawlContext crawlContext;
	
	protected final Map<PlanReference, ActorRef> supervisors = new HashMap<PlanReference, ActorRef>();
	private WaitingRoom waitingRoom;
	
	public Crawler(CrawlContext crawlContext){
		this.crawlContext = crawlContext;
		populateSupervisors();
		startWaitingRoom();
		System.out.println("Finished crawler constructor");
	}
	
	private void populateSupervisors(){
		for(ResourceContext resourceContext : crawlContext.getResourceContexts()){
			 ActorRef supervisor = getContext().actorOf(Props.create(ResourceSupervisor.class,resourceContext)
					 .withDispatcher("akka.master-dispatcher"));
			 supervisors.put(resourceContext.getPlanReference(), supervisor);
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
		} else if(message instanceof SeedWorkOrder){
			processSeed((SeedWorkOrder)message);
		} else if(message instanceof ResourceWorkResult){
			processResourceWorkResult((ResourceWorkResult) message);
		}
	}
	
	protected void processResourceWorkResult(ResourceWorkResult workResult) {
//		System.out.println("Processing work result in crawler");
		if(workResult.getWorkStatus() == WorkStatus.ERROR){
			System.out.println("Exception : " + workResult.getException());
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
		ResourceContext destinationContext = crawlContext.getResourceContext(discoveredSource.getDestinationPlan());
		ResourceWorkOrder workOrder = new ResourceWorkOrder(discoveredSource.getSource(),
				discoveredSource.getParent(),
				destinationContext);
		assignWork(workOrder);
	}
	
	public void startCrawl(){
//		System.out.println("Starting crawl");
//		crawlPlan.preCrawl();
//		ResourceWorkOrder workOrder = new ResourceWorkOrder(crawlPlan.getSeed(),
//				null,
//				crawlPlan.getDiscoveryPlans(crawlPlan.getSeedPlan().getUuid()));
//
//		assignWork(workOrder, crawlPlan.getSeedPlan().getUuid());
	}
	
	protected void processSeed(SeedWorkOrder seedWorkOrder){
		System.out.println("Processing seed in Crawler");
		ResourceWorkOrder workOrder = new ResourceWorkOrder(seedWorkOrder.getSource(),
				null,
				crawlContext.getResourceContext(seedWorkOrder.getPlanReference()));
		assignWork(workOrder);
	}
	
	protected void assignWork(ResourceWorkOrder workOrder){
//		System.out.println("Assigning work");
		waitingRoom.add(workOrder.getUuid(), ActorRef.noSender());
		supervisors.get(workOrder.getResourceContext().getPlanReference()).tell(workOrder, getSelf());
	}

}
