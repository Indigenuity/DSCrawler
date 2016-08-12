package experiment.crawling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class QueueCrawl {
	
	protected Map<Class<?>, Queue<?>> queues = new HashMap<Class<?>, Queue<?>>();
	
	
	public <T> void addQueue(Class<T> clazz, Queue<T> queue){
		queues.put(clazz, queue);
	}

}
