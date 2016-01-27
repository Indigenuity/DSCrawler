package async.work.urlresolve;

import async.work.WorkOrder;
import async.work.WorkType;

public class UrlResolveWorkOrder extends WorkOrder{
	
	protected String seed;
	
	public UrlResolveWorkOrder(String seed){
		super(WorkType.REDIRECT_RESOLVE);
		this.seed = seed;
	}

	public String getSeed() {
		return seed;
	}

	public void setSeed(String seed) {
		this.seed = seed;
	}
}
