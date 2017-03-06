package crawling.anansi;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.common.io.Files;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import async.async.Asyncleton;
import async.functionalwork.FunctionWorkOrder;
import async.functionalwork.FunctionalWorker;
import async.monitoring.WaitingRoom;
import crawling.HttpFetcher;
import global.Global;
import newwork.TypedWorkResult;
import persistence.PageCrawl;
import persistence.Site;
import persistence.SiteCrawl;
import play.Logger;
import utilities.DSFormatter;

public class SiteCrawler extends UntypedActor{
	
	private final static Pattern NO_CRAWL_FILE_EXTENSIONS = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|mp4|zip|gz|pdf|jpeg))$");
	
	/********** pre-initialized ********************/
	protected SiteCrawlConfig config = new SiteCrawlConfig();
	
	protected Set<URI> crawledUris = new HashSet<URI>();
	protected Set<String> crawledPaths = new HashSet<String>();
	protected Set<URI> uncrawledUris = new HashSet<URI>();
	
	protected String uriMutex = "Pistachio Pudding";
	
	
	
	
	/********* on-demand initialization **************/
	protected ActorRef pageCrawlMaster;
	protected CloseableHttpClient httpClient;
	protected SiteCrawl siteCrawl;
	protected URI crawlSeed;
	protected WaitingRoom httpWaitingRoom;
	

	public SiteCrawler(SiteCrawlConfig config, Site site) throws URISyntaxException {
		this.config = config;
		this.siteCrawl = new SiteCrawl(site);
		this.pageCrawlMaster = Asyncleton.getInstance().getMonotypeMaster(config.getNumWorkers(), FunctionalWorker.class);
		this.httpClient = config.buildHttpClient();
		this.httpWaitingRoom = new WaitingRoom("PageCrawls for " + siteCrawl.getSeed());
		this.crawlSeed = new URI(siteCrawl.getSeed());
	}
	
	public SiteCrawler(SiteCrawlConfig config, String seed) throws URISyntaxException {
		this.config = config;
		this.pageCrawlMaster = Asyncleton.getInstance().getMonotypeMaster(config.getNumWorkers(), FunctionalWorker.class);
		this.httpClient = config.buildHttpClient();
		this.httpWaitingRoom = new WaitingRoom("PageCrawls for " + seed);
		this.crawlSeed = new URI(seed);
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof SiteCrawlWorkOrder) {
			startCrawl();
		} else if(message instanceof PageFetchWorkResult) {
			processPageFetchWorkResult((PageFetchWorkResult) message);
		} else {
			System.out.println("Unknown message in onReceive for SiteCrawler");
			Logger.error("Unknown message in onReceive for SiteCrawler");
		}
		
	}
	
	public void processPageFetchWorkResult(PageFetchWorkResult workResult){
		System.out.println("Processing page fetch : " + workResult.getResult().getUri());
		System.out.println("Status code: " + workResult.getResult().getStatusCode());
	}
	
	public void startCrawl() throws IOException{
		System.out.println("Starting crawl : " + crawlSeed);
		processUri(crawlSeed);
	}
	
	public SiteCrawler startPageFetch(URI uri){
		System.out.println("Starting page fetch for URI : " + uri);
		PageFetchWorkOrder workOrder = new PageFetchWorkOrder(uri, httpClient);
		synchronized(uriMutex){
			this.crawledUris.add(uri);
			this.crawledPaths.add(uri.getPath());
		}
		pageCrawlMaster.tell(workOrder, getSelf());
		return this;
	}
	
	public SiteCrawler processUri(URI uri) {
		System.out.println("Processing URI : " + uri);

		if(config.isSingleHost() && !matchesSeedHost(uri)){
			return rejectUri(uri, "Uri's host doesn't match crawlseed's host");
		}
		
		if(!isCrawlableFileExtension(uri)){
			return rejectUri(uri, "Uri has uncrawlable file extension");
		}
		
		synchronized(uriMutex){
			if(!isUncrawled(uri)){
				return rejectUri(uri, "Uri already crawled");
			}
			if( config.isQueriesAreUnique() && !isUncrawledPath(uri)){
				return rejectUri(uri, "Path already crawled");
			}
			//Passed all the tests, queue up URI while still synced on mutex
			return startPageFetch(uri);
		}
	}
	
	public SiteCrawler rejectUri(URI uri, String reason) {
		synchronized(uriMutex){
			if(!crawledUris.contains(uri)){
				System.out.println("Not crawling uri : " + uri + "(" + reason + ")");
				uncrawledUris.add(uri);
			}
		}
		return this;
	}
	
	public boolean isUncrawled(URI uri) {
		return !this.crawledUris.contains(uri);
	}
	
	public boolean isUncrawledPath(URI uri) {
		return !this.crawledPaths.contains(uri.getPath());
	}
	
	public boolean matchesSeedHost(URI uri) {
		return StringUtils.equals(uri.getHost(), crawlSeed.getHost());
	}
	
	public boolean isCrawlableFileExtension(URI uri) {
		return !NO_CRAWL_FILE_EXTENSIONS.matcher(uri.getHost()).matches();
	}
	
	//These two methods exist to uniformly treat uris.  Queries and paths get chopped off here and there; it should be done consistently.  Excludes ports and user info.
//	public String getString(URI uri) {
//		return uri.getScheme() + "://" + uri.getHost() + uri.getPath() + "?" + uri.getQuery();
//	}
//	
//	public String getPathString(URI uri) {
//		return uri.getScheme() + "://" + uri.getHost() + uri.getPath();
//	}
	
	
	
//	private PageCrawl getPageCrawl(URL url) throws IOException{
//		String urlString = url.toString();
//		PageCrawl pageCrawl = new PageCrawl();
//		pageCrawl.setUrl(urlString);
//		
//		BufferedReader reader = null;
//		HttpURLConnection con = null;
//		
//			
//			StringBuilder result = new StringBuilder();
//			if(config.isUseProxy()){
//				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(config.getProxyUrl(), config.getProxyPort()));
//				con= (HttpURLConnection) url.openConnection(proxy);
//			}
//			else{
//				con= (HttpURLConnection) url.openConnection();
//			}
//			try{
//			con.setConnectTimeout(config.getConnectTimeout());
//			con.setReadTimeout(config.getReadTimeout());
//			con.setRequestMethod("GET");
//			con.setRequestProperty("User-Agent", config.getUserAgentString());
//			con.connect();
//			
//			reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
//		    String line;
//		    while ((line = reader.readLine()) != null) {
//		       result.append(line);
//		    }
//		    reader.close();
//		    con.disconnect();
//		    String pageText = result.toString();
//		    
//		    
//			pageCrawl.setHttpStatus(con.getResponseCode());
//			
//		    String path = url.getPath();
//			String query = url.getQuery();
//			String pathAndQuery = path + "?" + query;
//			String safePath = DSFormatter.makeSafeFilePath(pathAndQuery);
//			String filename = config.getStorageFolder().getAbsolutePath() + "/" + safePath;
//			 
//			File out = new File(filename);
//			Files.write(pageText.getBytes(), new File(filename));
//			pageCrawl.setFilename(safePath);
//			pageCrawl.setPath(path);
//			pageCrawl.setQuery(query);
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//			Logger.error("Error while trying to visit and store : " + url + " " + e);
//			System.out.println("Error while trying to visit and store : " + url + " " + e);
//			pageCrawl.setErrorMessage(e.getMessage());
//			con.disconnect();
//			reader.close();
//		}
//
//		return pageCrawl;
//	}
}
