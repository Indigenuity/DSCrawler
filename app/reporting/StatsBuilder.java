package reporting;

import java.util.HashMap;

import dao.GeneralDAO;
import dao.PlacesDealerDao;
import dao.SalesforceDao;
import dao.SitesDAO;
import persistence.Site;
import persistence.Site.RedirectType;
import persistence.Site.SiteStatus;
import play.db.jpa.JPA;
import salesforce.persistence.SalesforceAccount;
import salesforce.persistence.SalesforceAccountType;

public class StatsBuilder {

	@SuppressWarnings("serial")
	public static DashboardStats sitesDashboard() {
		DashboardStats stats = new DashboardStats("Sites Dashboard Stats");
		
		stats.put("Total Sites", GeneralDAO.countAll(Site.class));
		stats.put("Unapproved HTTP checks", GeneralDAO.getCount(Site.class, "fullyApprovedHttp", false));
		
		String queryString = "select count(s) from Site s join s.urlCheck uc where uc.statusCode = 403";
		stats.put("Forbidden Sites", JPA.em().createQuery(queryString, Long.class).getSingleResult());
		queryString = "select count(s) from Site s join s.urlCheck uc where uc.error = true";
		stats.put("True Error", JPA.em().createQuery(queryString, Long.class).getSingleResult());
		stats.put("Http Error",GeneralDAO.getCount(Site.class, "httpError", true));
		stats.put("Defunct Domain",GeneralDAO.getCount(Site.class, "defunctDomain", true));
		stats.put("Defunct Path",GeneralDAO.getCount(Site.class, "defunctPath", true));
		stats.put("Needs Review", GeneralDAO.getCount(Site.class, "needsReview", true));
		stats.put("Defunct Content",GeneralDAO.getCount(Site.class, "defunctContent", true));
		stats.put("No Redirect",GeneralDAO.getCount(Site.class, "redirectsTo", null));
		
//		for(SiteStatus status : SiteStatus.values()){
//			Long count = GeneralDAO.getCount(Site.class, "siteStatus", status);
//			if(count > 0){
//				stats.put("Site Status (" + status + ")", count);
//			}
//		}
		
		
		return stats;
	}
	
	public static DashboardStats salesforceDashboard() {
		DashboardStats stats = new DashboardStats("Salesforce Dashboard Stats");
		
		for(SiteStatus status : SiteStatus.values()){
			Long count = SalesforceDao.getCount("siteStatus", status);
			if(count > 0){
				stats.put("Site Status (" + status + ")", count);
			}
		}
		
		stats.put("Group Accounts", GeneralDAO.getCount(SalesforceAccount.class,  "accountType", SalesforceAccountType.GROUP));
		stats.put("Dealers", GeneralDAO.getCount(SalesforceAccount.class,  "accountType", SalesforceAccountType.DEALER));
		
		stats.put("Siteless Accounts", GeneralDAO.getCount(SalesforceAccount.class, "site", null));
		stats.put("Significant Differences", GeneralDAO.getCount(SalesforceAccount.class, "significantDifference", true));
		return stats;
	}
	
	public static DashboardStats placesDashboard() {
		DashboardStats stats = new DashboardStats("Places Dashboard Stats");
		stats.put("US Zip Codes", PlacesDealerDao.countUsZips());
		stats.put("US Zip Codes (Old)", PlacesDealerDao.countOldUsZips());
		stats.put("Canada Postal Codes", PlacesDealerDao.countCanadaPostals());
		stats.put("Canada Postal Codes (Old", PlacesDealerDao.countOldCanadaPostals());
		stats.put("Places Dealers", PlacesDealerDao.countPlacesDealers());
		stats.put("Places Dealers (Old)", PlacesDealerDao.countOldPlacesDealers());
		return stats;
	}
}
