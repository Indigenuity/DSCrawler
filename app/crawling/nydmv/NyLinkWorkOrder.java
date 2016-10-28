package crawling.nydmv;

import newwork.WorkOrder;

public class NyLinkWorkOrder extends WorkOrder {

	public final long nyDealerId;
	
	public NyLinkWorkOrder(long nyDealerId){
		this.nyDealerId = nyDealerId;
	}
}
