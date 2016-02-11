package async.work.siteupdate;

import java.util.Calendar;

import async.work.SingleStepJPAWorker;
import async.work.SingleStepWorker;
import async.work.WorkOrder;
import async.work.WorkResult;
import async.work.WorkStatus;
import persistence.Site;
import persistence.UrlCheck;
import play.Logger;
import play.db.jpa.JPA;
import utilities.DSFormatter;
import utilities.UrlSniffer;

public class SiteUpdateWorker extends SingleStepWorker {
	
	@Override
	public WorkResult processWorkOrder(WorkOrder workOrder) {
		return SiteUpdateWorker.doWorkOrder(workOrder);
	}	
	
	public static SiteUpdateWorkResult doWorkOrder(WorkOrder workOrder) {
		SiteUpdateWorkResult result = new SiteUpdateWorkResult();
		try{
//			System.out.println("doing some site updatework");
			SiteUpdateWorkOrder order = (SiteUpdateWorkOrder)workOrder;
			result.setSiteId(order.getSiteId());
			result.setUuid(order.getUuid());
			
			JPA.withTransaction( () -> {
				Site site = JPA.em().find(Site.class, order.getSiteId());
				UrlCheck urlCheck = UrlSniffer.checkUrl(site.getHomepage());
				urlCheck.setCheckDate(new java.sql.Date(Calendar.getInstance().getTimeInMillis()));
				JPA.em().persist(urlCheck);
				result.setUrlCheckId(urlCheck.getUrlCheckId());
				System.out.println("urlCheckId after persisting : " + result.getUrlCheckId());
				
				if(urlCheck.getStatusCode() == 200) {
					if(DSFormatter.equals(urlCheck.getResolvedSeed(), site.getHomepage())){
	//					System.out.println("no redirect");
						site.setRedirectResolveDate(urlCheck.getCheckDate());
						urlCheck.setAccepted(true);
						urlCheck.setNoChange(true);
						result.setWorkStatus(WorkStatus.WORK_COMPLETED);
						
					}
					else if(UrlSniffer.isGenericRedirect(urlCheck.getResolvedSeed(), site.getHomepage())){
	//					System.out.println("generic change");
						site.setHomepage(urlCheck.getResolvedSeed());
						site.setRedirectResolveDate(urlCheck.getCheckDate());
						urlCheck.setAccepted(true);
						result.setWorkStatus(WorkStatus.WORK_COMPLETED);
					}
				}
				if(result.getWorkStatus() != WorkStatus.WORK_COMPLETED){
	//				System.out.println("work needs review");
					result.setWorkStatus(WorkStatus.NEEDS_REVIEW);				
				}
			});
			
		}
		catch(Exception e) {
			Logger.error("Error in Site Update Worker: " + e);
			e.printStackTrace();
			result.setWorkStatus(WorkStatus.COULD_NOT_COMPLETE);
		}
		return result;
	}
	
	
//	public static SiteUpdateWorkResult updateSite(SiteUpdateWorkOrder workOrder) {
//		SiteUpdateWorkResult workResult = new SiteUpdateWorkResult();
//		result.setSiteId(order.getSiteId());
//		result.setUuid(order.getUuid());
//	}
	

}