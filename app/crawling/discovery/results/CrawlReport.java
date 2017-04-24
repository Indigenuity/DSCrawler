package crawling.discovery.results;

import java.util.HashMap;
import java.util.Map;

import crawling.discovery.execution.CrawlContext;
import crawling.discovery.execution.PlanId;
import crawling.discovery.execution.ResourceContext;

public class CrawlReport {
	
	private final Map<PlanId, ResourceReport> resourceReports = new HashMap<PlanId, ResourceReport>();
	
	private boolean maxPagesReached = false;
	
	public CrawlReport(CrawlContext context) {
		for(ResourceContext resourceContext : context.getResourceContexts()){
			ResourceReport resourceReport = new ResourceReport(resourceContext);
			resourceReports.put(resourceContext.getPlanId(), resourceReport);
		}

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
	
	
}
