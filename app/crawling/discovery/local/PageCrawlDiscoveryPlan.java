package crawling.discovery.local;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import analysis.PageCrawlAnalyzer;
import crawling.discovery.entities.Resource;
import crawling.discovery.execution.CrawlContext;
import crawling.discovery.execution.DiscoveredSource;
import crawling.discovery.planning.DiscoveryPlan;
import crawling.discovery.planning.ResourcePlan;
import crawling.discovery.planning.SingleDestinationDiscoveryPlan;
import persistence.PageCrawl;

public class PageCrawlDiscoveryPlan extends SingleDestinationDiscoveryPlan {

//	protected CrawlContext crawlContext;
//
//	public PageCrawlDiscoveryPlan(ResourcePlan destinationPlan) {
//		super(destinationPlan);
//	}
//
//	public PageCrawlDiscoveryPlan(String name, ResourcePlan destinationPlan) {
//		super(name, destinationPlan);
//	}
//
//	public Set<DiscoveredSource> discover(Resource resource) throws Exception {
////		PageCrawl pageCrawl = (PageCrawl) resource.getValue();
////		String text = PageCrawlAnalyzer.getText(pageCrawl);
////		Document doc = Jsoup.parse(text);
////		doc.setBaseUri((String)crawlContext.getContextObject("baseUriString"));
////		
//		Set<DiscoveredSource> discoveredSources = new HashSet<DiscoveredSource>();
////		for(Element element : doc.select("a[href]")){
////			try {
////				discoveredSources.add(new DiscoveredSource(resource, new URI(element.absUrl("href")), destinationPlan));
////			} catch (URISyntaxException e) {
////				//Bad href, do nothing
////			}
////		}
//		return discoveredSources;
//	}
	
	

}
