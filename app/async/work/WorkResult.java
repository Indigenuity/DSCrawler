package async.work;

public class WorkResult {

	protected final WorkOrder workOrder;
	public WorkResult(WorkOrder workOrder) {
		this.workOrder = workOrder;
	}
	public WorkOrder getWorkOrder() {
		return workOrder;
	}
	
	
	
}
