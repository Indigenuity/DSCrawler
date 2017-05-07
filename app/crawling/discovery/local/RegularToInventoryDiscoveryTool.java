package crawling.discovery.local;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import analysis.PageCrawlAnalyzer;
import crawling.discovery.entities.Resource;
import crawling.discovery.execution.DiscoveredSource;
import crawling.discovery.execution.DiscoveryContext;
import crawling.discovery.execution.PlanId;
import crawling.discovery.execution.ResourceWorkResult;
import crawling.discovery.html.HtmlUtils;
import crawling.discovery.html.InternalLinkDiscoveryTool;
import datadefinitions.inventory.InvType;
import datadefinitions.inventory.InventoryTool;
import persistence.PageCrawl;

public class RegularToInventoryDiscoveryTool extends InternalLinkDiscoveryTool {

	@Override
	public Set<DiscoveredSource> discover(ResourceWorkResult workResult, DiscoveryContext context) throws Exception {
		Set<DiscoveredSource> discoveredSources = new HashSet<DiscoveredSource>();
		for(Resource resource : workResult.getResources()){
			discoveredSources.addAll(discover(resource, context));
		}
		return discoveredSources;
	}
	
	public Set<DiscoveredSource> discover(Resource resource, DiscoveryContext context) throws Exception {
		Set<DiscoveredSource> discoveredSources = new HashSet<DiscoveredSource>();
		PlanId regularPlan = context.getDefaultDestination();
		PlanId inventoryPlan = (PlanId) context.getContextObject("inventoryPlanId");
		
		PageCrawl pageCrawl = (PageCrawl) resource.getValue();
		
		//Single discovery from redirects
		if(pageCrawl.getRedirectedUrl() != null){
			URI redirectedUri = new URI(pageCrawl.getRedirectedUrl());
			if(context.discoverSource(redirectedUri)){
				discoveredSources.add(new DiscoveredSource(resource, redirectedUri, resource.getPlanId()));
			}
			return discoveredSources;  	//No sense in digging through an empty file for more links
		}
		
		
		String text = PageCrawlAnalyzer.getText(pageCrawl);
		Document doc = Jsoup.parse(text);
		URI currentUri = (URI) resource.getSource();
		doc.setBaseUri(currentUri.toString());

		//Discover from all <a> tags as regular sources
		Set<URI> regularLinks = getRegularLinks(doc, currentUri);
		for(URI uri : regularLinks) {
			if(context.discoverSource(uri)){
				discoveredSources.add(new DiscoveredSource(resource, uri, regularPlan));
			}
		}
		
		//Discover inventory links for the inventory plan
		if(pageCrawl.getNewRoot() || pageCrawl.getUsedRoot() || resource.getPlanId().equals(inventoryPlan)){
			URI nextPageLink = getNextPageLink(pageCrawl.getInvType(), doc, currentUri);
			if(nextPageLink != null){
				//No if statement on discover, because during re-crawls we want inventory system to not be stopped by a link previously unrecognized as inventory
				context.discoverSource(nextPageLink);		 
				discoveredSources.add(new DiscoveredSource(resource, nextPageLink, inventoryPlan));
			}
		}
		
		return discoveredSources;
	}
	
	protected Set<URI> getRegularLinks(Document doc, URI currentUri){
		Set<URI> uris = new HashSet<URI>();
		for(Element element : doc.select("a[href]")){
			try {
				URI uri = new URI(element.absUrl("href"));
				uri = new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), uri.getPath(), uri.getQuery(), null); 	//Ditch user info and fragments
				if(HtmlUtils.isInternalLink(currentUri, uri) 
						&& isCrawlableFileExtension(uri)){
					uris.add(uri);
				}
			} catch (URISyntaxException e) {
				//Bad href, do nothing
			}
		}
		return uris;
	}
	
	protected URI getNextPageLink(InvType invType, Document doc, URI currentUri){
		InventoryTool tool = invType.getTool();
		return tool.getNextPageLink(doc, currentUri);
	}
}
