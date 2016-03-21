package agarbagefolder;

import async.work.WorkOrder;
import async.work.WorkType;

public class InfoFetchWorkOrder extends WorkOrder{

	public InfoFetchWorkOrder() {
		super(WorkType.INFO_FETCH);
	}
	private Long infoFetchId;
	
	public boolean doUrlCheck = false;
	public boolean doSiteUpdate = false;
	public boolean doSiteCrawl = false;
	public boolean doAmalgamation = false;
	public boolean doTextAnalysis = false;
	public boolean doDocAnalysis = false;
	public boolean doMetaAnalysis = false;
	public boolean doPlacesPageFetch = false;
	public Long getInfoFetchId() {
		return infoFetchId;
	}
	public void setInfoFetchId(Long infoFetchId) {
		this.infoFetchId = infoFetchId;
	}

	
}
