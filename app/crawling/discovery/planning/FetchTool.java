package crawling.discovery.planning;

import java.util.Set;

import crawling.discovery.entities.Resource;
import crawling.discovery.entities.ResourceId;
import crawling.discovery.execution.PlanId;
import crawling.discovery.execution.ResourceContext;

public interface FetchTool {

	
	
	
	
	public default void beforeFetch(Resource resource, ResourceContext context) throws Exception{
	}
	public default void afterFetch(Resource resource, ResourceContext context) throws Exception{
	}
	public default void onFetchError(Resource resource, ResourceContext context, Exception e){
	}
	
	public default void beforeDiscovery(Resource parent, ResourceContext context){
	}
	public default void afterDiscovery(Resource parent, Set<Resource> children, ResourceContext context){
	}
	public default void onDiscoveryError(Resource parent, ResourceContext context, Exception e){
	}
	
	public default void onDiscovery(Resource child, ResourceContext context) throws Exception{
	}	
}
