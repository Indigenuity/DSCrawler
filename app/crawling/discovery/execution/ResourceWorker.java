package crawling.discovery.execution;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import akka.actor.UntypedActor;
import crawling.discovery.control.CrawlUtil;
import crawling.discovery.entities.FlushableResource;
import crawling.discovery.entities.Resource;
import crawling.discovery.entities.ResourceId;
import crawling.discovery.planning.DiscoveryPlan;
import crawling.discovery.planning.ResourceFetchTool;
import crawling.discovery.planning.ResourcePlan;
import newwork.WorkStatus;

public class ResourceWorker extends UntypedActor {
	
	protected final ResourceFetchTool fetchTool;
	protected final ResourceContext context;
	protected final CrawlContext crawlContext;
	protected final Set<DiscoveryContext> discoveryContexts = new HashSet<DiscoveryContext>();
	
	/*****************  Stateful Fields *******************/
	protected ResourceWorkOrder workOrder;
	protected ResourceWorkResult workResult;
	protected Resource resource;
	
	
	/***************** End Stateful Fields ****************/
	
	public ResourceWorker(ResourceContext context){
		this.context = context;
		this.fetchTool = context.getFetchTool();
		this.crawlContext = context.getCrawlContext();
		for(PlanId planId : context.getDiscoveryPlans()){
			discoveryContexts.add(crawlContext.getDiscoveryContext(planId));
		}
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
//		System.out.println("Resource worker received work");
		establishState((ResourceWorkOrder)message);
		try{
			if(resource.getFetchStatus() == WorkStatus.ASSIGNED) {
				doFetchWork();
			}
			if(resource.getDiscoveryStatus() == WorkStatus.ASSIGNED){
				doDiscoveryWork();
			}
			workResult.setWorkStatus(WorkStatus.COMPLETE);
		} catch(NoCrawlPermitException | NotReadyForDiscoveryException e) {
			workResult.setWorkStatus(WorkStatus.ABORTED);
		} catch (Exception e){
			workResult.setException(e);
			workResult.setWorkStatus(WorkStatus.ERROR);
		} finally{
			flushResource();
		}
		finish();
	}
	
	protected void getWorkApproval() {
		if(!context.approveWork(resource) || !context.acquireWorkPermit()){
			throw new NoCrawlPermitException();
		}
	}

	protected void doFetchWork() throws Exception{
		try{
			preFetch();
			fetch();
			postFetch();
		} catch(NoCrawlPermitException e) {
//			System.out.println("no crawl permit : " + resource.getSource());
			resource.setFetchStatus(WorkStatus.ABORTED);
			throw e;
		} catch(Exception e) {
			resource.setFetchException(e);
			resource.setFetchStatus(WorkStatus.ERROR);
			fetchTool.onFetchError(resource, context, e);
			throw e;
		}
	}
	
	
	
	protected void doDiscoveryWork() throws Exception{
		try{
			preDiscovery();
			discovery();
			postDiscovery();
		} catch(NotReadyForDiscoveryException e) {
			resource.setDiscoveryStatus(WorkStatus.ABORTED);
			throw e;
		} catch(Exception e){
			resource.setDiscoveryException(e);
			resource.setDiscoveryStatus(WorkStatus.ERROR);
			fetchTool.onDiscoveryError(resource, context, e);
			throw e;
		}
	}
	
	protected void preFetch() throws Exception{
		resource.setFetchStatus(WorkStatus.STARTED);
		fetchTool.preFetch(resource, context);
		getWorkApproval();
	}
	
	protected void fetch() throws Exception{
		Object value = fetchTool.fetchValue(resource, context);
		resource.setValue(value);
	}
	
	protected void postFetch() throws Exception {
		fetchTool.postFetch(resource, context);
		resource.setFetchStatus(WorkStatus.COMPLETE);
	}
	
	protected void preDiscovery() throws Exception {
		if(!CrawlUtil.readyForDiscovery(resource)){
			throw new NotReadyForDiscoveryException();
		}
		resource.setDiscoveryStatus(WorkStatus.STARTED);
	}
	
	protected void discovery() throws Exception {
		for(DiscoveryContext discoveryContext : discoveryContexts){
			ResourceContext resourceContext = crawlContext.getResourceContext(discoveryContext.getDestinationPlanId());
			for(Object source : discoveryContext.discoverSources(resource)) {
//				System.out.println("Discovered from resource : " + resource.getResourceId() + " source : " + source);
				Resource child = resourceContext.generateResource(source, resource);
				CrawlUtil.flush(child);
			}
		}
	}
	
	protected void postDiscovery() throws Exception {
		fetchTool.postDiscovery(resource, context);
		resource.setDiscoveryStatus(WorkStatus.COMPLETE);
	}
	
	protected void flushResource(){
		try{
			CrawlUtil.flush(resource);
		}catch(Exception e) {
			System.out.println("***********************  Could not flush Resource! : " + resource);
			//TODO come up with better strategy than to swallow this exception
		}
	}
	
	protected void finish() {
		flushResource();
		getSender().tell(workResult, getSelf());
		clearState();
	}
	
	protected void clearState() {
		this.resource = null;
		this.workOrder = null;
		this.workResult = null;
	}
	
	protected void establishState(ResourceWorkOrder workOrder){
		clearState();
		this.resource = context.getResource(workOrder.getResourceId());
		this.workOrder = workOrder;
		this.workResult = new ResourceWorkResult(workOrder);
		this.workResult.setWorkStatus(WorkStatus.STARTED);
	}
}
