package async.analysis;

import persistence.SiteInformationOld;
import persistence.SiteSummary;
import play.Logger;
import akka.actor.UntypedActor;
import analysis.SiteAnalyzer;
import analysis.SiteSummarizer;
import async.work.SiteWork;

public class SummaryWorker extends UntypedActor {

	@Override
	public void onReceive(Object work) throws Exception {
		SiteWork siteWork = (SiteWork) work;
		SiteInformationOld siteInfo = siteWork.getSiteInfo();
		try {
			System.out.println("Generating Summary for : " + siteInfo.getSiteInformationId());
			SiteSummary summary = SiteSummarizer.summarizeSite(siteInfo);
			siteWork.setSiteSummary(summary);
			siteInfo.setSummaryCompleted(true);
			getSender().tell(siteWork, getSelf());
		} catch (Throwable e) {
			Logger.error("Error while generating summary on site : " + e);
		}
		

	}
	
	@Override
	public void postRestart(Throwable reason) throws Exception {
		Logger.error("Summary worker restarting");
		preStart();
	}
}
