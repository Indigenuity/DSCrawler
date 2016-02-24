package agarbagefolder.analysis;


import persistence.SiteInformationOld;
import play.Logger;
import akka.actor.UntypedActor;
import analysis.SiteAnalyzer;
import async.work.SiteWork;

public class MatchingWorker extends UntypedActor {

	@Override
	public void onReceive(Object work) throws Exception {
		SiteWork siteWork = (SiteWork) work;
		SiteInformationOld siteInfo = siteWork.getSiteInfo();
		try {
			System.out.println("Analyzing matches for : " + siteInfo.getSiteInformationId());
			SiteAnalyzer.analyzeMatches(siteInfo);
			siteInfo.setMatchesAnalyzed(true);
			getSender().tell(siteWork, getSelf());
		} catch (Throwable e) {
			Logger.error("Error while analyzing matches on site : " + e);
		}
		

	}
	
	@Override
	public void postRestart(Throwable reason) throws Exception {
		Logger.error("Matching worker restarting");
		preStart();
	}
}
