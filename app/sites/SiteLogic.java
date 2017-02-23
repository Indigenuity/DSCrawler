package sites;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.StringUtils;

import akka.actor.ActorRef;
import async.async.Asyncleton;
import async.functionalwork.ConsumerWorkOrder;
import async.functionalwork.JpaFunctionalBuilder;
import dao.SitesDAO;
import datadefinitions.StringExtraction;
import persistence.Site;
import persistence.Site.RedirectType;
import persistence.Site.SiteStatus;
import persistence.UrlCheck;
import play.db.jpa.JPA;
import utilities.DSFormatter;

public class SiteLogic {
	
	
	
//	public static Site fullValidateCheckRedirect(Site site) {
//		fullValidateCheckRedirect(site, )
//		validateUrl(site);
//		Site newSite = logicalRedirect(site);
//		if(newSite != null){
//			return fullValidateCheckRedirect(Site site);
//		}
//	}
//	
//	public static Site fullValidateCheckRedirect(Site site, int numRecursions) {
//		
//	}
	
	public static Site logicalRedirect(Site site) {
		String homepage = site.getHomepage();
		if(site.getBadUrlStructure()){
			homepage = DSFormatter.standardizeBaseUrl(homepage);
			if(!DSFormatter.isBadUrlStructure(homepage)){
				Site newSite = SitesDAO.getOrNewThreadsafe(homepage);
				redirect(site, newSite, RedirectType.STANDARDIZATION);
				return newSite;
			}
		} 
		if(site.getNotStandardQuery()){
			String autoRemoved = DSFormatter.autoRemoveQuery(homepage);
			if(!StringUtils.equals(homepage, autoRemoved)){
				Site newSite = SitesDAO.getOrNewThreadsafe(autoRemoved);
				redirect(site, newSite, RedirectType.QUERY_PARING);
				return newSite;
			}
		}
		return null;
	}
	
	public static void httpRedirect(Site site){
		UrlCheck urlCheck = site.getUrlCheck();
		if(urlCheck == null){
			return;
		}
		if(urlCheck.isStatusApproved() && !urlCheck.isNoChange()){
			Site newSite = SitesDAO.getOrNewThreadsafe(urlCheck.getResolvedSeed());
			redirect(site, newSite, RedirectType.HTTP);
		}
	}
	
	public static void validateUrl(Site site) {
		URL url;
		try {
			url = new URL(site.getHomepage());
		} catch (MalformedURLException e) {
			site.setBadUrlStructure(true);
			return;
		}
		site.setBadUrlStructure(DSFormatter.isBadUrlStructure(site.getHomepage()));
		site.setDefunctDomain(DSFormatter.isDefunctDomain(url));
		site.setDefunctPath(DSFormatter.isDefunctPath(url));
		site.setNotStandardHomepagePath(DSFormatter.isNotStandardHomepagePath(url));
		site.setUncrawlableDomain(DSFormatter.isUncrawlableDomain(url));
		site.setNotStandardQuery(DSFormatter.isBadQuery(url));
	}
	
	public static void checkUrl(Site site) {
		UrlCheck urlCheck = UrlChecker.checkUrl(site.getHomepage());
		JPA.em().persist(urlCheck);
		site.setUrlCheck(urlCheck);
		if(!urlCheck.isError() && !urlCheck.isStatusApproved()){
			site.setHttpError(true);
		} else if(!urlCheck.isError()) {
			site.setHttpError(false);
		}
	}
	
	public static void validateSitesById(List<Long> siteIds) {
		Asyncleton.getInstance().runConsumerMaster(25, 
				JpaFunctionalBuilder.wrapConsumerInFind(SiteLogic::validateSite, Site.class),
				siteIds.stream(), 
				true);
	}
	
	public static void validateSites(List<Site> sites) {
		validateSitesById(sites.stream()
				.map((site) -> site.getSiteId())
				.collect(Collectors.toList()));
	}

	public static UrlCheck validateSite(Site site) {
		System.out.println("checking site : " + site.getHomepage());
		UrlCheck urlCheck = UrlChecker.checkUrl(site.getHomepage());
		JPA.em().persist(urlCheck);
		site.setUrlCheck(urlCheck);
		SiteLogic.httpRedirect(site);
		return urlCheck;
	}
	
	public static void attemptParePath(Site site) {
		System.out.println("attempting path pare : " + site.getHomepage());
		String newHomepage = DSFormatter.parePath(site.getHomepage());
		UrlCheck urlCheck = UrlChecker.checkUrl(newHomepage);
		System.out.println("Resolved seed of attempt : " + urlCheck.getResolvedSeed());
		if(urlCheck.isStatusApproved() && !urlCheck.isError() && !StringUtils.equals(site.getHomepage(), urlCheck.getResolvedSeed())){
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
	
	public static void redirect(Site origin, Site destination, RedirectType redirectType) {
		if(origin.getSiteId() == destination.getSiteId()){
			throw new IllegalStateException("Cannot redirect site to itself: " + origin.getSiteId() + " (" + redirectType + ")");
		}
		origin.setRedirects(true);
		origin.setRedirectsTo(destination);
		origin.setRedirectReason(redirectType);
	}
	
	public static void unapproveSite(Site site) {
		site.setUnapproved(true);
	}

	
	
	public static Site approvePath(Site site) {
		site.setApprovedHomepagePath(true);
		return site;
	}

	public static Site markError(Site site) {
		site.setSiteStatus(SiteStatus.OTHER_ISSUE);
		return site;
	}

	public static Site markDefunct(Site site) {
		site.setSiteStatus(SiteStatus.DEFUNCT);
		return site;
	}

	public static Site approve(Site site) {
		site.setSiteStatus(SiteStatus.APPROVED);
		site.setForwardsTo(null);
		site.setManualForwardsTo(null);
		return site;
	}

	public static Site review(Site site) {
		site.setSiteStatus(SiteStatus.NEEDS_REVIEW);
		return site;
	}

	public static Site acceptRedirect(Site site, String newHomepage) {
		return SiteLogic.applyRedirect(site, newHomepage, true);
	}
	
	public static Site acceptRedirect(Site site) {
		return SiteLogic.applyRedirect(site, site.getUrlCheck().getResolvedSeed(), true);
	}

	public static Site reviewRedirect(Site site, String newHomepage) {
		return SiteLogic.applyRedirect(site, newHomepage, false);
	}

	public static Site applyRedirect(Site site, String newHomepage, boolean approved){
		Site newSite = SitesDAO.getFirst("homepage", newHomepage);
		if(newSite == null){
			newSite = new Site(newHomepage);
			JPA.em().persist(newSite);
			if(approved){
				newSite.setSiteStatus(SiteStatus.APPROVED);
			}
		}
		site.setSiteStatus(SiteStatus.REDIRECTS);
		site.setForwardsTo(newSite);
		return newSite;
	}

	//Returns the NEW site
	public static Site manuallyRedirect(Site site, String newHomepage) {
		Site newSite = SitesDAO.getOrNew(newHomepage);
		site.setManualForwardsTo(newSite);
		site.setSiteStatus(SiteStatus.MANUALLY_REDIRECTS);
		return newSite;
	}

	public static void acceptUrlCheck(Site site, boolean sharedSite) {
		UrlCheck urlCheck = site.getUrlCheck();
		if(urlCheck == null){
			throw new IllegalArgumentException("Can't accept UrlCheck of Site without UrlCheck : " + site.getSiteId());
		}
		
		if(urlCheck.isNoChange()){
			approve(site);
			site.setSharedSite(sharedSite);
		} else {
			acceptRedirect(site, urlCheck.getResolvedSeed())
				.setSharedSite(sharedSite);
		}
	}
	
	public static Site disapprove(Site site){
		site.setSiteStatus(SiteStatus.DISAPPROVED);
		return site;
	}
}
