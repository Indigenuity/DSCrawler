package crawling.anansi;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import global.Global;
import utilities.DSFormatter;

public class SiteCrawlConfig {

	public final static int DEFAULT_MAX_DEPTH_OF_CRAWLING = 1;
	public final static int DEFAULT_POLITENESS_DELAY = 1000; 
	public final static int DEFAULT_MAX_PAGES_TO_FETCH = 2000;
	public final static int DEFAULT_CONNECT_TIMEOUT = 15 * 1000;
	public final static int DEFAULT_READ_TIMEOUT = 15 * 1000;
	public final static int DEFAULT_NUM_WORKERS = 5;
	public final static String DEFAULT_STORAGE_ROOT = Global.getCrawlStorageFolder() + "/" + new Date();
	public final static String DEFAULT_USER_AGENT_STRING = Global.getDefaultUserAgentString();
	public final static boolean DEFAULT_USE_PROXY = Global.useProxy();
	public final static String DEFAULT_PROXY_URL = Global.getProxyUrl();
	public final static int DEFAULT_PROXY_PORT = Global.getProxyPort();

	protected String relativeStorageFolder = "/" + UUID.randomUUID().getLeastSignificantBits();
	protected String storageRoot = DEFAULT_STORAGE_ROOT;
	protected int maxCrawlDepth = DEFAULT_MAX_DEPTH_OF_CRAWLING;
	protected int politenessDelay = DEFAULT_POLITENESS_DELAY;
	protected int maxPagesToFetch = DEFAULT_MAX_PAGES_TO_FETCH;
	protected int numWorkers = DEFAULT_NUM_WORKERS;
	protected String userAgentString = DEFAULT_USER_AGENT_STRING;
	
	protected boolean queriesAreUnique = false;
	protected boolean singleHost = true;
	
	protected RequestConfig requestConfig;
	{
		Builder requestConfigBuilder = RequestConfig.custom()
				.setSocketTimeout(DEFAULT_READ_TIMEOUT)
				.setConnectionRequestTimeout(DEFAULT_CONNECT_TIMEOUT);
		if(DEFAULT_USE_PROXY){
			requestConfigBuilder.setProxy(new HttpHost(DEFAULT_PROXY_URL, DEFAULT_PROXY_PORT));
		}
		requestConfig = requestConfigBuilder.build();
	}
	
	public SiteCrawlConfig(){
	}
	
	public CloseableHttpClient buildHttpClient(){
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		CloseableHttpClient httpClient = HttpClients.custom()
				.setUserAgent(getUserAgentString())
				.setDefaultRequestConfig(getRequestConfig())
				.setConnectionManager(cm)
				.build();
		return httpClient;
	}
	
	public static CloseableHttpClient buildDefaultHttpClient(){
		SiteCrawlConfig config = new SiteCrawlConfig();
		return config.buildHttpClient();
	}
	
	public String getStorageFolder() {
		return getStorageRoot() + getRelativeStorageFolder();
	}

	public String getRelativeStorageFolder() {
		return relativeStorageFolder;
	}

	public void setRelativeStorageFolder(String relativeStorageFolder) {
		this.relativeStorageFolder = relativeStorageFolder;
	}

	public String getStorageRoot() {
		return storageRoot;
	}

	public void setStorageRoot(String storageRoot) {
		this.storageRoot = storageRoot;
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

	public int getNumWorkers() {
		return numWorkers;
	}

	public void setNumWorkers(int numWorkers) {
		this.numWorkers = numWorkers;
	}

	public String getUserAgentString() {
		return userAgentString;
	}

	public void setUserAgentString(String userAgentString) {
		this.userAgentString = userAgentString;
	}

	public RequestConfig getRequestConfig() {
		return requestConfig;
	}

	public void setRequestConfig(RequestConfig requestConfig) {
		this.requestConfig = requestConfig;
	}

	public boolean isQueriesAreUnique() {
		return queriesAreUnique;
	}

	public void setQueriesAreUnique(boolean queriesAreUnique) {
		this.queriesAreUnique = queriesAreUnique;
	}

	public boolean isSingleHost() {
		return singleHost;
	}

	public void setSingleHost(boolean singleHost) {
		this.singleHost = singleHost;
	}
	
}
