package crawling.discovery.async;

import crawling.discovery.planning.PrimaryResourcePlan;
import newwork.WorkResult;

public class FetchWorkResult<T> extends WorkResult {

	protected final FetchWorkOrder<T> workOrder;
	
	public FetchWorkResult(FetchWorkOrder<T> workOrder) {
		super(workOrder);
		this.workOrder = workOrder;
	}
	
	public PrimaryResourcePlan<T> getPlan(){
		return workOrder.getPlan();
	}

}
