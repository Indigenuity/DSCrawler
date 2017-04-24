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
import persistence.PageCrawl;
import persistence.Site;
import persistence.SiteCrawl;
import play.Logger;
import play.db.jpa.JPA;
import sites.SiteLogic;

public class SiteCrawlLogic {

	public static void removePageCrawl(PageCrawl pageCrawl){
		SiteCrawl siteCrawl = pageCrawl.getSiteCrawl();
		if(siteCrawl.getNewInventoryPage() == pageCrawl){
			siteCrawl.setNewInventoryPage(null);
		}
		if(siteCrawl.getUsedInventoryPage() == pageCrawl){
			siteCrawl.setUsedInventoryPage(null);
		}
		if(siteCrawl.getNewInventoryRoot() == pageCrawl){
			siteCrawl.setNewInventoryRoot(null);
		}
		if(siteCrawl.getUsedInventoryRoot() == pageCrawl){
			siteCrawl.setUsedInventoryRoot(null);
		}
		for(PageCrawl childPageCrawl : pageCrawl.getChildPages()){
			childPageCrawl.setParentPage(null);
		}
		JPA.em().remove(pageCrawl);
	}
	
	public static void replaceAsParent(PageCrawl oldParent, PageCrawl newParent){
		System.out.println("oldParent's children : " + oldParent.getChildPages());
		for(PageCrawl child : oldParent.getChildPages()){
			if(!child.equals(newParent)){
				child.setParentPage(newParent);
			}
			
		}
		newParent.setParentPage(oldParent.getParentPage());
	}
	
	public static void replaceAndRemove(PageCrawl oldParent, PageCrawl newParent){
		System.out.println("removing oldparent : " + oldParent.getPageCrawlId() + " with new parent : " + newParent.getPageCrawlId());
		replaceAsParent(oldParent, newParent);
		removePageCrawl(oldParent);
	}
	
	public static PageCrawl getPageCrawlByUrl(String url, SiteCrawl siteCrawl){
		List<PageCrawl> pageCrawls = JPA.em()
				.createQuery("select pc from SiteCrawl sc join sc.pageCrawls pc where sc.siteCrawlId = :siteCrawlId and pc.url = :url", PageCrawl.class)
				.setParameter("siteCrawlId", siteCrawl.getSiteCrawlId())
				.setParameter("url", url)
				.getResultList();
		if(pageCrawls.size() > 0){
			return pageCrawls.get(0);
		}
		return null;
	}
	
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
