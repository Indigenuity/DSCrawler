package crawling;


import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dao.SiteInformationDAO;
import persistence.SiteCrawl;
import persistence.SiteInformationOld;
import play.db.DB;
import play.db.jpa.JPA;
import utilities.DSFormatter;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import experiment.Storage;
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
		String encodedSeed = DSFormatter.encode(seed);
		String safeSeed = DSFormatter.makeSafePath(encodedSeed);
		String date = new SimpleDateFormat("MM-dd-yyyy").format(new java.util.Date());  
		String relativeStorageFolder = "/" + date + "/" + safeSeed;
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
        siteCrawl.setNumRepeatedUrls(crawlReport.repeatUrls);
        siteCrawl.setCrawlDepth(config.getMaxDepthOfCrawling());
        return siteCrawl;
	}
	
	public static SiteCrawl crawlSite(String seed) throws Exception {
		DealerCrawlConfig config = new DealerCrawlConfig();
		return crawlSite(seed, config);
	}
	
	//Crawl a site using default settings
	public static SiteInformationOld crawlURL(SiteInformationOld siteInfo) throws Exception {
		long start = System.currentTimeMillis();
		DealerCrawlConfig config = new DealerCrawlConfig();
		
		String url = siteInfo.getRedirectUrl();
		String safeUrl = DSFormatter.encode(url);
		URL temp = new URL(url);
		String domain = temp.getProtocol() + "://" + temp.getHost();
		String date = new SimpleDateFormat("MM-dd-yyyy").format(new java.util.Date());
		config.setRelativeStorageFolder("/" + safeUrl + "/" + date);
		
        
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        robotstxtConfig.setEnabled(false); 
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        DealerCrawlController controller = new DealerCrawlController(config, pageFetcher, robotstxtServer);
        controller.setCrawlSeed(url);
        controller.setCrawlDomain(domain);
        controller.addSeed(url);
        
        System.out.println("Starting Blocking Crawl for URL: " + url);
        controller.start(DealerCrawler.class, DEFAULT_NUMBER_OF_CRAWLERS);
//        controller.startNonBlocking(DealerCrawler.class, DEFAULT_NUMBER_OF_CRAWLERS);
//     // Wait for 30 seconds
//        Thread.sleep(30 * 1000);
//
//        // Send the shutdown request and then wait for finishing
//        //controller.Shutdown();
//        controller.waitUntilFinish();
        java.sql.Date timeNow = new java.sql.Date(Calendar.getInstance().getTimeInMillis());
        siteInfo.setCrawlDate(timeNow);
        siteInfo.setCrawlStorageFolder(config.getCrawlStorageFolder());
////        FileOutputStream fileOut = new FileOutputStream(config.getCrawlStorageFolder() + "/SiteInformation.ser");
//    	ObjectOutputStream out = new ObjectOutputStream(fileOut);
//    	out.writeObject(siteInfo);
//    	out.close();
//    	fileOut.close();
    	
    	long end = System.currentTimeMillis();
    	long time = (start-end) / 1000;
    	
        System.out.println("finished crawl: " + url + "(" + time + " seconds)");
        Storage.times.add(time);
        return siteInfo;
	}
	
	
}
