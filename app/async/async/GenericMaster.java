package async.async;

import java.util.ArrayList;
import java.util.List;

import play.Logger;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;
import async.work.Order;
import async.work.Result;
import newwork.WorkOrder;
import newwork.WorkResult;

public class GenericMaster extends UntypedActor {
	 

	private final int numWorkers;
	
	private Router router;
	
	private Class<?> clazz;
	
	private WaitingRoom waitingRoom;
	
	public GenericMaster(int numWorkers, ActorRef listener, Class<?> clazz) {
		System.out.println("Generic master starting with " + numWorkers + " workers of type " + clazz);
		this.numWorkers = numWorkers;
		this.clazz = clazz;
		this.waitingRoom = new WaitingRoom("Waiting room for " + clazz);
//		Router balancedRouter = getContext().actorOf(new BalancingPool(numWorkers).props(Props.create(clazz)), "BalancedRouter");
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
			if(work instanceof WorkResult) {
				WorkResult workResult = (WorkResult) work;
//				System.out.println("GenericMaster got work result: " + workResult);
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
				if(waitingRoom.size() == 0) {
					System.out.println("shutting down master and children");
					context().stop(getSelf());
				}
			}
//			else if (work instanceof InfoFetch) {
//				ActorRef r = getContext().actorOf(Props.create(clazz));
//			    r.tell(work, getSender());
//			}
			else if(work instanceof Terminated) {
				Logger.error("Generic Master (" + this.clazz + ") received terminated worker");
//				router = router.removeRoutee(((Terminated) work).actor());
//				ActorRef worker = getContext().actorOf(Props.create(this.clazz));
//				getContext().watch(worker);
//				router = router.addRoutee(new ActorRefRoutee(worker));
			}
			else if(work instanceof Order){
				Order<?> order = (Order<?>) work;
//				System.out.println("GenericMaster got work order: " + workOrder);
				if(!waitingRoom.add(order.getUuid(), getSender())){
					//TODO figure out what to do when duplicate work order is sent in
					return;
				}
				router.route(order, getSelf());
			} else if(work instanceof Result){
				Result result = (Result) work;
				System.out.println("GenericMaster got work result: " + result.getMessage());
				ActorRef customer = waitingRoom.remove(result.getUuid()); 
				if(customer == null){
					//TODO figure out what to do when receiving work result for no customer
					return;
				}
				if(customer.equals(ActorRef.noSender())){
					//TODO figure out what to do when customer didn't leave a number
					return;
				}
				customer.tell(result, getSelf());
			} else if(work instanceof WorkOrder) {
				WorkOrder workOrder = (WorkOrder) work;
				if(!waitingRoom.add(workOrder.getUuid(), getSender())){
					//TODO figure out what to do when duplicate work order is sent in
					return;
				}
				router.route(workOrder, getSelf());
			}
			else {
//				System.out.println("Got unknown work in Generic Master (" + this.clazz + ") : " + work);
//				Logger.warn("Got unknown work in Generic Master (" + this.clazz + ") : " + work);
				router.route(work, getSelf());
			}
		}
		catch(Exception e){
			Logger.error("Exception caught in Generic Master (" + this.clazz + ") : " + e);
			e.printStackTrace();
		}
	}

}
