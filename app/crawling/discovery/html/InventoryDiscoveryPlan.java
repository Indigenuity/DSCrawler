package crawling.discovery.html;

import crawling.discovery.execution.PlanReference;
import crawling.discovery.planning.DiscoveryPlan;
import crawling.discovery.planning.ResourcePlan;

public class InventoryDiscoveryPlan extends DiscoveryPlan {
	
	protected PlanReference inventoryDestination;
	
	protected void setInventoryDestination(ResourcePlan resourcePlan){
		this.inventoryDestination = resourcePlan.getPlanReference();
	}

}
