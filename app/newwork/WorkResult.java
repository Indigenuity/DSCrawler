package newwork;

public class WorkResult {

	protected Long workUuid;	//The UUID belongs to the work that was done
	protected Object result;
	protected WorkStatus workStatus = WorkStatus.NOT_STARTED;
	protected String error;
	
	public WorkResult(Long workUuid){
		this.workUuid = workUuid;
	}

	public Long getWorkUuid() {
		return workUuid;
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
