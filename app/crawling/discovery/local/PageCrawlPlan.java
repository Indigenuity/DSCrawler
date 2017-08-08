package crawling.discovery.local;

import crawling.discovery.html.HttpConfig;
import crawling.discovery.html.HttpToFilePlan;

public class PageCrawlPlan extends HttpToFilePlan {

	public final static int PAGE_CRAWL_DEFAULT_MAX_DEPTH_OF_CRAWLING = 1;
	public final static int PAGE_CRAWL_DEFAULT_MAX_PAGES_TO_FETCH = 1000;
	
	public PageCrawlPlan(HttpConfig config) {
		super(config);
		this.setResourceTool(new DSToFileTool(false));
		this.setMaxDepth(PAGE_CRAWL_DEFAULT_MAX_DEPTH_OF_CRAWLING);
		this.setMaxFetches(PAGE_CRAWL_DEFAULT_MAX_PAGES_TO_FETCH);
	}
}
