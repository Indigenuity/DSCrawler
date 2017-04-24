package crawling.discovery.planning;

import java.util.HashSet;
import java.util.Set;

import crawling.discovery.entities.Resource;
import crawling.discovery.entities.SourceQualification;
import crawling.discovery.entities.SourceQualification.QualificationStatus;
import crawling.discovery.execution.DiscoveryContext;
import crawling.discovery.execution.ResourceContext;
import crawling.discovery.execution.ResourceWorkOrder;
import crawling.discovery.execution.ResourceWorkResult;
import persistence.PageCrawl;

public abstract class ResourceFetchTool {

	protected abstract Set<Object> fetchResources(ResourceWorkOrder workOrder, ResourceContext context) throws Exception;
	
	public Set<Resource> generateResources(ResourceWorkOrder workOrder, ResourceContext context) throws Exception {
		Set<Object> values = fetchResources(workOrder, context);
		Set<Resource> resources = new HashSet<Resource>();
		for(Object value : values) {
			Resource resource = context.generateResource(workOrder.getSource(), value, workOrder.getParent());
			resources.add(resource);
		}
		return resources;
	}
	
	public void preFetch(ResourceWorkOrder workOrder, ResourceContext context) throws Exception{
	}
	public void postFetch(ResourceWorkResult workResult, ResourceContext context) throws Exception{
	}
	public void postDiscovery(ResourceWorkResult workResult, ResourceContext context) throws Exception{
	}	
	
}
