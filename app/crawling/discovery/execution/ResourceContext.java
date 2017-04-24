package crawling.discovery.execution;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.util.concurrent.RateLimiter;

import crawling.discovery.entities.Resource;
import crawling.discovery.entities.ResourceId;
import crawling.discovery.planning.ResourceFetchTool;
import crawling.discovery.planning.ResourcePlan;
import crawling.discovery.planning.ResourcePreOrder;

public class ResourceContext extends Context{
	
	protected final CrawlContext crawlContext;
	protected final ResourceFetchTool fetchTool;
	protected final int numWorkers;
	protected final PlanId planId;
	
	
	
	protected final Set<ResourceWorkResult> results = new HashSet<ResourceWorkResult>();
	protected final Set<PlanId> discoveryPlans = new HashSet<PlanId>();
	protected final Set<Resource> rootResources = new HashSet<Resource>();
	protected final Set<ResourcePreOrder> preOrders = new HashSet<ResourcePreOrder>();
	protected final Set<Resource> resources = new HashSet<Resource>();
	
	public ResourceContext(CrawlContext crawlContext, ResourcePlan resourcePlan){
		this.contextObjects.putAll(resourcePlan.getInitialContextObjects());
		this.rateLimiter = resourcePlan.getRateLimiter();
		this.fetchTool = resourcePlan.getFetchTool();
		this.crawlContext = crawlContext;
		this.discoveryPlans.addAll(resourcePlan.getDiscoveryPlans());
		this.numWorkers = resourcePlan.getNumWorkers();
		this.planId = resourcePlan.getPlanId();
		this.maxDepth = resourcePlan.getMaxDepth();
		this.maxPages = resourcePlan.getMaxPages();
		this.preOrders.addAll(resourcePlan.getPreOrders());
		this.resources.addAll(resourcePlan.getResources());
//		System.out.println("resource context preorders : " + preOrders.size());
	}
	
	public ResourceId getNextResourceId(){
		return crawlContext.getNextResourceId();
	}
	
	public Resource generateResource(Object source, Object value, Resource parent){
		return new Resource(source, value, parent, getNextResourceId(), this.planId);
	}
	
	public void storeWorkResult(ResourceWorkResult workResult){
		this.results.add(workResult);
		for(Resource resource : workResult.getResources()){
			checkAndMarkRoot(resource);
		}
	}
	
	protected boolean checkAndMarkRoot(Resource resource){
		if(resource.getParent() == null){
			return rootResources.add(resource);
		}
		return false;
	}
	
	@Override
	public boolean acquireWorkPermit(){
		return crawlContext.acquireWorkPermit() 
				&& super.acquireWorkPermit(); 
	}
	
	@Override
	protected boolean approveWork(ResourceWorkOrder workOrder){
		synchronized(permitMutex){
			//Must keep the short circuit or crawlcontext will record crawls as happening even if resourcecontext doesn't approve
			return super.approveWork(workOrder) && crawlContext.approveWork(workOrder);
		}
	}
	
	@Override
	public Object getContextObject(String key){
		synchronized(contextObjects){
			if(contextObjects.containsKey(key)){
				return contextObjects.get(key);
			}
			return crawlContext.getContextObject(key);
		}
	}
	
	
	public CrawlContext getCrawlContext() {
		return crawlContext;
	}

	public Set<ResourceWorkResult> getResults() {
		return results;
	}

	public Set<PlanId> getDiscoveryPlans() {
		return new HashSet<PlanId>(discoveryPlans);
	}
	
	public ResourceContext getResourceContext(PlanId planId){
		return crawlContext.getResourceContext(planId);
	}
	
	public DiscoveryContext getDiscoveryContext(PlanId planId){
		return crawlContext.getDiscoveryContext(planId);
	}

	public ResourceFetchTool getFetchTool() {
		return fetchTool;
	}

	public int getNumWorkers() {
		return numWorkers;
	}

	public PlanId getPlanId() {
		return planId;
	}

	public Set<Resource> getRootResources() {
		return rootResources;
	}

	public Set<ResourcePreOrder> getPreOrders() {
		return new HashSet<ResourcePreOrder>(preOrders);
	}
	
}
