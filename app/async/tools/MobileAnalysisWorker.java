package async.tools;

import akka.actor.UntypedActor;
import analysis.MobileCrawlAnalyzer;
import analysis.SiteCrawlAnalyzer;
import async.work.WorkItem;
import async.work.WorkStatus;
import persistence.MobileCrawl;
import persistence.SiteCrawl;
import play.Logger;
import play.db.jpa.JPA;

public class MobileAnalysisWorker extends UntypedActor {

	@Override
	public void onReceive(Object work) throws Exception {

		
		WorkItem workItem = (WorkItem) work;
		workItem.setWorkStatus(WorkStatus.WORK_IN_PROGRESS);
		System.out.println("Performing mobile analysis work : " + workItem.getMobileCrawlId());
		JPA.withTransaction( () -> {
			
			try{
				MobileCrawl crawl = JPA.em().find(MobileCrawl.class, workItem.getMobileCrawlId());
				MobileCrawlAnalyzer.analyzeMobileCrawl(crawl);
				crawl.setMobileAnalysisDone(true);
				workItem.setWorkStatus(WorkStatus.WORK_COMPLETED);
			}
			catch(Exception e) {
				Logger.error("Error in Mobile analysis: " + e);
				e.printStackTrace();
			}
		});
		getSender().tell(work, getSelf());
	}
	
	@Override
	public void postRestart(Throwable reason) throws Exception {
		Logger.error("Mobile analysis worker restarting");
		preStart();
	}
	

}