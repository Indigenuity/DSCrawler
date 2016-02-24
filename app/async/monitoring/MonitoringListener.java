package async.monitoring;

import persistence.SiteInformationOld;
import play.Logger;
import agarbagefolder.sniffer.SnifferWorker;
import akka.actor.UntypedActor;
import async.Asyncleton;
import async.monitoring.AsyncMonitor.CompletedWork;
import async.monitoring.AsyncMonitor.WorkInProgress;
import async.work.SiteWork;

public class MonitoringListener extends UntypedActor {

	static int total = 0;
	@Override
	public void onReceive(Object work) throws Exception {
//		System.out.println("total received in monitoring listener : " + ++total);
		try {
			if(work instanceof CompletedWork){
//				AsyncMonitor.instance().finishWork((CompletedWork)work);
			}
			else if(work instanceof WorkInProgress) {
//				AsyncMonitor.getInstance().addWork((WorkInProgress) work);
			}
			else if (work instanceof SiteWork) {
//				SiteWork siteWork = (SiteWork) work;
//				SiteInformation siteInfo = siteWork.getSiteInfo();
//				
////				System.out.println("Got SiteWork in Monitoring Listener : " + siteInfo.getId());
//				//Add work
//				if(siteWork.getRedirectResolveWork() == SiteWork.WORK_IN_PROGRESS){
//					WorkInProgress wip = new WorkInProgress(siteInfo.getSiteInformationId(), siteInfo.getSiteName(), siteInfo.getSiteUrl(), AsyncMonitor.SNIFFER_WORK);
//					AsyncMonitor.getInstance().addWork(wip);
//				}
//				if(siteWork.getCrawlWork() == SiteWork.WORK_IN_PROGRESS){
//					WorkInProgress wip = new WorkInProgress(siteInfo.getSiteInformationId(), siteInfo.getSiteName(), siteInfo.getSiteUrl(), AsyncMonitor.CRAWL_WORK);
//					AsyncMonitor.getInstance().addWork(wip);
//				}
//				if(siteWork.getMatchesWork() == SiteWork.WORK_IN_PROGRESS){
//					WorkInProgress wip = new WorkInProgress(siteInfo.getSiteInformationId(), siteInfo.getSiteName(), siteInfo.getSiteUrl(), AsyncMonitor.MATCHING_WORK);
//					AsyncMonitor.getInstance().addWork(wip);
//				}
//				if(siteWork.getStringExtractionWork() == SiteWork.WORK_IN_PROGRESS){
//					WorkInProgress wip = new WorkInProgress(siteInfo.getSiteInformationId(), siteInfo.getSiteName(), siteInfo.getSiteUrl(), AsyncMonitor.STRING_EXTRACTION_WORK);
//					AsyncMonitor.getInstance().addWork(wip);
//				}
//				if(siteWork.getStaffExtractionWork() == SiteWork.WORK_IN_PROGRESS){
//					WorkInProgress wip = new WorkInProgress(siteInfo.getSiteInformationId(), siteInfo.getSiteName(), siteInfo.getSiteUrl(), AsyncMonitor.STAFF_EXTRACTION_WORK);
//					AsyncMonitor.getInstance().addWork(wip);
//				}
//				
//				
//				//Finish Work
//				if(siteWork.getRedirectResolveWork() == SiteWork.WORK_COMPLETED){
//					CompletedWork cw = new CompletedWork(siteInfo.getSiteInformationId(), AsyncMonitor.SNIFFER_WORK);
//					AsyncMonitor.getInstance().finishWork(cw);
//				}
//				if(siteWork.getCrawlWork() == SiteWork.WORK_COMPLETED){
//					CompletedWork cw = new CompletedWork(siteInfo.getSiteInformationId(), AsyncMonitor.CRAWL_WORK);
//					AsyncMonitor.getInstance().finishWork(cw);
//				}
//				if(siteWork.getMatchesWork() == SiteWork.WORK_COMPLETED){
//					CompletedWork cw = new CompletedWork(siteInfo.getSiteInformationId(), AsyncMonitor.MATCHING_WORK);
//					AsyncMonitor.getInstance().finishWork(cw);
//				}
//				if(siteWork.getStringExtractionWork() == SiteWork.WORK_COMPLETED){
//					CompletedWork cw = new CompletedWork(siteInfo.getSiteInformationId(), AsyncMonitor.STRING_EXTRACTION_WORK);
//					AsyncMonitor.getInstance().finishWork(cw);
//				}
//				if(siteWork.getStaffExtractionWork() == SiteWork.WORK_COMPLETED){
//					CompletedWork cw = new CompletedWork(siteInfo.getSiteInformationId(), AsyncMonitor.STAFF_EXTRACTION_WORK);
//					AsyncMonitor.getInstance().finishWork(cw);
//				}
				
			}
			else {
				Logger.error("Monitoring Listener can't handle objects besides CompletedWork");
			}
		}
		catch(Exception e) {
			System.out.println("Error in Monitoring Listener: " + e.getClass());
			Logger.error("Error in Monitoring Listener : " + e );
		}
	}

}
