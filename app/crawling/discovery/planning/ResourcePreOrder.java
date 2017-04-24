package crawling.discovery.planning;

import crawling.discovery.entities.Resource;

public class ResourcePreOrder {
	
	protected final Object source;
	protected final Resource parent;
	
	public ResourcePreOrder(Object source, Resource parent){
		this.source = source;
		this.parent = parent;
	}

	public Object getSource() {
		return source;
	}

	public Resource getParent() {
		return parent;
	}

}
