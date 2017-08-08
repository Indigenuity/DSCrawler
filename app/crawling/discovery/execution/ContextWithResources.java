package crawling.discovery.execution;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.util.concurrent.RateLimiter;

import crawling.discovery.entities.Resource;
import crawling.discovery.entities.ResourceId;
import crawling.discovery.planning.ContextPlan;
import crawling.discovery.planning.ContextWithResourcesPlan;

public class ContextWithResources extends Context{
	
	protected final int maxDepth;
	protected final int maxFetches;
	protected final RateLimiter rateLimiter;
	
	protected final Object resourceMutex = new Object();
	
	protected final Map<ResourceId, Resource> resources = new ConcurrentHashMap<ResourceId, Resource>();
	protected final Set<Resource> fetchedResources = new HashSet<Resource>();

	public ContextWithResources(ContextWithResourcesPlan contextPlan) {
		super(contextPlan);
		this.rateLimiter = contextPlan.getRateLimiter();
		this.maxDepth = contextPlan.getMaxDepth();
		this.maxFetches = contextPlan.getMaxFetches();
	}
	
	public void addResource(Resource resource){
		synchronized(resourceMutex){
			resources.put(resource.getResourceId(), resource);
			if(!resource.getFetchStatus().isWorkPending()){
				markFetched(resource);
			}
		}
	}
	
	public void removeResource(Resource resource){
		synchronized(resourceMutex){
			resources.remove(resource.getResourceId());
			markUnfetched(resource);
		}
	}
	
	public boolean acquireFetchRatePermit(){
		if(rateLimiter != null){
			rateLimiter.acquire();
		}
		return true;
	}
	
	protected boolean isApprovableFetch(Resource resource){
		synchronized(resourceMutex){
			return !maxResourcesFetched() && !isOverMaxDepth(resource);
		}
	}
	
	protected boolean approveFetch(Resource resource){
		synchronized(resourceMutex){
			if(isApprovableFetch(resource) && acquireFetchRatePermit()){
				markFetched(resource);
				return true;
			}
			return false;
		}
	}
	
	public Resource getResource(ResourceId resourceId) {
		synchronized(resourceMutex){
			return resources.get(resourceId);
		}
	}
	
	public Set<Resource> getResources() {
		synchronized(resourceMutex){
			return new HashSet<Resource>(resources.values());
		}
	}

	public int getNumResourcesFetched(){
		synchronized(resourceMutex){
			return fetchedResources.size();
		}
	}
	
	public boolean markFetched(Resource resource){
		synchronized(resourceMutex){
			return resources.containsKey(resource) && fetchedResources.add(resource);
		}
	}
	
	public boolean markUnfetched(Resource resource){
		synchronized(resourceMutex){
			return fetchedResources.remove(resource);
		}
	}

	public boolean maxResourcesFetched(){
		synchronized(resourceMutex){
			return getNumResourcesFetched() >= maxFetches;
		}
	}
	
	public boolean isMaxDepth(Resource resource) {
		if(resource == null){
			return false;
		}
		return resource.getDepth() >= getMaxDepth();
	}
	
	public boolean isOverMaxDepth(Resource resource) {
		if(resource == null){
			return false;
		}
		return resource.getDepth() > getMaxDepth();
	}
	
	public int getMaxDepth() {
		return maxDepth;
	}

	public int getMaxFetches() {
		return maxFetches;
	}
}
