package crawling.discovery.entities;


public class GenericPrimaryResource extends GenericResource implements PrimaryResource{

	protected final Endpoint endpoint; 
	
	public GenericPrimaryResource(String name, Resource parent, Endpoint endpoint) {
		super(name, parent);
		this.endpoint = endpoint;
	}

	@Override
	public Endpoint getEndpoint() {
		return endpoint;
	}


}
