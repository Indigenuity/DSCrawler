package async.async;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.DeadLetterActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;
import async.monitoring.Lobby;
import async.monitoring.WaitingRoom;
import async.monitoring.WaitingRoom.WaitingRoomStatus;
import newwork.MasterWorkOrder;
import newwork.WorkOrder;
import newwork.WorkResult;
import newwork.WorkStatus;
import places.CanadaPostal;
import places.ZipLocation;
import play.Logger;

public abstract class TypedMaster<T> extends UntypedActor {
	 

	protected final int numWorkers;
	protected Router router;
	protected WaitingRoom waitingRoom;
	
	public TypedMaster(int numWorkers) {
		System.out.println("Monotype master starting with " + numWorkers + " workers of type " + getType());
		this.numWorkers = numWorkers;
		
		this.waitingRoom = new WaitingRoom("Waiting room for " + getType());
		Lobby.add(waitingRoom);
		List<Routee> routees = new ArrayList<Routee>();
	    for (int i = 0; i < this.numWorkers; i++) {
	      ActorRef r = getContext().actorOf(Props.create(getType()).withDispatcher("akka.worker-dispatcher"));
	      getContext().watch(r);
	      routees.add(new ActorRefRoutee(r));
	    }
	    router = new Router(new RoundRobinRoutingLogic(), routees); 
	}
	
	public abstract Class<T> getType(); 
	
	@Override
	public void onReceive(Object message) throws Exception {
//		System.out.println("received message");
		if(message instanceof WorkOrder){
			assignWork((WorkOrder) message);
		} else if(message instanceof WorkResult) {
			processWorkResult((WorkResult) message);
		} else if(message instanceof Terminated) {
			Logger.error("Monotype Master (" + getType() + ") received terminated worker");
			router = router.removeRoutee(((Terminated) message).actor());
			ActorRef worker = getContext().actorOf(Props.create(getType()));
			getContext().watch(worker);
			router = router.addRoutee(new ActorRefRoutee(worker));
		} else{
			assignWork(generateWorkOrder(message));
		}
	}
	
	protected abstract WorkOrder generateWorkOrder(Object message);
	
	protected void assignWork(WorkOrder workOrder) {
		if(!waitingRoom.add(workOrder.getUuid(), getSender())){
			System.out.println("duplicate work");
			//TODO figure out what to do when duplicate work order is sent in
			return;
		}
		router.route(workOrder, getSelf());
	}
	
	protected void processWorkResult(WorkResult workResult) {
		ActorRef customer = waitingRoom.remove(workResult.getUuid());
		if(workResult.getWorkStatus() == WorkStatus.ERROR){
			Logger.error("Work resulted in error : " + workResult.getError());
			System.out.println("***********************Work resulted in error!  Check logs for details.");
		}
		if(customer == null || customer.equals(getSelf()) || DeadLetterActorRef.class.isAssignableFrom(customer.getClass())){
			//TODO figure out what to do when customer didn't leave a number
		}else {
			customer.tell(workResult, getSelf());
		}
		if(waitingRoom.size() == 0) {
			doShutdown();
		}
	}
	
	protected void doShutdown(){
		System.out.println("Shutting down MonotypeMaster and children");
		waitingRoom.setRoomStatus(WaitingRoomStatus.FINISHED);
		context().stop(getSelf());
	}
}