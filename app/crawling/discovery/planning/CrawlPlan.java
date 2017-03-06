package crawling.discovery.planning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import crawling.discovery.execution.Crawl;

public class CrawlPlan {
	
	protected Map<Long, ResourcePlan> resourcePlans = new HashMap<Long, ResourcePlan>(); 
	protected Map<Long, Set<Long>> discoveryPaths = new HashMap<Long, Set<Long>>();
	protected Map<Long, Set<Long>> generationPaths= new HashMap<Long, Set<Long>>();
	
	protected final Consumer<Crawl> startPlan;
	
	public CrawlPlan(){
		this.startPlan = (crawl) -> {};
	}
	
	public CrawlPlan(Consumer<Crawl> startPlan) {
		this.startPlan = startPlan;
	}
	

	public Consumer<Crawl> getStartPlan() {
		return startPlan;
	}
	
	protected ResourcePlan getResourcePlan(long uuid){
		return resourcePlans.get(uuid);
	}
	
	protected boolean isRegistered(ResourcePlan source) {
		return resourcePlans.containsKey(source.getUuid());
	}
	
	public CrawlPlan registerResourcePlan(ResourcePlan resourcePlan){
		resourcePlans.put(resourcePlan.getUuid(), resourcePlan);
		return this;
	}
	
	public CrawlPlan registerDiscoveryPath(ResourcePlan source, ResourcePlan destination){
		if(!isRegistered(source) || !isRegistered(destination)){
			throw new IllegalArgumentException("Cannot register discovery path for unregistered ResourcePlan objects");
		}
		if(!discoveryPaths.containsKey(source.getUuid())){
			discoveryPaths.put(source.getUuid(), new HashSet<Long>());
		}
		discoveryPaths.get(source.getUuid()).add(destination.getUuid());
		return this;
	}
	
	public CrawlPlan registerGenerationPath(ResourcePlan source, ResourcePlan destination){
		if(!isRegistered(source) || !isRegistered(destination)){
			throw new IllegalArgumentException("Cannot register generation path for unregistered ResourcePlan objects");
		}
		if(!generationPaths.containsKey(source.getUuid())){
			generationPaths.put(source.getUuid(), new HashSet<Long>());
		}
		generationPaths.get(source.getUuid()).add(destination.getUuid());
		return this;
	}
	
}
