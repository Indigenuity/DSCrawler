package crawling.discovery.local;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import crawling.discovery.entities.DiscoveredSource;
import crawling.discovery.entities.Resource;
import crawling.discovery.execution.CrawlContext;
import crawling.discovery.execution.DiscoveryContext;
import crawling.discovery.execution.PlanId;
import crawling.discovery.html.HtmlResource;
import crawling.discovery.html.HtmlUtils;
import crawling.discovery.html.InternalLinkDiscoveryTool;
import datadefinitions.CrawlableIframeDomain;
import datadefinitions.inventory.InvType;
import datadefinitions.inventory.InvUtils;
import datadefinitions.inventory.InventoryTool;

public class DSDiscoveryTool extends InternalLinkDiscoveryTool{
	
	@Override
	public Set<DiscoveredSource> discover(Resource parent, DiscoveryContext discoveryContext) throws Exception {
		DSDiscoveryContext context = (DSDiscoveryContext) discoveryContext;
		PlanId inventoryPlanId = context.getInventoryPlanId();
		PlanId regularPlanId = context.getPrimaryDestinationResourcePlanId();
		PageCrawlResource resource = (PageCrawlResource)parent;
		
		Set<URI> links = findLinks(resource.getResponseFile());
		Set<URI> inventoryUris = findInventoryUris(resource, discoveryContext.getCrawlContext());
		inventoryUris.addAll(detectRootUris(links));
		links.removeAll(inventoryUris);
		
		Set<DiscoveredSource> sources = new HashSet<DiscoveredSource>();
		for(URI link : links){
			if(!StringUtils.isEmpty(link.toString())){
				sources.add(new DiscoveredSource(link, regularPlanId));
			}
		}
		for(URI uri : inventoryUris){
			if(!StringUtils.isEmpty(uri.toString())){
				sources.add(new DiscoveredSource(uri, inventoryPlanId));
			}
		}
		return sources;
	}
	
	protected Set<URI> detectRootUris(Set<URI> links){
		Set<URI> rootUris = new HashSet<URI>();
		for(URI uri : links){
			if(InvUtils.detectRootInvType(uri) != null){
				rootUris.add(uri);
			}
		}
		return rootUris;
	}
	
	protected Set<URI> findInventoryUris(PageCrawlResource resource, CrawlContext crawlContext) throws Exception{
		Set<URI> inventoryUris = new HashSet<URI>();
		if(shouldDiscoverInventoryPagination(resource, crawlContext)){
			URI nextPageLink = findInventoryNextPageLink(resource.getInvType(), resource.getResponseFile().getDocument(), resource.getUri());
			if(nextPageLink != null){
				inventoryUris.add(nextPageLink);
			}
		}
		URI redirectLink = findInventoryLinkFromRedirect(resource.getResponseFile());
		if(redirectLink != null){
			inventoryUris.add(redirectLink);
		}
		return inventoryUris;
	}
	
	protected URI findInventoryNextPageLink(InvType invType, Document doc, URI currentUri){
		InventoryTool tool = invType.getTool();
		return tool.getNextPageLink(doc, currentUri);
	}
	
	protected URI findInventoryLinkFromRedirect(HtmlResource htmlResource) throws Exception{
		if(InvUtils.detectRootInvType(htmlResource.getUri()) != null){
			return htmlResource.getUri();
		}
		return null;
	}

	protected boolean shouldDiscoverInventoryPagination(PageCrawlResource resource, CrawlContext crawlContext){
		return resource.isInventory()
				|| resource.isGeneralRoot()
				|| (resource.isNewRoot() && resource.getResourceId().equals(crawlContext.get("newRootResourceId")))
				|| (resource.isUsedRoot() && resource.getResourceId().equals(crawlContext.get("usedRootResourceId")));
	}

	@Override
	protected Set<URI> findLinksFromHtml(Document doc) throws Exception{
		Set<URI> allLinks = super.findLinksFromHtml(doc);
		for(Element element : doc.select("iframe[src]")){
			try {
				URI link = toUri(element.absUrl("src"));
				if(HtmlUtils.isCrawlableFileExtension(link) && isCrawlableIframe(link)){
					System.out.println("added crawlable iframe");
					allLinks.add(link);
				}
			} catch (URISyntaxException e) {/**Bad href, do nothing**/}
		}
		return allLinks;
	}
	
	protected boolean isCrawlableIframe(URI uri){
		for(CrawlableIframeDomain domain : CrawlableIframeDomain.values()){
			if(StringUtils.contains(uri.toString(), domain.getDefinition())){
				System.out.println("Found crawlable iframe : " + uri);
				return true;
			}
		}
		return false;
	}
	
	
}
