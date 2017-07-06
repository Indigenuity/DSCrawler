package crawling.discovery.local;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;

import crawling.discovery.entities.Resource;
import crawling.discovery.execution.DiscoveryContext;
import crawling.discovery.planning.DiscoveryTool;
import datadefinitions.inventory.InvType;
import datadefinitions.inventory.InvUtils;
import datadefinitions.inventory.InventoryTool;
import persistence.PageCrawl;
import persistence.SiteCrawl;
import play.db.jpa.JPA;
import sites.utilities.PageCrawlLogic;

public class InventoryDiscoveryTool implements DiscoveryTool{

	@Override
	public Set<Object> discover(Resource parent, DiscoveryContext context) throws Exception {
		Set<Object> discoveredSources = new HashSet<Object>();
		DSResponseFile responseFile = (DSResponseFile) parent.getValue();
		generateInventoryData(responseFile);
		if(isCrawlableInventoryPage(responseFile, context)){
//			System.out.println("Discovering inventory sources from resource at source : " + parent.getSource());
			URI nextPageLink = getNextPageLink(responseFile.getInvType(), responseFile.getDocument(), responseFile.getUri());
			if(nextPageLink != null && !StringUtils.isEmpty(nextPageLink.toString()) && context.discover(nextPageLink)){
				discoveredSources.add(nextPageLink);
			}	
		}
		return discoveredSources;
	}
	
	protected void generateInventoryData(DSResponseFile responseFile) throws Exception{
		if(!responseFile.getFile().exists()){
			return;
		}
		Document doc = responseFile.getDocument();
		URI currentUri = responseFile.getUri();
		
		InvType invType = InvUtils.getInvType(doc, currentUri);
		if(invType != null){
//			System.out.println("detected invtype : " + invType);
			responseFile.setNewRoot(invType.getTool().isNewRoot(currentUri));
			responseFile.setUsedRoot(invType.getTool().isUsedRoot(currentUri));
			responseFile.setInvType(invType);
		}
	}
	
	protected URI getNextPageLink(InvType invType, Document doc, URI currentUri){
		InventoryTool tool = invType.getTool();
		return tool.getNextPageLink(doc, currentUri);
	}
	
	protected boolean isCrawlableInventoryPage(DSResponseFile responseFile, DiscoveryContext context){
		responseFile.setDiscoveredNewRoot(responseFile.isNewRoot() && context.discoverContextObject("newRoot", responseFile));
		responseFile.setDiscoveredUsedRoot(responseFile.isUsedRoot() && context.discoverContextObject("usedRoot", responseFile));
		return responseFile.isDiscoveredNewRoot()
				|| responseFile.isDiscoveredUsedRoot()
				|| responseFile.isInventory();
	}

}
