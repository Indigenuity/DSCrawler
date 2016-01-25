package async.workers;

import java.util.UUID;

import akka.actor.UntypedActor;
import analysis.MobileCrawlAnalyzer;
import async.monitoring.AsyncMonitor;
import async.work.WorkItem;
import async.work.WorkStatus;
import async.work.WorkType;
import persistence.MobileCrawl;
import persistence.stateful.InfoFetch;
import play.Logger;
import play.db.jpa.JPA;

public class InfoFetchWorker extends UntypedActor {

	private Long uuid = UUID.randomUUID().getLeastSignificantBits();
	
	@Override
	public void onReceive(Object work) throws Exception {

		if(work instanceof InfoFetch) {
			InfoFetch infoFetch = (InfoFetch) work;
			AsyncMonitor.instance().addWip(WorkType.INFO_FETCH.toString(), uuid);
			System.out.println("Performing Info Fetch work : " + work);
			JPA.withTransaction( () -> {
				
				try{
					if(infoFetch.isDoUrlCheck() && infoFetch.getUrlCheckId() < 1){
						
					}
				}
				catch(Exception e) {
					Logger.error("Error in Info Fetch Worker: " + e);
					e.printStackTrace();
				}
			});
			AsyncMonitor.instance().finishWip(WorkType.INFO_FETCH.toString(), uuid);
	//		getSender().tell(work, getSelf());
		}
		else if(work instanceof WorkItem) {
			System.out.println("urlcheck : " + work);
		}
	}
	
	@Override
	public void postRestart(Throwable reason) throws Exception {
		Logger.error("Info Fetchworker restarting");
		preStart();
	}
	

}