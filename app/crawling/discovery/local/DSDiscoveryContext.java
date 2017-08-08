package crawling.discovery.local;

import crawling.discovery.execution.CrawlContext;
import crawling.discovery.execution.DiscoveryContext;
import crawling.discovery.execution.PlanId;
import crawling.discovery.planning.DiscoveryPlan;

public class DSDiscoveryContext extends DiscoveryContext {

	private final PlanId inventoryPlanId;
	
	public DSDiscoveryContext(CrawlContext crawlContext, DiscoveryPlan discoveryPlan) {
		super(crawlContext, discoveryPlan);
		this.inventoryPlanId = (PlanId)get("inventoryPlanId");
	}

	public PlanId getInventoryPlanId() {
		return inventoryPlanId;
	}
	
}
