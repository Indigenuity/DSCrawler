package async.work;

public class WorkOrder {
	
	protected WorkType workType;

	public WorkOrder(WorkType workType) {
		this.workType = workType;
	}
	
	public WorkType getWorkType() {
		return workType;
	}

	public void setWorkType(WorkType workType) {
		this.workType = workType;
	}
	
	

}
