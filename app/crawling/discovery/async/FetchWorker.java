package crawling.discovery.async;

import akka.actor.UntypedActor;
import crawling.discovery.entities.Endpoint;
import crawling.discovery.entities.PrimaryResource;
import crawling.discovery.entities.Resource;
import crawling.discovery.execution.ResourceRequest;
import crawling.discovery.planning.PrimaryResourcePlan;
import crawling.discovery.planning.ResourcePlan;

public class FetchWorker extends UntypedActor{

	
	@SuppressWarnings("unchecked")
	@Override
	public void onReceive(Object message) throws Exception {
		executeWorkOrder((FetchWorkOrder<? extends PrimaryResource>) message);
	}
	
	protected <T extends PrimaryResource> void executeWorkOrder(FetchWorkOrder<T> workOrder){
		PrimaryResourcePlan<T> primaryPlan = workOrder.getPlan();
		ResourceRequest<T> request = workOrder.getRequest();
		Endpoint endpoint = request.getEndpoint();
		Resource parent = request.getParent();
		
		T primaryResource = primaryPlan.getDerivationStrategy().apply(endpoint);
		workOrder.getPlan().getPersistStrategy().accept(primaryResource, parent);
		
		for(ResourcePlan<T, ?> derivedPlan : workOrder.getPlan().getDerivedResourcePlans()){
			executeDerivedPlan(derivedPlan, primaryResource);
		}
	}
	
	protected <T extends Resource, R> void executeDerivedPlan(ResourcePlan<T, R> derivedPlan, T parent){
		R derivedResource = derivedPlan.getDerivationStrategy().apply(parent);
		derivedPlan.getPersistStrategy().accept(derivedResource, parent);
	}
	
	

}
