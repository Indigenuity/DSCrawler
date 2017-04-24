package crawling.discovery.planning;

import java.util.Set;

import crawling.discovery.entities.Resource;
import crawling.discovery.execution.DiscoveredSource;
import crawling.discovery.execution.DiscoveryContext;
import crawling.discovery.execution.ResourceWorkResult;

public abstract class DiscoveryTool {
	public abstract Set<DiscoveredSource> discover(ResourceWorkResult workResult, DiscoveryContext context) throws Exception;

}
