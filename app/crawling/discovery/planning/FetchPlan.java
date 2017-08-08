package crawling.discovery.planning;

import crawling.discovery.execution.PlanId;

public class FetchPlan extends ContextPlan {

	private PlanId resourcePlanId;
	private FetchTool fetchTool;
	
	public FetchPlan(PlanId resourcePlanId, FetchTool fetchTool) {
		super();
		this.resourcePlanId = resourcePlanId;
		this.fetchTool = fetchTool;
	}
	
	public FetchPlan(PlanId resourcePlanId) {
		super();
		this.resourcePlanId = resourcePlanId;
	}

	public FetchTool getFetchTool() {
		return fetchTool;
	}

	public void setFetchTool(FetchTool fetchTool) {
		this.fetchTool = fetchTool;
	}

	public PlanId getResourcePlanId() {
		return resourcePlanId;
	}

	public void setResourcePlanId(PlanId resourcePlanId) {
		this.resourcePlanId = resourcePlanId;
	}
}
