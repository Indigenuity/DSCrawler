package async.registration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import async.tools.Tool;
import async.work.WorkType;

public class WorkerRegistry {
	private static final WorkerRegistry instance = new WorkerRegistry();
	
	private final Map<WorkType, RegistryEntry> registry = Collections.synchronizedMap(new HashMap<WorkType, RegistryEntry>());
	
	protected WorkerRegistry(){
//		for(WorkType workType : WorkType.values()){
//			if(workType.getDefaultWorker() != null) {
//				register(workType, workType.getDefaultWorker()); 
//			}
//		}
	}
	
	public static WorkerRegistry getInstance() {
		return instance;
	}
	
	public RegistryEntry getRegistrant(WorkType workType) {
		return registry.get(workType);
	}
	
	public void register(Tool tool) {
		for(WorkType workType : tool.getAbilities()){
			RegistryEntry entry = new RegistryEntry();
			entry.setClazz(tool.getClass());
			for(ContextItem item : tool.getRequiredItems(workType)){
				entry.addRequiredContextItem(item);
			}
			for(ContextItem item : tool.getResultItems(workType)){
				entry.addResultContextItem(item);
			}
			registry.put(workType, entry);
		}
	}
	
	//Synchronize for multiple operations on the registry
	public boolean register(WorkType workType, RegistryEntry entry) {
		synchronized(registry){
			if(workType == null || entry == null || registry.containsKey(workType)) {
				return false;
			}
			registry.put(workType, entry);
			return true;
		}
	}
	
	public void unRegister(WorkType workType) {
		registry.remove(workType);
	}
	
	public void replaceRegister(WorkType workType, RegistryEntry entry) {
		registry.replace(workType, entry);
	}
	
	//Return a copy of the registry.  Don't want anything iterating over it outside of this object
	public Map<WorkType, RegistryEntry> getRegistry() {
		Map<WorkType, RegistryEntry> copy = new HashMap<WorkType, RegistryEntry>();
		copy.putAll(registry);
		return copy;
	}
}
