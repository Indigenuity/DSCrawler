package async.registration;

public class ContextItem {

	private String name;
	private Class<?> clazz;
	private boolean nullable;
	
	public ContextItem(String name, Class<?> clazz, boolean nullable){
		this.name = name;
		this.clazz = clazz;
		this.nullable = nullable;
	}
	
	public String getName() {
		return name;
	}
	public Class<?> getClazz() {
		return clazz;
	}
	public boolean isNullable() {
		return nullable;
	}
	
	
}
