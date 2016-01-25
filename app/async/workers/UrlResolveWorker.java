package async.workers;

import java.util.UUID;

import akka.actor.UntypedActor;
import analysis.SiteCrawlAnalyzer;
import async.monitoring.AsyncMonitor;
import async.work.WorkItem;
import async.work.WorkStatus;
import async.work.WorkType;
import persistence.SiteCrawl;
import persistence.UrlCheck;
import play.Logger;
import play.db.jpa.JPA;
import utilities.UrlSniffer;

public class UrlResolveWorker  extends UntypedActor {
	
	private Long uuid = UUID.randomUUID().getLeastSignificantBits();

	@Override
	public void onReceive(Object work) throws Exception {
		
		
		WorkItem workItem = (WorkItem) work;
		workItem.setWorkStatus(WorkStatus.WORK_IN_PROGRESS);
		System.out.println("Performing Url Resolve work : " + workItem.getSiteCrawlId());
		AsyncMonitor.instance().addWip(WorkType.REDIRECT_RESOLVE.toString(), uuid);
		JPA.withTransaction( () -> {
			
			try{
				String seed = workItem.getSeed();
				UrlCheck urlCheck = UrlSniffer.checkUrl(seed);
				JPA.em().persist(urlCheck);
				System.out.println("id after persist : " + urlCheck.getUrlCheckId());
				workItem.setUrlCheckId(urlCheck.getUrlCheckId());
				workItem.setWorkStatus(WorkStatus.WORK_COMPLETED);				
			}
			catch(Exception e) {
				Logger.error("Error in Url Resolve: " + e);
				e.printStackTrace();
			}
		});
		AsyncMonitor.instance().finishWip(WorkType.REDIRECT_RESOLVE.toString(), uuid);
		getSender().tell(work, getSelf());
		
	}
	
	@Override
	public void postRestart(Throwable reason) throws Exception {
		Logger.error("Url Resolve worker restarting");
		preStart();
	}
	

}