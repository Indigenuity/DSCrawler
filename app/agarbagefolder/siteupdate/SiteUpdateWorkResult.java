package agarbagefolder.siteupdate;

import async.work.WorkResult;
import async.work.WorkType;

public class SiteUpdateWorkResult extends WorkResult{
	
	protected Long siteId;
	protected Long urlCheckId;
	
	public SiteUpdateWorkResult() {
		super();
		this.siteId = null;
		this.urlCheckId = null;
	}
	
	public SiteUpdateWorkResult(Long siteId, Long urlCheckId) {
		super();
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
