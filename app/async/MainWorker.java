package async;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.http.conn.ConnectTimeoutException;

import crawling.DealerCrawlController;
import dao.SiteInformationDAO;
import persistence.Site;
import persistence.SiteCrawl;
import persistence.SiteInformationOld;
import persistence.SiteSummary;
import play.Logger;
import play.db.DB;
import utilities.DSFormatter;
import utilities.UrlSniffer;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import analysis.SiteAnalyzer;
import analysis.SiteSummarizer;
import async.monitoring.AsyncMonitor;
import async.monitoring.AsyncMonitor.CompletedWork;
import async.monitoring.AsyncMonitor.WorkInProgress;
import async.work.SiteWork;

public class MainWorker extends UntypedActor {

	@Override
	public void onReceive(Object work) throws Exception {
		SiteWork siteWork = (SiteWork) work;
//		System.out.println("received");
		try{
		
			
//			if(siteWork.getAllWorkNeeded() == SiteWork.DO_WORK){
//				markWorkNeeded(siteWork);
//			}
//			
//			//Only perform one of the following async actions:
//			if(siteWork.getUrlWork() == SiteWork.DO_WORK){
//				doUrlWork(siteWork);
//			}  
//			else if(siteWork.getRedirectResolveWork() == SiteWork.DO_WORK){
//				Asyncleton.instance().getRedirectResolveMaster().tell(siteWork, getSelf());
//			}
//			else if(siteWork.getRestoreWork() == SiteWork.DO_WORK){
//				Asyncleton.instance().getDataTransferMaster().tell(siteWork, getSelf());;
//			}
//			else if(siteWork.getCrawlWork() == SiteWork.DO_WORK){
////				System.out.println("doing crawl work (" + site.getSiteId() + ")");
//				Asyncleton.instance().getCrawlingMaster().tell(siteWork, getSelf());
////				System.out.println("told work(" + site.getSiteId() + ")");
//			}
//			else if(siteWork.getDocAnalysisWork() == SiteWork.DO_WORK){
//				System.out.println("Doing doc analysis work for : " + siteWork.getSiteCrawlId());
//				Asyncleton.instance().getDocAnalysisMaster().tell(siteWork, getSelf());
//			}
//			else if(siteWork.getMetaAnalysisWork() == SiteWork.DO_WORK){
//				System.out.println("Doing meta analysis work for : " + siteWork.getSiteCrawlId());
//				Asyncleton.instance().getMetaAnalysisMaster().tell(siteWork, getSelf());
//			}
//			else if(siteWork.getAmalgamationWork() == SiteWork.DO_WORK){
//				System.out.println("Doing doc Amalgamation work for : " + siteWork.getSiteCrawlId());
//				Asyncleton.instance().getAmalgamationMaster().tell(siteWork, getSelf());
//			}
//			else if(siteWork.getTextAnalysisWork() == SiteWork.DO_WORK){
//				System.out.println("Doing text analysis work for : " + siteWork.getSiteCrawlId());
//				Asyncleton.instance().getTextAnalysisMaster().tell(siteWork, getSelf());
//			}
////			else if(siteWork.getInferenceWork() == SiteWork.DO_WORK){
////				Asyncleton.instance().getInferenceMaster().tell(siteWork, getSelf());
////			}
//			else if(siteWork.getMatchesWork() == SiteWork.DO_WORK) {
//				Asyncleton.instance().getMatchingMaster().tell(siteWork, getSelf());
//			}
//			else if(siteWork.getStringExtractionWork() == SiteWork.DO_WORK) {
//				Asyncleton.instance().getStringExtractionMaster().tell(siteWork, getSelf());
//			}
//			else if(siteWork.getStaffExtractionWork() == SiteWork.DO_WORK) {
//				Asyncleton.instance().getStaffExtractionMaster().tell(siteWork, getSelf());
//			}
//			else if(siteWork.getSummaryWork() == SiteWork.DO_WORK) {
//				Asyncleton.instance().getSummaryMaster().tell(siteWork, getSelf());
//			}
//			else if(siteWork.getCustomWork() == SiteWork.DO_WORK) {
//				System.out.println("Doing custom work");
//				doCustomWork(siteWork);
//			}
//			else if(siteWork.getBackupWork() == SiteWork.DO_WORK){
//				Asyncleton.instance().getDataTransferMaster().tell(siteWork, getSelf());;
//			}
//			else {
//				System.out.println("Work Completed. SiteId : " + siteWork.getSiteId() + " SiteCrawlId : " + siteWork.getSiteCrawlId());
//			}
		}
		catch(Exception e) {
			Logger.error("Caught exception in MainWorker  (SiteId : " + siteWork.getSiteId() + " SiteCrawlId : " + siteWork.getSiteCrawlId() + ") : " + e);
			e.printStackTrace();
		}
		
		//No response sent to master
	}
	
	private void markWorkNeeded(SiteWork siteWork) {
		SiteInformationOld siteInfo = siteWork.getSiteInfo();
		if(siteInfo == null){
			Logger.error("Tried to mark work on null SiteInformation");
			return;
		}
		
		if(DSFormatter.isEmpty(siteInfo.getIntermediateUrl())){
			siteWork.setUrlWork(SiteWork.DO_WORK);
		}
		if(siteInfo.getCrawlDate() == null){
			siteWork.setCrawlWork(SiteWork.DO_WORK);
		}
		if(!siteInfo.isMatchesAnalyzed()){
			siteWork.setMatchesWork(SiteWork.DO_WORK);
		}
		if(!siteInfo.isStringExtractionsAnalyzed()){
			siteWork.setStringExtractionWork(SiteWork.DO_WORK);
		}
		if(!siteInfo.isStaffExtractionsAnalyzed()){
			siteWork.setStaffExtractionWork(SiteWork.DO_WORK);
		}
		if(!siteInfo.isSummaryCompleted()){
			siteWork.setSummaryWork(SiteWork.DO_WORK);
		}
		
		siteWork.setAllWorkNeeded(SiteWork.NO_WORK);
	}
	
	private void doCustomWork(SiteWork siteWork) throws IllegalArgumentException, IllegalAccessException {
//		siteWork.setCustomWork(SiteWork.WORK_COMPLETED);
//		SiteSummarizer.inferScheduler(siteWork.getSiteSummary());
//		Asyncleton.instance().getMainListener().tell(siteWork,  getSelf());
	}
	
	private static String getIntermediateUrl(String givenUrl) {
		String intermediateUrl = DSFormatter.toHttp(givenUrl);
		intermediateUrl = DSFormatter.removeQueryString(intermediateUrl);
		return intermediateUrl;
	}
	
	private void doUrlWork(SiteWork siteWork) throws MalformedURLException, IOException, SQLException {
		
		Site site = siteWork.getSite();
		//Nothing to do if there isn't a url; mark the site as such 
		if(site.getHomepage() == null || site.getHomepage().equals("")){
			site.setHomepageNeedsReview(true);
		}
		
		String intermediateUrl = DSFormatter.toHttp(site.getHomepage());
		intermediateUrl = DSFormatter.removeQueryString(intermediateUrl);
		if(!DSFormatter.isApprovedUrl(intermediateUrl)){
			System.out.println("unapproved url : " + intermediateUrl);
			site.setHomepageNeedsReview(true);
		}
		else {
			site.setHomepage(intermediateUrl);
			site.setHomepageNeedsReview(false);
		}
		
		
		siteWork.setUrlWork(SiteWork.WORK_COMPLETED);
//		Asyncleton.instance().getMainListener().tell(siteWork, getSelf());
	}
	
	@Override
	public void postRestart(Throwable reason) throws Exception {
		Logger.error("Main worker restarting");
		preStart();
	}
	

}
