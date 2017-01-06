package async.functionalwork;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;
import async.async.WaitingRoom;
import newwork.WorkOrder;
import newwork.WorkResult;
import play.Logger;

public class FunctionalMaster extends UntypedActor {
	 

	private final int numWorkers;
	
	private Router router;
	
	private Class<?> clazz;
	
	private WaitingRoom waitingRoom;
	
	public FunctionalMaster(int numWorkers, ActorRef listener, Class<?> clazz) {
		System.out.println("Functional master starting with " + numWorkers + " workers of type " + clazz);
		this.numWorkers = numWorkers;
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
	public void onReceive(Object message) throws Exception {
		if(message instanceof WorkOrder){
			processWorkOrder((WorkOrder) message);
		} else if(message instanceof WorkResult) {
			processWorkResult((WorkResult) message);
		}else if(message instanceof Terminated) {
			Logger.error("Generic Master (" + this.clazz + ") received terminated worker");
			router = router.removeRoutee(((Terminated) message).actor());
			ActorRef worker = getContext().actorOf(Props.create(this.clazz));
			getContext().watch(worker);
			router = router.addRoutee(new ActorRefRoutee(worker));
		}
	}
	
	public void processWorkOrder(WorkOrder workOrder){
		
	}
	
	public void processWorkResult(WorkResult workResult) {
		
	}
}
