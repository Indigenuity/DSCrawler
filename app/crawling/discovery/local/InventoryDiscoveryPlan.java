package crawling.discovery.local;

import crawling.discovery.execution.PlanId;
import crawling.discovery.planning.DiscoveryPlan;
import crawling.discovery.planning.ResourcePlan;

public class InventoryDiscoveryPlan extends DiscoveryPlan {
	
	protected PlanId inventoryDestination;
	
	protected void setInventoryDestination(ResourcePlan resourcePlan){
		this.inventoryDestination = resourcePlan.getPlanId();
	}

}
