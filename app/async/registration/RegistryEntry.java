package async.registration;

import java.util.ArrayList;
import java.util.List;

public class RegistryEntry {

	private Class<?> clazz;
	private List<RequiredContextItem> requiredContextItems = new ArrayList<RequiredContextItem>();
	
	public Class<?> getClazz() {
		return clazz;
	}
	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}
	public List<RequiredContextItem> getRequiredContextItems() {
		return requiredContextItems;
	}
	public void setRequiredContextItems(List<RequiredContextItem> requiredContextItems) {
		this.requiredContextItems = requiredContextItems;
	}
	
	
}
