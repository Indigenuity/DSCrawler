package crawling.discovery.planning;

import java.util.Set;

import crawling.discovery.entities.Resource;
import crawling.discovery.entities.ResourceId;
import crawling.discovery.execution.CrawlContext;
import crawling.discovery.execution.ResourceContext;

public interface ResourceTool {
	
	
	public default void assignValue(Resource resource, Object value, CrawlContext context){
		resource.setValue(value);
	}
	
	public Object fetchValue(Resource resource, CrawlContext context) throws Exception;
	public Resource generateResource(Object source, Resource parent, ResourceId resourceId, CrawlContext context) throws Exception;
	public Resource generateResource(PreResource preResource, Resource parent, ResourceId resourceId , CrawlContext context) throws Exception;
	
	public default void beforeFetch(Resource resource, CrawlContext context) throws Exception{
	}
	public default void afterFetch(Resource resource, CrawlContext context) throws Exception{
	}
	public default void onFetchError(Resource resource, CrawlContext context, Exception e) throws Exception{
	}
	public default void beforeDiscovery(Resource parent, CrawlContext context) throws Exception{
	}
	public default void afterDiscovery(Resource parent, CrawlContext context) throws Exception{
	}
	public default void onDiscoveryError(Resource parent, CrawlContext context, Exception e) throws Exception{
	}
	public default void onDiscovery(Resource child, CrawlContext context) throws Exception{
	}
}
