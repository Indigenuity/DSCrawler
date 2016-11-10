package async.work;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TypedWorkResult {
	
	protected WorkStatus workStatus;
	protected String note;
	protected TypedWorkOrder workOrder;
	protected Map<String, String> contextItems = new HashMap<String, String>();
	
	public TypedWorkResult(TypedWorkOrder workOrder) {
		this.workOrder = workOrder;
		this.workStatus = WorkStatus.NO_WORK;
	}
	public TypedWorkResult() {
		this.workStatus = WorkStatus.NO_WORK;
	}

	public TypedWorkResult(WorkType workType) {
		this.workStatus = WorkStatus.NO_WORK;
	}
	public TypedWorkResult(WorkType workType, WorkStatus workStatus) {
		this.workStatus = workStatus;
	}
	
	public WorkStatus getWorkStatus() {
		return workStatus;
	}
	public void setWorkStatus(WorkStatus workStatus) {
		this.workStatus = workStatus;
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
	public TypedWorkOrder getWorkOrder() {
		return workOrder;
	}
	public void setWorkOrder(TypedWorkOrder workOrder) {
		this.workOrder = workOrder;
	}
	
	
	
}
