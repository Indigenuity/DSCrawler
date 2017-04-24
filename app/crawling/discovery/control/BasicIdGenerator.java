package crawling.discovery.control;

import java.util.concurrent.atomic.AtomicLong;

import crawling.discovery.entities.BasicResourceId;
import crawling.discovery.entities.ResourceId;

public class BasicIdGenerator implements IdGenerator {
	protected AtomicLong resourceIdIndex = new AtomicLong(0);
	
	@Override
	public ResourceId generateId() {
		return new BasicResourceId(resourceIdIndex.incrementAndGet());
	}

}
