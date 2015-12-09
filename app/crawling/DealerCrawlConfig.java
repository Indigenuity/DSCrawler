package crawling;


import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import global.Global;

public class DealerCrawlConfig extends CrawlConfig {
	
	protected final static int DEFAULT_MAX_DEPTH_OF_CRAWLING = 1;
	protected final static int DEFAULT_POLITENESS_DELAY = 1000; 
	protected final static int DEFAULT_MAX_PAGES_TO_FETCH = 2000;
	protected final static int DEFAULT_TIMEOUT = 30 * 1000;
	protected final static String DEFAULT_USER_AGENT_STRING = Global.DEFAULT_USER_AGENT_STRING;
	protected final static String DEFAULT_CRAWL_STORAGE_ROOT = Global.CRAWL_STORAGE_FOLDER;  
	
	protected String relativeStorageFolder = "";
	
	public DealerCrawlConfig(){
		super();
		this.setMaxDepthOfCrawling(DEFAULT_MAX_DEPTH_OF_CRAWLING);
		this.setPolitenessDelay(DEFAULT_POLITENESS_DELAY);
		this.setMaxPagesToFetch(DEFAULT_MAX_PAGES_TO_FETCH);
		this.setConnectionTimeout(DEFAULT_TIMEOUT);
		this.setUserAgentString(DEFAULT_USER_AGENT_STRING);
		this.setCrawlStorageFolder(DEFAULT_CRAWL_STORAGE_ROOT);
	} 
	
	@Override
	public String getCrawlStorageFolder() {
		return super.getCrawlStorageFolder() + this.getRelativeStorageFolder();
	}
	
	public String getRelativeStorageFolder() {
		return this.relativeStorageFolder;
	}
	
	public void setRelativeStorageFolder(String relativeStorageFolder) {
		this.relativeStorageFolder = relativeStorageFolder;
	}
	
	

}
