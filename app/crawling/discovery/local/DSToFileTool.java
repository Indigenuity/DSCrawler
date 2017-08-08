package crawling.discovery.local;

import java.io.File;
import java.net.URI;

import org.jsoup.nodes.Document;

import crawling.discovery.entities.Resource;
import crawling.discovery.entities.ResourceId;
import crawling.discovery.execution.CrawlContext;
import crawling.discovery.execution.DiscoveryContext;
import crawling.discovery.execution.ResourceContext;
import crawling.discovery.html.HttpResponseFile;
import crawling.discovery.html.HttpToFileTool;
import crawling.discovery.planning.PreResource;
import datadefinitions.inventory.InvType;
import datadefinitions.inventory.InvUtils;
import newwork.WorkStatus;
import persistence.PageCrawl;
import sites.utilities.PageCrawlLogic;

public class DSToFileTool extends HttpToFileTool {
	
	private boolean pagedInventory = false;
	public DSToFileTool(boolean pagedInventory){
		this.pagedInventory = pagedInventory;
	}
	
	@Override
	public void afterFetch(Resource resource, CrawlContext context) throws Exception{
		
	}
	
	@Override
	public Resource generateResource(Object source, Resource parent, ResourceId resourceId, CrawlContext context) throws Exception{
		PageCrawlResource resource = new PageCrawlResource(source, parent, resourceId);
		resource.setInventory(pagedInventory);
		return resource;
	}
	
	@Override
	public Resource generateResource(PreResource preResource, Resource parent, ResourceId resourceId , CrawlContext context) throws Exception{
		PageCrawlResource resource = new PageCrawlResource((PageCrawlPreResource)preResource, parent, resourceId);
		resource.setInventory(pagedInventory);
		return resource;
	}
	
	@Override
	public void beforeDiscovery(Resource resource, CrawlContext context) throws Exception{
		refreshInventoryData((PageCrawlResource)resource, context);
	}
	
	public static void refreshInventoryData(PageCrawlResource resource, CrawlContext context) throws Exception{
		HttpResponseFile responseFile = resource.getResponseFile();
		URI currentUri = responseFile.getUri();
		Document doc = null;
		if(resource != null && responseFile.getFile().exists()){
			doc = responseFile.getDocument();
		}
		
		InvType invType = InvUtils.detectInvType(doc, currentUri);
		if(invType != null){
//			System.out.println("detected invtype : " + invType);
			resource.setNewRoot(invType.getTool().isNewRoot(currentUri));
			resource.setUsedRoot(invType.getTool().isUsedRoot(currentUri));
			resource.setGeneralRoot(invType.getTool().isGeneralRoot(currentUri));
			resource.setInvType(invType);
		}
		
		/*
		 * Ensure there is always at most one newRoot and usedRoot Resource for the crawl, and that those Resources are actually roots 
		 */
		if(resource.isNewRoot()){
			context.discoverContextObject("newRootResourceId", resource.getResourceId());
		} else {
			context.remove("newRootResourceId", resource.getResourceId());
		}
		
		if(resource.isUsedRoot()){
			context.discoverContextObject("usedRootResourceId", resource.getResourceId());
		} else {
			context.remove("usedRootResourceId", resource.getResourceId());
		}
	}
	
	
}
