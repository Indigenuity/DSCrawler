package crawling.discovery.planning;

import crawling.discovery.entities.Resource;
import crawling.discovery.entities.ResourceId;
import crawling.discovery.execution.ResourceContext;

public interface DerivedResourceTool extends ResourceFetchTool {

	public Resource generateResource(Object source, Object value, Resource parent, ResourceId resourceId, ResourceContext context) throws Exception;
	
	public default Resource generateResource(Object source, Resource parent, ResourceId resourceId, ResourceContext context) throws Exception{
		throw new UnsupportedOperationException("Cannot create derived resource with no value");
	}
}
