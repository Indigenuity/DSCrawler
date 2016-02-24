package agarbagefolder.textanalysis;

import async.work.WorkResult;

public class TextAnalysisWorkResult extends WorkResult {
	
	private Long siteCrawlId;

	public Long getSiteCrawlId() {
		return siteCrawlId;
	}

	public void setSiteCrawlId(Long siteCrawlId) {
		this.siteCrawlId = siteCrawlId;
	}
	
}
