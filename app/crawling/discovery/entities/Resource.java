package crawling.discovery.entities;

import java.util.List;

public interface Resource{
	
	public String getName();
	public Resource getParent();
	public List<List<Resource>> getChildResourceLists();
}
