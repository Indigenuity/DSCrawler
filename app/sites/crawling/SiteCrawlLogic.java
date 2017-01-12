package sites.crawling;

import java.util.List;
import java.util.function.Consumer;

import akka.actor.ActorRef;
import async.async.Asyncleton;
import async.functionalwork.ConsumerWorkOrder;
import crawling.DealerCrawlController;
import datadefinitions.NoCrawlDomain;
import persistence.Site;
import persistence.SiteCrawl;
import play.Logger;
import play.db.jpa.JPA;

public class SiteCrawlLogic {

	
	public static void crawlSites(List<Long> siteIds){
		ActorRef workMaster = Asyncleton.getInstance().getFunctionalMaster(25, false);
		Consumer<Long> crawler =SiteCrawlLogic::crawlSite;
		for(Long siteId : siteIds){
			ConsumerWorkOrder<Long> workOrder = new ConsumerWorkOrder<Long>(crawler, siteId);
			workMaster.tell(workOrder, ActorRef.noSender());
		}
	}
	
	public static void crawlSite(Long siteId){
		
		try{
			Site[] siteTemp = new Site[1];
			JPA.withTransaction( () -> {
				siteTemp[0]= JPA.em().find(Site.class, siteId);
			});
			Site site = siteTemp[0];
			boolean shouldCrawl = true;
			for(NoCrawlDomain domain : NoCrawlDomain.values()) {
				if(site.getHomepage().contains(domain.definition)) 
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
