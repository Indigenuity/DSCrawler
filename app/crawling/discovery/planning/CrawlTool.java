package crawling.discovery.planning;

import crawling.discovery.execution.CrawlContext;
import crawling.discovery.execution.SeedWorkOrder;
import crawling.discovery.results.CrawlReport;

public abstract class CrawlTool {

	public void preCrawl(CrawlContext crawlContext){
		System.out.println("super precrawl");
	}
	
	public void preProcessSeed(CrawlContext crawlContext, SeedWorkOrder seedWorkOrder){
		
	}

	public void postCrawl(CrawlContext crawlContext, CrawlReport crawlReport){
		
	}
}
