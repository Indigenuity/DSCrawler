package async.work;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import async.newwork.SuperTaskContext;

public class WorkOrder {
	
	protected WorkType workType;
	protected Long uuid = UUID.randomUUID().getLeastSignificantBits();
	protected Map<String, String> contextItems = new HashMap<String, String>();
	protected SuperTaskContext superTaskContext;
	
	
	public WorkOrder(WorkType workType) {
		this.workType = workType;
	}
	
	public WorkType getWorkType() {
		return workType;
	}

	public void setWorkType(WorkType workType) {
		this.workType = workType;
	}

	public Long getUuid() {
		return uuid;
	}

	public void setUuid(Long uuid) {
		this.uuid = uuid;
	}
	public SuperTaskContext getSuperTaskContext() {
		return superTaskContext;
	}
	public void setSuperTaskContext(SuperTaskContext superTaskContext) {
		this.superTaskContext = superTaskContext;
	}
	
	public synchronized Map<String, String> getContextItems() {
		Map<String, String> returned = new HashMap<String, String>();
		returned.putAll(contextItems);
		return returned;
	}
	public synchronized String addContextItem(String name, String item) {
		return contextItems.put(name, item);
	}
	public synchronized String removeContextItems(String name) {
		return contextItems.remove(name);
	}
	public synchronized String getContextItem(String name) {
		return contextItems.get(name);
	}
	
}
