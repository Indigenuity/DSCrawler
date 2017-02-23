package sites.crawling;

import java.util.List;
import java.util.function.Consumer;

import akka.actor.ActorRef;
import async.async.Asyncleton;
import async.functionalwork.ConsumerWorkOrder;
import async.functionalwork.JpaFunctionalBuilder;
import crawling.DealerCrawlController;
import dao.SitesDAO;
import datadefinitions.NoCrawlDomain;
import persistence.Site;
import persistence.SiteCrawl;
import play.Logger;
import play.db.jpa.JPA;
import sites.SiteLogic;

public class SiteCrawlLogic {

	
	public static void crawlSites(List<Long> siteIds){
		Asyncleton.getInstance().runConsumerMaster(25, 
				JpaFunctionalBuilder.wrapConsumerInTransactionFind(SiteCrawlLogic::crawlSite, Site.class),
				siteIds.stream(), 
				false);
	}
	
	public static void crawlStaleSites(List<Long> siteIds){
		Asyncleton.getInstance().runConsumerMaster(25, 
				JpaFunctionalBuilder.wrapConsumerInTransactionFind(SiteCrawlLogic::crawlStaleSite, Site.class),
				siteIds.stream(), 
				false);
	}
	
	public static void crawlSite(Site site){
		crawlSite(site, true);
	}
	
	public static void crawlStaleSite(Site site){
		crawlSite(site, false);
	}
	
	public static void crawlSite(Site site, boolean crawlIfFresh){
		try{
			boolean shouldCrawl = true;
			for(NoCrawlDomain domain : NoCrawlDomain.values()) {
				if(site.getHomepage().contains(domain.definition)) 
					shouldCrawl = false;
			}
			
			if(!crawlIfFresh && site.getMostRecentCrawl()!= null && SitesDAO.STALE_DATE.before(site.getMostRecentCrawl())){		//most recent sitecrawl is too fresh to do another one.
				shouldCrawl = false;
			}
			if(shouldCrawl){
				
				final SiteCrawl siteCrawl = DealerCrawlController.crawlSite(site.getHomepage());
				JPA.withTransaction(() -> {
					siteCrawl.setSite(site);
					JPA.em().persist(siteCrawl);
				});
			}
		}catch(Exception e){
			Logger.error("error while crawling : " + e);
			throw new RuntimeException(e);
		}
	}
}
