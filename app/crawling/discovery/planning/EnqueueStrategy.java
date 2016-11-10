package crawling.discovery.planning;

import crawling.discovery.entities.Endpoint;
import crawling.discovery.entities.Resource;
import crawling.discovery.execution.FetchQueue;
import crawling.discovery.execution.ResourceRequest;

public class EnqueueStrategy<T> implements PersistStrategy<Endpoint> {

	private FetchQueue<T> queue;
	private EnqueueStrategy(FetchQueue<T> queue) {
		this.queue = queue;
	}
	
	public void accept(Endpoint endpoint, Resource parent) {
		ResourceRequest<T> request = new ResourceRequest<T>(endpoint, parent);
		this.queue.add(request);
	}
	
	public static <T> EnqueueStrategy<T> put(FetchQueue<T> queue){
		return new EnqueueStrategy<T>(queue); 
	}
}
