package agarbagefolder;


import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import async.work.TypedWorkOrder;
import play.Logger;

public class UniqueMaster extends UntypedActor {
	

	
	private Class<?> clazz;
	
	
	public UniqueMaster( ActorRef listener, Class<?> clazz) {
		this.clazz = clazz;
	}
	
	@Override
	public void onReceive(Object work) throws Exception {
		try{
			if (work instanceof TypedWorkOrder) {
				TypedWorkOrder workOrder = (TypedWorkOrder) work;
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