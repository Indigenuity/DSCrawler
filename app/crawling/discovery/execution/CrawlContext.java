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
import crawling.discovery.planning.ResourcePlan;

public class CrawlContext extends Context {
	
	
	private CrawlTool crawlTool;
	private IdGenerator idGenerator;
	
	protected final Map<PlanId, ResourceContext> resourceContexts = new HashMap<PlanId, ResourceContext>();
	protected final Map<PlanId, DiscoveryContext> discoveryContexts = new HashMap<PlanId, DiscoveryContext>();
	
	public CrawlContext(CrawlPlan crawlPlan){
		this.contextObjects.putAll(crawlPlan.getInitialContextObjects());
		this.rateLimiter = crawlPlan.getRateLimiter();
		this.maxDepth = crawlPlan.getMaxDepth();
		this.setMaxPages(crawlPlan.getMaxPages());
		this.crawlTool = crawlPlan.getCrawlTool();
		this.idGenerator = crawlPlan.getIdGenerator();
		generateContexts(crawlPlan);
//		System.out.println("maxPages in crawlcontext : " + getMaxPages());
	}
	
	protected void generateContexts(CrawlPlan crawlPlan){
		for(ResourcePlan resourcePlan : crawlPlan.getResourcePlans()){
			resourceContexts.put(resourcePlan.getPlanId(), resourcePlan.generateContext(this));
		}
		for(DiscoveryPlan discoveryPlan : crawlPlan.getDiscoveryPlans()){
			discoveryContexts.put(discoveryPlan.getPlanId(), discoveryPlan.generateContext(this));
		}
	}
	
	public ResourceContext getResourceContext(PlanId planId){
		return resourceContexts.get(planId);
	}
	
	public DiscoveryContext getDiscoveryContext(PlanId planId){
		return discoveryContexts.get(planId);
	}
	
	public Set<ResourceContext> getResourceContexts() {
		return new HashSet<ResourceContext>(resourceContexts.values());
	}

	public Set<DiscoveryContext> getDiscoveryContexts() {
		return new HashSet<DiscoveryContext>(discoveryContexts.values());
	}

	public ResourceId getNextResourceId(){
		return idGenerator.generateId();
	}
	
	public CrawlTool getCrawlTool() {
		return crawlTool;
	}

	public void setCrawlTool(CrawlTool crawlTool) {
		this.crawlTool = crawlTool;
	}
	
}
