package crawling.discovery.local;

import crawling.discovery.planning.DiscoveryPlan;

public class InventoryDiscoveryPlan extends DiscoveryPlan {
	
	
	public InventoryDiscoveryPlan(){
		super();
		this.setDiscoveryTool(new InventoryDiscoveryTool());
	}
	

}
