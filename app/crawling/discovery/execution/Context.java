package crawling.discovery.execution;

import java.util.HashMap;
import java.util.Map;

import com.google.common.util.concurrent.RateLimiter;

public abstract class Context {
	protected final Map<String, Object> contextObjects = new HashMap<String, Object>();
	protected RateLimiter rateLimiter = null;
	
	protected final Object permitMutex = new Object();
	
	public Context() {}
	public Context(Map<String, Object> contextObjects) {
		this.contextObjects.putAll(contextObjects);
	}
	
	public RateLimiter getRateLimiter() {
		return rateLimiter;
	}
	public void setRateLimiter(RateLimiter rateLimiter) {
		this.rateLimiter = rateLimiter;
	}
	public Object getContextObject(String key) {
		return contextObjects.get(key);
	}
	public Object hasContextObject(String key) {
		return contextObjects.containsKey(key);
	}
	public boolean acquireCrawlPermit(){
		if(!this.generateCrawlPermit()){
			return false;
		}
		if(rateLimiter != null){
			rateLimiter.acquire();
		}
		return true;
	}
	protected boolean generateCrawlPermit(){
		return true;
	}
}
