package crawling.discovery.execution;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.util.concurrent.RateLimiter;

import crawling.discovery.entities.Resource;
import crawling.discovery.entities.ResourceId;
import crawling.discovery.planning.PreResource;
import crawling.discovery.planning.ResourceFetchTool;
import crawling.discovery.planning.ResourcePlan;
import crawling.discovery.planning.ResourcePreOrder;
import newwork.WorkStatus;

public class ResourceContext extends Context{
	
	protected final CrawlContext crawlContext;
	protected final ResourceFetchTool fetchTool;
	protected final int numWorkers;
	
	
	
	protected final Set<PlanId> discoveryPlans = new HashSet<PlanId>();
	protected final Map<ResourceId, Resource> resources = new HashMap<ResourceId, Resource>();
	
	public ResourceContext(CrawlContext crawlContext, ResourcePlan resourcePlan){
		super(resourcePlan);
		this.fetchTool = resourcePlan.getFetchTool();
		this.crawlContext = crawlContext;
		this.discoveryPlans.addAll(resourcePlan.getDiscoveryPlans());
		this.numWorkers = resourcePlan.getNumWorkers();
//		System.out.println("resource context preorders : " + preOrders.size());
	}
	
	public Resource preGenerateResource(PreResource preResource, Resource parent) throws Exception{
		Resource resource = fetchTool.generateResource(preResource, parent, this.getNextResourceId(), this);
		this.addResource(resource);
		if(resource.getFetchStatus() != WorkStatus.UNASSIGNED){
			incrementNumResourcesCrawled();
		}
		return resource;
	}
	
	public Resource generateResource(Object source, Resource parent) throws Exception {
		Resource resource = fetchTool.generateResource(source, parent, this.getNextResourceId(), this);
		this.addResource(resource);
		return resource;
	}
	
	public void addResource(Resource resource){
		resources.put(resource.getResourceId(), resource);
		crawlContext.addResource(resource);
	}
	
	public Resource getResource(ResourceId resourceId) {
		return resources.get(resourceId);
	}
	
	public Set<Resource> getResources() {
		return new HashSet<Resource>(resources.values());
	}
	
	protected ResourceId getNextResourceId(){
		return crawlContext.getNextResourceId();
	}
	
	@Override
	public boolean acquireWorkPermit(){
		return crawlContext.acquireWorkPermit() 
				&& super.acquireWorkPermit(); 
	}
	
	@Override
	protected boolean approveWork(Resource resource){
		synchronized(permitMutex){
			//Must keep the short circuit or crawlcontext will record crawls as happening even if resourcecontext doesn't approve
			return super.approveWork(resource) && crawlContext.approveWork(resource);
		}
	}
	
	public CrawlContext getCrawlContext() {
		return crawlContext;
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

}
