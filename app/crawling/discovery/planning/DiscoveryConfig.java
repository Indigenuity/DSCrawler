package crawling.discovery.planning;

import java.util.LinkedHashSet;
import java.util.Set;

import crawling.discovery.execution.PlanId;

public class DiscoveryConfig extends Config {

	protected final Set<PlanId> destinations = new LinkedHashSet<PlanId>();

	public Set<PlanId> getDestinations() {
		return destinations;
	}
	
	public boolean registerDestination(PlanId resourcePlanId){
		return destinations.add(resourcePlanId);
	}
	
	
}
