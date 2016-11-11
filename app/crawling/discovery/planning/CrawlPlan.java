package crawling.discovery.planning;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import crawling.discovery.execution.Crawl;

public class CrawlPlan {

//	protected List<FetchPlan<?, ?>> fetchPlans = new ArrayList<FetchPlan<?, ?>>();
	
	protected final List<PrimaryResourcePlan<?>> resourcePlans = new ArrayList<PrimaryResourcePlan<?>>();
	protected final Consumer<Crawl> startPlan;
	
	public CrawlPlan(){
		this.startPlan = (crawl) -> {};
	}
	
	public CrawlPlan(Consumer<Crawl> startPlan) {
		this.startPlan = startPlan;
	}
	
	public List<PrimaryResourcePlan<?>> getResourcePlans(){
		List<PrimaryResourcePlan<?>> returned = new ArrayList<PrimaryResourcePlan<?>>();
		returned.addAll(resourcePlans);
		return returned;
	}
	
	public CrawlPlan addResourcePlan(PrimaryResourcePlan<?> resourcePlan) {
		this.resourcePlans.add(resourcePlan);
		return this;
	}

	public Consumer<Crawl> getStartPlan() {
		return startPlan;
	}
	
}
