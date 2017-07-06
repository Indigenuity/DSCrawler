package crawling.discovery.execution;

import crawling.discovery.entities.ResourceId;
import newwork.WorkOrder;

public class ResourceWorkOrder extends WorkOrder{
	
	protected final ResourceId resourceId;
	
	public ResourceWorkOrder(ResourceId resourceId) {
		super();
		this.resourceId = resourceId;
	}

	public ResourceId getResourceId() {
		return resourceId;
	}
}
