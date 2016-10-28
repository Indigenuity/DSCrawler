package agarbagefolder.amalgamation;

import async.work.TypedWorkOrder;
import async.work.WorkType;

public class AmalgamationWorkOrder extends TypedWorkOrder {

	private Long siteCrawlId;
	
	public AmalgamationWorkOrder() {
		super(WorkType.AMALGAMATION);
	}
	public AmalgamationWorkOrder(Long siteCrawlId) {
		super(WorkType.AMALGAMATION);
		this.siteCrawlId = siteCrawlId;
	}

	public Long getSiteCrawlId() {
		return siteCrawlId;
	}

	public void setSiteCrawlId(Long siteCrawlId) {
		this.siteCrawlId = siteCrawlId;
	}
}
