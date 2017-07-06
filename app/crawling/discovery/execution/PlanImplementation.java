package crawling.discovery.execution;

import crawling.discovery.planning.Plan;

public class PlanImplementation {

	protected final PlanId planId;
	protected final String name;
	
	public PlanImplementation(Plan plan){
		this.planId = plan.getPlanId();
		this.name = plan.getName();
	}

	public PlanId getPlanId() {
		return planId;
	}

	public String getName() {
		return name;
	}
	
}
