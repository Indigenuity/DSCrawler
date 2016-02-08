package async.work;

import java.util.UUID;

public class WorkResult {
	
	protected WorkType workType;
	protected WorkStatus workStatus;
	protected Long uuid;
	protected String note;
	
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
	
	
	
	
}
