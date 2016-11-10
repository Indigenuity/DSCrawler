package agarbagefolder.docanalysis;

import async.work.TypedWorkResult;

public class DocAnalysisWorkResult extends TypedWorkResult {
	
	private Long siteCrawlId;

	public Long getSiteCrawlId() {
		return siteCrawlId;
	}

	public void setSiteCrawlId(Long siteCrawlId) {
		this.siteCrawlId = siteCrawlId;
	}
	
}
