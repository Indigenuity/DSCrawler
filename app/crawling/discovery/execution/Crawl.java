package crawling.discovery.execution;

import java.util.Collection;
import java.util.List;

import crawling.discovery.entities.Resource;
import crawling.discovery.planning.CrawlPlan;
import crawling.discovery.planning.PrimaryResourcePlan;

public class Crawl implements Resource {
	
	
	
	QueueMap queueMap = new QueueMap();
	CrawlPlan plan;

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
