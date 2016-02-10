package async.docanalysis;

import persistence.SiteCrawl;
import play.Logger;
import play.db.jpa.JPA;
import analysis.SiteCrawlAnalyzer;
import async.work.SingleStepWorker;
import async.work.WorkOrder;
import async.work.WorkResult;
import async.work.WorkStatus;

public class DocAnalysisWorker extends SingleStepWorker { 
	@Override
	public WorkResult processWorkOrder(WorkOrder workOrder) {
		return doWorkOrder(workOrder);
	}
	
	public static DocAnalysisWorkResult doWorkOrder(WorkOrder workOrder) {
		System.out.println("DocAnalysisWorker processing WorkOrder : " + workOrder);
		
		DocAnalysisWorkResult result = new DocAnalysisWorkResult();
		DocAnalysisWorkOrder work = (DocAnalysisWorkOrder) workOrder;
		try{
			Long siteCrawlId = work.getSiteCrawlId();
			result.setSiteCrawlId(siteCrawlId);
			result.setUuid(workOrder.getUuid());
			
			SiteCrawl siteCrawl = JPA.em().find(SiteCrawl.class, siteCrawlId);
			siteCrawl.initAll();
			SiteCrawlAnalyzer.docAnalysis(siteCrawl);
			siteCrawl.setDocAnalysisDone(true);
			result.setWorkStatus(WorkStatus.WORK_COMPLETED);
		}
		catch(Exception e) {
			Logger.error("Error in Doc Analysis: " + e);
			e.printStackTrace();
			result.setWorkStatus(WorkStatus.COULD_NOT_COMPLETE);
			result.setNote(e.getMessage());
		}
		return result;
	}

}