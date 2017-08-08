package crawling.discovery.planning;

import java.util.HashSet;
import java.util.Set;

import crawling.discovery.entities.DiscoveredSource;
import crawling.discovery.entities.Resource;
import crawling.discovery.execution.DiscoveryContext;
import crawling.discovery.execution.PlanId;

public interface DiscoveryTool {
	public Set<DiscoveredSource> discover(Resource parent, DiscoveryContext context) throws Exception;
}
