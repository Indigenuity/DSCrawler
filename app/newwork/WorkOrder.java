package newwork;

import java.util.UUID;

public class WorkOrder {

	
	protected final Long uuid = UUID.randomUUID().getLeastSignificantBits();		//The UUID belongs to the work that was done
	
	public Long getUuid() {
		return uuid;
	}
}
