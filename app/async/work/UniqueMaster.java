package async.work;

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
import async.Asyncleton;
import async.WaitingRoom;
import async.monitoring.AsyncMonitor;
import async.work.infofetch.InfoFetch;
import play.Logger;

public class UniqueMaster extends UntypedActor {
	

	
	private final ActorRef listener;
	
	private Class<?> clazz;
	
	private WaitingRoom waitingRoom;
	
	public UniqueMaster( ActorRef listener, Class<?> clazz) {
		this.listener = listener;
		this.clazz = clazz;
	}
	
	@Override
	public void onReceive(Object work) throws Exception {
		try{
			if (work instanceof WorkOrder) {
				WorkOrder workOrder = (WorkOrder) work;
				ActorRef r = getContext().actorOf(Props.create(clazz));
//				System.out.println("got work order: " + workOrder);
				r.tell(workOrder, getSender());
			}
			else if(work instanceof Terminated) {
				Logger.error("Unique Master (" + this.clazz + ") received terminated worker");
			}
			else {
				System.out.println("Got unknown work in Unique Master (" + this.clazz + ") : " + work);
				Logger.warn("Got unknown work in Unique Master (" + this.clazz + ") : " + work);
			}
		}
		catch(Exception e){
			Logger.error("Exception caught in Unique Master (" + this.clazz + ") : " + e);
			e.printStackTrace();
		}
	}

}