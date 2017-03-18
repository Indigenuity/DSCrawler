package crawling.discovery.planning;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import crawling.discovery.execution.CrawlContext;
import crawling.discovery.execution.PlanReference;

public class CrawlPlan extends Plan{
	
	public final static int DEFAULT_MAX_DEPTH_OF_CRAWLING = 1;
	public final static int DEFAULT_MAX_PAGES_TO_FETCH = 2000;
	
	private int maxDepth = DEFAULT_MAX_DEPTH_OF_CRAWLING;
	private int maxPages = DEFAULT_MAX_PAGES_TO_FETCH;
	private CrawlTool crawlTool;
	
	protected final Set<ResourcePlan> resourcePlans = new HashSet<ResourcePlan>();
	protected final Set<DiscoveryPlan> discoveryPlans = new HashSet<DiscoveryPlan>();
	protected final Map<ResourcePlan, Set<Object>> seedSources = new HashMap<ResourcePlan, Set<Object>>();
	
	
	public CrawlPlan(){
	}
	
	protected boolean isRegistered(ResourcePlan resourcePlan) {
		return resourcePlans.contains(resourcePlan);
	}
	
	protected boolean isRegistered(DiscoveryPlan discoveryPlan) {
		return discoveryPlans.contains(discoveryPlan);
	}
	
	public CrawlPlan registerResourcePlan(ResourcePlan resourcePlan){
		resourcePlans.add(resourcePlan);
		return this;
	}
	
	public CrawlPlan registerDiscoveryPlan(DiscoveryPlan discoveryPlan){
		discoveryPlans.add(discoveryPlan);
		return this;
	}
	
	public CrawlPlan registerSeedSource(ResourcePlan resourcePlan, Object source){
		if(!isRegistered(resourcePlan)){
			throw new IllegalArgumentException("Cannot register a seed source with an unregistered ResourcePlan object");
		}
		if(!seedSources.containsKey(resourcePlan)){
			seedSources.put(resourcePlan, new HashSet<Object>());
		}
		seedSources.get(resourcePlan).add(source);
		return this;
	}
	
	public CrawlPlan putContextObject(String key, Object value){
		initialContextObjects.put(key, value);
		return this;
	}

	public int getMaxDepth() {
		return maxDepth;
	}

	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	public int getMaxPages() {
		return maxPages;
	}

	public void setMaxPages(int maxPages) {
		this.maxPages = maxPages;
	}

	public Set<ResourcePlan> getResourcePlans() {
		return resourcePlans;
	}

	public Set<DiscoveryPlan> getDiscoveryPlans() {
		return discoveryPlans;
	}

	public Map<ResourcePlan, Set<Object>> getSeedSources() {
		return seedSources;
	}

	public CrawlTool getCrawlTool() {
		return crawlTool;
	}

	public void setCrawlTool(CrawlTool crawlTool) {
		this.crawlTool = crawlTool;
	}
	public synchronized CrawlContext generateContext(){
		return new CrawlContext(this);
	}
	
}
