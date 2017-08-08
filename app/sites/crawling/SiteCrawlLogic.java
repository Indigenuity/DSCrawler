package sites.crawling;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.persistence.NoResultException;

import org.apache.commons.lang3.StringUtils;

import akka.actor.ActorRef;
import async.async.Asyncleton;
import async.functionalwork.ConsumerWorkOrder;
import async.functionalwork.JpaFunctionalBuilder;
import crawling.DealerCrawlController;
import crawling.discovery.execution.CrawlOrder;
import crawling.discovery.execution.CrawlSupervisor;
import crawling.discovery.local.SiteCrawlPlan;
import dao.SitesDAO;
import datadefinitions.NoCrawlDomain;
import global.Global;
import persistence.PageCrawl;
import persistence.Site;
import persistence.SiteCrawl;
import play.Logger;
import play.db.jpa.JPA;
import sites.utilities.PageCrawlLogic;
import sites.utilities.SiteLogic;
import utilities.Tim;

public class SiteCrawlLogic {
	

	public static final int SMALL_CRAWL_THRESHOLD = 5;
	
	public static PageCrawl getPageCrawlByUrl(String url, SiteCrawl siteCrawl){
		for(PageCrawl pageCrawl : siteCrawl.getPageCrawls()){
			if(StringUtils.equals(url, pageCrawl.getUrl())){
				return pageCrawl;
			}
		}
		return null;
	}

	
	//Should be called after more PageCrawls have been refreshed or added to the SiteCrawl
	public static void updateErrorStatus(SiteCrawl siteCrawl){
		siteCrawl.setHasErrors(false);
		siteCrawl.setInventoryCrawlSuccess(true);
		List<PageCrawl> blankPageCrawls = new ArrayList<PageCrawl>();
		for(PageCrawl pageCrawl : siteCrawl.getPageCrawls()){
			if(StringUtils.isEmpty(pageCrawl.getUrl())){
				siteCrawl.setHasErrors(true);
				blankPageCrawls.add(pageCrawl);
				continue;
			}
		
			if(PageCrawlLogic.isFailedCrawl(pageCrawl)){
				siteCrawl.setHasErrors(true);
				if(PageCrawlLogic.isInventoryPage(pageCrawl)){
					siteCrawl.setInventoryCrawlSuccess(false);
				}
			}
			if(PageCrawlLogic.isInventoryPage(pageCrawl) && PageCrawlLogic.isUncrawled(pageCrawl)){
				siteCrawl.setInventoryCrawlSuccess(false);
			}
		}
		for(PageCrawl pageCrawl : blankPageCrawls){
			PageCrawlLogic.removePageCrawl(pageCrawl);
		}
		
		if(siteCrawl.getUsedInventoryRoot() == null || siteCrawl.getNewInventoryRoot() == null){
			siteCrawl.setInventoryCrawlSuccess(false);
		}
	}
	
	public static SiteCrawl updateLastCrawl(Site site){
		SiteCrawl siteCrawl = getMostRecentCrawl(site);
		site.setLastCrawl(siteCrawl);
		return siteCrawl;
	}
	
	public static void queueNewCrawl(Site site){
		if(site.getUncrawlableDomain()){
			Logger.error("Not queueing site with uncrawlable domain for new sitecrawl : " + site.getHomepage());
			return;
		
		}
		SiteCrawlPlan crawlPlan = new SiteCrawlPlan(site);
		queueCrawl(crawlPlan);
	}
	
	public static void queueRecrawl(SiteCrawl siteCrawl){
		//TODO remove updating error status once all the pagecrawls with null urls are removed
		updateErrorStatus(siteCrawl);
		JPA.em().getTransaction().commit();
		JPA.em().getTransaction().begin();
		siteCrawl = JPA.em().find(SiteCrawl.class, siteCrawl.getSiteCrawlId());
		SiteCrawlPlan crawlPlan = new SiteCrawlPlan(siteCrawl);
		queueCrawl(crawlPlan);
	}
	
	public static void queueCrawl(SiteCrawlPlan crawlPlan){
		Asyncleton.getInstance().queueOneShotMessage(CrawlSupervisor.class, new CrawlOrder(crawlPlan));
	}
	
	public static void ensureCrawl(Site site, boolean ensureFresh, boolean ensureErrorRecrawl, boolean ensureInventorySuccess){
		SiteCrawl siteCrawl = site.getLastCrawl();
		
		if(siteCrawl == null){
			queueNewCrawl(site);
		} else if(ensureFresh && isStale(siteCrawl)){
			queueNewCrawl(site);
		} else if(ensureErrorRecrawl && siteCrawl.getHasErrors()){
			queueRecrawl(siteCrawl);
		} else if(ensureInventorySuccess && !siteCrawl.getInventoryCrawlSuccess()){
			queueRecrawl(siteCrawl);
		}
	}
	
	public static boolean isStale(SiteCrawl siteCrawl){
		if(siteCrawl == null){
			return true;
		}
		return Global.getStaleDate().after(siteCrawl.getCrawlDate());
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
