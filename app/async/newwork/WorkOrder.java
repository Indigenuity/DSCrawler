package async.newwork;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import async.work.WorkType;

public class WorkOrder {

	protected WorkType workType;
	protected final Long uuid = UUID.randomUUID().getLeastSignificantBits();
	protected Map<String, Object> contextItems = new HashMap<String, Object>();
	protected SuperTaskContext superTaskContext;
	public WorkType getWorkType() {
		return workType;
	}
	public void setWorkType(WorkType workType) {
		this.workType = workType;
	}
	public Long getUuid() {
		return uuid;
	}
	public Map<String, Object> getContextItems() {
		return contextItems;
	}
	public void setContextItems(Map<String, Object> contextItems) {
		this.contextItems = contextItems;
	}
	public SuperTaskContext getSuperTaskContext() {
		return superTaskContext;
	}
	public void setSuperTaskContext(SuperTaskContext superTaskContext) {
		this.superTaskContext = superTaskContext;
	}
	
	
	
	
}
