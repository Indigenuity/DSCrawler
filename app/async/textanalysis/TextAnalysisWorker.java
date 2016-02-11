package async.textanalysis;

import persistence.SiteCrawl;
import play.Logger;
import play.db.jpa.JPA;
import akka.actor.UntypedActor;
import analysis.SiteCrawlAnalyzer;
import async.docanalysis.DocAnalysisWorkOrder;
import async.docanalysis.DocAnalysisWorkResult;
import async.work.SingleStepWorker;
import async.work.WorkItem;
import async.work.WorkOrder;
import async.work.WorkResult;
import async.work.WorkStatus;

public class TextAnalysisWorker extends SingleStepWorker { 
	@Override
	public WorkResult processWorkOrder(WorkOrder workOrder) {
		return doWorkOrder(workOrder);
	}
	
	public static TextAnalysisWorkResult doWorkOrder(WorkOrder workOrder) {
		System.out.println("TextAnalysisWorker processing WorkOrder : " + workOrder);
		
		TextAnalysisWorkResult result = new TextAnalysisWorkResult();
		TextAnalysisWorkOrder work = (TextAnalysisWorkOrder) workOrder;
		try{
			Long siteCrawlId = work.getSiteCrawlId();
			result.setSiteCrawlId(siteCrawlId);
			result.setUuid(workOrder.getUuid());
			JPA.withTransaction( () -> {
				SiteCrawl siteCrawl = JPA.em().find(SiteCrawl.class, siteCrawlId);
				siteCrawl.initAll();
				SiteCrawlAnalyzer.textAnalysis(siteCrawl);
				siteCrawl.setTextAnalysisDone(true);
			});
			result.setWorkStatus(WorkStatus.WORK_COMPLETED);
		}
		catch(Exception e) {
			Logger.error("Error in Text Analysis: " + e);
			e.printStackTrace();
			result.setWorkStatus(WorkStatus.COULD_NOT_COMPLETE);
			result.setNote(e.getMessage());
		}
		return result;
	}
}