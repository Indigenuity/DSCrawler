package async.work;

public class WorkResult {
	
	protected WorkType workType;
	protected WorkStatus workStatus;
	
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
	
	
}
