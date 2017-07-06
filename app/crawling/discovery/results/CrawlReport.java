package crawling.discovery.results;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import crawling.discovery.entities.Resource;
import crawling.discovery.execution.CrawlContext;
import crawling.discovery.execution.PlanId;
import crawling.discovery.execution.ResourceContext;

public class CrawlReport {
	
	private final Map<PlanId, ResourceReport> resourceReports = new HashMap<PlanId, ResourceReport>();
	protected final Map<String, Object> contextObjects = new ConcurrentHashMap<String, Object>();
	
	private boolean maxPagesReached = false;
	
	public CrawlReport(CrawlContext context) {
		for(ResourceContext resourceContext : context.getResourceContexts()){
			ResourceReport resourceReport = new ResourceReport(resourceContext);
			resourceReports.put(resourceContext.getPlanId(), resourceReport);
		}
		contextObjects.putAll(context.getContextObjects());
		maxPagesReached = context.maxResourcesReached();
//		System.out.println("maxPagesReached : " + maxPagesReached);
//		System.out.println("num resources crawled : " + context.getNumResourcesCrawled());
	}

	public Map<PlanId, ResourceReport> getResourceReports() {
		return resourceReports;
	}

	public boolean isMaxPagesReached() {
		return maxPagesReached;
	}

	public void setMaxPagesReached(boolean maxPagesReached) {
		this.maxPagesReached = maxPagesReached;
	}
	
	public Set<Resource> getAllResources(){
		Set<Resource> resources = new HashSet<Resource>();
		for(ResourceReport report : resourceReports.values()){
			resources.addAll(report.getResources());
		}
		return resources;
	}

	public Map<String, Object> getContextObjects() {
		return contextObjects;
	}
	
	
}
