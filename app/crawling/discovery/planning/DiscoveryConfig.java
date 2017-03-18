package crawling.discovery.planning;

import java.util.LinkedHashSet;
import java.util.Set;

import crawling.discovery.execution.PlanReference;

public class DiscoveryConfig extends Config {

	protected final Set<PlanReference> destinations = new LinkedHashSet<PlanReference>();

	public Set<PlanReference> getDestinations() {
		return destinations;
	}
	
	public boolean registerDestination(PlanReference resourcePlanReference){
		return destinations.add(resourcePlanReference);
	}
	
	
}
