package crawling.discovery.entities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import crawling.discovery.execution.PlanId;
import crawling.discovery.planning.PreResource;
import newwork.WorkStatus;


public class Resource {
	
	
	protected Object value;
	protected final Object source;
	protected final Resource parent;
	protected final Set<Resource> children = new HashSet<Resource>();
	protected final ResourceId resourceId;
	
	protected WorkStatus fetchStatus = WorkStatus.UNASSIGNED;
	protected Exception fetchException = null;
	protected WorkStatus discoveryStatus= WorkStatus.UNASSIGNED;
	protected Exception discoveryException = null;
	
	public Resource(Object source, Object value, Resource parent, ResourceId resourceId) {
		this(source, parent, resourceId);
		this.value = value;
	}
	
	public Resource(Object source, Resource parent, ResourceId resourceId) {
		this.source = source;
		this.parent = parent;
		if(parent != null){
			this.parent.addChild(this);
		}
		this.resourceId = resourceId;
	}
	
	public Resource(PreResource preResource, Resource parent, ResourceId resourceId) {
		this(preResource.getSource(), preResource.getValue(), parent, resourceId);
		this.fetchStatus = preResource.getFetchStatus();
		this.fetchException = preResource.getFetchException();
		this.discoveryStatus = preResource.getDiscoveryStatus();
		this.discoveryException = preResource.getDiscoveryException();
	}
	
	public Resource getRoot() {
		if(this.parent == null){
			return this;
		}
		return getParent().getRoot();
	}
	
	public int getDepth() {
		if(this.parent == null){
			return 0;
		}
		return 1 + parent.getDepth();
	}
	
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Resource getParent() {
		return parent;
	}
	public Set<Resource> getChildren() {
		return new HashSet<Resource>(children);
	}
	protected boolean addChild(Resource child) {
		return this.children.add(child);
	}
//	public boolean removeChild(Resource child) {
//		return this.children.remove(child);
//	}
	public ResourceId getResourceId() {
		return resourceId;
	}
	public Object getSource() {
		return source;
	}
	public WorkStatus getFetchStatus() {
		return fetchStatus;
	}

	public void setFetchStatus(WorkStatus fetchStatus) {
		this.fetchStatus = fetchStatus;
	}

	public Exception getFetchException() {
		return fetchException;
	}

	public void setFetchException(Exception fetchException) {
		this.fetchException = fetchException;
	}

	public WorkStatus getDiscoveryStatus() {
		return discoveryStatus;
	}

	public void setDiscoveryStatus(WorkStatus discoveryStatus) {
		this.discoveryStatus = discoveryStatus;
	}

	public Exception getDiscoveryException() {
		return discoveryException;
	}

	public void setDiscoveryException(Exception discoveryException) {
		this.discoveryException = discoveryException;
	}

}
