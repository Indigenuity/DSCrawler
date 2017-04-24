package crawling.discovery.planning;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import crawling.discovery.execution.PlanId;

public class CrawlStructure {

	protected Map<PlanId, ResourceConfig> configs = new HashMap<PlanId, ResourceConfig>();
	
	public CrawlStructure(){
		
	}
	
	public PlanId registerConfig(ResourceConfig config){
		PlanId planId = new PlanId();
		configs.put(planId, config);
		return planId;
	}
}
