package agarbagefolder.urlresolve;

import async.work.TypedWorkOrder;
import async.work.WorkType;

public class UrlResolveWorkOrder extends TypedWorkOrder{
	
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
