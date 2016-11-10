package crawling.discovery.execution;

import akka.actor.UntypedActor;
import crawling.discovery.entities.Endpoint;
import crawling.discovery.entities.Resource;
import crawling.discovery.planning.ResourcePlan;

public class FetchWorker<T extends Resource> extends UntypedActor{

	@Override
	public void onReceive(Object message) throws Exception {

		@SuppressWarnings("unchecked")
		FetchWorkOrder<T> workOrder = (FetchWorkOrder<T>) message;
		Endpoint endpoint = workOrder.getRequest().getEndpoint();
		Resource parent = workOrder.getRequest().getParent();
		T primaryResource = workOrder.getPlan().getDerivationStrategy().apply(endpoint);
		workOrder.getPlan().getPersistStrategy().accept(primaryResource, parent);
		
		for(ResourcePlan<T, ?> derivedPlan : workOrder.getPlan().getDerivedResourcePlans()){
			applyPlan(primaryResource, derivedPlan);
		}
		
	}
	
	protected <R> void applyPlan(T primaryResource, ResourcePlan<T, R> derivedPlan){
		R derivedResource = derivedPlan.getDerivationStrategy().apply(primaryResource);
		derivedPlan.getPersistStrategy().accept(derivedResource, primaryResource);
	}

}
