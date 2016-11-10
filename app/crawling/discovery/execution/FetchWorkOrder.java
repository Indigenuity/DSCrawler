package crawling.discovery.execution;

import crawling.discovery.planning.PrimaryResourcePlan;
import newwork.WorkOrder;

public class FetchWorkOrder<T> extends WorkOrder {
	
	protected final PrimaryResourcePlan<T> plan;
	protected final ResourceRequest<T> request;
	

	public FetchWorkOrder(PrimaryResourcePlan<T> plan, ResourceRequest<T> request) {
		super();
		this.plan = plan;
		this.request = request;
	}
	
	
	public PrimaryResourcePlan<T> getPlan() {
		return plan;
	}
	public ResourceRequest<T> getRequest() {
		return request;
	}
	
}
