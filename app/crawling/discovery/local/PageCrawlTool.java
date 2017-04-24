package crawling.discovery.local;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.nodes.Document;

import crawling.discovery.entities.Resource;
import crawling.discovery.execution.CrawlContext;
import crawling.discovery.execution.ResourceContext;
import crawling.discovery.execution.ResourceWorkOrder;
import crawling.discovery.html.HttpResponseFile;
import crawling.discovery.html.HttpToFileTool;
import datadefinitions.inventory.InvType;
import datadefinitions.inventory.InvUtils;
import persistence.PageCrawl;
import persistence.SiteCrawl;
import play.db.jpa.JPA;
import sites.crawling.SiteCrawlLogic;

public class PageCrawlTool extends HttpToFileTool{

	private boolean pagedInventory = false;
	public PageCrawlTool(boolean pagedInventory){
		this.pagedInventory = pagedInventory;
	}
	
	@Override
	public Set<Object> fetchResources(ResourceWorkOrder workOrder, ResourceContext context) throws Exception { 
		Set<Object> responseFiles = super.fetchResources(workOrder, context);
		Set<Object> pageCrawls = new HashSet<Object>();
		for(Object responseFile : responseFiles){
			JPA.withTransaction(() -> {
				PageCrawl pageCrawl =generatePageCrawl((HttpResponseFile)responseFile, context);
				assignParentRelationships(pageCrawl, workOrder.getParent());
				assignSiteCrawlRelationships(pageCrawl, context);
				pageCrawls.add(pageCrawl);
			});
				
		}
		return pageCrawls;
	}
	
	protected boolean isInventoryContext(ResourceContext context){
		return context.getPlanId().equals(context.getCrawlContext().getContextObject("inventoryPlanId"));
	}
	
	protected PageCrawl generatePageCrawl(HttpResponseFile responseFile, ResourceContext context) throws Throwable{
		PageCrawl pageCrawl = toPageCrawl(responseFile);
		JPA.em().persist(pageCrawl);
		return pageCrawl;
	}
	
	protected void assignSiteCrawlRelationships(PageCrawl pageCrawl, ResourceContext context){
		SiteCrawl siteCrawl = JPA.em().find(SiteCrawl.class, context.getCrawlContext().getContextObject("siteCrawlId"));
		PageCrawl oldPageCrawl = SiteCrawlLogic.getPageCrawlByUrl(pageCrawl.getUrl(), siteCrawl);
		if(oldPageCrawl != null){
			SiteCrawlLogic.replaceAndRemove(oldPageCrawl, pageCrawl);
		}
		siteCrawl.addPageCrawl(pageCrawl);
//		pageCrawl.setSiteCrawl(siteCrawl);
		if(pageCrawl.getNewRoot() && siteCrawl.getNewInventoryRoot() == null){
			siteCrawl.setNewInventoryRoot(pageCrawl);
		}
		if(pageCrawl.getUsedRoot() && siteCrawl.getUsedInventoryRoot() == null){
			siteCrawl.setUsedInventoryRoot(pageCrawl);
		}
		
		if(isInventoryContext(context)){
			pageCrawl.setPagedInventory(true);
		}
	}
	
	protected void assignParentRelationships(PageCrawl pageCrawl, Resource parent){
		if(parent != null){
			PageCrawl parentPage = (PageCrawl)parent.getValue();
			pageCrawl.setParentPage(parentPage);
//			pageCrawl.setInvType(parentPage.getInvType());
		}
	}
	
	protected PageCrawl toPageCrawl(HttpResponseFile responseFile) throws Exception{
		PageCrawl pageCrawl = new PageCrawl();
		pageCrawl.setPagedInventory(this.pagedInventory);
		
		copyGeneralData(responseFile, pageCrawl);
		copyInventoryData(responseFile, pageCrawl);
		
		return pageCrawl;
	}
	
	protected void copyGeneralData(HttpResponseFile responseFile, PageCrawl pageCrawl){
		pageCrawl.setStatusCode(responseFile.getStatusCode());
		pageCrawl.setUrl(responseFile.getUri().toString());
		pageCrawl.setFilename(responseFile.getFile().getAbsolutePath());
		if(responseFile.getRedirectedUri() != null){
			pageCrawl.setRedirectedUrl(responseFile.getRedirectedUri().toString());
		}
		
	}
	
	protected void copyInventoryData(HttpResponseFile responseFile, PageCrawl pageCrawl) throws Exception{
		Document doc = responseFile.getDocument();
		URI currentUri = responseFile.getUri();
		doc.setBaseUri(currentUri.toString());
		InvType invType = InvUtils.getInvType(doc, responseFile.getUri());
		if(invType != null){
//			System.out.println("detected invtype : " + invType);
			pageCrawl.setNewPath(invType.getTool().isNewPath(responseFile.getUri()));
			pageCrawl.setNewRoot(invType.getTool().isNewRoot(responseFile.getUri()));
			pageCrawl.setUsedPath(invType.getTool().isUsedPath(responseFile.getUri()));
			pageCrawl.setUsedRoot(invType.getTool().isUsedRoot(responseFile.getUri()));
			pageCrawl.setInvType(invType);
		}
	}

}
