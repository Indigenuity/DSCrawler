package crawling.discovery.execution;

import crawling.discovery.entities.Resource;
import newwork.WorkOrder;

public class SeedWorkOrder extends WorkOrder{

	protected final Object source;
	protected final PlanReference planReference;
	public SeedWorkOrder(Object source, PlanReference planReference) {
		super();
		this.source = source;
		this.planReference = planReference;
	}
	public Object getSource() {
		return source;
	}
	public PlanReference getPlanReference() {
		return planReference;
	}
}
