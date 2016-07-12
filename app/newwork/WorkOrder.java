package newwork;

import java.util.Properties;
import java.util.UUID;

public abstract class WorkOrder {

	protected final Long workUuid = UUID.randomUUID().getLeastSignificantBits();		//The UUID belongs to the work that was done
	protected String customId;
	protected Properties config;
	
	public WorkOrder(){
		initProperties();
	}
	
	public Long getWorkUuid() {
		return workUuid;
	}

	public String getCustomId() {
		return customId;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}
	
	protected abstract void initProperties();
}
