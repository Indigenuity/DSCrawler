package agarbagefolder.analysis;

import play.Logger;
import akka.actor.UntypedActor;
import experiment.Experiment;

public class AnalysisListener extends UntypedActor {

	@Override
	public void onReceive(Object arg0) throws Exception {
		Logger.error("Code should not reach analysis listener");
		System.out.println("Finished Analyzing all the sites!");
		this.getContext().system().shutdown();
	}

}