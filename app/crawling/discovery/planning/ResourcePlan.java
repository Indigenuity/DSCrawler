package crawling.discovery.planning;


import java.util.HashSet;
import java.util.Set;

import crawling.discovery.entities.Resource;
import crawling.discovery.execution.CrawlContext;
import crawling.discovery.execution.PlanId;
import crawling.discovery.execution.ResourceContext;

public abstract class ResourcePlan extends ContextPlan { 
	
	public final static int DEFAULT_NUM_WORKERS = 5;
	public final static int DEFAULT_MAX_DEPTH_OF_CRAWLING = Integer.MAX_VALUE;
	public final static int DEFAULT_MAX_PAGES_TO_FETCH = Integer.MAX_VALUE;
	
	protected int maxDepth = DEFAULT_MAX_DEPTH_OF_CRAWLING;
	protected int maxPages = DEFAULT_MAX_PAGES_TO_FETCH;
	protected int numWorkers = DEFAULT_NUM_WORKERS;
	
	protected final Set<PlanId> discoveryPlans = new HashSet<PlanId>();
	protected final Set<PreResource> resources = new HashSet<PreResource>();
	
	protected ResourceFetchTool fetchTool;
	
	public ResourcePlan(ResourceFetchTool fetchTool) {
		this.fetchTool = fetchTool;
	}
	
	public ResourcePlan() {
	}
	
	public synchronized ResourceContext generateContext(CrawlContext crawlContext) throws Exception{
		ResourceContext context =  new ResourceContext(crawlContext, this);
		return context;
	}
	
	public int getNumWorkers() {
		return numWorkers;
	}

	public void setNumWorkers(int numWorkers) {
		this.numWorkers = numWorkers;
	}

	public void registerDiscoveryPlan(DiscoveryPlan discoveryPlan){
		this.discoveryPlans.add(discoveryPlan.getPlanId());
	}
	
	public void registerDiscoveryPlan(PlanId planId){
		this.discoveryPlans.add(planId);
	}

	public ResourceFetchTool getFetchTool() {
		return fetchTool;
	}

	public <T extends ResourceFetchTool> void setFetchTool(T fetchTool) {
		this.fetchTool = fetchTool;
	}

	public Set<PlanId> getDiscoveryPlans() {
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

	public boolean addResource(PreResource resource){
		return this.resources.add(resource);
	}
	
	public Set<PreResource> getResources() {
		return resources;
	}
	
}
