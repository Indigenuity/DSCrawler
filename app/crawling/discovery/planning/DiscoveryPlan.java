package crawling.discovery.planning;


import crawling.discovery.execution.CrawlContext;
import crawling.discovery.execution.DiscoveryContext;
import crawling.discovery.execution.PlanReference;

public class DiscoveryPlan extends Plan {

	protected PlanReference defaultDestination;
	protected DiscoveryTool discoveryTool;
	
	public DiscoveryPlan(){
	}

	public DiscoveryTool getDiscoveryTool() {
		return discoveryTool;
	}

	public void setDiscoveryTool(DiscoveryTool discoveryTool) {
		this.discoveryTool = discoveryTool;
	}

	public synchronized DiscoveryContext generateContext(CrawlContext crawlContext){
		return new DiscoveryContext(crawlContext, this);
	}

	public PlanReference getDefaultDestination() {
		return defaultDestination;
	}

	public void setDefaultDestination(PlanReference defaultDestination) {
		this.defaultDestination = defaultDestination;
	}
	
	public void setDefaultDestination(ResourcePlan defaultDestinationPlan) {
		this.defaultDestination = defaultDestinationPlan.getPlanReference();
	}
	
}
