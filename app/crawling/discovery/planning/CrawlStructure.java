package crawling.discovery.planning;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import crawling.discovery.execution.PlanReference;

public class CrawlStructure {

	protected Map<PlanReference, ResourceConfig> configs = new HashMap<PlanReference, ResourceConfig>();
	
	public CrawlStructure(){
		
	}
	
	public PlanReference registerConfig(ResourceConfig config){
		PlanReference reference = new PlanReference();
		configs.put(reference, config);
		return reference;
	}
}
