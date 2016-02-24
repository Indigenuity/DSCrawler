package async;

import java.util.ArrayList;
import java.util.List;

import persistence.SiteCrawl;
import play.Logger;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;
import async.monitoring.AsyncMonitor;
import async.tools.DocAnalysisTool;
import async.work.SiteWork;
import async.work.WorkItem;
import async.work.WorkOrder;
import async.work.WorkResult;
import async.work.WorkSet;
import async.work.WorkStatus;
import async.work.infofetch.InfoFetch;

public class GenericMaster extends UntypedActor {
	

	private final int numWorkers;
	
	private final ActorRef listener;
	private Router router;
	
	private Class<?> clazz;
	
	private WaitingRoom waitingRoom;
	
	public GenericMaster(int numWorkers, ActorRef listener, Class<?> clazz) {
		this.numWorkers = numWorkers;
		this.listener = listener;
		this.clazz = clazz;
		this.waitingRoom = new WaitingRoom("Waiting room for " + clazz);
		List<Routee> routees = new ArrayList<Routee>();
	    for (int i = 0; i < this.numWorkers; i++) {
	      ActorRef r = getContext().actorOf(Props.create(clazz));
	      getContext().watch(r);
	      routees.add(new ActorRefRoutee(r));
	    }
	    router = new Router(new RoundRobinRoutingLogic(), routees); 
	    
	}
	
	@Override
	public void onReceive(Object work) throws Exception {
		try{
//			System.out.println("received message in generic master (" + this.clazz + ") : " + work);
			if(work instanceof WorkItem) {
				WorkItem workItem = (WorkItem) work;
				if(workItem.getWorkStatus() == WorkStatus.DO_WORK){
					workItem.setWorkStatus(WorkStatus.WORK_IN_PROGRESS);
					router.route(workItem, getSender());
				}
				else if(workItem.getWorkStatus() == WorkStatus.WORK_COMPLETED){
					AsyncMonitor.instance().finishWip(workItem.getWorkType().toString(), workItem.getUuid());
					Asyncleton.instance().getMainMaster().tell(workItem, getSelf());
				}
				else if(workItem.getWorkStatus() == WorkStatus.WORK_IN_PROGRESS){	//Worker ended in error
					AsyncMonitor.instance().finishWip(workItem.getWorkType().toString(), workItem.getUuid());
				}
				
			}
			else if (work instanceof WorkOrder) {
				WorkOrder workOrder = (WorkOrder) work;
//				System.out.println("got work order: " + workOrder);
				if(!waitingRoom.add(workOrder.getUuid(), getSender())){
					//TODO figure out what to do when duplicate work order is sent in
					return;
				}
				router.route(workOrder, getSelf());
			}
			else if(work instanceof WorkResult) {
				WorkResult workResult = (WorkResult) work;
				ActorRef customer = waitingRoom.remove(workResult.getUuid());
				if(customer == null){
					//TODO figure out what to do when receiving work result for no customer
					return;
				}
				if(customer.equals(ActorRef.noSender())){
					//TODO figure out what to do when customer didn't leave a number
					return;
				}
				customer.tell(workResult, getSelf());
				
			}
//			else if (work instanceof InfoFetch) {
//				ActorRef r = getContext().actorOf(Props.create(clazz));
//			    r.tell(work, getSender());
//			}
			else if(work instanceof Terminated) {
				Logger.error("Generic Master (" + this.clazz + ") received terminated worker");
				router = router.removeRoutee(((Terminated) work).actor());
				ActorRef worker = getContext().actorOf(Props.create(this.clazz));
				getContext().watch(worker);
				router = router.addRoutee(new ActorRefRoutee(worker));
			}
			else {
				System.out.println("Got unknown work in Generic Master (" + this.clazz + ") : " + work);
				Logger.warn("Got unknown work in Generic Master (" + this.clazz + ") : " + work);
			}
		}
		catch(Exception e){
			Logger.error("Exception caught in Generic Master (" + this.clazz + ") : " + e);
			e.printStackTrace();
		}
	}

}
