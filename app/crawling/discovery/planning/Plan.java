package crawling.discovery.planning;

import java.util.HashMap;
import java.util.Map;

import com.google.common.util.concurrent.RateLimiter;

import crawling.discovery.execution.Context;
import crawling.discovery.execution.CrawlContext;
import crawling.discovery.execution.PlanReference;
import crawling.discovery.execution.ResourceContext;

public abstract class Plan {

	protected final Map<String, Object> initialContextObjects = new HashMap<String, Object>();
	protected final PlanReference planReference = new PlanReference();
	
	protected RateLimiter rateLimiter = null;
	protected String name = "Resource Plan";
	public RateLimiter getRateLimiter() {
		return rateLimiter;
	}
	public void setRateLimiter(RateLimiter rateLimiter) {
		this.rateLimiter = rateLimiter;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Map<String, Object> getInitialContextObjects() {
		return initialContextObjects;
	}
	public PlanReference getPlanReference() {
		return planReference;
	}
}
