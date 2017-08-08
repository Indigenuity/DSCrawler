package sites.utilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import akka.actor.ActorRef;
import async.async.Asyncleton;
import async.functionalwork.ConsumerWorkOrder;
import async.functionalwork.JpaFunctionalBuilder;
import dao.SitesDAO;
import datadefinitions.StringExtraction;
import datadefinitions.StringMatchUtils;
import datadefinitions.newdefinitions.AutoRemoveQuery;
import global.Global;
import persistence.Site;
import persistence.SiteCrawl;
import persistence.Site.RedirectType;
import persistence.Site.SiteStatus;
import persistence.UrlCheck;
import play.db.jpa.JPA;
import sites.UrlChecker;
import utilities.DSFormatter;
import utilities.HttpUtils;
import utilities.UrlUtils;

public class SiteLogic {
	
	public static boolean isBlankSite(Site site){
		if(site == null || StringUtils.isEmpty(site.getHomepage())){
			return true;
		}
		return false;
	}
	
	public static boolean redirectPathIsTrivial(Site site){
		return redirectIsTrivial(site) && redirectPathIsTrivial(site.getRedirectsTo());
	}
	
	public static boolean redirectIsTrivial(Site site){
		if(site == null || site.getRedirectsTo() == null){
			return true;
		}
		
		if(site.getRedirectReason() == RedirectType.STANDARDIZATION || site.getRedirectReason() == RedirectType.QUERY_PARING){
			return true;
		}
		if(site.getRedirectReason() == RedirectType.HTTP){
			return UrlUtils.isGenericRedirect(site.getRedirectsTo().getHomepage(), site.getHomepage());
		}
		return false;
	}
	
	//***************** Detecting and generating redirects ************************//
	
	public static Site getRedirectEndpoint(Site site, boolean allowManualRedirects){
		if(site == null) {
			return null;
		}
		Site destination = site.getRedirectsTo();
		if(destination != null){
			if(site.getSiteId() == destination.getSiteId()){
				throw new IllegalStateException("Illegal State!  Site redirects to itself : " + site.getSiteId() + " : " + site.getHomepage());
			}
			return getRedirectEndpoint(destination, allowManualRedirects);
		}
		return site;
	}
	
	public static Site refreshRedirectPath(Site site, boolean forceHttpCheck){
		Site redirected = null;
		Site currentSite = site;
		int redirectCount = 0;
		while((redirected = tryRedirect(currentSite, forceHttpCheck)) != null && redirectCount < 500) {
			currentSite = redirected;
//			System.out.println("AFter try redirect : " + redirected.getHomepage());
			redirectCount++;
		};
		return currentSite;
	}
	
	//forceHttpCheck will make an http check for the Site even if it has a recent check
	public static Site tryRedirect(Site site, boolean forceHttpCheck){
		System.out.println("trying redirects : " + site.getHomepage());
		Site redirected = tryRedirectByStructure(site);
		if(redirected != null){
			System.out.println("found structural redirect : " + redirected.getHomepage());
			return redirected; 
		}
		return tryRedirectByHttp(site, forceHttpCheck);
	}
	
	public static Site tryRedirectByStructure(Site site) {
		Site redirected = tryRedirectByStandardization(site);
		if(redirected != null){
			return redirected; 
		}
		redirected = tryRedirectByQueryAutoRemove(site);
		return redirected;
	}
	
	public static Site tryRedirectByStandardization(Site site){
		if(site == null){
			return SitesDAO.getOrNewThreadsafe("");
		}
		if(site.getBadUrlStructure()){
			String standardized = standardizeBaseUrl(site.getHomepage());
			if(!isBadUrlStructure(standardized)){
//				System.out.println("Found standardization redirect : " + standardized);
				return redirect(site, standardized, RedirectType.STANDARDIZATION);
			}
		}
		return null;
	}
	
	public static Site tryRedirectByQueryAutoRemove(Site site){
		if(site.getNotStandardQuery()){
			String autoRemoved = autoRemoveQuery(site.getHomepage());
			if(!StringUtils.equals(site.getHomepage(), autoRemoved)){
//				System.out.println("Found autoRemove redirect : " + autoRemoved);
				return redirect(site, autoRemoved, RedirectType.QUERY_PARING);
			}
		}
		return null;
	}
	
	public static Site tryRedirectByHttp(Site site, boolean forceHttpCheck){
		if(forceHttpCheck || isStaleHttpCheck(site.getUrlCheck())){
			performHttpCheck(site);
		}
		return applyHttpCheck(site);
	}
	
	public static UrlCheck performHttpCheck(Site site) {
		UrlCheck urlCheck = UrlChecker.checkUrl(site.getHomepage());
		JPA.em().persist(urlCheck);
		site.setUrlCheck(urlCheck);
		return urlCheck;
	}
	
	public static Site applyHttpCheck(Site site){
		UrlCheck urlCheck = site.getUrlCheck();
		site.setHttpError(urlCheck.isError());
		site.setBadStatus(!urlCheck.isStatusApproved());
		if(!urlCheck.isError() && !urlCheck.isNoChange()){
			System.out.println("going to redirect based on http");
			return redirect(site, urlCheck.getResolvedSeed(), RedirectType.HTTP);
		}
		if(!urlCheck.isError() && urlCheck.isNoChange()){
			clearRedirects(site);
		}
		return null;
	}
	
	public static Site redirect(Site origin, Site destination, RedirectType redirectType) {
		if(origin.getSiteId() == destination.getSiteId()){
			throw new IllegalStateException("Cannot redirect site to itself: " + origin.getSiteId() + " (" + redirectType + ")");
		}
		origin.setRedirectsTo(destination);
		origin.setRedirectReason(redirectType);
		return destination;
	}
	
	public static Site redirect(Site origin, String destinationString, RedirectType redirectType) {
//		System.out.println("redirecting from " + origin + " to " + destinationString);
		if(homepageEquals(origin.getRedirectsTo(), destinationString)){		//Save a DB call (in getOrNewThreadsafe) if we can.  
			return redirect(origin, origin.getRedirectsTo(), redirectType);
		}
		return redirect(origin, SitesDAO.getOrNewThreadsafe(destinationString), redirectType);
	}
	
	public static void clearRedirects(Site site){
		site.setRedirectReason(null);
		site.setRedirectsTo(null);
	}
	
	public static boolean homepageEquals(Site site, String url){
		return site != null && StringUtils.equals(url, site.getHomepage());
	}

	
	//***************Analyzing Site URLs **********************************//
	
	public static void analyzeUrlStructure(Site site) {
//		System.out.println("Analyzing structure : " + site.getHomepage());
		URL url;
		try {
			url = new URL(site.getHomepage());
		} catch (MalformedURLException e) {
			site.setBadUrlStructure(true);
			return;
		}
		site.setDomain(UrlUtils.removeWww(url.getHost()));
		site.setBadUrlStructure(SiteLogic.isBadUrlStructure(site.getHomepage()));
		site.setDefunctDomain(DSFormatter.isDefunctDomain(url));
		site.setDefunctPath(DSFormatter.isDefunctPath(url));
		site.setNotStandardHomepagePath(DSFormatter.isNotStandardHomepagePath(url));
		site.setUncrawlableDomain(DSFormatter.isUncrawlableDomain(url));
		site.setNotStandardQuery(DSFormatter.isBadQuery(url));
	}
	
	//****************** Mass tasks *********************************//
	
	
	
	
	public static boolean isStaleHttpCheck(UrlCheck httpCheck){
		if(httpCheck == null || httpCheck.getCheckDate() == null || httpCheck.getCheckDate().before(Global.getStaleDate())){
			return true;
		}
		return false;
	}

	public static void attemptParePath(Site site) {
		System.out.println("attempting path pare : " + site.getHomepage());
		String newHomepage = DSFormatter.parePath(site.getHomepage());
		UrlCheck urlCheck = UrlChecker.checkUrl(newHomepage);
		System.out.println("Resolved seed of attempt : " + urlCheck.getResolvedSeed());
		if(HttpUtils.isSuccessful(urlCheck.getStatusCode()) && !urlCheck.isError() && !StringUtils.equals(site.getHomepage(), urlCheck.getResolvedSeed())){
			Site newSite = SitesDAO.getOrNew(urlCheck.getResolvedSeed());
			redirect(site, newSite, RedirectType.PATH_PARING);
		} else {
			site.setApprovedHomepagePath(true);
			System.out.println("Redirects back to original.  Approving path ");
		}
		
	}
	
	public static void parePath(Site site) {
		System.out.println("Path paring for site : " + site.getSiteId());
		String newHomepage = DSFormatter.parePath(site.getHomepage());
		System.out.println("New Homepage is : " + newHomepage);
		
		Site newSite = SitesDAO.getOrNewThreadsafe(newHomepage);
		redirect(site, newSite, RedirectType.PATH_PARING);
	}
	
	public static String standardizeBaseUrl(String original) {
		try {
			original = UrlUtils.toHttp(original);
			URL url = new URL(original);
			String rebuilt = url.toString();
			if(StringUtils.isEmpty(url.getPath()) && StringUtils.isEmpty(url.getQuery())){
				rebuilt = rebuilt + "/";
			}
			return rebuilt;
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Illegal url");
		}
	}

	public static String autoRemoveQuery(String original){
		try {
			URL url = new URL(original);
			String rebuilt = url.getProtocol() + "://" + url.getHost() + url.getPath();
			if(!StringMatchUtils.matchesAny(AutoRemoveQuery.values(), url.getQuery())){
				if(StringUtils.isEmpty(url.getPath())){
					rebuilt += "/";
				} else{
					rebuilt += "?" + url.getQuery();
				} 
			} 
			return rebuilt;
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Illegal url");
		}
	}

	public static boolean isBadUrlStructure(String urlString){
		try {
			URL url = new URL(urlString);
			if(StringUtils.isEmpty(url.getPath())){
				return true;
			}
			Matcher matcher = StringExtraction.HOST.getPattern().matcher(url.getHost());
			if(!matcher.matches()){
				return true;
			}
			if(DSFormatter.hasBadUrlCharacters(urlString)){
				return true;
			}
		} catch (MalformedURLException e) {
			return true;
		}
		return false;
	}
	

	public void refreshLastCrawl(Site site){
		Date maxDate = null;
		for(SiteCrawl siteCrawl : site.getCrawls()){
			if(maxDate == null || siteCrawl.getCrawlDate().compareTo(maxDate) > 0){
				maxDate = siteCrawl.getCrawlDate();
				site.setLastCrawl(siteCrawl);
			}
		}
	}
	
}
