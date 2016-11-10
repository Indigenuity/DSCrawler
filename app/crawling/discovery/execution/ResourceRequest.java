package crawling.discovery.execution;

import crawling.discovery.entities.Endpoint;
import crawling.discovery.entities.Resource;

public class ResourceRequest<R> {

	protected final Endpoint endpoint;
	protected final Resource parent;
	public ResourceRequest(Endpoint endpoint, Resource parent) {
		super();
		this.endpoint = endpoint;
		this.parent = parent;
	}
	public Endpoint getEndpoint() {
		return endpoint;
	}
	public Resource getParent() {
		return parent;
	}
	
	
	
	
}
