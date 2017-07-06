package sites.crawling;

import java.util.List;
import java.util.function.Consumer;

import javax.persistence.NoResultException;

import akka.actor.ActorRef;
import async.async.Asyncleton;
import async.functionalwork.ConsumerWorkOrder;
import async.functionalwork.JpaFunctionalBuilder;
import crawling.DealerCrawlController;
import crawling.discovery.local.SiteCrawlPlan;
import dao.SitesDAO;
import datadefinitions.NoCrawlDomain;
import persistence.PageCrawl;
import persistence.Site;
import persistence.SiteCrawl;
import play.Logger;
import play.db.jpa.JPA;
import sites.SiteLogic;
import sites.utilities.PageCrawlLogic;

public class SiteCrawlLogic {
	
	

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
		PageCrawlLogic.removePageCrawl(oldParent);
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
	
	public static void queueCrawl(Site site){
		SiteCrawlPlan crawlPlan = new SiteCrawlPlan(site);
		Asyncleton.getInstance().getCrawlMaster().tell(crawlPlan, ActorRef.noSender());
	}
	
	public static void queueRecrawl(SiteCrawl siteCrawl){
		SiteCrawlPlan crawlPlan = new SiteCrawlPlan(siteCrawl);
		Asyncleton.getInstance().getCrawlMaster().tell(crawlPlan, ActorRef.noSender());
	}
	
	public static void ensureFreshInventorySiteCrawl(Site site){
		System.out.println("ensuring inventorysitecrawl : "  + site.getHomepage());
		SiteCrawl siteCrawl = getMostRecentCrawl(site);
		
		if(isStale(siteCrawl)){
			System.out.println("stale : " + siteCrawl.getCrawlDate());
			queueCrawl(site);
		} else if(!isSatisfactoryInventoryCrawl(siteCrawl)){
			System.out.println("unsatisfactory inventory crawl: " + siteCrawl);
			queueRecrawl(siteCrawl);
		}
	}
	
	public static void ensureFreshInventorySiteCrawls(int limit, int offset){
		String queryString = "select s.siteId from Site s";
		List<Long> siteIds = JPA.em().createQuery(queryString, Long.class).setMaxResults(limit).setFirstResult(offset).getResultList();
		System.out.println("siteIds : " + siteIds.size());
		Asyncleton.getInstance().runConsumerMaster(25, 
				JpaFunctionalBuilder.wrapConsumerInFind(SiteCrawlLogic::ensureFreshInventorySiteCrawl, Site.class), 
				siteIds.stream(),
				true);
	}
	
	public static boolean isSatisfactoryInventoryCrawl(SiteCrawl siteCrawl){
		System.out.println("suc : " + siteCrawl.getInventoryCrawlSuccess());
		return siteCrawl != null && siteCrawl.getInventoryCrawlSuccess() && siteCrawl.getNewInventoryRoot() != null && siteCrawl.getUsedInventoryRoot() != null;
	}
	
	public static boolean isStale(SiteCrawl siteCrawl){
		if(siteCrawl == null){
			return true;
		}
		return SitesDAO.STALE_DATE.after(siteCrawl.getCrawlDate());
	}
	
	public static SiteCrawl getMostRecentCrawl(Site site){
		String queryString = "select sc FROM Site s "
				+ "join s.crawls sc "
				+ "where s.siteId = :siteId "
				+ "order by sc.crawlDate desc";
		try{
			return JPA.em().createQuery(queryString, SiteCrawl.class)
				.setMaxResults(1)
				.setParameter("siteId", site.getSiteId())
				.getSingleResult();
		}catch(NoResultException e){
			return null;
		}
	}
}
