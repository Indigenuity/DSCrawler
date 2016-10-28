package crawling.discovery;

public abstract class Resource{
	
	private final String name;
	private final Resource parent;
	
	public Resource(Resource parent, String name){
		this.parent = parent;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Resource getParent() {
		return parent;
	}

}
