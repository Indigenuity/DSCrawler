package crawling.discovery.planning;

import java.util.HashMap;
import java.util.Map;

import com.google.common.util.concurrent.RateLimiter;

import crawling.discovery.execution.Context;
import crawling.discovery.execution.CrawlContext;
import crawling.discovery.execution.PlanId;
import crawling.discovery.execution.ResourceContext;

public abstract class Plan {

	protected final Map<String, Object> initialContextObjects = new HashMap<String, Object>();
	protected final PlanId planId = new PlanId();
	
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
	public PlanId getPlanId() {
		return planId;
	}
	
	public Object putContextObject(String key, Object value){
		synchronized(initialContextObjects){
			return initialContextObjects.put(key, value);
		}
	}
}
