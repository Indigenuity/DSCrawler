package experiment;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import async.async.Asyncleton;
import newwork.WorkResult;
import newwork.urlcheck.UrlCheckWorkOrder;

public class MyExperimentWorker extends UntypedActor{

	private Boolean started = false;
	@Override
	public void onReceive(Object message) throws Exception {
		
	}

}
