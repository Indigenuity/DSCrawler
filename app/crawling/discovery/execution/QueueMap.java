package crawling.discovery.execution;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import crawling.discovery.planning.PrimaryResourcePlan;

public class QueueMap {

	private Map<PrimaryResourcePlan<?>, FetchQueue<?>> map = new HashMap<PrimaryResourcePlan<?>, FetchQueue<?>>();
	
	public <T> void put(PrimaryResourcePlan<T> key, FetchQueue<T> value) {
		map.put(key, value); 
	}

	@SuppressWarnings("unchecked")
	public <T> FetchQueue<T> get(PrimaryResourcePlan<T> key) {
		return (FetchQueue<T>) map.get(key); 
	}
	
	public Collection<FetchQueue<?>> getQueues(){
		return map.values();
	}
}
