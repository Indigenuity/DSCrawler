package crawling.discovery.entities;

import java.util.List;


public class Resource {
	
	protected Object value;
	protected Resource parent;
	protected List<Resource> children;
	protected final ResourceId resourceId;
	
	public Resource(Object value, ResourceId resourceId) {
		this.setValue(value);
		this.resourceId = resourceId;
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
	public void setParent(Resource parent) {
		this.parent = parent;
	}
	public List<Resource> getChildren() {
		return children;
	}
	public void addChild(Resource child) {
		this.children.add(child);
	}
	public ResourceId getResourceId() {
		return resourceId;
	}
}
