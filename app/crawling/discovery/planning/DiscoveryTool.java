package crawling.discovery.planning;

import java.util.Set;

import crawling.discovery.entities.Resource;
import crawling.discovery.execution.DiscoveredSource;
import crawling.discovery.execution.DiscoveryContext;

public abstract class DiscoveryTool {
	public abstract Set<DiscoveredSource> discover(Resource resource, DiscoveryContext context) throws Exception;

}
