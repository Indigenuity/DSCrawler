package sites.utilities;

import dao.GeneralDAO;
import persistence.Site;
import play.db.jpa.JPA;
import reporting.DashboardStats;
import sites.persistence.SiteSet;

public class SiteSetLogic {

	
	public static DashboardStats getDashboard(Long siteSetId) {
		DashboardStats stats = new DashboardStats("Site Set Dashboard Stats");
		
		int numSites = SiteSetDao.getSiteIds(siteSetId).size();
//		int sitesWithCrawls = SiteSetDao.sitesWithCrawls(siteSetId).size();
//		int staleCrawls = SiteSetDao.sitesWithStaleCrawls(siteSetId).size();
		int freshCrawls = SiteSetDao.sitesWithFreshCrawls(siteSetId).size();
		int goodSizeCrawls = SiteSetDao.sitesWithGoodSizeCrawls(siteSetId).size();
		int noErrorCrawls = SiteSetDao.sitesWithNoErrorCrawls(siteSetId).size();
		int goodInventoryCrawls = SiteSetDao.sitesWithGoodInventoryCrawls(siteSetId).size();
		int goodCrawls = SiteSetDao.sitesWithGoodCrawls(siteSetId).size();
				
		stats.put("Total Sites", numSites);
//		stats.put("Sites With Crawls", (sitesWithCrawls*100/numSites) + "% (" + sitesWithCrawls + ")");
//		stats.put("Stale Crawls", (staleCrawls*100/sitesWithCrawls) + "% (" + staleCrawls + ")");
		stats.put("Fresh Crawls", (freshCrawls*100/numSites) + "% (" + freshCrawls + ")");
		stats.put("Good Size Crawls", (goodSizeCrawls*100/freshCrawls) + "% (" + goodSizeCrawls + ")");
		stats.put("No Error Crawls", (noErrorCrawls*100/freshCrawls) + "% (" + noErrorCrawls + ")");
		stats.put("Good Inventory Crawls", (goodInventoryCrawls*100/freshCrawls) + "% (" + goodInventoryCrawls + ")");
		stats.put("Good Crawls", (goodCrawls*100/freshCrawls) + "% (" + goodCrawls + ")");
		
//		
//		stats.put("Unapproved HTTP checks", GeneralDAO.getCount(Site.class, "fullyApprovedHttp", false));
//		
//		String queryString = "select count(s) from Site s join s.urlCheck uc where uc.statusCode = 403";
//		stats.put("Forbidden Sites", JPA.em().createQuery(queryString, Long.class).getSingleResult());
//		queryString = "select count(s) from Site s join s.urlCheck uc where uc.error = true";
//		stats.put("True Error", JPA.em().createQuery(queryString, Long.class).getSingleResult());
//		stats.put("Http Error",GeneralDAO.getCount(Site.class, "httpError", true));
//		stats.put("Defunct Domain",GeneralDAO.getCount(Site.class, "defunctDomain", true));
//		stats.put("Defunct Path",GeneralDAO.getCount(Site.class, "defunctPath", true));
//		stats.put("Needs Review", GeneralDAO.getCount(Site.class, "needsReview", true));
//		stats.put("Defunct Content",GeneralDAO.getCount(Site.class, "defunctContent", true));
//		stats.put("No Redirect",GeneralDAO.getCount(Site.class, "redirectsTo", null));
		
//		for(SiteStatus status : SiteStatus.values()){
//			Long count = GeneralDAO.getCount(Site.class, "siteStatus", status);
//			if(count > 0){
//				stats.put("Site Status (" + status + ")", count);
//			}
//		}
		
		
		return stats;
	}
}
