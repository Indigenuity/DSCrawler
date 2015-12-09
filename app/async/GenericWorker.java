package async;

import play.Logger;
import akka.actor.UntypedActor;
import async.work.WorkSet;

public class GenericWorker extends UntypedActor {

	@Override
	public void onReceive(Object work) throws Exception {
		try{
//			if(!(work instanceof WorkSet)){
//				throw new IllegalArgumentException("Generic Worker can't do work on any objects but WorkSet : " + work);
//			}
			System.out.println("got work");
			
			
		}
		catch(Exception e) {
			Logger.error("Caught exception in GenericWorker  : " + e);
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void postRestart(Throwable reason) throws Exception {
		Logger.error("Generic worker restarting");
		preStart();
	}
	

}
