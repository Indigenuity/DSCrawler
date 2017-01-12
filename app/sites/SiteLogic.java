package sites;

import java.util.List;
import java.util.function.Consumer;

import akka.actor.ActorRef;
import async.async.Asyncleton;
import async.functionalwork.ConsumerWorkOrder;
import async.functionalwork.JpaFunctionalBuilder;
import dao.SitesDAO;
import persistence.Site;
import persistence.Site.SiteStatus;
import persistence.UrlCheck;
import play.db.jpa.JPA;
import utilities.UrlSniffer;

public class SiteLogic {
	
	public static void validateSites(List<Site> sites) {
		ActorRef workMaster = Asyncleton.getInstance().getFunctionalMaster(5, true);
		Consumer<Long> validator = JpaFunctionalBuilder.wrapConsumerInFind(SiteLogic::validateSite, Site.class);
		for(Site site : sites){
			ConsumerWorkOrder<Long> workOrder = new ConsumerWorkOrder<Long>(validator, site.getSiteId());
			workMaster.tell(workOrder, ActorRef.noSender());
		}
	}

	public static void validateSite(Site site) {
		System.out.println("checking site : " + site.getHomepage());
		UrlCheck urlCheck = UrlSniffer.checkUrl(site.getHomepage());
		JPA.em().persist(urlCheck);
		site.setUrlCheck(urlCheck);
		SiteLogic.applyUrlCheck(site);
	}

	public static void applyUrlCheck(Site site){
		UrlCheck urlCheck = site.getUrlCheck();
		if(urlCheck == null){
			return;
		}
		
		if(urlCheck.isError()) {
			SiteLogic.markError(site);
		} else if(urlCheck.getStatusCode() >= 400){
			SiteLogic.markDefunct(site);
		} else if(urlCheck.isNoChange()){
			if(urlCheck.isAllApproved()){
				SiteLogic.approve(site);
			} else if(site.getSiteStatus() != SiteStatus.APPROVED){
				SiteLogic.review(site);
			}
		} else {
			if(urlCheck.isAllApproved()){
				SiteLogic.acceptRedirect(site, urlCheck.getResolvedSeed());
			} else if (urlCheck.isDomainApproved()){
				SiteLogic.reviewRedirect(site, urlCheck.getResolvedSeed());
			} else {
				SiteLogic.review(site);
			}
		}
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

	public static Site reviewRedirect(Site site, String newHomepage) {
		return SiteLogic.applyRedirect(site, newHomepage, false);
	}

	public static Site applyRedirect(Site site, String newHomepage, boolean approved){
		Site newSite = new Site(newHomepage);
		if(approved){
			newSite.setSiteStatus(SiteStatus.APPROVED);
		} else {
			newSite.setSiteStatus(SiteStatus.UNVALIDATED);
		}
		JPA.em().persist(newSite);
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
}
