package reporting;

import dao.GeneralDAO;
import dao.PlacesDealerDao;
import dao.SalesforceDao;
import persistence.Site;
import persistence.Site.SiteStatus;
import salesforce.persistence.SalesforceAccount;
import salesforce.persistence.SalesforceAccountType;

public class StatsBuilder {

	public static DashboardStats sitesDashboard() {
		DashboardStats stats = new DashboardStats("Sites Dashboard Stats");
		
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
