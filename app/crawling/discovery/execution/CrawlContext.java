package crawling.discovery.execution;

import java.util.HashMap;
import java.util.Map;

public class CrawlContext {

	private volatile Map<String, Object> contextObjects = new HashMap<String, Object>();
	
	public Object getContextObject(String key){
		synchronized(contextObjects){
			return contextObjects.get(key);
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
}
