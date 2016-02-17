package async.work;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WorkResult {
	
	protected WorkType workType;
	protected WorkStatus workStatus;
	protected Long uuid;
	protected String note;
	
	protected WorkOrder workOrder;
	protected Map<String, String> contextItems = new HashMap<String, String>();
	
	public WorkResult(WorkOrder workOrder) {
		this.workOrder = workOrder;
		this.workType = workOrder.workType;
		this.workStatus = WorkStatus.NO_WORK;
	}
	public WorkResult() {
		this.workType = WorkType.NO_WORK;
		this.workStatus = WorkStatus.NO_WORK;
	}

	public WorkResult(WorkType workType) {
		this.workType = workType;
		this.workStatus = WorkStatus.NO_WORK;
	}
	public WorkResult(WorkType workType, WorkStatus workStatus) {
		this.workType = workType;
		this.workStatus = workStatus;
	}
	
	public WorkType getWorkType() {
		return workType;
	}

	public void setWorkType(WorkType workType) {
		this.workType = workType;
	}
	public WorkStatus getWorkStatus() {
		return workStatus;
	}
	public void setWorkStatus(WorkStatus workStatus) {
		this.workStatus = workStatus;
	}

	public Long getUuid() {
		return uuid;
	}

	public void setUuid(Long uuid) {
		this.uuid = uuid;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
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
