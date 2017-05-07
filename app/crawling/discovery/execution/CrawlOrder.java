package crawling.discovery.execution;

import crawling.discovery.planning.CrawlPlan;
import newwork.WorkOrder;

public class CrawlOrder extends WorkOrder{
	
	protected final CrawlPlan crawlPlan;
	
	public CrawlOrder(CrawlPlan crawlPlan){
		this.crawlPlan = crawlPlan;
	}

	public CrawlPlan getCrawlPlan() {
		return crawlPlan;
	}

}
