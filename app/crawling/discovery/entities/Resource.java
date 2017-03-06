package crawling.discovery.entities;

import java.util.List;


public class Resource<V> {
	
	protected V value;
	protected Resource<?> parent;
	protected List<Resource<?>> children;
	
	public Resource(V value) {
		this.setValue(value);
	}
	
	public Resource<?> getRoot() {
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
	
	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

	public Resource<?> getParent() {
		return parent;
	}
	public void setParent(Resource<?> parent) {
		this.parent = parent;
	}
	public List<Resource<?>> getChildren() {
		return children;
	}
	public void addChild(Resource<?> child) {
		this.children.add(child);
	}
}
