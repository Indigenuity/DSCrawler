package newwork;

public class WorkResult {

	protected Long uuid;	//The UUID belongs to the work that was done
	protected Object result;
	protected WorkStatus workStatus = WorkStatus.NOT_STARTED;
	protected String error;
	
	public WorkResult(Long workUuid){
		this.uuid = workUuid;
	}
	
	public WorkResult(WorkOrder workOrder) {
		this.uuid = workOrder.getUuid();
	}

	public Long getWorkUuid() {
		return uuid;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public WorkStatus getWorkStatus() {
		return workStatus;
	}

	public void setWorkStatus(WorkStatus workStatus) {
		this.workStatus = workStatus;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
	
	
}
