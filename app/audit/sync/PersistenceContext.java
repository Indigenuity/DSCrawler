package audit.sync;

public interface PersistenceContext {

	public <T> T insert(T record);
	public <T> T update(T record);
	public <T> T outdate(T record);
	
	public <K, T> T fetch(K key, Class<T> clazz);
	
	public void flush();
	public void commit();
	
}
