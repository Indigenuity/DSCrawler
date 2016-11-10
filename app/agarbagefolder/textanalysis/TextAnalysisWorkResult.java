package agarbagefolder.textanalysis;

import async.work.TypedWorkResult;

public class TextAnalysisWorkResult extends TypedWorkResult {
	
	private Long siteCrawlId;

	public Long getSiteCrawlId() {
		return siteCrawlId;
	}

	public void setSiteCrawlId(Long siteCrawlId) {
		this.siteCrawlId = siteCrawlId;
	}
	
}
