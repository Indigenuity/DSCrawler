package crawling.discovery.local;

import crawling.discovery.html.InternalLinkDiscoveryTool;
import crawling.discovery.planning.DiscoveryPlan;

public class PageCrawlDiscoveryPlan extends DiscoveryPlan {

	public PageCrawlDiscoveryPlan() {
		super();
		this.setDiscoveryTool(new InternalLinkDiscoveryTool());
	}
	
}
