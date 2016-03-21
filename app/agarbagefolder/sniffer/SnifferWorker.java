package agarbagefolder.sniffer;

import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.ConnectTimeoutException;

import agarbagefolder.SiteWork;
import dao.SiteInformationDAO;
import akka.actor.UntypedActor;
import async.async.Asyncleton;
import async.work.WorkItem;
import async.work.WorkStatus;
import persistence.CrawlSet;
import persistence.Site;
import persistence.SiteInformationOld;
import play.Logger;
import play.db.DB;
import play.db.jpa.JPA;
import utilities.DSFormatter;
import utilities.UrlSniffer;

public class SnifferWorker extends UntypedActor {
	@Override
	public void onReceive(Object work) throws Exception {

		WorkItem workItem = (WorkItem) work;
		workItem.setWorkStatus(WorkStatus.WORK_IN_PROGRESS);
		JPA.withTransaction( () -> {
			
			Site site = JPA.em().find(Site.class, workItem.getSiteId());
			CrawlSet crawlSet = null;
			if(workItem.getCrawlSetId() != 0){
				crawlSet = JPA.em().find(CrawlSet.class, workItem.getCrawlSetId());
			}
			
			try {
				//Basic URL Validation
				if(StringUtils.isEmpty(site.getHomepage())){
					throw new IllegalArgumentException("Empty homepage");
				}
				site.setHomepage(DSFormatter.toHttp(site.getHomepage()));
				
				System.out.println("checking redirect resolve");
				site.setSuggestedHomepage(UrlSniffer.getRedirectedUrl(site.getHomepage()));
				
				
				URL validUrl = new URL(site.getSuggestedHomepage());
				String rebuilt = DSFormatter.standardize(site.getSuggestedHomepage());
				System.out.println("rebuilt : " + rebuilt);
				site.setSuggestedHomepage(rebuilt);
				
				boolean genericRedirect = UrlSniffer.isGenericRedirect(site.getSuggestedHomepage(), site.getHomepage());
				boolean maybeDefunct = checkDefunct(site.getSuggestedHomepage());
				boolean isApprovedEnding = DSFormatter.isApprovedUrl(rebuilt);
				boolean queryIsApproved	= validateQuery(validUrl);
				
				String message = "";
				
				
				if(!isApprovedEnding){
					message += "Not approved url ending; ";
					site.setHomepageNeedsReview(true);
				}
				
				if(!queryIsApproved){
					message += "Unapproved query string; ";
					site.setHomepageNeedsReview(true);
				}
				
				if(!genericRedirect) {
					message += "Site redirects to new URL; ";
					site.setHomepageNeedsReview(true);
				}
				
				if(maybeDefunct) {
					site.setMaybeDefunct(maybeDefunct);
					site.setHomepageNeedsReview(false);
					message = "";
				}
				
				site.setReviewReason(message);
				
				if(genericRedirect && isApprovedEnding && queryIsApproved){
					
					site.getRedirectUrls().add(site.getHomepage());
					site.setHomepage(site.getSuggestedHomepage());
					site.setSuggestedHomepage(null);
					site.setDomain(validUrl.getHost());
					site.setStandardizedHomepage(DSFormatter.standardize(site.getHomepage()));
					site.setHomepageNeedsReview(false);
					site.setReviewLater(false);
				}
				
				if(!site.isHomepageNeedsReview() && crawlSet != null){
					crawlSet.finishRedirectResolve(site);
				}
				site.setRedirectResolveDate(new java.sql.Date(Calendar.getInstance().getTimeInMillis()));
			}
			catch(MalformedURLException e) {
				site.setInvalidUrl(true);
				site.setHomepageNeedsReview(false);
			}
			catch(Exception e) {
				Logger.error("Unexpected error in sniffer worker : " + e);
				site.setHomepageNeedsReview(true);
				site.setRedirectResolveDate(new java.sql.Date(Calendar.getInstance().getTimeInMillis()));
				site.setReviewReason(e.getMessage());
			}
		});
		
		workItem.setWorkStatus(WorkStatus.WORK_COMPLETED);
		getSender().tell(workItem, getSelf());
		
	}
	
	private static boolean validateQuery(URL url) throws MalformedURLException {
		if(url.getQuery() == null || url.getQuery().equals(""))
			return true;
		return false;
	}
	
	private static boolean checkDefunct(String url) {
		url = url.toLowerCase();
		if(url.contains("UnusedDomains.htm")){
			return true;
		}
		if(url.contains("dealershipblackhole")){
			return true;
		}
		
		return false;
	}
	
	@Override
	public void postRestart(Throwable reason) throws Exception {
		Logger.debug("Sniffer worker restarting");
		preStart();
	}
	
}
