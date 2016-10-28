package newwork;

import java.util.Properties;
import java.util.UUID;

public abstract class StandardWorkOrder extends WorkOrder {

	
	protected String customId;
	protected Properties config;
	
	public StandardWorkOrder(){
		initProperties();
	}
	
	public String getCustomId() {
		return customId;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}
	
	protected abstract void initProperties();
}
