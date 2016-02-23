package async.work;

import java.util.Calendar;

import async.work.siteupdate.SiteUpdateWorkOrder;
import async.work.siteupdate.SiteUpdateWorkResult;
import async.work.siteupdate.SiteUpdateWorker;
import dao.SitesDAO;
import persistence.Site;
import persistence.UrlCheck;
import play.Logger;
import play.db.jpa.JPA;
import utilities.DSFormatter;
import utilities.UrlSniffer;

public class SiteImportWorker extends SingleStepWorker {
	
	@Override
	public WorkResult processWorkOrder(WorkOrder workOrder) {
		return SiteImportWorker.doWorkOrder(workOrder);
	}	
	
	public static WorkResult doWorkOrder(WorkOrder workOrder) {
		WorkResult result = new WorkResult(workOrder);
		try{
			
//			System.out.println("doing some site updatework");
			Long urlCheckId = Long.parseLong(workOrder.getContextItem("urlCheckId"));
			Boolean franchise = Boolean.parseBoolean(workOrder.getContextItem("franchise"));
			
			JPA.withTransaction( () -> {
				UrlCheck urlCheck = JPA.em().find(UrlCheck.class, urlCheckId);
				Site existingSite = SitesDAO.getFirst("homepage", urlCheck.getResolvedSeed(), 0);
				
				if(existingSite == null) {
					if(urlCheck.isAllApproved()){
						Site site = new Site();
						site.setHomepage(urlCheck.getResolvedSeed());
						site.setDomain(urlCheck.getResolvedHost());
						site.setFranchise(franchise);
						site.setCreatedDate(new java.sql.Date(Calendar.getInstance().getTime().getTime()));
						site.setRedirectResolveDate(new java.sql.Date(Calendar.getInstance().getTime().getTime()));
						site.setLanguagePath(urlCheck.isLanguagePath());
						site.setLanguageQuery(urlCheck.isLanguageQuery());
						urlCheck.setAccepted(true);
						
						JPA.em().persist(site);
						result.setWorkStatus(WorkStatus.WORK_COMPLETED);
						result.addContextItem("siteId", site.getSiteId() + "");
					}
					else {
						result.setNote("URL not fully approved");
						result.setWorkStatus(WorkStatus.NEEDS_REVIEW);
					}
				}
				else {
					result.setNote("Site already exists with this homepage");
					result.setWorkStatus(WorkStatus.NEEDS_REVIEW);
					result.addContextItem("siteId", existingSite.getSiteId() + "");
				}
			});
			
		}
		catch(Exception e) {
			Logger.error("Error in Site Import Worker: " + e);
			e.printStackTrace();
			result.setWorkStatus(WorkStatus.NEEDS_REVIEW);
			result.setNote("Exception : " + e.getMessage());
		}
		return result;
	}
	
	
//	public static SiteUpdateWorkResult updateSite(SiteUpdateWorkOrder workOrder) {
//		SiteUpdateWorkResult workResult = new SiteUpdateWorkResult();
//		result.setSiteId(order.getSiteId());
//		result.setUuid(order.getUuid());
//	}
	

}