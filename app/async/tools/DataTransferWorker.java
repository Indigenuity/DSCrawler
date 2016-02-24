package async.tools;

import datatransfer.FileMover;
import persistence.SiteCrawl;
import play.Logger;
import play.db.jpa.JPA;
import akka.actor.UntypedActor;
import async.work.SiteWork;

public class DataTransferWorker extends UntypedActor {

	private static int count = 0;
	@Override
	public void onReceive(Object work) throws Exception {

		
		try{
			if(work instanceof SiteWork){
				SiteWork siteWork = (SiteWork) work;
				JPA.withTransaction( () -> {
					SiteCrawl siteCrawl = JPA.em().find(SiteCrawl.class, siteWork.getSiteCrawlId());
					if(siteCrawl == null) {
						throw new IllegalStateException("Can't find sitecrawl with id : " + siteWork.getSiteCrawlId() + "; Can't move files");
					}
					siteCrawl.initPageData();
					if(siteWork.getRestoreWork() == SiteWork.WORK_IN_PROGRESS) {
						FileMover.allToLocal(siteCrawl);
						siteCrawl.setFilesMoved(false);
						
					}
					else if(siteWork.getBackupWork() == SiteWork.WORK_IN_PROGRESS) {
						FileMover.allToSecondary(siteCrawl);
						siteCrawl.setFilesMoved(true);
					}
					
					System.out.println("Moved files for : " + siteCrawl.getSiteCrawlId());
				});
				
				if(siteWork.getRestoreWork() == SiteWork.WORK_IN_PROGRESS) {
					siteWork.setRestoreWork(SiteWork.WORK_COMPLETED);
				}
				else if(siteWork.getBackupWork() == SiteWork.WORK_IN_PROGRESS) {
					siteWork.setBackupWork(SiteWork.WORK_COMPLETED);
				}
			}
		}
		catch(Exception e) {
			Logger.error("error while data transfer : " + e);
			e.printStackTrace();
		}
		getSender().tell(work, getSelf());
	}
	
	@Override
	public void postRestart(Throwable reason) throws Exception {
		Logger.error("Data transfer worker restarting");
		preStart();
	}
	

}