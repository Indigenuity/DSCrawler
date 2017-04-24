package crawling.discovery.html;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import crawling.discovery.entities.Resource;
import crawling.discovery.execution.DiscoveredSource;
import crawling.discovery.execution.DiscoveryContext;
import crawling.discovery.execution.ResourceWorkResult;
import crawling.discovery.planning.DiscoveryTool;
import newwork.WorkStatus;

public class InternalLinkDiscoveryTool extends DiscoveryTool{
	
	protected final static Pattern NO_CRAWL_FILE_EXTENSIONS = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|mp4|zip|gz|pdf|jpeg))$");
	

	@Override
	public Set<DiscoveredSource> discover(ResourceWorkResult workResult, DiscoveryContext context) throws Exception {
		Set<DiscoveredSource> discoveredSources = new HashSet<DiscoveredSource>();
		for(Resource resource : workResult.getResources()){
			discoveredSources.addAll(discoverFromResource(resource, context));
		}
//		if(workResult.getWorkStatus() == WorkStatus.INCOMPLETE){
//			workResult.
//		}
		return discoveredSources;
	}
	
	protected Set<DiscoveredSource> discoverFromResource(Resource resource, DiscoveryContext context) throws Exception{
		HtmlResource htmlResource =(HtmlResource) resource.getValue(); 
		Document doc = htmlResource.getDocument();
		URI currentUri = htmlResource.getUri();
		Set<DiscoveredSource> discoveredSources = new HashSet<DiscoveredSource>();
		for(Element element : doc.select("a[href]")){
			try {
				URI uri = toUri(element);
				if(HtmlUtils.isInternalLink(currentUri, uri) 
						&& isCrawlableFileExtension(uri)
						&& context.discoverSource(uri)){
					discoveredSources.add(new DiscoveredSource(resource, uri, context.getDefaultDestination()));
				}
				
			} catch (URISyntaxException e) {/**Bad href, do nothing**/}
		}
		return discoveredSources;		
	}
	
//	protected Set<DiscoveredSource> discoverFromResource(Resource resource, DiscoveryContext context) throws Exception{
//	
//	}
	
	protected URI toUri(Element linkElement) throws URISyntaxException{
		URI uri = new URI(linkElement.absUrl("href"));
		uri = new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), uri.getPath(), uri.getQuery(), null); 	//Ditch user info and fragments
		return uri;
	}
	
	protected boolean isCrawlableFileExtension(URI uri) {
		return !NO_CRAWL_FILE_EXTENSIONS.matcher(uri.getHost()).matches();
	}
	
	
	
}
