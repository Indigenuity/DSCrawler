package crawling.discovery.entities;

import java.util.ArrayList;
import java.util.List;

public class GenericResource implements Resource {

	protected final String name;
	protected final Resource parent;
	protected final List<List<Resource>> childResourceLists = new ArrayList<List<Resource>>();
	
	public GenericResource(String name, Resource parent){
		this.parent = parent;
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Resource getParent() {
		return parent;
	}

	@Override
	public List<List<Resource>> getChildResourceLists() {
		return childResourceLists;
	}
	
	
}
