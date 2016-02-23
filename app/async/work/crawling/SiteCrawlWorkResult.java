package async.work.crawling;

import async.work.WorkResult;
import async.work.WorkType;

public class SiteCrawlWorkResult extends WorkResult {

	private Long siteId;
	private Long siteCrawlId;
	
	public SiteCrawlWorkResult() {
		super(WorkType.SITE_CRAWL);
		this.siteId = null;
	}
	
	public SiteCrawlWorkResult(Long siteId) {
		super(WorkType.SITE_CRAWL);
		this.siteId = siteId;
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public Long getSiteCrawlId() {
		return siteCrawlId;
	}

	public void setSiteCrawlId(Long siteCrawlId) {
		this.siteCrawlId = siteCrawlId;
	}
	
	
}
