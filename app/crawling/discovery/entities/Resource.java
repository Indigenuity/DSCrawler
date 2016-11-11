package crawling.discovery.entities;

import java.util.List;

import crawling.discovery.execution.Crawl;

public interface Resource{
	
	public String getName();
	public Resource getParent();
	public List<List<Resource>> getChildResourceLists();
	public default Crawl getRoot() {
		return getParent().getRoot();
	}
}
