package async.work.urlresolve;

import async.work.SingleStepJPAWorker;
import async.work.WorkOrder;
import async.work.WorkResult;
import async.work.WorkStatus;
import persistence.UrlCheck;
import play.Logger;
import play.db.jpa.JPA;
import utilities.UrlSniffer;

public class UrlResolveWorker  extends SingleStepJPAWorker {
	
	@Override
	protected WorkResult processWorkOrder(WorkOrder workOrder) {
		UrlResolveWorkResult result = new UrlResolveWorkResult();
		try{
			String seed = ((UrlResolveWorkOrder)workOrder).getSeed();
			result.setSeed(seed);
			UrlCheck urlCheck = UrlSniffer.checkUrl(seed);
			JPA.em().persist(urlCheck);
			System.out.println("id after persist : " + urlCheck.getUrlCheckId());
			result.setUrlCheckId(urlCheck.getUrlCheckId());
			result.setWorkStatus(WorkStatus.WORK_COMPLETED);
		}
		catch(Exception e) {
			Logger.error("Error in Url Resolve: " + e);
			e.printStackTrace();
			result.setWorkStatus(WorkStatus.COULD_NOT_COMPLETE);
		}
		return result;
	}	
	

}