package reporting;

import java.util.HashMap;

import dao.GeneralDAO;
import dao.PlacesDealerDao;
import dao.SalesforceDao;
import persistence.Site;
import persistence.Site.RedirectType;
import persistence.Site.SiteStatus;
import salesforce.persistence.SalesforceAccount;
import salesforce.persistence.SalesforceAccountType;

public class StatsBuilder {

	@SuppressWarnings("serial")
	public static DashboardStats sitesDashboard() {
		DashboardStats stats = new DashboardStats("Sites Dashboard Stats");
		stats.put("Total Sites", GeneralDAO.countAll(Site.class));
		stats.put("Bad URL Structure",GeneralDAO.getCount(Site.class, new HashMap<String, Object>() {{
			put("badUrlStructure", true);
			put("redirects", false);}}));
		
		stats.put("Defunct Domain",GeneralDAO.getCount(Site.class, "defunctDomain", true));
		stats.put("Defunct Path",GeneralDAO.getCount(Site.class, "defunctPath", true));
		stats.put("Uncrawlable Domain",GeneralDAO.getCount(Site.class, "uncrawlableDomain", true));
		stats.put("Uncrawlable Path",GeneralDAO.getCount(Site.class, "uncrawlablePath", true));
		stats.put("Not Standard Homepage",GeneralDAO.getCount(Site.class, new HashMap<String, Object>() {{
			put("notStandardHomepagePath", true);
			put("approvedHomepagePath", false);
			put("redirects", false);}}));
		stats.put("Not Standard Query",GeneralDAO.getCount(Site.class, new HashMap<String, Object>() {{
			put("notStandardQuery", true);
			put("redirects", false);}}));
		stats.put("Approved Homepage Path",GeneralDAO.getCount(Site.class, "approvedHomepagePath", true));
		stats.put("Http Error",GeneralDAO.getCount(Site.class, "httpError", true));
		stats.put("Defunct Content",GeneralDAO.getCount(Site.class, "defunctContent", true));
		stats.put("Redirects",GeneralDAO.getCount(Site.class, "redirects", true));
		
		for(SiteStatus status : SiteStatus.values()){
			Long count = GeneralDAO.getCount(Site.class, "siteStatus", status);
			if(count > 0){
				stats.put("Site Status (" + status + ")", count);
			}
		}
		
		
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
