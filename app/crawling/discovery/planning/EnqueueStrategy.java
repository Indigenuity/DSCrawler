package crawling.discovery.planning;

import java.util.function.Consumer;

import crawling.discovery.entities.Endpoint;
import crawling.discovery.entities.Resource;
import crawling.discovery.execution.Crawl;
import crawling.discovery.execution.FetchQueue;
import crawling.discovery.execution.ResourceRequest;

public class EnqueueStrategy<T> implements PersistStrategy<Endpoint> {

	private PrimaryResourcePlan<T> plan;
	private EnqueueStrategy(PrimaryResourcePlan<T> plan) {
		this.plan = plan;
	}
	
	public void accept(Endpoint endpoint, Resource resource) {
		ResourceRequest<T> request = new ResourceRequest<T>(endpoint, resource);
		Crawl crawl = resource.getRoot();
		FetchQueue<T> fetchQueue = crawl.getFetchQueue(plan);
		fetchQueue.add(request);
	}
	
	public static <T> EnqueueStrategy<T> put(PrimaryResourcePlan<T> queue){
		return new EnqueueStrategy<T>(queue); 
	}
	
	public Consumer<Crawl> asStartPlan(Endpoint endpoint, Resource resource){
		return (crawl) -> {
			this.accept(endpoint, resource);
		};
	}
}
