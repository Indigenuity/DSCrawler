package crawling.discovery.entities;

public abstract class FlushableResource extends Resource{
	
	private Object key;
	private FlushState state;
	
	
	public FlushableResource(Object value, ResourceId resourceId) {
		super(value, resourceId);
		// TODO Auto-generated constructor stub
	}

	public abstract Object produceValue(Object key);
	
	@Override
	public synchronized Object getValue(){
		if(this.getState() == FlushState.FLUSHED){
			this.setValue(produceValue(this.getKey()));
		}
		return this.value;
	}
	
	@Override
	public synchronized void setValue(Object value) {
		this.value = value;
		this.state = FlushState.FILLED;
	}
	
	public synchronized void flush() {
		this.value = null;
		this.state = FlushState.FLUSHED;
	}

	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
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
