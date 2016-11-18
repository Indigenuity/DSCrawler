package crawling.discovery.execution;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import crawling.discovery.entities.Resource;
import crawling.discovery.planning.CrawlPlan;
import crawling.discovery.planning.PrimaryResourcePlan;
import crawling.discovery.planning.ResourcePlan;

public class Crawl implements Resource {
	
	
	
	QueueMap queueMap = new QueueMap();
	CrawlPlan plan;
	
	Map<ResourcePlan<?, ?>, List<?>> resultLists = new LinkedHashMap<ResourcePlan<?,?>, List<?>>();

	public Crawl(CrawlPlan plan) {
		this.plan = plan;
		for(PrimaryResourcePlan<?> resourcePlan : plan.getResourcePlans()){
			generateFetchQueue(resourcePlan);
		}
	}
	
	public void start(){
		plan.getStartPlan().accept(this);
	}
	
	public FetchQueue<?> getReadyQueue(){
		for(FetchQueue<?> queue : queueMap.getQueues()){
			if(!queue.queueIsEmpty()){
				return queue;
			}
		}
		return null;
	}
	
	public Collection<FetchQueue<?>> getQueues(){
		return queueMap.getQueues();
	}
	
	public <R> void persistResults(ResourcePlan<?, R> resourcePlan, List<R> results){
		Objects.requireNonNull(resourcePlan);
		Objects.requireNonNull(results);
		synchronized(resultLists){
			
			@SuppressWarnings("unchecked")
			List<R> current = (List<R>) this.resultLists.get(resourcePlan);
			if(current == null){
				this.resultLists.put(resourcePlan, results);
			} else {
				current.addAll(results);
			}
		}
	}
	
	// TODO this is entirely not threadsafe, especially with the Worker system
	public Map<ResourcePlan<?, ?>, List<?>> getResultLists(){
		return resultLists;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private <T> void generateFetchQueue(PrimaryResourcePlan<T> resourcePlan){
		FetchQueue<T> fetchQueue = new FetchQueue<T>(resourcePlan);
		queueMap.put(resourcePlan, fetchQueue);
	}
	
	public <T> FetchQueue<T> getFetchQueue(PrimaryResourcePlan<T> resourcePlan) {
		return queueMap.get(resourcePlan);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource getParent() {
		return null;
	}

	@Override
	public List<List<Resource>> getChildResourceLists() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Crawl getRoot(){
		return this;
	}
	
}
