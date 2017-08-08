package crawling.discovery.planning;

import java.util.HashSet;
import java.util.Set;

import crawling.discovery.entities.DiscoveredSource;
import crawling.discovery.entities.Resource;
import crawling.discovery.execution.DiscoveryContext;
import crawling.discovery.execution.PlanId;

public interface SimpleDiscoveryTool extends DiscoveryTool {

	public default Set<DiscoveredSource> discover(Resource parent, DiscoveryContext context) throws Exception {
		PlanId primaryDestinationPlanId = context.getPrimaryDestinationResourcePlanId();
		if(primaryDestinationPlanId == null){
			throw new IllegalStateException("Can't use SimpleDiscoveryTool in a DiscoveryContext without primaryDestinationPlanId set.");
		}
		
		Set<DiscoveredSource> sources = new HashSet<DiscoveredSource>();
		for(Object source : discoverSources(parent, context)){
			sources.add(new DiscoveredSource(source, primaryDestinationPlanId));
		}
		return sources;
	}
	
	public Set<Object> discoverSources(Resource parent, DiscoveryContext context) throws Exception;
}
