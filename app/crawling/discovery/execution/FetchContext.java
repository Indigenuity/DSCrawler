package crawling.discovery.execution;

import crawling.discovery.planning.FetchPlan;
import crawling.discovery.planning.FetchTool;

public class FetchContext extends Context {

	private final CrawlContext crawlContext;
	private final ResourceContext resourceContext;
	private final FetchTool fetchTool;
	
	public FetchContext(CrawlContext crawlContext, FetchPlan fetchPlan) {
		super(fetchPlan);
		this.crawlContext = crawlContext;
		this.resourceContext = crawlContext.getResourceContext(fetchPlan.getResourcePlanId());
		this.fetchTool = fetchPlan.getFetchTool();
	}

	public CrawlContext getCrawlContext() {
		return crawlContext;
	}

	public ResourceContext getResourceContext() {
		return resourceContext;
	}

	public FetchTool getFetchTool() {
		return fetchTool;
	}

}
