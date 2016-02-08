package async.work;

import java.util.UUID;

public class WorkOrder {
	
	protected WorkType workType;
	private Long uuid = UUID.randomUUID().getLeastSignificantBits();

	public WorkOrder(WorkType workType) {
		this.workType = workType;
	}
	
	public WorkType getWorkType() {
		return workType;
	}

	public void setWorkType(WorkType workType) {
		this.workType = workType;
	}

	public Long getUuid() {
		return uuid;
	}

	public void setUuid(Long uuid) {
		this.uuid = uuid;
	}
	
	
	
	

}
