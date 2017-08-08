package crawling.discovery.local;

import crawling.discovery.execution.PlanId;
import crawling.discovery.planning.DiscoveryPlan;

public class PageCrawlDiscoveryPlan extends DiscoveryPlan {

	public PageCrawlDiscoveryPlan(PlanId sourceResourcePlanId) {
		super(sourceResourcePlanId);
		this.setDiscoveryTool(new DSDiscoveryTool());
	}
	
}
