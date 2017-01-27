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
	
	public static void validateSitesById(List<Long> siteIds) {
		ActorRef workMaster = Asyncleton.getInstance().getFunctionalMaster(25, true);
		Consumer<Long> validator = JpaFunctionalBuilder.wrapConsumerInFind(SiteLogic::validateSite, Site.class);
		for(Long siteId: siteIds){
			ConsumerWorkOrder<Long> workOrder = new ConsumerWorkOrder<Long>(validator, siteId);
			workMaster.tell(workOrder, ActorRef.noSender());
		}
	}
	
	public static void validateSites(List<Site> sites) {
		ActorRef workMaster = Asyncleton.getInstance().getFunctionalMaster(25, true);
		Consumer<Long> validator = JpaFunctionalBuilder.wrapConsumerInFind(SiteLogic::validateSite, Site.class);
		for(Site site : sites){
			ConsumerWorkOrder<Long> workOrder = new ConsumerWorkOrder<Long>(validator, site.getSiteId());
			workMaster.tell(workOrder, ActorRef.noSender());
		}
	}

	public static UrlCheck validateSite(Site site) {
		System.out.println("checking site : " + site.getHomepage());
		UrlCheck urlCheck = UrlSniffer.checkUrl(site.getHomepage());
		JPA.em().persist(urlCheck);
		site.setUrlCheck(urlCheck);
		SiteLogic.applyUrlCheck(site);
		return urlCheck;
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
			if(urlCheck.isAllApproved() && site.getForwardsTo() == null && site.getManualForwardsTo() == null){
				SiteLogic.approve(site);
			} else if(site.getSiteStatus() != SiteStatus.APPROVED){
				SiteLogic.review(site);
			}
		} else {
			if(site.getForwardsTo()!= null && !urlCheck.getResolvedSeed().equals(site.getForwardsTo().getHomepage())){
				SiteLogic.review(site);
			} else if(site.getManualForwardsTo()!= null && !urlCheck.getResolvedSeed().equals(site.getManualForwardsTo().getHomepage())){
				SiteLogic.review(site);
			} else if(urlCheck.isAllApproved()){
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
