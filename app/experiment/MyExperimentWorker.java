package experiment;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import async.async.Asyncleton;
import newwork.TerminalWorker;
import newwork.WorkResult;
import newwork.urlcheck.UrlCheckWorkOrder;

public class MyExperimentWorker extends UntypedActor{

	private Boolean started = false;
	@Override
	public void onReceive(Object message) throws Exception {
		if(!started){
			System.out.println("starting actor experiment");
			started = true;
			ActorRef urlCheckWorker = Asyncleton.getInstance().getMainSystem().actorOf(Props.create(TerminalWorker.class));
			UrlCheckWorkOrder workOrder = new UrlCheckWorkOrder("http://www.conquerclub.com");
			urlCheckWorker.tell(workOrder, getSelf());
		}else {
			WorkResult workResult = (WorkResult) message;
			System.out.println("got result : " + workResult);
			System.out.println("status : " + workResult.getWorkStatus());
			System.out.println("result : " + workResult.getResult());
			System.out.println("error : " + workResult.getError());
		}
		
	}

}
