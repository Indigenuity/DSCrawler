package crawling.discovery.execution;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import akka.actor.UntypedActor;
import crawling.discovery.entities.FlushableResource;
import crawling.discovery.entities.Resource;
import crawling.discovery.planning.DiscoveryPlan;
import crawling.discovery.planning.ResourceFetchTool;
import crawling.discovery.planning.ResourcePlan;
import newwork.WorkStatus;

public class ResourceWorker extends UntypedActor {
	
	protected final ResourceFetchTool fetchTool;
	
	/*****************  Stateful Fields *******************/
	protected final Set<Resource> resources = new HashSet<Resource>();
	protected final Set<DiscoveredSource> discoveredSources = new HashSet<DiscoveredSource>();
	protected ResourceWorkOrder workOrder;
	protected ResourceContext context;
	ResourceWorkResult workResult;
	/***************** End Stateful Fields ****************/
	
	public ResourceWorker(ResourceFetchTool fetchTool){
		this.fetchTool = fetchTool;
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		clearState();
		establishState((ResourceWorkOrder)message);
		try{
			preFetch();
			fetch();
			postFetch();
			
		} catch(NoCrawlPermitException e){
			sendNoCrawl();
		} catch(Exception e) {
			sendError(e);
		}
	}
	
	protected void preFetch() throws Exception{
		fetchTool.preFetch(workOrder, context);
		if(!context.acquireCrawlPermit()){
			throw new NoCrawlPermitException();
		}
	}
	
	protected void fetch() throws Exception{
		resources.addAll(fetchTool.fetchResource(workOrder, context));
		resources.stream().forEach((resource) -> {resource.setParent(workOrder.getParent());});
		workResult.addResources(resources);
	}
	
	protected void postFetch() throws Exception {
		fetchTool.postFetch(workResult, context);
		makeDiscoveries();
		fetchTool.postDiscovery(workResult, context);
		flushResources();
		sendResult();
	}
	
	protected void flushResources(){
		for(Resource resource : resources){
			if(resource instanceof FlushableResource){
				((FlushableResource)resource).flush();
			}
		}
	}
	
	protected void makeDiscoveries() throws Exception{
		for(PlanReference reference : context.getDiscoveryPlans()){
			DiscoveryContext discoveryContext = context.getDiscoveryContext(reference);
			for(Resource resource : resources){
				workResult.addDiscoveredSources(discoveryContext.getDiscoveryTool().discover(resource, discoveryContext));
			}
		}
	}
	
	protected void sendResult(){
		workResult.setWorkStatus(WorkStatus.COMPLETE);
		getSender().tell(workResult, getSelf());
	}
	
	protected void sendError(Exception e){
		workResult.setException(e);
		workResult.setWorkStatus(WorkStatus.ERROR);
		getSender().tell(workResult, getSelf());
	}
	
	protected void sendNoCrawl(){
		workResult.setWorkStatus(WorkStatus.NOT_STARTED);
		getSender().tell(workResult, getSelf());
	}
	
	protected void clearState() {
		this.resources.clear();
		this.discoveredSources.clear();
		this.workOrder = null;
		this.workResult = null;
		this.context = null;
	}
	
	protected void establishState(ResourceWorkOrder workOrder){
		this.workOrder = workOrder;
		this.workResult = new ResourceWorkResult(workOrder);
		this.context = workOrder.getResourceContext();
	}

}
