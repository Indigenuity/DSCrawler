package crawling.discovery.planning;

import java.util.Set;

import crawling.discovery.entities.Resource;
import crawling.discovery.execution.DiscoveryContext;

public interface DiscoveryTool {
	
	public Set<Object> discover(Resource parent, DiscoveryContext context) throws Exception;

}
