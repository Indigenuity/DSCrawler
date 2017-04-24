package crawling.discovery.entities;

import crawling.discovery.execution.PlanId;

public abstract class FlushableResource extends Resource{
	
	protected Object key;
	protected FlushState state;
	
	
	public FlushableResource(Object source, Object value, Resource parent, ResourceId resourceId, PlanId planId) {
		super(source, value, parent, resourceId, planId);
	}
	
	public FlushableResource(Object source, Object value, Resource parent){
		super(source, value, parent);
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
