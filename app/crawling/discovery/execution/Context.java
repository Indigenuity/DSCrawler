package crawling.discovery.execution;

import java.util.HashMap;
import java.util.Map;

import com.google.common.util.concurrent.RateLimiter;

import crawling.discovery.entities.Resource;

public abstract class Context {
	
	protected int maxDepth;
	protected int maxPages;
	protected int numResourcesCrawled = 0;
	
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
	
	public boolean discoverContextObject(String key, Object value){
		synchronized(contextObjects){
			if(contextObjects.containsKey(key)){
				return false;
			}
			contextObjects.put(key, value);
			return true;
		}
	}
	
	public Object putContextObject(String key, Object value){
		synchronized(contextObjects){
			return contextObjects.put(key, value);
		}
	}
	
	public boolean contextContains(String key){
		synchronized(contextObjects){
			return contextObjects.containsKey(key);
		}
	}
	
	public boolean acquireWorkPermit(){
		if(rateLimiter != null){
			rateLimiter.acquire();
		}
		return true;
	}
	
	protected boolean approveWork(ResourceWorkOrder workOrder){
		synchronized(permitMutex){
			if(maxResourcesReached()){
//				System.out.println("max pages reached");
				return false;
			}
			if(!isDepthApproved(workOrder)){
//				System.out.println("depth not approved : " + getDepth(workOrder) + " max : " + getMaxDepth() + this.getClass().getSimpleName());
				return false;
			}
//			System.out.println("work approved : " + workOrder.getSource());
			numResourcesCrawled++;
			return true;	
		}
	}
	
	public boolean isDepthApproved(ResourceWorkOrder workOrder){
		return !isMaxDepth(workOrder.getParent());
	}
	
	public boolean maxResourcesReached(){
		return numResourcesCrawled >= maxPages;
	}
	
	public boolean isMaxDepth(Resource parent) {
		if(parent == null){
			return false;
		}
		return parent.getDepth() >= getMaxDepth();
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
	
	public int getNumResourcesCrawled() {
		return numResourcesCrawled;
	}

	public void setNumResourcesCrawled(int numResourcesCrawled) {
		this.numResourcesCrawled = numResourcesCrawled;
	}
	
	
}
