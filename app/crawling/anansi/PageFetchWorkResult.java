package crawling.anansi;

import newwork.TypedWorkResult;

public class PageFetchWorkResult extends TypedWorkResult<PageFetch>{

	public PageFetchWorkResult(PageFetchWorkOrder workOrder, PageFetch result) {
		super(workOrder, result);
	}
}
