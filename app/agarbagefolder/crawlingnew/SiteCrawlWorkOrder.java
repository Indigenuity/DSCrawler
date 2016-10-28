package agarbagefolder.crawlingnew;

import async.work.TypedWorkOrder;
import async.work.WorkType;

public class SiteCrawlWorkOrder extends TypedWorkOrder {

	private Long siteId; 
	
	public SiteCrawlWorkOrder(Long siteId) {
		super(WorkType.SITE_CRAWL);
		this.siteId = siteId;
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}
	
	

}
