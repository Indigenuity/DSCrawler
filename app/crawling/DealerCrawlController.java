package crawling;


import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import persistence.SiteCrawl;
import utilities.DSFormatter;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import global.Global;

public class DealerCrawlController extends CrawlController {
	  
	protected final static int DEFAULT_NUMBER_OF_CRAWLERS = 1;
	
	
	
	protected String crawlSeed;
	protected String crawlDomain;
	
	protected CrawlReport report; 
	
	protected SiteCrawl siteCrawl;
	
	public DealerCrawlController(DealerCrawlConfig config, PageFetcher pageFetcher, RobotstxtServer robotstxtServer) throws Exception {
		super(config, pageFetcher, robotstxtServer);
	} 
	 
	public String getCrawlStorageFolder() {
		return config.getCrawlStorageFolder();
	}
	
	public String getCrawlSeed() {
		return this.crawlSeed;
	}
	
	public String getCrawlDomain() {
		return this.crawlDomain;
	}
	
	public void setCrawlSeed(String crawlSeed) {
		this.crawlSeed = crawlSeed;
	}
	
	public void setCrawlDomain(String crawlDomain) {
		this.crawlDomain = crawlDomain;
	}
	
	public CrawlReport getReport() {
		return report;
	}

	public void setReport(CrawlReport report) {
		this.report = report;
	}
	
	
	public SiteCrawl getSiteCrawl() {
		return siteCrawl;
	}

	public void setSiteCrawl(SiteCrawl siteCrawl) {
		this.siteCrawl = siteCrawl;
	}

	public static SiteCrawl crawlHomepage(String seed) throws Exception {
		DealerCrawlConfig config = new DealerCrawlConfig();
		config.setMaxDepthOfCrawling(0);
		return crawlSite(seed, config);		
	}

	public static SiteCrawl crawlSite(String seed, DealerCrawlConfig config) throws Exception {
//		seed = seed.toLowerCase();
		System.out.println("DealerCrawlController starting crawl from thread " + Thread.currentThread().getName());
		String encodeedSeed = DSFormatter.makeSafeFilePath(seed);
		String date = new SimpleDateFormat("MM-dd-yyyy").format(new java.util.Date());  
		String relativeStorageFolder = "/" + date + "/" + encodeedSeed;
		config.setRelativeStorageFolder(relativeStorageFolder);
		if(Global.useProxy()) {
//			System.out.println("Using proxy");
			config.setProxyHost(Global.getProxyUrl());
			config.setProxyPort(Global.getProxyPort());
		}
		
		URL url = new URL(seed);
		String domain = url.getProtocol() + "://" + url.getHost();
		
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
	    robotstxtConfig.setEnabled(false); 
	    RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
	    DealerCrawlController controller = new DealerCrawlController(config, pageFetcher, robotstxtServer);
	    SiteCrawl siteCrawl = new SiteCrawl(seed);
	    controller.setSiteCrawl(siteCrawl);
        controller.setCrawlSeed(seed);
        controller.setCrawlDomain(domain);
        controller.addSeed(seed);
        
        
        System.out.println("Starting Blocking Crawl for URL: " + url);
        controller.start(DealerCrawler.class, DEFAULT_NUMBER_OF_CRAWLERS);
        
        CrawlReport crawlReport = controller.getReport(); 
        
        
        siteCrawl.setStorageFolder(relativeStorageFolder);
        siteCrawl.setCrawlDate(new java.sql.Date(Calendar.getInstance().getTimeInMillis()));
//        siteCrawl.setFailedUrls(crawlReport.failedUrls);
//        siteCrawl.setCrawledUrls(crawlReport.visitedUrls);
//        siteCrawl.setNumRepeatedUrls(crawlReport.repeatUrls);
        siteCrawl.setCrawlDepth(config.getMaxDepthOfCrawling());
        return siteCrawl;
	}
	
	public static SiteCrawl crawlSite(String seed) throws Exception {
		DealerCrawlConfig config = new DealerCrawlConfig();
		return crawlSite(seed, config);
	}
}
