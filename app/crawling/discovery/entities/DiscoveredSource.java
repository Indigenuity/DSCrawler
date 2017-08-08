package crawling.discovery.entities;

import crawling.discovery.execution.PlanId;

public class DiscoveredSource {
	private final Object source;
	private final PlanId resourcePlanId;
	
	public DiscoveredSource(Object source, PlanId resourcePlanId) {
		super();
		if(source == null){
			throw new IllegalArgumentException("Cannot instantiate DiscoveredSource with null source");
		}
		if(resourcePlanId == null){
			throw new IllegalArgumentException("Cannot instantiate DiscoveredSource with null resourcePlanId");
		}
		this.source = source;
		this.resourcePlanId = resourcePlanId;
	}
	public Object getSource() {
		return source;
	}
	public PlanId getResourcePlanId() {
		return resourcePlanId;
	}
	
	

}
