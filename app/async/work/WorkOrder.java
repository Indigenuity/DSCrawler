package async.work;

import java.util.UUID;

public class WorkOrder {

	protected Long uuid = UUID.randomUUID().getLeastSignificantBits();
	
	public Long getUuid() {
		return uuid;
	}

	public void setUuid(Long uuid) {
		this.uuid = uuid;
	}
}
