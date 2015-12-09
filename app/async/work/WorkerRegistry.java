package async.work;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import async.Asyncleton;

public class WorkerRegistry {
	private static final WorkerRegistry instance = new WorkerRegistry();
	
	private final Map<WorkType, Class<?>> registry = Collections.synchronizedMap(new HashMap<WorkType, Class<?>>());
	
	protected WorkerRegistry(){
		for(WorkType workType : WorkType.values()){
			if(workType.getDefaultWorker() != null) {
				register(workType, workType.getDefaultWorker()); 
			}
		}
	}
	
	public static WorkerRegistry getInstance() {
		return instance;
	}
	
	public Class<?> getRegistrant(WorkType workType) {
		return registry.get(workType);
	}
	
	//Synchronize for multiple operations on the registry
	public boolean register(WorkType workType, Class<?> clazz) {
		synchronized(registry){
			if(workType == null || clazz == null || registry.containsKey(workType)) {
				return false;
			}
			registry.put(workType, clazz);
			return true;
		}
	}
	
	public void unRegister(WorkType workType) {
		registry.remove(workType);
	}
	
	public void replaceRegister(WorkType workType, Class<?> clazz) {
		registry.replace(workType, clazz);
	}
	
	//Return a copy of the registry.  Don't want anything iterating over it outside of this object
	public Map<WorkType, Class<?>> getRegistry() {
		Map<WorkType, Class<?>> copy = new HashMap<WorkType, Class<?>>();
		copy.putAll(registry);
		return copy;
	}
}
