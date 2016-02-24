package agarbagefolder.crawling;

import experiment.Experiment;
import akka.actor.UntypedActor;

public class CrawlingListener extends UntypedActor {

	@Override
	public void onReceive(Object arg0) throws Exception {
		
		System.out.println("Finished crawling all the sites!");
		this.getContext().system().shutdown();
	}

}
