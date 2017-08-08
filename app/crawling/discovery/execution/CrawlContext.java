package crawling.discovery.execution;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.util.concurrent.RateLimiter;

import crawling.discovery.control.IdGenerator;
import crawling.discovery.entities.BasicResourceId;
import crawling.discovery.entities.DiscoveredSource;
import crawling.discovery.entities.Resource;
import crawling.discovery.entities.ResourceId;
import crawling.discovery.planning.CrawlPlan;
import crawling.discovery.planning.CrawlTool;
import crawling.discovery.planning.DiscoveryPlan;
import crawling.discovery.planning.DiscoveryPoolPlan;
import crawling.discovery.planning.PreResource;
import crawling.discovery.planning.ResourcePlan;
import newwork.WorkStatus;

public class CrawlContext extends ContextWithResources {
	
	
	private CrawlTool crawlTool;
	private IdGenerator idGenerator;
	
	protected final Map<PlanId, ResourceContext> resourceContexts = new HashMap<PlanId, ResourceContext>();
	protected final Map<PlanId, DiscoveryContext> discoveryContexts = new HashMap<PlanId, DiscoveryContext>();
	protected final Map<PlanId, Set<DiscoveryContext>> resourceToDiscoveryMap = new HashMap<PlanId, Set<DiscoveryContext>>();
	
	protected final Map<PlanId, DiscoveryPool> discoveryPools = new HashMap<PlanId, DiscoveryPool>();
	
	protected final Map<Resource, PlanId> resourceTypes = new HashMap<Resource, PlanId>();
	
	public CrawlContext(CrawlPlan crawlPlan) throws Exception{
		super(crawlPlan);
		this.crawlTool = crawlPlan.getCrawlTool();
		this.idGenerator = crawlPlan.getIdGenerator();
		generateContextsAndPools(crawlPlan);
//		System.out.println("maxPages in crawlcontext : " + getMaxPages());
	}
	
	protected void generateContextsAndPools(CrawlPlan crawlPlan) throws Exception{
		for(ResourcePlan resourcePlan : crawlPlan.getResourcePlans()){
			resourceContexts.put(resourcePlan.getPlanId(), resourcePlan.generateContext(this));
			resourceToDiscoveryMap.put(resourcePlan.getPlanId(), new HashSet<DiscoveryContext>());
//			System.out.println("generated resource context : " + resourcePlan.getPlanId());
		}
		for(DiscoveryPoolPlan poolPlan : crawlPlan.getDiscoveryPoolPlans()){
			discoveryPools.put(poolPlan.getPlanId(), poolPlan.generatePool(this));
//			System.out.println("generated discovery pool : " + poolPlan.getPlanId());
		}
		for(DiscoveryPlan discoveryPlan : crawlPlan.getDiscoveryPlans()){
			DiscoveryContext discoveryContext = discoveryPlan.generateContext(this);
			discoveryContexts.put(discoveryPlan.getPlanId(), discoveryContext);
			resourceToDiscoveryMap.get(discoveryPlan.getSourceResourcePlanId()).add(discoveryContext);
//			System.out.println("generated discovery context : " + discoveryPlan.getPlanId());
		}
	}
	
	public Resource preGenerateResource(PreResource preResource, Resource parent) throws Exception{
		synchronized(resourceMutex){
			DiscoveryContext discoveryContext = getDiscoveryContext(preResource.getDiscoveredByPlanId());
			if(!discoveryContext.preDiscover(preResource.getSource())){
				throw new IllegalStateException("Can't pregenerate resource with duplicate source ( " + preResource.getSource() + " ) in DiscoveryContext : " + discoveryContext.getName() + " (PlanId " + discoveryContext.getPlanId() + ")");
			}
			ResourceContext resourceContext = getResourceContext(preResource.getPlanId());
			Resource resource = resourceContext.getResourceTool().generateResource(preResource, parent, this.getNextResourceId(), this);
			addResource(resource, resourceContext);
			return resource;
		}
	}
	
	public Resource generateResource(DiscoveredSource source, Resource parent) throws Exception{
		synchronized(resourceMutex){
			ResourceContext resourceContext = resourceContexts.get(source.getResourcePlanId());
			Resource resource = resourceContext.getResourceTool().generateResource(source.getSource(), parent, getNextResourceId(), this);
			addResource(resource, resourceContext);
			return resource;
		}
	}
	
	@Override
	public void addResource(Resource resource){
		throw new UnsupportedOperationException("Cannot add Resource to CrawlContext via 'addResource(Resource)'.  Instead use 'generateResource(DiscoveredSource, Resource)'");
	}
	
	private void addResource(Resource resource, ResourceContext resourceContext){
		synchronized(resourceMutex){
			super.addResource(resource);
			this.resources.put(resource.getResourceId(), resource);
			assignResourceType(resource, resourceContext.getPlanId());
			resourceContext.addResource(resource);
		}
	}
	
	@Override
	public void removeResource(Resource resource){
		throw new UnsupportedOperationException("Cannot remove Resources from CrawlContext");
	}
	
	@Override
	protected boolean approveFetch(Resource resource){
		synchronized(resourceMutex){
			ResourceContext resourceContext = getResourceContext(resource.getResourceId());
			if(isApprovableFetch(resource) && acquireFetchRatePermit() && resourceContext.approveFetch(resource)){
				markFetched(resource);
				return true;
			}
			return false;
		}
	}
	
	public void changeResourceType(Resource resource, PlanId destinationResourcePlanId){
		synchronized(resourceMutex){
			PlanId currentResourcePlanId = resourceTypes.get(resource);
			ResourceContext currentResourceContext = resourceContexts.get(currentResourcePlanId);
			ResourceContext destinationResourceContext = resourceContexts.get(destinationResourcePlanId);
			currentResourceContext.removeResource(resource);
			destinationResourceContext.addResource(resource);
			resourceTypes.put(resource, destinationResourcePlanId);
		}
	}
	
	protected ResourceContext getResourceContext(ResourceId resourceId){
		synchronized(resourceMutex){
			return resourceContexts.get(
					resourceTypes.get(
					resources.get(resourceId)));
		}
	}
	
	public PlanId getResourcePlanId(ResourceId resourceId){
		synchronized(resourceMutex){
			return resourceContexts.get(
					resourceTypes.get(
					resources.get(resourceId)))
					.getPlanId();
		}
	}
	
	public PlanId getResourceType(ResourceId resourceId){
		synchronized(resourceMutex){
			return resourceTypes.get(
					resources.get(resourceId));
		}
	}
	
	public ResourceId getNextResourceId(){
		return idGenerator.generateId();
	}
	
	public ResourceContext getResourceContext(PlanId planId){
		return resourceContexts.get(planId);
	}
	
	public DiscoveryContext getDiscoveryContext(PlanId planId){
		return discoveryContexts.get(planId);
	}
	
	public DiscoveryPool getDiscoveryPool(PlanId planId){
		return discoveryPools.get(planId);
	}
	
	public Set<ResourceContext> getResourceContexts() {
		return new HashSet<ResourceContext>(resourceContexts.values());
	}

	public Set<DiscoveryContext> getDiscoveryContexts() {
		return new HashSet<DiscoveryContext>(discoveryContexts.values());
	}
	
	public Set<DiscoveryContext> getDiscoveryContexts(PlanId resourcePlanId) {
		return new HashSet<DiscoveryContext>(resourceToDiscoveryMap.get(resourcePlanId));
	}
	
	public Set<DiscoveryPool> getDiscoveryPools() {
		return new HashSet<DiscoveryPool>(discoveryPools.values());
	}
	
	public Set<Resource> getResources() {
		return new HashSet<Resource>(resources.values());
	}
	
	public Resource getResource(ResourceId resourceId) {
		return resources.get(resourceId);
	}
	
	private PlanId assignResourceType(Resource resource, PlanId resourcePlanId){
		return resourceTypes.put(resource, resourcePlanId);
	}

	public CrawlTool getCrawlTool() {
		return crawlTool;
	}

	public void setCrawlTool(CrawlTool crawlTool) {
		this.crawlTool = crawlTool;
	}
	
}
