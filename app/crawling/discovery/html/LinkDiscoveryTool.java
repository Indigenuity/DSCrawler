package crawling.discovery.html;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import crawling.discovery.entities.Resource;
import crawling.discovery.execution.DiscoveryContext;
import crawling.discovery.planning.DiscoveryTool;
import utilities.HttpUtils;

public class LinkDiscoveryTool implements DiscoveryTool {

	@Override
	public Set<Object> discover(Resource parent, DiscoveryContext context) throws Exception {
		HtmlResource htmlResource = (HtmlResource) parent.getValue();
		
		//Single discovery from redirects
		//No sense in digging through an empty resource for more links
		Set<URI> links = new HashSet<URI>();
		if(HttpUtils.isRedirect(htmlResource.getStatusCode())){	
			links.addAll(generateFromRedirect(htmlResource));
		} else if(HttpUtils.isSuccessful(htmlResource.getStatusCode())) {
			links.addAll(generateFromHtml(htmlResource));
		} else {
		}
		return discoverLinks(links, context);
	}
	
	protected Set<Object> discoverLinks(Set<URI> links, DiscoveryContext context) throws Exception {
		Set<Object> discoveredLinks = new HashSet<Object>();
		for(URI link : links){
			if(context.discover(link)){
				discoveredLinks.add(link);
			}
		}
		return discoveredLinks;
	}
	
	protected Set<URI> generateFromRedirect(HtmlResource htmlResource) {
//		System.out.println("LinkDiscoverytool generating from redirect");
		Set<URI> links = new HashSet<URI>();
		links.add(htmlResource.getRedirectedUri());
		return links;
	}
	
	protected Set<URI> generateFromHtml(HtmlResource htmlResource) throws Exception{
//		System.out.println("LinkDiscoverytool generating from html");
		Document doc = htmlResource.getDocument();
		Set<URI> links = new HashSet<URI>();
		for(Element element : doc.select("a[href]")){
			try {
				URI link = toUri(element);
				if(HtmlUtils.isCrawlableFileExtension(link)){
					links.add(link);
				}
			} catch (URISyntaxException e) {/**Bad href, do nothing**/}
		}
		return links;
	}

	protected URI toUri(Element linkElement) throws URISyntaxException{
		String href = linkElement.absUrl("href");
		if(href == null || href.equals("")){
			throw new URISyntaxException(href, "Empty href");
		}
		URI uri = new URI(href);
		uri = new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), uri.getPath(), uri.getQuery(), null); 	//Ditch user info and fragments
		return uri;
	}
}
