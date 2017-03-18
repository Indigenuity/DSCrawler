package crawling.discovery.execution;

import crawling.discovery.entities.Resource;
import crawling.discovery.planning.ResourcePlan;

public class DiscoveredSource {
	
	protected final Resource parent;
	protected final Object source;
	protected final PlanReference destinationPlan;
	
	public DiscoveredSource(Resource parent, Object source, PlanReference destinationPlan) {
		this.parent = parent;
		this.source = source;
		this.destinationPlan = destinationPlan;
	}
	public Resource getParent() {
		return parent;
	}
	public Object getSource() {
		return source;
	}
	public PlanReference getDestinationPlan() {
		return destinationPlan;
	}
}