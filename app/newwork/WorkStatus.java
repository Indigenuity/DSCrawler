package newwork;

public enum WorkStatus {
	UNASSIGNED, ASSIGNED, STARTED, PRECOMPLETED, COMPLETE,  ABORTED, ERROR;
	
	public boolean isWorkPending(){
		return this.ordinal() <= STARTED.ordinal();
	}
}