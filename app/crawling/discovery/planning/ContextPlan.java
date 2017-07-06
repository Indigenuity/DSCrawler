package crawling.discovery.planning;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.util.concurrent.RateLimiter;

public class ContextPlan extends Plan {

	protected final Map<String, Object> initialContextObjects =new ConcurrentHashMap<String, Object>();
	protected RateLimiter rateLimiter = null;
	
	protected int maxDepth;
	protected int maxPages;
	
	public RateLimiter getRateLimiter() {
		return rateLimiter;
	}
	public void setRateLimiter(RateLimiter rateLimiter) {
		this.rateLimiter = rateLimiter;
	}
	public Map<String, Object> getInitialContextObjects() {
		return new HashMap<String, Object>(initialContextObjects);
	}
	public Object removeContextObject(String key){
		return initialContextObjects.remove(key);
	}
	public Object putContextObject(String key, Object value){
		return initialContextObjects.put(key, value);
	}
	public int getMaxDepth() {
		return maxDepth;
	}
	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}
	public int getMaxPages() {
		return maxPages;
	}
	public void setMaxPages(int maxPages) {
		this.maxPages = maxPages;
	}
	
}
