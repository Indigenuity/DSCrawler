package crawling.discovery.execution;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.util.concurrent.RateLimiter;

import crawling.discovery.entities.Resource;
import crawling.discovery.planning.ContextPlan;
import newwork.WorkStatus;

public abstract class Context extends PlanImplementation {
	
	protected final Map<String, Object> contextObjects = new ConcurrentHashMap<String, Object>();
	
	public Context(ContextPlan contextPlan) {
		super(contextPlan);
		this.contextObjects.putAll(contextPlan.getInitialContextObjects());
		
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
	
	public Object remove(Object key) {
		return contextObjects.remove(key);
	}
	
	public Map<String, Object> getContextObjects() {
		return new HashMap<String, Object>(contextObjects);
	}
}
