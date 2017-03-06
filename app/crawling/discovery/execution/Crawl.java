package crawling.discovery.execution;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import crawling.discovery.entities.Resource;
import crawling.discovery.planning.CrawlPlan;
import crawling.discovery.planning.ResourceHandler;

public class Crawl {
	
	
	
	
	CrawlPlan plan;
	
	Map<ResourceHandler<?, ?>, List<?>> resultLists = new LinkedHashMap<ResourceHandler<?,?>, List<?>>();

	public Crawl(CrawlPlan plan) {
		this.plan = plan;
	}
	
	public void start(){
		plan.getStartPlan().accept(this);
	}
	
	public <R extends Resource<?>> void persistResults(ResourceHandler<?, R> resourcePlan, List<R> results){
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
	public Map<ResourceHandler<?, ?>, List<?>> getResultLists(){
		return resultLists;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
