package async.work.urlresolve;

import async.work.WorkResult;
import async.work.WorkStatus;
import async.work.WorkType;

public class UrlResolveWorkResult extends WorkResult{
	
	
	protected String seed;
	protected Long urlCheckId;
	
	public UrlResolveWorkResult() {
		super(WorkType.REDIRECT_RESOLVE);
		this.seed = null;
		this.urlCheckId = null;
	}
	
	public UrlResolveWorkResult(String seed, Long urlCheckId) {
		super(WorkType.REDIRECT_RESOLVE);
		this.seed = seed;
		this.urlCheckId = urlCheckId;
	}
	public UrlResolveWorkResult(String seed, Long urlCheckId, WorkStatus workStatus) {
		super(WorkType.REDIRECT_RESOLVE, workStatus);
		this.seed = seed;
		this.urlCheckId = urlCheckId;
		this.workStatus = workStatus;
	}
	public String getSeed() {
		return seed;
	}
	public void setSeed(String seed) {
		this.seed = seed;
	}
	public Long getUrlCheckId() {
		return urlCheckId;
	}
	public void setUrlCheckId(Long urlCheckId) {
		this.urlCheckId = urlCheckId;
	}
	
	

}
