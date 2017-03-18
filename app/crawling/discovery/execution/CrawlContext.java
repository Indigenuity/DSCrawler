package crawling.discovery.execution;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.util.concurrent.RateLimiter;

import crawling.discovery.entities.BasicResourceId;
import crawling.discovery.entities.Resource;
import crawling.discovery.entities.ResourceId;
import crawling.discovery.planning.CrawlPlan;
import crawling.discovery.planning.CrawlTool;
import crawling.discovery.planning.DiscoveryPlan;
import crawling.discovery.planning.ResourcePlan;

public class CrawlContext extends Context {
	
	
	private int maxDepth;
	private int maxPages;
	private CrawlTool crawlTool;
	
	protected int numResourcesCrawled = 0;
	
	protected AtomicLong resourceIdIndex = new AtomicLong(0);
	
	protected final Map<PlanReference, ResourceContext> resourceContexts = new HashMap<PlanReference, ResourceContext>();
	protected final Map<PlanReference, DiscoveryContext> discoveryContexts = new HashMap<PlanReference, DiscoveryContext>();
	
	public CrawlContext(CrawlPlan crawlPlan){
		this.contextObjects.putAll(crawlPlan.getInitialContextObjects());
		this.rateLimiter = crawlPlan.getRateLimiter();
		this.maxDepth = crawlPlan.getMaxDepth();
		this.maxPages = crawlPlan.getMaxPages();
		this.crawlTool = crawlPlan.getCrawlTool();
		generateContexts(crawlPlan);
	}
	
	protected void generateContexts(CrawlPlan crawlPlan){
		for(ResourcePlan resourcePlan : crawlPlan.getResourcePlans()){
			resourceContexts.put(resourcePlan.getPlanReference(), resourcePlan.generateContext(this));
		}
		for(DiscoveryPlan discoveryPlan : crawlPlan.getDiscoveryPlans()){
			discoveryContexts.put(discoveryPlan.getPlanReference(), discoveryPlan.generateContext(this));
		}
	}
	
	public ResourceContext getResourceContext(PlanReference planReference){
		return resourceContexts.get(planReference);
	}
	
	public DiscoveryContext getDiscoveryContext(PlanReference planReference){
		return discoveryContexts.get(planReference);
	}
	
	public Set<ResourceContext> getResourceContexts() {
		return new HashSet<ResourceContext>(resourceContexts.values());
	}

	public Set<DiscoveryContext> getDiscoveryContexts() {
		return new HashSet<DiscoveryContext>(discoveryContexts.values());
	}

	public ResourceId getNextResourceId(){
		return new BasicResourceId(resourceIdIndex.incrementAndGet());
	}
	
	public Object getContextObject(String key){
		synchronized(contextObjects){
			return contextObjects.get(key);
		}
	}
	
	public Object putContextObject(String key, Object value){
		synchronized(contextObjects){
			return contextObjects.put(key, value);
		}
	}
	
	public boolean contextContains(String key){
		synchronized(contextObjects){
			return contextObjects.containsKey(key);
		}
	}
	
	protected boolean generateCrawlPermit(){
		synchronized(permitMutex){
			if(numResourcesCrawled >= maxPages){
				return false;
			}
			numResourcesCrawled++;
		}
		return true;
	}
	
	public boolean qualifyParent(Resource parent) {
		return !isMaxDepth(parent);
	}
	
	public boolean maxResourcesReached(){
		return numResourcesCrawled >= maxPages;
	}
	
	public boolean isMaxDepth(Resource parent) {
		if(parent == null){
			return false;
		}
		return parent.getDepth() >= getMaxDepth();
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

	public void setRateLimiter(RateLimiter rateLimiter) {
		this.rateLimiter = rateLimiter;
	}

	public CrawlTool getCrawlTool() {
		return crawlTool;
	}

	public void setCrawlTool(CrawlTool crawlTool) {
		this.crawlTool = crawlTool;
	}
	
}
