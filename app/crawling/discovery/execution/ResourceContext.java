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

public class ResourceContext extends Context{
	
	protected final CrawlContext crawlContext;
	protected final ResourceFetchTool fetchTool;
	protected final int numWorkers;
	protected final PlanReference planReference;
	private int maxDepth;
	private int maxPages;
	
	protected int numResourcesCrawled = 0;
	
	protected final Set<Object> sources = new HashSet<Object>();
	protected final Set<Object> uncrawledSources = new HashSet<Object>();
	protected final Map<Object, Exception> failedSources = new HashMap<Object, Exception>();
	protected final Set<ResourceWorkResult> results = new HashSet<ResourceWorkResult>();
	protected final Set<PlanReference> discoveryPlans = new HashSet<PlanReference>();
	
	public ResourceContext(CrawlContext crawlContext, ResourcePlan resourcePlan){
		this.contextObjects.putAll(resourcePlan.getInitialContextObjects());
		this.rateLimiter = resourcePlan.getRateLimiter();
		this.fetchTool = resourcePlan.getFetchTool();
		this.crawlContext = crawlContext;
		this.discoveryPlans.addAll(resourcePlan.getDiscoveryPlans());
		this.numWorkers = resourcePlan.getNumWorkers();
		this.planReference = resourcePlan.getPlanReference();
		this.maxDepth = resourcePlan.getMaxDepth();
		this.maxPages = resourcePlan.getMaxPages();
	}
	
	public void markUncrawled(Object source){
		this.uncrawledSources.add(source);
	}
	
	public void markFailed(Object source, Exception e){
		this.failedSources.put(source, e);
	}
	
	public ResourceId getNextResourceId(){
		return crawlContext.getNextResourceId();
	}
	
	public Object getContextObject(String key){
		synchronized(contextObjects){
			if(contextObjects.containsKey(key)){
				return contextObjects.get(key);
			}
			return crawlContext.getContextObject(key);
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
	
	protected boolean discoverSource(Object source){
		synchronized(sources){
			return sources.add(source);
		}
	}
	
	public boolean acquireCrawlPermit(){
		return crawlContext.acquireCrawlPermit() && super.acquireCrawlPermit(); 
	}

	public void storeWorkResult(ResourceWorkResult workResult){
		this.results.add(workResult);
	}

	public CrawlContext getCrawlContext() {
		return crawlContext;
	}

	public void setRateLimiter(RateLimiter rateLimiter) {
		this.rateLimiter = rateLimiter;
	}

	public Set<Object> getSources() {
		return sources;
	}

	public Set<ResourceWorkResult> getResults() {
		return results;
	}

	public Set<PlanReference> getDiscoveryPlans() {
		return new HashSet<PlanReference>(discoveryPlans);
	}
	
	public ResourceContext getResourceContext(PlanReference planReference){
		return crawlContext.getResourceContext(planReference);
	}
	
	public DiscoveryContext getDiscoveryContext(PlanReference planReference){
		return crawlContext.getDiscoveryContext(planReference);
	}

	public int getNumResourcesCrawled() {
		return numResourcesCrawled;
	}

	public void setNumResourcesCrawled(int numResourcesCrawled) {
		this.numResourcesCrawled = numResourcesCrawled;
	}

	public ResourceFetchTool getFetchTool() {
		return fetchTool;
	}

	public Set<Object> getUncrawledSources() {
		return new HashSet<Object>(uncrawledSources);
	}

	public int getNumWorkers() {
		return numWorkers;
	}

	public PlanReference getPlanReference() {
		return planReference;
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
}
