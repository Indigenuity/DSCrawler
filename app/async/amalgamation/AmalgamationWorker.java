package async.amalgamation;

import java.io.File;

import datatransfer.Amalgamater;
import global.Global;
import persistence.CrawlSet;
import persistence.Site;
import persistence.SiteCrawl;
import play.Logger;
import play.db.jpa.JPA;
import akka.actor.UntypedActor;
import analysis.SiteCrawlAnalyzer;
import async.work.SiteWork;
import async.work.WorkItem;
import async.work.WorkStatus;

public class AmalgamationWorker extends UntypedActor {

	private static int count = 0;
	@Override
	public void onReceive(Object work) throws Exception {

		System.out.println("received amalgamation work");
		WorkItem workItem = (WorkItem) work;
		workItem.setWorkStatus(WorkStatus.WORK_IN_PROGRESS);
		JPA.withTransaction( () -> {
			
			try{
				SiteCrawl siteCrawl = JPA.em().find(SiteCrawl.class, workItem.getSiteCrawlId());
				File storageFolder = new File(Global.getCrawlStorageFolder() + "/" + siteCrawl.getStorageFolder());
				File destination = new File(Global.getCombinedStorageFolder() + "/" + siteCrawl.getStorageFolder());
				File amalgamatedFile = Amalgamater.amalgamateFiles(storageFolder, destination);
				siteCrawl.setAmalgamationDone(true);
				workItem.setWorkStatus(WorkStatus.WORK_COMPLETED);
			}
			catch(Exception e) {
				Logger.error("error during amalgamation: " + e);
				e.printStackTrace();
			}
			
		});
		getSender().tell(workItem, getSelf());
	}
	
	@Override
	public void postRestart(Throwable reason) throws Exception {
		Logger.error("Amalgamation worker restarting");
		preStart();
	}
	

}