package crawling.anansi;

import global.Global;

public class SiteCrawlConfig {

	public final static int DEFAULT_MAX_DEPTH_OF_CRAWLING = 1;
	public final static int DEFAULT_POLITENESS_DELAY = 1000; 
	public final static int DEFAULT_MAX_PAGES_TO_FETCH = 2000;
	public final static int DEFAULT_CONNECT_TIMEOUT = 15 * 1000;
	public final static int DEFAULT_READ_TIMEOUT = 15 * 1000;
	public final static int DEFAULT_NUM_WORKERS = 5;
	public final static String DEFAULT_USER_AGENT_STRING = Global.getDefaultUserAgentString();
	public final static String DEFAULT_CRAWL_STORAGE_ROOT = Global.getCrawlStorageFolder();
	public final static boolean DEFAULT_USE_PROXY = Global.useProxy();
	public final static String DEFAULT_PROXY_URL = Global.getProxyUrl();
	public final static int DEFAULT_PROXY_PORT = Global.getProxyPort();
	
	protected String relativeStorageFolder = "";
	protected String crawlStorageRoot = DEFAULT_CRAWL_STORAGE_ROOT;
	protected int maxCrawlDepth = DEFAULT_MAX_DEPTH_OF_CRAWLING;
	protected int politenessDelay = DEFAULT_POLITENESS_DELAY;
	protected int maxPagesToFetch = DEFAULT_MAX_PAGES_TO_FETCH;
	protected int numWorkers = DEFAULT_NUM_WORKERS;
	protected int connectTimeout = DEFAULT_CONNECT_TIMEOUT;
	protected int readyTimeout = DEFAULT_READ_TIMEOUT;
	protected String userAgentString = DEFAULT_USER_AGENT_STRING;
	protected boolean useProxy = DEFAULT_USE_PROXY;
	protected String proxyUrl = DEFAULT_PROXY_URL;
	protected int proxyPort = DEFAULT_PROXY_PORT;
	
	public SiteCrawlConfig(){
	}


	public String getRelativeStorageFolder() {
		return relativeStorageFolder;
	}


	public void setRelativeStorageFolder(String relativeStorageFolder) {
		this.relativeStorageFolder = relativeStorageFolder;
	}


	public String getCrawlStorageRoot() {
		return crawlStorageRoot;
	}


	public void setCrawlStorageRoot(String crawlStorageRoot) {
		this.crawlStorageRoot = crawlStorageRoot;
	}


	public int getMaxCrawlDepth() {
		return maxCrawlDepth;
	}


	public void setMaxCrawlDepth(int maxCrawlDepth) {
		this.maxCrawlDepth = maxCrawlDepth;
	}


	public int getPolitenessDelay() {
		return politenessDelay;
	}


	public void setPolitenessDelay(int politenessDelay) {
		this.politenessDelay = politenessDelay;
	}


	public int getMaxPagesToFetch() {
		return maxPagesToFetch;
	}


	public void setMaxPagesToFetch(int maxPagesToFetch) {
		this.maxPagesToFetch = maxPagesToFetch;
	}
	public String getUserAgentString() {
		return userAgentString;
	}


	public void setUserAgentString(String userAgentString) {
		this.userAgentString = userAgentString;
	}


	public int getNumWorkers() {
		return numWorkers;
	}
	public void setNumWorkers(int numWorkers) {
		this.numWorkers = numWorkers;
	}


	public boolean isUseProxy() {
		return useProxy;
	}


	public void setUseProxy(boolean useProxy) {
		this.useProxy = useProxy;
	}


	public String getProxyUrl() {
		return proxyUrl;
	}


	public void setProxyUrl(String proxyUrl) {
		this.proxyUrl = proxyUrl;
	}


	public int getProxyPort() {
		return proxyPort;
	}


	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}


	public int getConnectTimeout() {
		return connectTimeout;
	}


	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}


	public int getReadyTimeout() {
		return readyTimeout;
	}


	public void setReadyTimeout(int readyTimeout) {
		this.readyTimeout = readyTimeout;
	} 

	
}
