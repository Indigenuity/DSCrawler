package crawling.discovery.execution;

import java.util.HashSet;
import java.util.Set;

import crawling.discovery.entities.Resource;
import crawling.discovery.planning.DiscoveryPlan;
import newwork.WorkOrder;

public class ResourceWorkOrder extends WorkOrder{
	
	protected final Object source;
	protected final Resource parent;
	protected final PlanId planId;
	
	public ResourceWorkOrder(Object source, Resource parent, PlanId planId) {
		super();
		this.source = source;
		this.parent = parent;
		this.planId = planId;
	}
	public Object getSource() {
		return source;
	}
	public Resource getParent() {
		return parent;
	}
	public PlanId getPlanId() {
		return planId;
	}
	
}
