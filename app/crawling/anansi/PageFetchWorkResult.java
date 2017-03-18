package crawling.anansi;

import newwork.TypedWorkResult;

public class PageFetchWorkResult extends TypedWorkResult<UriFetch>{

	public PageFetchWorkResult(PageFetchWorkOrder workOrder, UriFetch result) {
		super(workOrder, result);
	}
}
