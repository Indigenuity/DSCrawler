package crawling.discovery.planning;


import java.util.HashSet;
import java.util.Set;

import crawling.discovery.execution.CrawlContext;
import crawling.discovery.execution.PlanReference;
import crawling.discovery.execution.ResourceContext;

public abstract class ResourcePlan extends Plan {
	
	public final static int DEFAULT_NUM_WORKERS = 5;
	public final static int DEFAULT_MAX_DEPTH_OF_CRAWLING = Integer.MAX_VALUE;
	public final static int DEFAULT_MAX_PAGES_TO_FETCH = Integer.MAX_VALUE;
	
	protected int maxDepth = DEFAULT_MAX_DEPTH_OF_CRAWLING;
	protected int maxPages = DEFAULT_MAX_PAGES_TO_FETCH;
	protected int numWorkers = DEFAULT_NUM_WORKERS;
	
	protected final Set<PlanReference> discoveryPlans = new HashSet<PlanReference>();
	
	protected ResourceFetchTool fetchTool;
	
	public ResourcePlan(ResourceFetchTool fetchTool) {
		this.fetchTool = fetchTool;
	}
	
	public ResourcePlan() {
	}
	
	public synchronized ResourceContext generateContext(CrawlContext crawlContext){
		return new ResourceContext(crawlContext, this);
	}
	
	public int getNumWorkers() {
		return numWorkers;
	}

	public void setNumWorkers(int numWorkers) {
		this.numWorkers = numWorkers;
	}

	public void registerDiscoveryPlan(DiscoveryPlan discoveryPlan){
		this.discoveryPlans.add(discoveryPlan.getPlanReference());
	}
	
	public void registerDiscoveryPlan(PlanReference reference){
		this.discoveryPlans.add(reference);
	}

	public ResourceFetchTool getFetchTool() {
		return fetchTool;
	}

	public <T extends ResourceFetchTool> void setFetchTool(T fetchTool) {
		this.fetchTool = fetchTool;
	}

	public Set<PlanReference> getDiscoveryPlans() {
		return discoveryPlans;
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
	public Object putContextObject(String key, Object value){
		synchronized(initialContextObjects){
			return initialContextObjects.put(key, value);
		}
	}
}
