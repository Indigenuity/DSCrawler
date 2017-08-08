package crawling.discovery.execution;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import akka.actor.UntypedActor;
import crawling.discovery.control.CrawlUtil;
import crawling.discovery.entities.DiscoveredSource;
import crawling.discovery.entities.FlushableResource;
import crawling.discovery.entities.Resource;
import crawling.discovery.entities.ResourceId;
import crawling.discovery.planning.DiscoveryPlan;
import crawling.discovery.planning.FetchTool;
import crawling.discovery.planning.ResourcePlan;
import crawling.discovery.planning.ResourceTool;
import newwork.WorkStatus;

public class ResourceWorker extends UntypedActor {
	
	protected final ResourceTool resourceTool;
	protected final ResourceContext resourceContext;
	protected final CrawlContext crawlContext;
	protected final Set<DiscoveryContext> discoveryContexts = new HashSet<DiscoveryContext>();
	
	/*****************  Stateful Fields *******************/
	protected ResourceWorkOrder workOrder;
	protected ResourceWorkResult workResult;
	protected Resource resource;
	
	
	/***************** End Stateful Fields ****************/
	
	public ResourceWorker(ResourceContext resourceContext){
		this.resourceContext = resourceContext;
		this.resourceTool = resourceContext.getResourceTool();
		this.crawlContext = resourceContext.getCrawlContext();
		for(DiscoveryContext discoveryContext : this.crawlContext.getDiscoveryContexts(resourceContext.getPlanId())){
			discoveryContexts.add(discoveryContext);
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
			resourceTool.onFetchError(resource, crawlContext, e);
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
			resourceTool.onDiscoveryError(resource, crawlContext, e);
			throw e;
		}
	}
	
	protected void getFetchApproval() {
		if(!crawlContext.approveFetch(resource)){
			throw new NoCrawlPermitException();
		}
	}
	
	protected void preFetch() throws Exception{
		resource.setFetchStatus(WorkStatus.STARTED);
		resourceTool.beforeFetch(resource, crawlContext);
		getFetchApproval();
	}
	
	protected void fetch() throws Exception{
		Object value = resourceTool.fetchValue(resource, crawlContext);
		resourceTool.assignValue(resource, value, crawlContext);
	}
	
	protected void postFetch() throws Exception {
		resourceTool.afterFetch(resource, crawlContext);
		resource.setFetchStatus(WorkStatus.COMPLETE);
	}
	
	protected void preDiscovery() throws Exception {
		if(!CrawlUtil.readyForDiscovery(resource)){
			throw new NotReadyForDiscoveryException();
		}
		resourceTool.beforeDiscovery(resource, crawlContext);
		resource.setDiscoveryStatus(WorkStatus.STARTED);
	}
	
	protected void discovery() throws Exception {
		for(DiscoveryContext discoveryContext : discoveryContexts){
			for(DiscoveredSource source : discoveryContext.discoverSources(resource)){
				Resource child = crawlContext.generateResource(source, resource);
				resourceTool.onDiscovery(child, crawlContext);
				CrawlUtil.flush(child);
			}
		}
	}
	
	protected void postDiscovery() throws Exception {
		resourceTool.afterDiscovery(resource, crawlContext);
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
		this.resource = crawlContext.getResource(workOrder.getResourceId());
		this.workOrder = workOrder;
		this.workResult = new ResourceWorkResult(workOrder);
		this.workResult.setWorkStatus(WorkStatus.STARTED);
	}
}
