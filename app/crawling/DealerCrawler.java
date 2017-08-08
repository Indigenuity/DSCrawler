package crawling;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import persistence.PageCrawl;
import persistence.SiteCrawl;
import play.Logger;
import utilities.DSFormatter;
import utilities.UrlUtils;

import com.google.common.io.Files;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class DealerCrawler extends WebCrawler {

	//This is a github testing comment again and again
	public final static String CONTENT_FILE_SUFFIX = "HTML-";
	
	
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|mp4|zip|gz|pdf|jpeg))$");
	
	private File storageFolder;
	private String crawlDomain = "Default Value";
	
	private Set<String> visitedUrls = new HashSet<String>();
	private Set<String> failedUrls = new HashSet<String>();
	
	private int repeatUrls = 0;
	
	private SiteCrawl siteCrawl;
	 
	
	@Override 
	public void onStart() {
		try { 
			DealerCrawlController myController = (DealerCrawlController) this.getMyController();
			storageFolder = new File(myController.getCrawlStorageFolder());
	        if (!storageFolder.exists()) {
	          storageFolder.mkdirs();
	        }
	        siteCrawl = myController.getSiteCrawl();
	        
	        crawlDomain = myController.getCrawlDomain();
//	        System.out.println("crawlseed : " + crawlSeed);
//	        System.out.println("crawlDomain : " + crawlDomain);
		}
        catch(Exception e) {
        	Logger.error("caught exception in onStart");
        	Logger.error(e.toString());
        	e.printStackTrace();
        }
	} 
	

	/* 
	 * Rules for visiting a page: 
	 * 		must not be a resource like css, js, or images
	 * 		must start with the same domain and protocol as the seed url
	 * 		must not have visited previously
	 * 			--NOTE: this string is assembled from the protocol and hostname 
	 * 					in order to exclude port numbers often given by redirects
	 * 
	 */
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		
		try{
			
			String href = url.getURL().toLowerCase();
//			System.out.println("checking shouldVisit for href : " + href);
			String lowerDomain = crawlDomain.toLowerCase();
			
			if(href == null){
//				System.out.println("Not visiting null href");
				return false;
			}
			
			if(!href.startsWith(lowerDomain)) {
//				System.out.println("Not intrasite Url : " + href);
				return false;
			}
//			siteCrawl.addIntrasiteLink(href);
			
			String noQuery = UrlUtils.removeQueryString(href);
//			if(!siteCrawl.addUniqueCrawledPageUrl(noQuery) && !siteCrawl.isFollowNonUnique()){
////				System.out.println("Not following non unique page url : " + noQuery);
//				return false;
//			}
			
			if(FILTERS.matcher(noQuery).matches()) {
//				System.out.println("Wrong Content Type : " + href);
				return false;
			}
			
//			if(!siteCrawl.addCrawledUrl(href)){
//				repeatUrls++;
//				if(repeatUrls % 1000 == 0){
////					System.out.println("repeated Href : " + repeatUrls);
//				}
//				return false; 
//			}
			
		}
		catch (Exception e) {
			Logger.error("Caught Exception in shouldVisit");
			Logger.error(e.toString());
//			System.out.println("exception in should visit");
			
			return false;
		}
//		System.out.println("following url : " + url.getURL());
		return true;
	}
	
	@Override
	public void visit (Page page) {
		
		PageCrawl pageCrawl = null;
		try {
			String urlString = page.getWebURL().getURL();
			pageCrawl = new PageCrawl(urlString);
			siteCrawl.addPageCrawl(pageCrawl);
			URL url = new URL(urlString);
			
			String path = url.getPath();
			String query = url.getQuery();
			String pathAndQuery = path + "?" + query;
			String safePath;
			if(!StringUtils.isEmpty(query)){
				safePath = DSFormatter.makeSafeFilePath(pathAndQuery);
			} else {
				safePath = DSFormatter.makeSafeFilePath(path);
			}
			String filename = storageFolder.getAbsolutePath() + "/" + safePath;
			
			pageCrawl.setUrl(urlString); 
			pageCrawl.setPath(path);
			pageCrawl.setQuery(query);
			pageCrawl.setStatusCode(page.getStatusCode());
			 
//			System.out.println("visiting page : " + url);
			 
			if (page.getParseData() instanceof HtmlParseData) {
//				Set<WebURL> links = page.getParseData().getOutgoingUrls();
//				for(WebURL link : links){
//					siteCrawl.addLink(link.getURL());
//					pageCrawl.addLink(link.getURL());
//				}
			    // HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
				//Record raw crawl data to file
				File out = new File(filename);
//					        	 System.out.println("filename : " + filename);
				if(!out.exists()){
					Files.write(page.getContentData(), new File(filename));
				}
				pageCrawl.setFilename(safePath);
			}
		}
		catch(Exception e) {
			Logger.error("Error while trying to visit and store : " + page.getWebURL().getURL() + " " + e);
//			System.out.println("Error while trying to visit and store : " + page.getWebURL().getURL() + " " + e);
//			siteCrawl.addFailedUrl(page.getWebURL().getURL());
			pageCrawl.setErrorMessage(e.getMessage());
			failedUrls.add(page.getWebURL().getURL());
		}
	}
	@Override
	protected void onContentFetchError(WebURL webUrl){
//		System.out.println("Content fetch error for : " + webUrl.getURL());
		Logger.error("Content fetch error for : " + webUrl.getURL());
		failedUrls.add(webUrl.getURL());
	}
	
	@Override
	public void onBeforeExit() {
//		System.out.println("Submitting crawl report");
		CrawlReport report = new CrawlReport(visitedUrls, failedUrls, repeatUrls);
		DealerCrawlController myController = (DealerCrawlController) this.getMyController();
		myController.setReport(report);
	}
	
	@Override
	protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
//		System.out.println("handling page status code : " + statusCode);
//		System.out.println("description : " + statusDescription);
//		System.out.println("url : " + webUrl);
	}
	
}
