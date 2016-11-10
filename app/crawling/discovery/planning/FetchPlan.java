package crawling.discovery.planning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import crawling.discovery.entities.Endpoint;

public class FetchPlan<V extends Endpoint, T> {

	protected final FetchStrategy<V, T> fetchStrategy;
	protected final List<ResourcePlan<T, ?>> resourcePlans = new ArrayList<ResourcePlan<T, ?>>();
	
	public FetchPlan(FetchStrategy<V, T> fetchStrategy) {
		super();
		this.fetchStrategy = fetchStrategy;
	}

	public FetchStrategy<V, T> getFetchStrategy() {
		return fetchStrategy;
	}
	
	public FetchPlan<V, T> addResourcePlan(ResourcePlan<T, ?> resourcePlan){
		resourcePlans.add(resourcePlan);
		return this;
	}

	public List<ResourcePlan<T, ?>> getResourcePlans() {
		return Collections.unmodifiableList(resourcePlans);
	}
	
	
	
	
	
}
