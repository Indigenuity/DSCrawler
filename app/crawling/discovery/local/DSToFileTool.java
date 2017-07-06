package crawling.discovery.local;

import java.io.File;
import java.net.URI;

import org.jsoup.nodes.Document;

import crawling.discovery.entities.Resource;
import crawling.discovery.entities.ResourceId;
import crawling.discovery.execution.ResourceContext;
import crawling.discovery.html.HttpResponseFile;
import crawling.discovery.html.HttpToFileTool;
import crawling.discovery.planning.PreResource;
import datadefinitions.inventory.InvType;
import datadefinitions.inventory.InvUtils;
import persistence.PageCrawl;
import sites.utilities.PageCrawlLogic;

public class DSToFileTool extends HttpToFileTool {
	
	private boolean pagedInventory = false;
	public DSToFileTool(boolean pagedInventory){
		this.pagedInventory = pagedInventory;
	}
	
	@Override
	public Object fetchValue(Resource resource, ResourceContext context) throws Exception {
		HttpResponseFile responseFile = (HttpResponseFile)super.fetchValue(resource, context);
		DSResponseFile dsResponseFile = new DSResponseFile(responseFile);
		if(resource.getValue() != null){
			dsResponseFile.setPageCrawlId(((DSResponseFile)resource.getValue()).getPageCrawlId());
		}
		dsResponseFile.setInventory(pagedInventory);
		return dsResponseFile;
	}
	
}
