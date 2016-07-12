package crawling;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class SitCrawlConfig {
	
	public static final Integer DEFAULT_CRAWL_DEPTH = 0;
	public static final Integer DEFAULT_MAX_PAGES = 500;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected long siteCrawlConfigId;
	
	private int crawlDepth = DEFAULT_CRAWL_DEPTH;				//Crawl depth of 0 indicates no crawling beyond the seed
	private int maxPages = DEFAULT_MAX_PAGES;
	
	private String suggestedNewInventoryPath;
	private String suggestedUsedInventoryPath;
	
	private List<String> generalInventoryPaths = new ArrayList<String>();
	
	private Boolean simulateMobile = false;
	
	
}
