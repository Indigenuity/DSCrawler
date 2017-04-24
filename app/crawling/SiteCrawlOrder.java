package crawling;

import newwork.WorkOrder;

public class SiteCrawlOrder extends WorkOrder{

	private final long siteId;
	public SiteCrawlOrder(long siteId){
		this.siteId = siteId;
	}
	public long getSiteId() {
		return siteId;
	}
}
