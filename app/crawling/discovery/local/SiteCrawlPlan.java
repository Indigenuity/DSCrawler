package crawling.discovery.local;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import com.google.common.util.concurrent.RateLimiter;

import crawling.discovery.entities.Resource;
import crawling.discovery.html.HttpConfig;
import crawling.discovery.planning.CrawlPlan;
import crawling.discovery.planning.DiscoveryPlan;
import crawling.discovery.planning.ResourcePreOrder;
import global.Global;
import persistence.PageCrawl;
import persistence.Site;
import persistence.SiteCrawl;
import utilities.HttpUtils;

public class SiteCrawlPlan extends CrawlPlan {
	
	public static final int DEFAULT_REGULAR_DEPTH = 1;
	public static final int DEFAULT_INVENTORY_DEPTH = 2000;
	public static final int DEFAULT_PERMITS_PER_SECOND = 1;
	public static final int DEFAULT_MAX_PAGES = 3000;

	protected HttpConfig config;
	protected PageCrawlPlan resourcePlan;
	protected PageCrawlPlan inventoryPlan;
	protected DiscoveryPlan discoveryPlan;
	
	protected final Set<ResourcePreOrder> preOrders = new HashSet<ResourcePreOrder>();
	protected final Set<Object> sources = new HashSet<Object>();
	protected final Set<PageCrawlResource> resources = new HashSet<PageCrawlResource>();
	
	protected SiteCrawlPlan(){
		setCrawlTool(new SiteCrawlTool());
		setRateLimiter(RateLimiter.create(DEFAULT_PERMITS_PER_SECOND));
		initHttpConfig();
		initResourcePlan();
		initInventoryPlan();
		initDiscoveryPlan();
		setMaxDepth(Math.max(DEFAULT_REGULAR_DEPTH, DEFAULT_INVENTORY_DEPTH));
		setMaxPages(DEFAULT_MAX_PAGES);
	}
	
	public SiteCrawlPlan(Site site) {
		this();
		putContextObject("siteId", site.getSiteId());
		generatePreOrder(site.getHomepage(), null);
		fillSources();
	}
	
	public SiteCrawlPlan(SiteCrawl siteCrawl){
		this();
		putContextObject("siteCrawlId", siteCrawl.getSiteCrawlId());
		generateResources(siteCrawl);
		generatePreOrdersFromResources(siteCrawl);
		fillSources();
		readSettings(siteCrawl);
	}
	
	
	protected void readSettings(SiteCrawl siteCrawl){
		this.setMaxDepth(siteCrawl.getMaxDepth());
		this.setMaxPages(siteCrawl.getMaxPages());
	}
	
	protected void generateResources(SiteCrawl siteCrawl){
		for(PageCrawl pageCrawl : siteCrawl.getRoots()){
			generateResources(pageCrawl, null);
		}
	}
	
	protected void generateResources(PageCrawl pageCrawl, PageCrawlResource parent){
		PageCrawlResource resource = new PageCrawlResource(pageCrawl.getUri(), pageCrawl, parent);
		resources.add(resource);
		for(PageCrawl child : pageCrawl.getChildPages()){
			generateResources(child, resource);
		}
	}
	
	protected void generatePreOrdersFromResources(SiteCrawl siteCrawl){
		for(PageCrawlResource resource : resources){
			generatePreOrderFromSelf(resource);
			generatePreOrdersFromUncrawledAndError(resource);
		}
		if(resources.size() < 1){
			generatePreOrder(siteCrawl.getSeed(), null);
		}
	}
	
	protected void generatePreOrderFromSelf(PageCrawlResource resource){
		PageCrawl pageCrawl = (PageCrawl) resource.getValue();
		if(needsRecrawl(pageCrawl)){
			ResourcePreOrder preOrder = new ResourcePreOrder(pageCrawl.getUri(), resource.getParent());
			if(pageCrawl.getPagedInventory()){
				inventoryPlan.addPreOrder(preOrder);
				preOrders.add(preOrder);
			} else {
				resourcePlan.addPreOrder(preOrder);
				preOrders.add(preOrder);
			}
		}
	}
	
	protected void generatePreOrdersFromUncrawledAndError(PageCrawlResource resource){
		PageCrawl pageCrawl = (PageCrawl) resource.getValue();
		for(String url : pageCrawl.getFailedUrls()){
			generatePreOrder(url, resource);
		}
		for(String url : pageCrawl.getUnCrawledUrls()){
			generatePreOrder(url, resource);
		}
		for(String url : pageCrawl.getFailedInventoryUrls()){
			generateInventoryPreOrder(url, resource);
		}
		for(String url : pageCrawl.getUnCrawledInventoryUrls()){
			generateInventoryPreOrder(url, resource);
		}
		
	}
	
	protected boolean needsRecrawl(PageCrawl pageCrawl){
		return HttpUtils.isError(pageCrawl.getStatusCode()) || pageCrawl.getErrorMessage() != null;
	}
	
	protected void generatePreOrder(String url, PageCrawlResource parent){
		URI uri = generateUri(url);
		ResourcePreOrder preOrder = new ResourcePreOrder(uri, parent);
		resourcePlan.addPreOrder(preOrder);
		preOrders.add(preOrder);
	}
	
	protected void generateInventoryPreOrder(String url, PageCrawlResource parent){
		URI uri = generateUri(url);
		ResourcePreOrder preOrder = new ResourcePreOrder(uri, parent);
		inventoryPlan.addPreOrder(preOrder);
		preOrders.add(preOrder);
	}
	
	protected URI generateUri(String uriString){
		try {
			return new URI(uriString);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Cannot generate URI from invalid uriString : " + uriString);
		}
	}
	
	protected void fillSources(){
		for(ResourcePreOrder preOrder : preOrders){
			discoveryPlan.addStartingSource(preOrder.getSource());
		}
		for(Resource resource : resources){
			discoveryPlan.addStartingSource(resource.getSource());
		}
	}
	
	/******************************  All SiteCrawlPlan ******************************/
	
	protected void initHttpConfig(){
		config = new HttpConfig();
		config.setUserAgent(Global.getDefaultUserAgentString());
		config.setUseProxy(true);
		config.setProxyAddress(Global.getProxyUrl());
		config.setProxyPort(Global.getProxyPort());
		config.setFollowRedirects(false);
		putContextObject("httpConfig", config);
	}
	
	protected void initResourcePlan(){
		resourcePlan = new PageCrawlPlan(config);
		resourcePlan.setMaxDepth(DEFAULT_REGULAR_DEPTH);
		registerResourcePlan(resourcePlan);
	}
	
	protected void initInventoryPlan(){
		inventoryPlan = new PageCrawlPlan(config);
		inventoryPlan.setMaxDepth(DEFAULT_INVENTORY_DEPTH);
		registerResourcePlan(inventoryPlan);
		putContextObject("inventoryPlanId", inventoryPlan.getPlanId());
	}
	
	protected void initDiscoveryPlan() {
		discoveryPlan = new DiscoveryPlan();
		discoveryPlan.setDiscoveryTool(new RegularToInventoryDiscoveryTool());
		discoveryPlan.setDefaultDestination(resourcePlan);
		
		resourcePlan.registerDiscoveryPlan(discoveryPlan);
		inventoryPlan.registerDiscoveryPlan(discoveryPlan);
		
		registerDiscoveryPlan(discoveryPlan);
	}
	
	/**************************Getters and setters *******************************/

	public PageCrawlPlan getResourcePlan() {
		return resourcePlan;
	}

	public void setResourcePlan(PageCrawlPlan resourcePlan) {
		this.resourcePlan = resourcePlan;
	}

	public PageCrawlPlan getInventoryPlan() {
		return inventoryPlan;
	}

	public void setInventoryPlan(PageCrawlPlan inventoryPlan) {
		this.inventoryPlan = inventoryPlan;
	}

	public DiscoveryPlan getDiscoveryPlan() {
		return discoveryPlan;
	}

	public void setDiscoveryPlan(DiscoveryPlan discoveryPlan) {
		this.discoveryPlan = discoveryPlan;
	}

	public Set<Object> getSources() {
		return sources;
	}
	
	
}
