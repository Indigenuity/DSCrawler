package agarbagefolder.analysis;

import persistence.SiteInformationOld;
import play.Logger;
import agarbagefolder.SiteWork;
import akka.actor.UntypedActor;
import analysis.SiteAnalyzer;

public class StaffExtractionWorker extends UntypedActor {

	@Override
	public void onReceive(Object work) throws Exception {
		SiteWork siteWork = (SiteWork) work;
		SiteInformationOld siteInfo = siteWork.getSiteInfo();
		try {
			System.out.println("Analyzing staff for : " + siteInfo.getSiteInformationId());
			SiteAnalyzer.analyzeStaff(siteInfo);
			siteInfo.setStaffExtractionsAnalyzed(true);
			getSender().tell(siteWork, getSelf());
		} catch (Throwable e) {
			Logger.error("Error while analyzing Staff on site : " + e);
		}
		

	}
	
	@Override
	public void postRestart(Throwable reason) throws Exception {
		Logger.error("Staff Extraction worker restarting");
		preStart();
	}
}
