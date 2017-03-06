package crawling.discovery.entities;

public abstract class FlushableResource<K, V> extends Resource<V>{
	
	private K key;
	private FlushState state;
	
	public FlushableResource(K key, V value) {
		super(value);
		this.key = key;
		
	}
	
	public abstract V produceValue(K key);
	
	@Override
	public synchronized V getValue(){
		if(this.getState() == FlushState.FLUSHED){
			this.setValue(produceValue(this.getKey()));
		}
		return this.value;
	}
	
	@Override
	public synchronized void setValue(V value) {
		this.value = value;
		this.state = FlushState.FILLED;
	}
	
	public synchronized void flush() {
		this.value = null;
		this.state = FlushState.FLUSHED;
	}

	public K getKey() {
		return key;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public FlushState getState() {
		return state;
	}

	public void setState(FlushState state) {
		this.state = state;
	}
	
	
	public enum FlushState{
		FILLED, FLUSHED;
	}
}
