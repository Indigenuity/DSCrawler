package crawling.discovery.planning;

import crawling.discovery.execution.CrawlContext;
import crawling.discovery.execution.SeedWorkOrder;
import crawling.discovery.results.CrawlReport;

public abstract class CrawlTool {
	
	public void preResourcePopulation(CrawlContext crawlContext){
	}

	public void preCrawl(CrawlContext crawlContext){
	}
	
	public void preProcessSeed(CrawlContext crawlContext, SeedWorkOrder seedWorkOrder){
	}
	
	public void beforeFinish(CrawlContext crawlContext){
		
	}

	public void postCrawl(CrawlContext crawlContext, CrawlReport crawlReport){
	}
}
