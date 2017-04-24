package crawling.discovery.execution;

import crawling.discovery.entities.Resource;
import newwork.WorkOrder;

public class SeedWorkOrder extends WorkOrder{

	protected final Object source;
	protected final PlanId planId;
	public SeedWorkOrder(Object source, PlanId planId) {
		super();
		this.source = source;
		this.planId = planId;
	}
	public Object getSource() {
		return source;
	}
	public PlanId getPlanId() {
		return planId;
	}
}
