package crawling.discovery.html;

import java.io.FileInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import analysis.PageCrawlAnalyzer;
import crawling.discovery.entities.Resource;
import crawling.discovery.execution.DiscoveredSource;
import crawling.discovery.execution.DiscoveryContext;
import crawling.discovery.execution.PlanReference;
import crawling.discovery.planning.DiscoveryTool;
import persistence.PageCrawl;

public class InventoryDiscoveryTool extends DiscoveryTool{

	@Override
	public Set<DiscoveredSource> discover(Resource resource, DiscoveryContext context) throws Exception {
		PlanReference regularPlan = context.getDefaultDestination();
		PlanReference inventoryPlan = (PlanReference) context.getContextObject("inventoryDestination");
		
		HttpResponseFile responseFile = (HttpResponseFile) resource.getValue();
		String text = IOUtils.toString(new FileInputStream(responseFile.getFile()), "UTF-8");
		Document doc = Jsoup.parse(text);
		doc.setBaseUri((String)context.getContextObject("baseUriString"));
		
		Set<DiscoveredSource> discoveredSources = new HashSet<DiscoveredSource>();
		for(Element element : doc.select("a[href]")){
			try {
				URI uri = new URI(element.absUrl("href"));
				if(context.discoverSource(uri)){
					
				}
				discoveredSources.add(new DiscoveredSource(resource, uri, context.getDefaultDestination()));
			} catch (URISyntaxException e) {
				//Bad href, do nothing
			}
		}
		return discoveredSources;
	}
	
//	protected boolean isNewPage(URI uri){
//		for(InventoryPage )
//	}
//	
//	protected boolean isUsedPage(URI uri){
//		
//	}

}
