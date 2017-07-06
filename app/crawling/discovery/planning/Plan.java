package crawling.discovery.planning;

import crawling.discovery.execution.PlanId;

public abstract class Plan {
	
	protected final PlanId planId = new PlanId();
	protected String name = "Unnamed Plan";
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public PlanId getPlanId() {
		return planId;
	}
}
