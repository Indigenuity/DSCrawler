package crawling.discovery.html;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import crawling.discovery.entities.DiscoveredSource;
import crawling.discovery.entities.Resource;
import crawling.discovery.execution.DiscoveryContext;
import crawling.discovery.execution.PlanId;
import crawling.discovery.planning.DiscoveryTool;
import utilities.HttpUtils;

public class LinkDiscoveryTool implements DiscoveryTool {

	@Override
	public Set<DiscoveredSource> discover(Resource parent, DiscoveryContext context) throws Exception {
		PlanId resourcePlanId = context.getPrimaryDestinationResourcePlanId();
		if(resourcePlanId == null){
			throw new IllegalArgumentException("Cannot discover links for DiscoveryContext without primaryDestinationResourcePlanId set");
		}
		if(parent.getValue() == null || !(parent.getValue() instanceof HtmlResource)){
			return new HashSet<DiscoveredSource>();
		}
		Set<URI> links = findLinks((HtmlResource)parent.getValue());
		Set<DiscoveredSource> sources = new HashSet<DiscoveredSource>();
		for(URI link : links){
			sources.add(new DiscoveredSource(link, resourcePlanId));
		}
		return sources;
	}
	
	protected Set<URI> findLinks(HtmlResource htmlResource) throws Exception{
		if(HttpUtils.isError(htmlResource.getStatusCode())){
			return findLinksFromError(htmlResource);
		}
		if(HttpUtils.isRedirect(htmlResource.getStatusCode())){
			return findLinksFromRedirect(htmlResource);
		}
		return findLinksFromSuccess(htmlResource);
	}
	
	protected Set<URI> findLinksFromError(HtmlResource htmlResource){
		return new HashSet<URI>();
	}
	
	protected Set<URI> findLinksFromRedirect(HtmlResource htmlResource) {
		Set<URI> links = new HashSet<URI>();
		links.add(htmlResource.getRedirectedUri());
		return links;
	}
	
	protected Set<URI> findLinksFromSuccess(HtmlResource htmlResource) throws Exception{
		if(htmlResource.docExists()){
			return findLinksFromHtml(htmlResource.getDocument());
		}
		return new HashSet<URI>();
	}
	
	protected Set<URI> findLinksFromHtml(Document doc) throws Exception{
		Set<URI> links = new HashSet<URI>();
		for(Element element : doc.select("a[href]")){
			try {
				URI link = toUri(element.absUrl("href"));
				if(HtmlUtils.isCrawlableFileExtension(link)){
					links.add(link);
				}
			} catch (URISyntaxException e) {/**Bad href, do nothing**/}
		}
		return links;
	}

	protected URI toUri(String href) throws URISyntaxException{
		if(href == null || href.trim().equals("")){
			throw new URISyntaxException(href, "Empty href");
		}
		URI uri = new URI(href);
		uri = new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), uri.getPath(), uri.getQuery(), null); 	//Ditch user info and fragments
		return uri;
	}
}
