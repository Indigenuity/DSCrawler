package crawling.discovery.planning;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.util.concurrent.RateLimiter;

public class ContextPlan extends Plan {

	protected final Map<String, Object> initialContextObjects =new ConcurrentHashMap<String, Object>();
	
	public Map<String, Object> getInitialContextObjects() {
		return new HashMap<String, Object>(initialContextObjects);
	}
	public Object removeContextObject(String key){
		return initialContextObjects.remove(key);
	}
	public Object putContextObject(String key, Object value){
		return initialContextObjects.put(key, value);
	}
	
}
