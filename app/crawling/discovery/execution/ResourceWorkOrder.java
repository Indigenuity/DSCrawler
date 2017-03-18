package crawling.discovery.execution;

import java.util.HashSet;
import java.util.Set;

import crawling.discovery.entities.Resource;
import crawling.discovery.planning.DiscoveryPlan;
import newwork.WorkOrder;

public class ResourceWorkOrder extends WorkOrder{

	protected final Object source;
	protected final Resource parent;
	protected final ResourceContext resourceContext;
	
	public ResourceWorkOrder(Object source, Resource parent, ResourceContext resourceContext) {
		super();
		this.source = source;
		this.parent = parent;
		this.resourceContext = resourceContext;
	}
	public Object getSource() {
		return source;
	}
	public Resource getParent() {
		return parent;
	}
	public ResourceContext getResourceContext() {
		return resourceContext;
	}
	
}
