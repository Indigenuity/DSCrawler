package async.docanalysis;

import async.work.WorkResult;

public class DocAnalysisWorkResult extends WorkResult {
	
	private Long siteCrawlId;

	public Long getSiteCrawlId() {
		return siteCrawlId;
	}

	public void setSiteCrawlId(Long siteCrawlId) {
		this.siteCrawlId = siteCrawlId;
	}
	
}
