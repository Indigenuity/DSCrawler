package agarbagefolder.crawlingnew;

import async.work.TypedWorkResult;
import async.work.WorkType;

public class SiteCrawlWorkResult extends TypedWorkResult {

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
