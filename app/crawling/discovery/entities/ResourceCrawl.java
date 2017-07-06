package crawling.discovery.entities;

import java.util.HashSet;
import java.util.Set;

public class ResourceCrawl {
	
	protected final Set<Object> seedSources = new HashSet<Object>();
	protected final Set<Resource> resources = new HashSet<Resource>();
}
