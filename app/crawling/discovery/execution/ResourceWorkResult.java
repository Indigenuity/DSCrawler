package crawling.discovery.execution;

import java.util.HashSet;
import java.util.Set;

import crawling.discovery.entities.Resource;
import crawling.discovery.entities.ResourceId;
import newwork.WorkResult;

public class ResourceWorkResult extends WorkResult{

	public final ResourceId resourceId;
	
	public ResourceWorkResult(ResourceWorkOrder workOrder) {
		super(workOrder);
		this.resourceId = workOrder.getResourceId();
	}

	public ResourceId getResourceId() {
		return resourceId;
	}
	
}
