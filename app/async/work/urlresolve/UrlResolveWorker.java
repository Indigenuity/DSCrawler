package async.work.urlresolve;

import java.util.Calendar;

import async.work.SingleStepJPAWorker;
import async.work.SingleStepWorker;
import async.work.WorkOrder;
import async.work.WorkResult;
import async.work.WorkStatus;
import persistence.UrlCheck;
import play.Logger;
import play.db.jpa.JPA;
import utilities.UrlSniffer;

public class UrlResolveWorker  extends SingleStepWorker { 
	
	@Override
	public WorkResult processWorkOrder(WorkOrder workOrder) {
		System.out.println("UrlResolveWorker processing WorkOrder : " + workOrder);
		UrlResolveWorkResult result = new UrlResolveWorkResult();
		try{
			String seed = ((UrlResolveWorkOrder)workOrder).getSeed();
			result.setSeed(seed);
			result.setUuid(workOrder.getUuid());
			UrlCheck urlCheck = UrlSniffer.checkUrl(seed);
			urlCheck.setCheckDate(new java.sql.Date(Calendar.getInstance().getTimeInMillis()));
			JPA.withTransaction( () -> {
				JPA.em().persist(urlCheck);				
			});
//			System.out.println("id after persist : " + urlCheck.getUrlCheckId());
			result.setUrlCheckId(urlCheck.getUrlCheckId());
			result.setWorkStatus(WorkStatus.WORK_COMPLETED);
//			System.out.println("UrlResolveWorker done processing work order");
		}
		catch(Exception e) {
			Logger.error("Error in Url Resolve: " + e);
			e.printStackTrace();
			result.setWorkStatus(WorkStatus.COULD_NOT_COMPLETE);
		}
		return result;
	}	
	

}