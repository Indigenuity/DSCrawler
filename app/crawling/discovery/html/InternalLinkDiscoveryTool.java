package crawling.discovery.html;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class InternalLinkDiscoveryTool extends LinkDiscoveryTool{
	
	@Override
	protected Set<URI> generateFromRedirect(HtmlResource htmlResource) {
		Set<URI> links = super.generateFromRedirect(htmlResource);
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
	protected Set<URI> generateFromHtml(HtmlResource htmlResource) throws Exception{
		Set<URI> allLinks = super.generateFromHtml(htmlResource);
		Set<URI> internalLinks = new HashSet<URI>();
		URI currentUri = htmlResource.getUri();
		for(URI link : allLinks){
			if(HtmlUtils.isInternalLink(currentUri, link)){
				internalLinks.add(link);
			}
		}
		return internalLinks;
	}
}
