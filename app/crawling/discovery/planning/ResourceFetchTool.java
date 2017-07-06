package crawling.discovery.planning;

import java.util.Set;

import crawling.discovery.entities.Resource;
import crawling.discovery.entities.ResourceId;
import crawling.discovery.execution.PlanId;
import crawling.discovery.execution.ResourceContext;

public interface ResourceFetchTool {

	public Object fetchValue(Resource resource, ResourceContext context) throws Exception;
	
	public Resource generateResource(Object source, Resource parent, ResourceId resourceId, ResourceContext context) throws Exception;
	
	public Resource generateResource(PreResource preResource, Resource parent, ResourceId resourceId , ResourceContext context) throws Exception;
	
	public default void preFetch(Resource resource, ResourceContext context) throws Exception{
	}
	public default void postFetch(Resource resource, ResourceContext context) throws Exception{
	}
	public default void onFetchError(Resource resource, ResourceContext context, Exception e){
	}
	public default void onDiscoveryError(Resource parent, ResourceContext context, Exception e){
	}
	public default void afterDiscovery(Resource parent, Set<Resource> children, ResourceContext context){
	}
	
	
	public default void postDiscovery(Resource child, ResourceContext context) throws Exception{
	}	
	
	
}
