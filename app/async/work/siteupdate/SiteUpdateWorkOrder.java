package async.work.siteupdate;

import async.work.WorkOrder;
import async.work.WorkType;

public class SiteUpdateWorkOrder extends WorkOrder{

	protected Long siteId;
	protected Long urlCheckId;
	
	public SiteUpdateWorkOrder(Long siteId) {
		super(WorkType.SITE_UPDATE);
		this.siteId = siteId;
	}
	
	public SiteUpdateWorkOrder(Long siteId, Long urlCheckId) {
		super(WorkType.SITE_UPDATE);
		this.siteId = siteId;
		this.urlCheckId = urlCheckId;
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public Long getUrlCheckId() {
		return urlCheckId;
	}

	public void setUrlCheckId(Long urlCheckId) {
		this.urlCheckId = urlCheckId;
	}
	
}
