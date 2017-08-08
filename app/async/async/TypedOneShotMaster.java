package async.async;

import java.util.HashMap;
import java.util.Map;

import akka.actor.ActorRef;
import akka.actor.DeadLetterActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import async.monitoring.Lobby;
import async.monitoring.WaitingRoom;
import async.monitoring.WaitingRoom.BackOrder;
import async.monitoring.WaitingRoom.WaitingRoomStatus;
import newwork.WorkOrder;
import newwork.WorkResult;
import newwork.WorkStatus;
import play.Logger;

public class TypedOneShotMaster<T> extends UntypedActor {
	
	protected final Class<T> clazz;
	protected int maxWorkers;
	protected WaitingRoom waitingRoom;
	protected final Map<ActorRef, Long> workers = new HashMap<ActorRef, Long>();
	
	public TypedOneShotMaster(int maxWorkers, Class<T> clazz) {
		System.out.println("TypedOneShotMaster firing up with " + maxWorkers + " max workers of type " + clazz);
		this.maxWorkers = maxWorkers;
		this.clazz = clazz;
		
		this.waitingRoom = new WaitingRoom("Waiting room for " + clazz);
		Lobby.add(waitingRoom);
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
//		System.out.println("TypedOneShotMaster received message : " + message);
		if(message instanceof WorkOrder){
			processWorkOrder((WorkOrder)message, getSender());
		} else if(message instanceof WorkResult) {
			processWorkResult((WorkResult) message);
		} else if(message instanceof Terminated) {
			processTerminated((Terminated) message);
		} else if(message instanceof MaxWorkerConfig){
			this.maxWorkers = ((MaxWorkerConfig) message).getMaxWorkers();
//			System.out.println("Setting max workers : " + this.maxWorkers);
		}
		queueBackOrders();
	}
	
	private void processWorkOrder(WorkOrder workOrder, ActorRef customer){
//		System.out.println("TypedOneShotMaster processing work order");
		if(atMaxWorkers()){
			waitingRoom.storeBackOrder(workOrder, customer);
		} else {
			createAndAssignWorker(workOrder, customer);
		}
	}
	
	private void createAndAssignWorker(WorkOrder workOrder, ActorRef customer){
		ActorRef worker = getContext().actorOf(Props.create(clazz).withDispatcher("akka.worker-dispatcher"));
	    getContext().watch(worker);
	    workers.put(worker,  workOrder.getUuid());
	    waitingRoom.add(workOrder.getUuid(), customer);
	    worker.tell(workOrder, getSelf());
	}
	
	protected void processWorkResult(WorkResult workResult) {
		ActorRef customer = waitingRoom.remove(workResult.getUuid());
		workers.remove(getSender());
		context().stop(getSender());
		sendWorkResult(workResult, customer);
	}
	
	protected void sendWorkResult(WorkResult workResult, ActorRef customer){
		if(customer == null || customer.equals(getSelf()) || DeadLetterActorRef.class.isAssignableFrom(customer.getClass())){
			//TODO figure out what to do when customer didn't leave a number
		}else {
			customer.tell(workResult, getSelf());
		}
	}
	
	private void processTerminated(Terminated message){
//		Logger.error("TypedOneShotMaster received terminated worker : " + clazz);
		Long uuid = workers.remove(((Terminated) message).actor());
		ActorRef customer = waitingRoom.remove(uuid);
		WorkResult workResult = new WorkResult(uuid);
		workResult.setWorkStatus(WorkStatus.ERROR);
		sendWorkResult(workResult, customer);
	}
	
	protected void queueBackOrders(){
		while(!atMaxWorkers() && waitingRoom.backOrderCount() > 0){
			BackOrder backOrder = waitingRoom.retrieveBackOrder();
			processWorkOrder(backOrder.getWorkOrder(), backOrder.getCustomer());
		}
	}

	private boolean atMaxWorkers(){
		return workers.size() >= maxWorkers;
	}
	
	@Override
	public void postStop() throws Exception {
		waitingRoom.setRoomStatus(WaitingRoomStatus.FINISHED);
	}

}
