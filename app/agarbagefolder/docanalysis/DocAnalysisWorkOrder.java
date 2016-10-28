package agarbagefolder.docanalysis;

import async.work.TypedWorkOrder;
import async.work.WorkType;

public class DocAnalysisWorkOrder extends TypedWorkOrder{

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
