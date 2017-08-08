package crawling.discovery.results;

import java.util.HashSet;
import java.util.Set;

import crawling.discovery.entities.Resource;
import crawling.discovery.execution.PlanId;
import crawling.discovery.execution.ResourceContext;
import crawling.discovery.execution.ResourceWorkResult;
import newwork.WorkStatus;

public class ResourceReport {
	
	protected final int numResourcesCrawled;
	protected final PlanId planId;
	
//	protected final Set<ResourceWorkResult> results = new HashSet<ResourceWorkResult>();
//	protected final Set<ResourceWorkResult> notStartedResults = new HashSet<ResourceWorkResult>();
//	protected final Set<ResourceWorkResult> errorResults = new HashSet<ResourceWorkResult>();
//	protected final Set<ResourceWorkResult> completeResults = new HashSet<ResourceWorkResult>();
//	protected final Set<ResourceWorkResult> incompleteResults = new HashSet<ResourceWorkResult>();
	
//	protected final Set<Resource> rootResources = new HashSet<Resource>();
	
	protected final Set<Resource> resources = new HashSet<Resource>();
	
	public ResourceReport(ResourceContext context) {
//		for(ResourceWorkResult result : context.getResults()){
//			results.add(result);
//			if(result.getWorkStatus() == WorkStatus.UNASSIGNED){
//				notStartedResults.add(result);
//			} else if(result.getWorkStatus() == WorkStatus.ERROR){
//				errorResults.add(result);
//			} else if(result.getWorkStatus() == WorkStatus.COMPLETE){
//				completeResults.add(result);
//			} else if(result.getWorkStatus() == WorkStatus.STARTED){
//				incompleteResults.add(result);
//			} 
//		}
		
		this.numResourcesCrawled = context.getNumResourcesFetched();
//		this.rootResources.addAll(context.getRootResources());
		this.resources.addAll(context.getResources());
		this.planId = context.getPlanId();
	}

	public int getNumResourcesCrawled() {
		return numResourcesCrawled;
	}

//	public Set<ResourceWorkResult> getResults() {
//		return results;
//	}
//
//	public Set<ResourceWorkResult> getNotStartedResults() {
//		return notStartedResults;
//	}
//
//	public Set<ResourceWorkResult> getErrorResults() {
//		return errorResults;
//	}
//
//	public Set<ResourceWorkResult> getCompleteResults() {
//		return completeResults;
//	}
//
//	public Set<ResourceWorkResult> getIncompleteResults() {
//		return incompleteResults;
//	}
//
//	public Set<Resource> getRootResources() {
//		return rootResources;
//	}

	public PlanId getPlanId() {
		return planId;
	}

	public Set<Resource> getResources() {
		return resources;
	}

}
