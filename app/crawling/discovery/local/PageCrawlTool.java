package crawling.discovery.local;

import java.net.URI;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.nodes.Document;

import crawling.discovery.entities.Resource;
import crawling.discovery.entities.ResourceId;
import crawling.discovery.execution.CrawlContext;
import crawling.discovery.execution.ResourceContext;
import crawling.discovery.execution.ResourceWorkOrder;
import crawling.discovery.html.HttpResponseFile;
import crawling.discovery.html.HttpToFileTool;
import crawling.discovery.planning.PreResource;
import datadefinitions.inventory.InvType;
import datadefinitions.inventory.InvUtils;
import newwork.WorkStatus;
import persistence.PageCrawl;
import persistence.SiteCrawl;
import play.db.jpa.JPA;
import sites.crawling.SiteCrawlLogic;
import sites.utilities.PageCrawlLogic;
import utilities.DSFormatter;
import utilities.HttpUtils;

public class PageCrawlTool extends HttpToFileTool{

	private boolean pagedInventory = false;
	public PageCrawlTool(boolean pagedInventory){
		this.pagedInventory = pagedInventory;
	}
	
	@Override
	public void postFetch(Resource resource, ResourceContext context) throws Exception {
	}
	
	public void assignParent(Resource parent, Set<Object> sources){
		if(parent != null){
			PageCrawl parentCrawl = (PageCrawl)parent.getValue();
		}
	}
	
	@Override
	public Resource generateResource(Object source, Resource parent, ResourceId resourceId, ResourceContext context) throws Exception{
		PageCrawl[] pageCrawls = new PageCrawl[1];
		JPA.withTransaction(() -> {
//			PageCrawl pageCrawl = generatePageCrawl((URI)source, context);
//			JPA.em().persist(pageCrawl);
//			assignSiteCrawlRelationships(pageCrawl, context);
//			assignParentRelationships(pageCrawl, parent);
//			pageCrawls[0] = pageCrawl;
		});
		
		PageCrawlResource resource = new PageCrawlResource(source, pageCrawls[0], parent, resourceId, context.getPlanId());
		return resource;
	}
	
	protected PageCrawl generatePageCrawl(URI uri) throws Throwable{
		PageCrawl pageCrawl = new PageCrawl();
		pageCrawl.setUrl(uri.toString());
		pageCrawl.setPagedInventory(this.pagedInventory);
		return pageCrawl;
	}
	
	protected void assignParentRelationships(PageCrawl pageCrawl, Resource parent){
		if(parent != null){
			PageCrawl parentPage = (PageCrawl)parent.getValue();
			pageCrawl.setParentPage(parentPage);
//			pageCrawl.setInvType(parentPage.getInvType());
		}
	}
	
	@Override
	public Resource generateResource(PreResource preResource, Resource parent, ResourceId resourceId , ResourceContext context) throws Exception{
		if(preResource.getValue() == null){
			return generateResource(preResource.getSource(), parent, resourceId, context);	//Uncrawled source
		}
		PageCrawlResource pageCrawlResource = new PageCrawlResource(preResource,
				parent,
				resourceId,
				context.getPlanId());
		JPA.withTransaction(() -> {
			generateInventoryData(pageCrawlResource);
		});
		return pageCrawlResource;
	}
	
	@Override
	public Object fetchValue(Resource resource, ResourceContext context) throws Exception { 
		HttpResponseFile responseFile = (HttpResponseFile)super.fetchValue(resource, context);
		PageCrawlResource pageCrawlResource = (PageCrawlResource)resource;
		PageCrawl[] pageCrawls = new PageCrawl[1];
		
		
		JPA.withTransaction(() -> {
			pageCrawls[0] = pageCrawlResource.getPageCrawl();
			PageCrawlLogic.clearResults(pageCrawls[0]);
			recordResults(responseFile, pageCrawls[0]);
			generateInventoryData(pageCrawlResource);
			pageCrawls[0] = JPA.em().merge(pageCrawls[0]);
		});
		
		return pageCrawls[0];
	}
	
	protected void recordResults(HttpResponseFile responseFile, PageCrawl pageCrawl){
		pageCrawl.setStatusCode(responseFile.getStatusCode());
		pageCrawl.setFilename(responseFile.getFile().getAbsolutePath());
		if(responseFile.getRedirectedUri() != null){
			pageCrawl.setRedirectedUrl(responseFile.getRedirectedUri().toString());
		}
		pageCrawl.setCrawlDate(new Date());
	}
	
	protected void generateInventoryData(PageCrawlResource pageCrawlResource) throws Exception{
		PageCrawl pageCrawl = pageCrawlResource.getPageCrawl();
		if(!PageCrawlLogic.fileExists(pageCrawl)){
			return;
		}
		Document doc = PageCrawlLogic.getDocument(pageCrawl);
		URI currentUri = pageCrawl.getUri();
		
		InvType invType = InvUtils.getInvType(doc, currentUri);
		if(invType != null){
//			System.out.println("detected invtype : " + invType);
			pageCrawl.setNewPath(invType.getTool().isNewPath(currentUri));
			pageCrawl.setNewRoot(invType.getTool().isNewRoot(currentUri));
			pageCrawl.setUsedPath(invType.getTool().isUsedPath(currentUri));
			pageCrawl.setUsedRoot(invType.getTool().isUsedRoot(currentUri));
			pageCrawl.setInvType(invType);
		}
		assignSiteCrawlInventoryRelationships(pageCrawl);
	}
	
	protected void assignSiteCrawlInventoryRelationships(PageCrawl pageCrawl){
		SiteCrawl siteCrawl = pageCrawl.getSiteCrawl();
		if(pageCrawl.getNewRoot() && siteCrawl.getNewInventoryRoot() == null){
			siteCrawl.setNewInventoryRoot(pageCrawl);
		}
		if(pageCrawl.getUsedRoot() && siteCrawl.getUsedInventoryRoot() == null){
			siteCrawl.setUsedInventoryRoot(pageCrawl);
		}
	}
	
	@Override
	public void afterDiscovery(Resource parent, Set<Resource> children, ResourceContext context){
		JPA.withTransaction(() -> {
			Object siteCrawlId = context.getCrawlContext().get("siteCrawlId");
			SiteCrawl siteCrawl = JPA.em().find(SiteCrawl.class, siteCrawlId);
			for(Resource child : children){
				siteCrawl.addPageCrawl((PageCrawl)child.getValue());
				
			}
			
		});
	}
	
	@Override
	public void onFetchError(Resource resource, ResourceContext context, Exception e){
		PageCrawlResource pageCrawlResource = (PageCrawlResource)resource;
		
		JPA.withTransaction(() -> {
			PageCrawl pageCrawl = pageCrawlResource.getPageCrawl();
//			PageCrawlLogic.clearResults(pageCrawl);
			pageCrawl.setStatusCode(null);
			pageCrawl.setErrorMessage(DSFormatter.toString(resource.getFetchException()));
			JPA.em().merge(pageCrawl);
		});
	}
	
	@Override
	public void onDiscoveryError(Resource parent, ResourceContext context, Exception e){
		PageCrawlResource pageCrawlResource = (PageCrawlResource)parent;
		
		JPA.withTransaction(() -> {
			PageCrawl pageCrawl = pageCrawlResource.getPageCrawl();
//			PageCrawlLogic.clearResults(pageCrawl);
			pageCrawl.setStatusCode(null);
			pageCrawl.setErrorMessage(DSFormatter.toString(parent.getDiscoveryException()));
			JPA.em().merge(pageCrawl);
		});
	}

}
