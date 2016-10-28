package crawling.discovery;

public class EnqueueStrategy implements PersistStrategy<Endpoint> {

	private FetchQueue queue;
	private EnqueueStrategy(FetchQueue queue) {
		this.queue = queue;
	}
	
	public void accept(Endpoint endpoint) {
		this.queue.add(endpoint);
	}
	
	public static EnqueueStrategy put(FetchQueue queue){
		return new EnqueueStrategy(queue); 
	}
}
