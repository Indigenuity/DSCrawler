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
import crawling.discovery.entities.Resource;
import crawling.discovery.entities.ResourceId;
import crawling.discovery.planning.CrawlPlan;
import crawling.discovery.planning.CrawlTool;
import crawling.discovery.planning.DiscoveryPlan;
import crawling.discovery.planning.DiscoveryPoolPlan;
import crawling.discovery.planning.PreResource;
import crawling.discovery.planning.ResourcePlan;
import newwork.WorkStatus;

public class CrawlContext extends Context {
	
	
	private CrawlTool crawlTool;
	private IdGenerator idGenerator;
	
	protected final Map<PlanId, ResourceContext> resourceContexts = new HashMap<PlanId, ResourceContext>();
	protected final Map<PlanId, DiscoveryContext> discoveryContexts = new HashMap<PlanId, DiscoveryContext>();
	protected final Map<PlanId, DiscoveryPool> discoveryPools = new HashMap<PlanId, DiscoveryPool>();
	protected final Map<ResourceId, Resource> resources = new HashMap<ResourceId, Resource>();
	
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
//			System.out.println("generated resource context : " + resourcePlan.getPlanId());
		}
		for(DiscoveryPoolPlan poolPlan : crawlPlan.getDiscoveryPoolPlans()){
			discoveryPools.put(poolPlan.getPlanId(), poolPlan.generatePool(this));
//			System.out.println("generated discovery pool : " + poolPlan.getPlanId());
		}
		for(DiscoveryPlan discoveryPlan : crawlPlan.getDiscoveryPlans()){
			discoveryContexts.put(discoveryPlan.getPlanId(), discoveryPlan.generateContext(this));
//			System.out.println("generated discovery context : " + discoveryPlan.getPlanId());
		}
	}
	
	public Resource preGenerateResource(PreResource preResource, Resource parent) throws Exception{
		DiscoveryContext discoveryContext = getDiscoveryContext(preResource.getDiscoveredByPlanId());
		if(discoveryContext.preDiscover(preResource.getSource())){
			ResourceContext resourceContext = getResourceContext(preResource.getPlanId());
			Resource resource = resourceContext.preGenerateResource(preResource, parent);
			if(resource.getFetchStatus() != WorkStatus.UNASSIGNED){
				incrementNumResourcesCrawled();
			}
			return resource;
		}
		throw new IllegalStateException("Can't pregenerate resource with duplicate source : " + preResource.getSource());
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
	
	public Set<DiscoveryPool> getDiscoveryPools() {
		return new HashSet<DiscoveryPool>(discoveryPools.values());
	}
	
	public Set<Resource> getResources() {
		return new HashSet<Resource>(resources.values());
	}
	
	public void addResource(Resource resource){
		this.resources.put(resource.getResourceId(), resource);
	}
	
	public Resource getResource(ResourceId resourceId) {
		return resources.get(resourceId);
	}

	public CrawlTool getCrawlTool() {
		return crawlTool;
	}

	public void setCrawlTool(CrawlTool crawlTool) {
		this.crawlTool = crawlTool;
	}
	
}
