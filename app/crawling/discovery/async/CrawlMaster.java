package crawling.discovery.async;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;
import async.async.WaitingRoom;
import crawling.discovery.execution.Crawl;
import crawling.discovery.execution.FetchQueue;
import crawling.discovery.planning.CrawlPlan;
import crawling.discovery.planning.PrimaryResourcePlan;

public class CrawlMaster extends UntypedActor {

	
	private final int numWorkers;
	private Crawl crawl;
	private Router router;
	private ActorRef customer = null;
	
	private WaitingRoom waitingRoom;
	
	public CrawlMaster(int numWorkers) {
		this.numWorkers = numWorkers;
		List<Routee> routees = new ArrayList<Routee>();
	    for (int i = 0; i < this.numWorkers; i++) {
	      ActorRef r = getContext().actorOf(Props.create(FetchWorker.class));
	      getContext().watch(r);
	      routees.add(new ActorRefRoutee(r));
	    }
	    router = new Router(new RoundRobinRoutingLogic(), routees);
	    waitingRoom = new WaitingRoom("Waiting room for " + FetchWorker.class);
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof CrawlPlan) {
			startCrawl((CrawlPlan) message); 
		}
		
		enqueueAvailableWork();
		if(waitingRoom.isEmpty()){
			end();
		}
	}
	
	private void startCrawl(CrawlPlan crawlPlan){
		if(customer != null){
			throw new IllegalStateException("CrawlMaster cannot run crawls for multiple customers.  Submit only one CrawlPlan per CrawlMaster");
		}
		customer = getSender();
		crawl = new Crawl(crawlPlan);
		crawl.start();
	}
	
	private boolean enqueueAvailableWork() {
		FetchQueue<?> fetchQueue = null;
		boolean workEnqueued = false;
		while ((fetchQueue = crawl.getReadyQueue()) != null){
			workEnqueued = true;
			emptyQueue(fetchQueue);
		}
		return workEnqueued;
	}
	
	private <T> void emptyQueue(FetchQueue<T> fetchQueue){
		FetchWorkOrder<T> workOrder;
		while((workOrder = fetchQueue.next()) != null){
			router.route(workOrder, getSelf());
		}
	}
	
	private void end() {
		
	}
	
	
	

}
