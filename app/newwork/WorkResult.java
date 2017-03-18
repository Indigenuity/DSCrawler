package newwork;

public class WorkResult {

	private final Long uuid;	//The UUID belongs to the work that was done
	private Object result;
	private WorkStatus workStatus = WorkStatus.NOT_STARTED;
	private String error;
	private Exception exception;
	
	public WorkResult(Long workUuid){
		this.uuid = workUuid;
	}
	
	public WorkResult(WorkOrder workOrder) {
		this.uuid = workOrder.getUuid();
	}

	public Long getUuid() {
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

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
		this.setError(exception.getMessage());
	}
}
