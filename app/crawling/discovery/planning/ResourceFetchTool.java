package crawling.discovery.planning;

import java.util.Set;

import crawling.discovery.entities.Resource;
import crawling.discovery.entities.SourceQualification;
import crawling.discovery.entities.SourceQualification.QualificationStatus;
import crawling.discovery.execution.ResourceContext;
import crawling.discovery.execution.ResourceWorkOrder;
import crawling.discovery.execution.ResourceWorkResult;

public abstract class ResourceFetchTool {

	public abstract Set<Resource> fetchResource(ResourceWorkOrder workOrder, ResourceContext context) throws Exception;
	
	public void preFetch(ResourceWorkOrder workOrder, ResourceContext context) throws Exception{
	}
	public void postFetch(ResourceWorkResult workResult, ResourceContext context) throws Exception{
	}
	public void postDiscovery(ResourceWorkResult workResult, ResourceContext context) throws Exception{
	}	
	
}
