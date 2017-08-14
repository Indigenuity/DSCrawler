package sites.utilities;

import java.util.List;
import java.util.Set;

import javax.persistence.TemporalType;

import global.Global;
import persistence.Site.SiteStatus;
import play.db.jpa.JPA;
import sites.crawling.SiteCrawlLogic;
import sites.persistence.SiteSet;

public class SiteSetDao {
	
	public static List<Long> getSiteIds(Long siteSetId){
		String queryString = "select s.siteId from SiteSet ss join ss.sites s where ss.siteSetId = :siteSetId";
		List<Long> siteIds = JPA.em().createQuery(queryString, Long.class)
				.setParameter("siteSetId", siteSetId)
				.getResultList();
		return siteIds;
	}
	
	public static List<Long> sitesWithGoodCrawls(Long siteSetId){
		String queryString = "select s.siteId from SiteSet ss join ss.sites s join s.lastCrawl sc where sc.crawlDate > :staleDate and ss.siteSetId = :siteSetId"
				+ " and size(sc.pageCrawls) > :smallCrawlThreshold and sc.hasErrors = false";
//				+ " and size(sc.pageCrawls) > :smallCrawlThreshold and sc.hasErrors = false and sc.inventoryCrawlSuccess = true";
		List<Long> siteIds = JPA.em().createQuery(queryString, Long.class)
				.setParameter("siteSetId", siteSetId)
				.setParameter("staleDate",  Global.getStaleDate(), TemporalType.DATE)
				.setParameter("smallCrawlThreshold", SiteCrawlLogic.SMALL_CRAWL_THRESHOLD)
				.getResultList();
		return siteIds;
	}
	
	public static List<Long> sitesWithCrawls(Long siteSetId){
		String queryString = "select s.siteId from SiteSet ss join ss.sites s where ss.siteSetId = :siteSetId and size(s.crawls) > 0";
		List<Long> siteIds = JPA.em().createQuery(queryString, Long.class)
				.setParameter("siteSetId", siteSetId)
				.getResultList();
		return siteIds;
	}
	
	public static List<Long> sitesWithStaleCrawls(Long siteSetId){
		String queryString = "select s.siteId from SiteSet ss join ss.sites s left join s.lastCrawl sc where s.lastCrawl is null or sc.crawlDate < :staleDate and ss.siteSetId = :siteSetId";
		List<Long> siteIds = JPA.em().createQuery(queryString, Long.class)
				.setParameter("staleDate",  Global.getStaleDate(), TemporalType.DATE)
				.setParameter("siteSetId", siteSetId)
				.getResultList();
		return siteIds;
	}
	
	public static List<Long> sitesWithFreshCrawls(Long siteSetId){
		String queryString = "select s.siteId from SiteSet ss join ss.sites s join s.lastCrawl sc where sc.crawlDate > :staleDate and ss.siteSetId = :siteSetId";
		List<Long> siteIds = JPA.em().createQuery(queryString, Long.class)
				.setParameter("staleDate",  Global.getStaleDate(), TemporalType.DATE)
				.setParameter("siteSetId", siteSetId)
				.getResultList();
		return siteIds;
	}
	
	public static List<Long> freshSiteCrawls(Long siteSetId){
		String queryString = "select sc.siteCrawlId from SiteSet ss join ss.sites s join s.lastCrawl sc where sc.crawlDate > :staleDate and ss.siteSetId = :siteSetId";
		List<Long> siteIds = JPA.em().createQuery(queryString, Long.class)
				.setParameter("staleDate",  Global.getStaleDate(), TemporalType.DATE)
				.setParameter("siteSetId", siteSetId)
				.getResultList();
		return siteIds;
	}
	
	
	public static List<Long> sitesWithSmallCrawls(Long siteSetId){
		String queryString = "select s.siteId from SiteSet ss join ss.sites s join s.lastCrawl sc where ss.siteSetId = :siteSetId and sc.crawlDate > :staleDate "
				+ " and size(sc.pageCrawls) < :smallCrawlThreshold";
		List<Long> siteIds = JPA.em().createQuery(queryString, Long.class)
				.setParameter("siteSetId", siteSetId)
				.setParameter("staleDate",  Global.getStaleDate(), TemporalType.DATE)
				.setParameter("smallCrawlThreshold", SiteCrawlLogic.SMALL_CRAWL_THRESHOLD)
				.getResultList();
		return siteIds;
	}
	
	public static List<Long> sitesWithGoodSizeCrawls(Long siteSetId){
		String queryString = "select s.siteId from SiteSet ss join ss.sites s join s.lastCrawl sc where ss.siteSetId = :siteSetId and sc.crawlDate > :staleDate "
				+ " and size(sc.pageCrawls) >= :smallCrawlThreshold";
		List<Long> siteIds = JPA.em().createQuery(queryString, Long.class)
				.setParameter("siteSetId", siteSetId)
				.setParameter("staleDate",  Global.getStaleDate(), TemporalType.DATE)
				.setParameter("smallCrawlThreshold", SiteCrawlLogic.SMALL_CRAWL_THRESHOLD)
				.getResultList();
		return siteIds;
	}
	
	public static List<Long> sitesWithErrorCrawls(Long siteSetId){
		String queryString = "select distinct s.siteId from SiteSet ss join ss.sites s join s.lastCrawl sc where ss.siteSetId = :siteSetId and sc.crawlDate > :staleDate "
				+ " and sc.hasErrors = true";
		List<Long> siteIds = JPA.em().createQuery(queryString, Long.class)
				.setParameter("siteSetId", siteSetId)
				.setParameter("staleDate",  Global.getStaleDate(), TemporalType.DATE)
				.getResultList();
		return siteIds;
	}
	
	public static List<Long> sitesWithNoErrorCrawls(Long siteSetId){
		String queryString = "select distinct s.siteId from SiteSet ss join ss.sites s join s.lastCrawl sc where ss.siteSetId = :siteSetId and sc.crawlDate > :staleDate "
				+ " and sc.hasErrors = false";
		List<Long> siteIds = JPA.em().createQuery(queryString, Long.class)
				.setParameter("siteSetId", siteSetId)
				.setParameter("staleDate",  Global.getStaleDate(), TemporalType.DATE)
				.getResultList();
		return siteIds;
	}
	
	public static List<Long> sitesWithBadInventoryCrawls(Long siteSetId){
		String queryString = "select distinct s.siteId from SiteSet ss join ss.sites s join s.lastCrawl sc where ss.siteSetId = :siteSetId and sc.crawlDate > :staleDate "
				+ " and (sc.inventoryCrawlSuccess = false or size(sc.pageCrawls) < :smallCrawlThreshold)";
		List<Long> siteIds = JPA.em().createQuery(queryString, Long.class)
				.setParameter("siteSetId", siteSetId)
				.setParameter("staleDate",  Global.getStaleDate(), TemporalType.DATE)
				.setParameter("smallCrawlThreshold", SiteCrawlLogic.SMALL_CRAWL_THRESHOLD)
				.getResultList();
		return siteIds;
	}
	
	public static List<Long> sitesWithGoodInventoryCrawls(Long siteSetId){
		String queryString = "select distinct s.siteId from SiteSet ss join ss.sites s join s.lastCrawl sc where ss.siteSetId = :siteSetId and sc.crawlDate > :staleDate "
				+ " and sc.inventoryCrawlSuccess = true and size(sc.pageCrawls) >= :smallCrawlThreshold";
		List<Long> siteIds = JPA.em().createQuery(queryString, Long.class)
				.setParameter("siteSetId", siteSetId)
				.setParameter("staleDate",  Global.getStaleDate(), TemporalType.DATE)
				.setParameter("smallCrawlThreshold", SiteCrawlLogic.SMALL_CRAWL_THRESHOLD)
				.getResultList();
		return siteIds;
	}
	
}
