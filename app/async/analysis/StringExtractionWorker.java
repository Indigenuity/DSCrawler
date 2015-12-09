package async.analysis;

import persistence.SiteInformationOld;
import play.Logger;
import akka.actor.UntypedActor;
import analysis.SiteAnalyzer;
import async.work.SiteWork;

public class StringExtractionWorker extends UntypedActor {

	@Override
	public void onReceive(Object work) throws Exception {
		SiteWork siteWork = (SiteWork) work;
		SiteInformationOld siteInfo = siteWork.getSiteInfo();
		try {
			System.out.println("Analyzing String Extractions for : " + siteInfo.getSiteInformationId());
			SiteAnalyzer.analyzeStringExtractions(siteInfo);
			siteInfo.setStringExtractionsAnalyzed(true);
			getSender().tell(siteWork, getSelf());
		} catch (Throwable e) {
			Logger.error("Error while analyzing string extractions on site : " + e);
		}
		

	}
	
	@Override
	public void postRestart(Throwable reason) throws Exception {
		Logger.error("String Extraction worker restarting");
		preStart();
	}
}
