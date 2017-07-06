package crawling.discovery.planning;

import java.util.HashSet;
import java.util.Set;

import crawling.discovery.execution.CrawlContext;
import crawling.discovery.execution.DiscoveryContext;
import crawling.discovery.execution.PlanId;

public class DiscoveryPlan extends ContextPlan {

	protected PlanId destinationPlanId;
	protected DiscoveryTool discoveryTool;
	protected DiscoveryPoolPlan discoveryPoolPlan = new DiscoveryPoolPlan();
	
	public DiscoveryPlan(){ }

	public DiscoveryPlan(DiscoveryTool discoveryTool, DiscoveryPoolPlan discoveryPoolPlan) {
		super();
		this.discoveryTool = discoveryTool;
		this.discoveryPoolPlan = discoveryPoolPlan;
	}
	
	public synchronized DiscoveryContext generateContext(CrawlContext crawlContext){
		return new DiscoveryContext(crawlContext, this);
	}
	
	public DiscoveryTool getDiscoveryTool() {
		return discoveryTool;
	}

	public void setDiscoveryTool(DiscoveryTool discoveryTool) {
		this.discoveryTool = discoveryTool;
	}

	public PlanId getDestinationPlanId() {
		return destinationPlanId;
	}

	public void setDestinationPlanId(PlanId destinationPlanId) {
		this.destinationPlanId = destinationPlanId;
	}

	public DiscoveryPoolPlan getDiscoveryPoolPlan() {
		return discoveryPoolPlan;
	}

	public void setDiscoveryPoolPlan(DiscoveryPoolPlan discoveryPoolPlan) {
		this.discoveryPoolPlan = discoveryPoolPlan;
	}
	
	
}
