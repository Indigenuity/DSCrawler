package crawling.discovery.async;

import akka.actor.UntypedActor;
import crawling.discovery.execution.FetchQueue;

public class FetchQueueWorker<T> extends UntypedActor {

	private FetchQueue<T> fetchQueue;
	@Override
	public void onReceive(Object arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	

}
