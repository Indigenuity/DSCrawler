package crawling.discovery.html;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.nodes.Document;

public class InternalLinkDiscoveryTool extends LinkDiscoveryTool{
	
	@Override
	protected Set<URI> findLinksFromRedirect(HtmlResource htmlResource) {
		Set<URI> links = super.findLinksFromRedirect(htmlResource);
		Set<URI> internalLinks = new HashSet<URI>();
		URI currentUri = htmlResource.getUri();
		for(URI link : links){
			if(HtmlUtils.isInternalLink(currentUri, link)){
				internalLinks.add(link);
			}
		}
		return internalLinks;
	}
	
	@Override
	protected Set<URI> findLinksFromHtml(Document doc) throws Exception{
		Set<URI> allLinks = super.findLinksFromHtml(doc);
		Set<URI> internalLinks = new HashSet<URI>();
		URI currentUri = new URI(doc.baseUri());
		for(URI link : allLinks){
			if(HtmlUtils.isInternalLink(currentUri, link)){
				internalLinks.add(link);
			}
		}
		return internalLinks;
	}
}
