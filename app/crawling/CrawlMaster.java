package crawling;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.util.concurrent.RateLimiter;

import akka.actor.ActorRef;
import akka.actor.DeadLetterActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;
import async.async.Asyncleton;
import async.async.TypedMaster;
import async.monitoring.Lobby;
import async.monitoring.WaitingRoom;
import async.monitoring.WaitingRoom.BackOrder;
import async.monitoring.WaitingRoom.WaitingRoomStatus;
import crawling.discovery.execution.CrawlOrder;
import crawling.discovery.execution.CrawlSupervisor;
import crawling.discovery.execution.EndWhenReady;
import crawling.discovery.execution.PlanId;
import crawling.discovery.execution.SeedWorkOrder;
import crawling.discovery.html.HttpConfig;
import crawling.discovery.html.HttpToFilePlan;
import crawling.discovery.local.SiteCrawlPlan;
import crawling.discovery.local.PageCrawlTool;
import crawling.discovery.local.SiteCrawlTool;
import crawling.discovery.planning.CrawlPlan;
import crawling.discovery.planning.DiscoveryPlan;
import global.Global;
import newwork.StartWork;
import newwork.WorkOrder;
import newwork.WorkResult;
import newwork.WorkStatus;
import persistence.Site;
import play.Logger;
import play.db.jpa.JPA;

public class CrawlMaster extends UntypedActor {
	
	
	
	protected final int numWorkers;
	protected final Class<CrawlSupervisor> clazz = CrawlSupervisor.class;
	protected WaitingRoom waitingRoom;
	protected final Map<ActorRef, Long> supervisors = new HashMap<ActorRef, Long>();
	
	public CrawlMaster(int numWorkers) {
		System.out.println("CrawlMaster firing up with " + numWorkers + " simultaneous workers of type " + clazz);
		this.numWorkers = numWorkers;
		
		this.waitingRoom = new WaitingRoom("Waiting room for " + clazz);
		Lobby.add(waitingRoom);
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof CrawlOrder){
			processCrawlOrder((CrawlOrder)message, getSender());
		} else if(message instanceof WorkResult) {
			processWorkResult((WorkResult) message);
		} else if(message instanceof CrawlPlan){
			CrawlOrder crawlOrder = new CrawlOrder((CrawlPlan)message);
			processCrawlOrder(crawlOrder, getSender());
		} else if(message instanceof Terminated) {
			Logger.error("Crawl Master received terminated CrawlSupervisor");
			Long uuid = supervisors.remove(((Terminated) message).actor());
			ActorRef customer = waitingRoom.remove(uuid);
			WorkResult workResult = new WorkResult(uuid);
			workResult.setWorkStatus(WorkStatus.ERROR);
		} 
		checkForBackOrders();
	}
	
	private void processCrawlOrder(WorkOrder crawlOrder, ActorRef customer){
		if(atMaxSupervisors()){
			waitingRoom.storeBackOrder(crawlOrder, customer);
		} else {
			generateSupervisor(crawlOrder, customer);
		}
	}
	
	private boolean atMaxSupervisors(){
		return supervisors.size() >= numWorkers;
	}
	
	private void generateSupervisor(WorkOrder crawlOrder, ActorRef customer){
		ActorRef supervisor = getContext().actorOf(Props.create(clazz).withDispatcher("akka.worker-dispatcher"));
	    getContext().watch(supervisor);
	    supervisors.put(supervisor,  crawlOrder.getUuid());
	    waitingRoom.add(crawlOrder.getUuid(), customer);
	    supervisor.tell(crawlOrder, getSelf());
	}
	
	protected void processWorkResult(WorkResult workResult) {
		ActorRef customer = waitingRoom.remove(workResult.getUuid());
		supervisors.remove(getSender());
		sendWorkResult(workResult, customer);
	}
	
	protected void sendWorkResult(WorkResult workResult, ActorRef customer){
		if(customer == null || customer.equals(getSelf()) || DeadLetterActorRef.class.isAssignableFrom(customer.getClass())){
			//TODO figure out what to do when customer didn't leave a number
		}else {
			customer.tell(workResult, getSelf());
		}
	}
	
	protected void checkForBackOrders(){
		if(atMaxSupervisors()){
			return;
		}
		if(waitingRoom.backOrderCount() > 0){
			BackOrder backOrder = waitingRoom.retrieveBackOrder();
			processCrawlOrder(backOrder.getWorkOrder(), backOrder.getCustomer());
		} else if(waitingRoom.size() == 0) {
//			doShutdown();
		}
	}
	
	protected void doShutdown(){
		System.out.println("Shutting down CrawlMaster and supervisors");
		waitingRoom.setRoomStatus(WaitingRoomStatus.FINISHED);
		context().stop(getSelf());
	}

}
