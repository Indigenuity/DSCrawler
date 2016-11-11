package crawling.discovery.planning;

import java.util.ArrayList;
import java.util.List;

import crawling.discovery.entities.Endpoint;

public class PrimaryResourcePlan<T> extends ResourcePlan<Endpoint, T> {
	
	
	protected final List<ResourcePlan<T, ?>> derivedResourcePlans = new ArrayList<ResourcePlan<T, ?>>();
	
	public PrimaryResourcePlan(DerivationStrategy<Endpoint, T> derivationStrategy, PersistStrategy<T> persistStrategy) {
		super(derivationStrategy, persistStrategy);
	}

	public List<ResourcePlan<T, ?>> getDerivedResourcePlans() {
		List<ResourcePlan<T, ?>> returned = new ArrayList<ResourcePlan<T, ?>>();
		returned.addAll(derivedResourcePlans);
		return returned;
	}
	
	public PrimaryResourcePlan<T> addDerivedResourcePlan(ResourcePlan<T, ?> resourcePlan){
		this.derivedResourcePlans.add(resourcePlan);
		return this;
	}

	
}
