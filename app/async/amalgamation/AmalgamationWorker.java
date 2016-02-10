package async.amalgamation;

import java.io.File;

import datatransfer.Amalgamater;
import global.Global;
import persistence.SiteCrawl;
import play.Logger;
import play.db.jpa.JPA;
import async.work.SingleStepWorker;
import async.work.WorkOrder;
import async.work.WorkResult;
import async.work.WorkStatus;

public class AmalgamationWorker extends SingleStepWorker { 
	@Override
	public WorkResult processWorkOrder(WorkOrder workOrder) {
		return doWorkOrder(workOrder);
	}
	
	public static AmalgamationWorkResult doWorkOrder(WorkOrder workOrder) {
		System.out.println("AmalgamationWorker processing WorkOrder : " + workOrder);
		
		AmalgamationWorkResult result = new AmalgamationWorkResult();
		AmalgamationWorkOrder work = (AmalgamationWorkOrder) workOrder;
		try{
			Long siteCrawlId = work.getSiteCrawlId();
			result.setSiteCrawlId(siteCrawlId);
			result.setUuid(workOrder.getUuid());
			
			SiteCrawl siteCrawl = JPA.em().find(SiteCrawl.class, siteCrawlId);
			File storageFolder = new File(Global.getCrawlStorageFolder() + "/" + siteCrawl.getStorageFolder());
			File destination = new File(Global.getCombinedStorageFolder() + "/" + siteCrawl.getStorageFolder());
			Amalgamater.amalgamateFiles(storageFolder, destination);
			siteCrawl.setAmalgamationDone(true);
			result.setWorkStatus(WorkStatus.WORK_COMPLETED);
		}
		catch(Exception e) {
			Logger.error("Error in Amalgamation: " + e);
			e.printStackTrace();
			result.setWorkStatus(WorkStatus.COULD_NOT_COMPLETE);
			result.setNote(e.getMessage());
		}
		return result;
	}

}