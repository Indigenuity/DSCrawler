package crawling.discovery.execution;

import java.util.HashMap;
import java.util.Map;

import com.google.common.util.concurrent.RateLimiter;

import crawling.discovery.planning.ContextPlan;
import crawling.discovery.planning.Plan;

public class ContextPlanImplementation extends PlanImplementation {

	protected final Map<String, Object> contextObjects = new HashMap<String, Object>();
	protected final RateLimiter rateLimiter;
	
	public ContextPlanImplementation(ContextPlan plan) {
		super(plan);
		this.contextObjects.putAll(plan.getInitialContextObjects());
		this.rateLimiter = plan.getRateLimiter();
	}

}
