package crawling.discovery.entities;

import crawling.discovery.execution.PlanId;
import crawling.discovery.planning.PreResource;

public abstract class FlushableResource extends Resource{
	
	protected FlushState state = FlushState.FLUSHED;
	
	
	public FlushableResource(Object source, Object value, Resource parent, ResourceId resourceId, PlanId planId) {
		super(source, parent, resourceId, planId);
		this.setValue(value);
	}
	
	public FlushableResource(Object source, Resource parent, ResourceId resourceId, PlanId planId) {
		super(source, parent, resourceId, planId);
	}
	
	public FlushableResource(PreResource preResource, Resource parent, ResourceId resourceId, PlanId planId){
		this(preResource.getSource(), parent, resourceId, planId);
		this.fetchStatus = preResource.getFetchStatus();
		this.fetchException = preResource.getFetchException();
		this.discoveryStatus = preResource.getDiscoveryStatus();
		this.discoveryException = preResource.getDiscoveryException();
	}
	
	public abstract Object produceValue();
	
	@Override
	public synchronized Object getValue(){
		if(this.getState() == FlushState.FLUSHED){
			this.setValue(produceValue());
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
