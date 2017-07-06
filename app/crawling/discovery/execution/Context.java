package crawling.discovery.execution;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.util.concurrent.RateLimiter;

import crawling.discovery.entities.Resource;
import crawling.discovery.planning.ContextPlan;

public abstract class Context extends PlanImplementation {
	
	protected final int maxDepth;
	protected final int maxPages;
	
	protected volatile int numResourcesCrawled = 0;
	
	protected final Map<String, Object> contextObjects = new ConcurrentHashMap<String, Object>();
	protected final RateLimiter rateLimiter;
	
	protected final Object permitMutex = new Object();
	
	public Context(ContextPlan contextPlan) {
		super(contextPlan);
		this.contextObjects.putAll(contextPlan.getInitialContextObjects());
		this.rateLimiter = contextPlan.getRateLimiter();
		this.maxDepth = contextPlan.getMaxDepth();
		this.maxPages = contextPlan.getMaxPages();
	}

	public boolean acquireWorkPermit(){
		if(rateLimiter != null){
			rateLimiter.acquire();
		}
		return true;
	}
	
	protected boolean approveWork(Resource resource){
		synchronized(permitMutex){
			if(maxResourcesReached()){
//				System.out.println("max pages reached");
				return false;
			}
			if(!isDepthApproved(resource)){
//				System.out.println("depth not approved : " + getDepth(workOrder) + " max : " + getMaxDepth() + this.getClass().getSimpleName());
				return false;
			}
//			System.out.println("work approved : " + workOrder.getSource());
			incrementNumResourcesCrawled();
			return true;	
		}
	}
	
	protected int incrementNumResourcesCrawled(){
		synchronized(permitMutex){
			numResourcesCrawled++;
			return numResourcesCrawled;
		}
	}
	
	public boolean isDepthApproved(Resource resource){
		return resource.getDepth() <= getMaxDepth();
	}
	
	public boolean maxResourcesReached(){
		return numResourcesCrawled >= maxPages;
	}
	
	public boolean isMaxDepth(Resource resource) {
		if(resource == null){
			return false;
		}
		return resource.getDepth() >= getMaxDepth();
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
	
	public Object get(Object key){
		return contextObjects.get(key);
	}
	
	public boolean containsKey(Object key) {
		return contextObjects.containsKey(key);
	}

	public Object put(String key, Object value) {
		return contextObjects.put(key, value);
	}

	public  Object putIfAbsent(String key, Object value) {
		return contextObjects.putIfAbsent(key, value);
	}

	public boolean remove(Object key, Object value) {
		return contextObjects.remove(key, value);
	}
	
	public int getMaxDepth() {
		return maxDepth;
	}

	public int getMaxPages() {
		return maxPages;
	}
	
	public int getNumResourcesCrawled() {
		return numResourcesCrawled;
	}

	public void setNumResourcesCrawled(int numResourcesCrawled) {
		this.numResourcesCrawled = numResourcesCrawled;
	}

	public Map<String, Object> getContextObjects() {
		return new HashMap<String, Object>(contextObjects);
	}
	
	
}
