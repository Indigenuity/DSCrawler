package async.work.crawling;

import async.work.WorkOrder;
import async.work.WorkType;

public class SiteCrawlWorkOrder extends WorkOrder {

	private Long siteId; 
	
	public SiteCrawlWorkOrder(Long siteId) {
		super(WorkType.CRAWL);
		this.siteId = siteId;
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}
	
	

}
