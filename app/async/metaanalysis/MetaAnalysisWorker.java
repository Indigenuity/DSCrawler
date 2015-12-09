package async.metaanalysis;

import persistence.SiteCrawl;
import play.Logger;
import play.db.jpa.JPA;
import akka.actor.UntypedActor;
import analysis.SiteCrawlAnalyzer;
import async.work.WorkItem;
import async.work.WorkStatus;

public class MetaAnalysisWorker  extends UntypedActor {

	@Override
	public void onReceive(Object work) throws Exception {

		System.out.println("received meta analysis work");
		WorkItem workItem = (WorkItem) work;
		workItem.setWorkStatus(WorkStatus.WORK_IN_PROGRESS);
		JPA.withTransaction( () -> {
			
			try{
				SiteCrawl siteCrawl = JPA.em().find(SiteCrawl.class, workItem.getSiteCrawlId());
				SiteCrawlAnalyzer.metaAnalysis(siteCrawl);
				siteCrawl.setMetaAnalysisDone(true);
				workItem.setWorkStatus(WorkStatus.WORK_COMPLETED);
			}
			catch(Exception e) {
				Logger.error("Error in meta analysis: " + e);
				e.printStackTrace();
			}
		});
		getSender().tell(work, getSelf());
	}
	
	@Override
	public void postRestart(Throwable reason) throws Exception {
		Logger.error("Meta analysis worker restarting");
		preStart();
	}
	

}