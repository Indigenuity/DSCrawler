package crawling.discovery.entities;

import java.util.List;

import crawling.discovery.execution.PlanId;


public class Resource {
	
	protected Object source;
	protected Object value;
	protected Resource parent;
	protected List<Resource> children;
	protected ResourceId resourceId;
	protected PlanId planId;
	
	
	public Resource(Object source, Object value, Resource parent, ResourceId resourceId, PlanId planId) {
		this(source, value, parent);
		this.planId = planId;
		this.resourceId = resourceId;
		
	}
	
	public Resource(Object source, Object value, Resource parent){
		this.setValue(value);
		this.source = source;
		this.parent = parent;
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

	public Object getSource() {
		return source;
	}

	public void setSource(Object source) {
		this.source = source;
	}

	public PlanId getPlanId() {
		return planId;
	}

	public void setPlanId(PlanId planId) {
		this.planId = planId;
	}

	public void setResourceId(ResourceId resourceId) {
		this.resourceId = resourceId;
	}

	public void setChildren(List<Resource> children) {
		this.children = children;
	}

}
