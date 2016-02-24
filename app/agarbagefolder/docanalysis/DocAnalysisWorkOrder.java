package agarbagefolder.docanalysis;

import async.work.WorkOrder;
import async.work.WorkType;

public class DocAnalysisWorkOrder extends WorkOrder{

	private Long siteCrawlId;
	
	public DocAnalysisWorkOrder() {
		super(WorkType.DOC_ANALYSIS);
	}
	public DocAnalysisWorkOrder(Long siteCrawlId) {
		super(WorkType.DOC_ANALYSIS);
		this.siteCrawlId = siteCrawlId;
	}

	public Long getSiteCrawlId() {
		return siteCrawlId;
	}

	public void setSiteCrawlId(Long siteCrawlId) {
		this.siteCrawlId = siteCrawlId;
	}

	
}
