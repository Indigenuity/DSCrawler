package async.textanalysis;

import persistence.SiteCrawl;
import play.Logger;
import play.db.jpa.JPA;
import akka.actor.UntypedActor;
import analysis.SiteCrawlAnalyzer;
import async.work.WorkItem;
import async.work.WorkStatus;

public class TextAnalysisWorker extends UntypedActor {

	@Override
	public void onReceive(Object work) throws Exception {
		
		WorkItem workItem = (WorkItem) work;
		workItem.setWorkStatus(WorkStatus.WORK_IN_PROGRESS);
		System.out.println("Performing meta analysis work : " + workItem.getSiteCrawlId());
		JPA.withTransaction( () -> {
			
			try{
				SiteCrawl siteCrawl = JPA.em().find(SiteCrawl.class, workItem.getSiteCrawlId());
				siteCrawl.initAll();
				SiteCrawlAnalyzer.textAnalysis(siteCrawl);
				siteCrawl.setTextAnalysisDone(true);
				workItem.setWorkStatus(WorkStatus.WORK_COMPLETED);				
			}
			catch(Exception e) {
				Logger.error("Error in Text Analysis: " + e);
				e.printStackTrace();
			}
		});
		getSender().tell(work, getSelf());
	}
	
	@Override
	public void postRestart(Throwable reason) throws Exception {
		Logger.error("TextAnalysis worker restarting");
		preStart();
	}
	

}