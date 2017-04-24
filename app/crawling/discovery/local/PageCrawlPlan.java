package crawling.discovery.local;

import crawling.discovery.html.HttpConfig;
import crawling.discovery.html.HttpToFilePlan;

public class PageCrawlPlan extends HttpToFilePlan {

	public PageCrawlPlan(HttpConfig config) {
		super(config);
		this.setFetchTool(new PageCrawlTool(false));
	}
}
