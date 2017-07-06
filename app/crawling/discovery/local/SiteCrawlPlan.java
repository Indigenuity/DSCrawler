package crawling.discovery.local;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import com.google.common.util.concurrent.RateLimiter;

import crawling.discovery.entities.Resource;
import crawling.discovery.execution.DiscoveryPool;
import crawling.discovery.html.HttpConfig;
import crawling.discovery.planning.CrawlPlan;
import crawling.discovery.planning.DiscoveryPlan;
import crawling.discovery.planning.DiscoveryPoolPlan;
import crawling.discovery.planning.PreResource;
import crawling.discovery.planning.ResourcePlan;
import crawling.discovery.planning.ResourcePreOrder;
import global.Global;
import newwork.WorkStatus;
import persistence.PageCrawl;
import persistence.Site;
import persistence.SiteCrawl;
import sites.utilities.PageCrawlLogic;
import utilities.HttpUtils;

public class SiteCrawlPlan extends CrawlPlan {
	
	public static final int DEFAULT_REGULAR_DEPTH = 1;
	public static final int DEFAULT_INVENTORY_DEPTH = 2000;
	public static final int DEFAULT_PERMITS_PER_SECOND = 1;
	public static final int DEFAULT_MAX_PAGES = 3000;

	protected HttpConfig config;
	protected ResourcePlan regularPlan;
	protected ResourcePlan inventoryPlan;
	protected DiscoveryPlan regularDiscoveryPlan;
	protected DiscoveryPlan inventoryDiscoveryPlan;
	
	protected SiteCrawlPlan(){
		setCrawlTool(new SiteCrawlTool());
		setRateLimiter(RateLimiter.create(DEFAULT_PERMITS_PER_SECOND));
		initHttpConfig();
		initResourcePlan();
		initInventoryPlan();
		initDiscoveryPlans();
		setMaxDepth(Math.max(DEFAULT_REGULAR_DEPTH, DEFAULT_INVENTORY_DEPTH));
		setMaxPages(DEFAULT_MAX_PAGES);
	}
	
	public SiteCrawlPlan(Site site) {
		this();
		setName("SiteCrawl for Site " + site.getSiteId() + " : " + site.getHomepage());
		putContextObject("siteId", site.getSiteId());
		URI homepage = generateUri(site.getHomepage());
		generateSeedResource(homepage);
	}
	
	public SiteCrawlPlan(SiteCrawl siteCrawl){
		this();
		setName("SiteCrawl for Site " + siteCrawl.getSite().getSiteId() + " : " + siteCrawl.getSite().getHomepage());
		putContextObject("siteCrawlId", siteCrawl.getSiteCrawlId());
		generateResources(siteCrawl);
		readSettings(siteCrawl);
	}
	
	protected void generateSeedResource(URI seed){
		PreResource resource = new PreResource(seed,null, regularPlan.getPlanId(), regularDiscoveryPlan.getPlanId());
		addResource(resource);
	}
	
	protected void readSettings(SiteCrawl siteCrawl){
		this.setMaxDepth(siteCrawl.getMaxDepth());
		this.setMaxPages(siteCrawl.getMaxPages());
	}
	
	protected void fillInventoryContext(SiteCrawl siteCrawl) {
		if(siteCrawl.getNewInventoryRoot() != null){
			putContextObject("newRoot", siteCrawl.getNewInventoryRoot().getPageCrawlId());
		}
		if(siteCrawl.getUsedInventoryRoot() != null){
			putContextObject("usedRoot", siteCrawl.getUsedInventoryRoot().getPageCrawlId());
		}
		
	}
	
	protected void generateResources(SiteCrawl siteCrawl){
//		System.out.println("Generating PreResources for SiteCrawl : " + siteCrawl.getSiteCrawlId());
//		System.out.println("roots : " + siteCrawl.getRoots().size());
//		System.out.println("total : " + siteCrawl.getPageCrawls().size());
		Set<PageCrawl> roots = siteCrawl.getRoots();
		if(roots.size() > 0){
			for(PageCrawl pageCrawl : siteCrawl.getRoots()){
				generateResources(pageCrawl, null);
			}
		} else {
			generateSeedResource(generateUri(siteCrawl.getSeed()));
		}
	}
	
	protected void generateResources(PageCrawl pageCrawl, PreResource parent){
//		System.out.println("Generating preresources for pagecrawl " + pageCrawl.getPageCrawlId() + " : " + pageCrawl.getUrl());
		ResourcePlan resourcePlan;
		DiscoveryPlan discoveryPlan;
		if(pageCrawl.getPagedInventory()){
			resourcePlan = inventoryPlan;
			discoveryPlan = inventoryDiscoveryPlan;
		} else{
			resourcePlan = regularPlan;
			discoveryPlan = regularDiscoveryPlan;
		}
		
		DSResponseFile responseFile = toResponseFile(pageCrawl);
		PreResource resource = new PreResource(responseFile.getUri(), responseFile, parent, resourcePlan.getPlanId(), discoveryPlan.getPlanId());
		setStatus(pageCrawl, resource);
		generateChildResources(pageCrawl, resource);
		addResource(resource);
	}
	
	protected DSResponseFile toResponseFile(PageCrawl pageCrawl){
		DSResponseFile responseFile = new DSResponseFile(pageCrawl.getUri(), new File(pageCrawl.getFilename()));
		responseFile.setInventory(pageCrawl.getPagedInventory());
		responseFile.setUsedRoot(pageCrawl.getUsedRoot());
		responseFile.setNewRoot(pageCrawl.getNewRoot());
		responseFile.setInvType(pageCrawl.getInvType());
		responseFile.setStatusCode(pageCrawl.getStatusCode());
		responseFile.setRedirectedUri(pageCrawl.getRedirectedUri());
		
		return responseFile;
	}
	
	protected void setStatus(PageCrawl pageCrawl, PreResource resource){
		if(PageCrawlLogic.isFailedCrawl(pageCrawl) || PageCrawlLogic.isUncrawled(pageCrawl)){
//			System.out.println("Assigning as failed or uncrawled : " + pageCrawl.getUrl());
			resource.setFetchStatus(WorkStatus.UNASSIGNED);
		} else {
			resource.setFetchStatus(WorkStatus.PRECOMPLETED);
		}
		resource.setDiscoveryStatus(WorkStatus.UNASSIGNED);
	}
	
	protected void generateChildResources(PageCrawl pageCrawl, PreResource resource){
		for(PageCrawl child : pageCrawl.getChildPages()){
			generateResources(child, resource);
		}
	}
	
	protected URI generateUri(String uriString){
		try {
			return new URI(uriString);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Cannot generate URI from invalid uriString : " + uriString);
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
		regularPlan = new PageCrawlPlan(config);
		regularPlan.setMaxDepth(DEFAULT_REGULAR_DEPTH);
		registerResourcePlan(regularPlan);
	}
	
	protected void initInventoryPlan(){
		inventoryPlan = new InventoryPlan(config);
		inventoryPlan.setMaxDepth(DEFAULT_INVENTORY_DEPTH);
		registerResourcePlan(inventoryPlan);
		putContextObject("inventoryPlanId", inventoryPlan.getPlanId());
	}
	
	protected void initDiscoveryPlans() {
		regularDiscoveryPlan = new PageCrawlDiscoveryPlan();
		regularDiscoveryPlan.setDestinationPlanId(regularPlan.getPlanId());
		registerDiscoveryPlan(regularDiscoveryPlan);
		
		inventoryDiscoveryPlan = new InventoryDiscoveryPlan();
		inventoryDiscoveryPlan.setDestinationPlanId(inventoryPlan.getPlanId());
		registerDiscoveryPlan(inventoryDiscoveryPlan);
		
		regularPlan.registerDiscoveryPlan(regularDiscoveryPlan);
		regularPlan.registerDiscoveryPlan(inventoryDiscoveryPlan);
		
		inventoryPlan.registerDiscoveryPlan(inventoryDiscoveryPlan);
	}

	
	/**************************Getters and setters *******************************/
	
	public HttpConfig getConfig() {
		return config;
	}

	public void setConfig(HttpConfig config) {
		this.config = config;
	}

	public ResourcePlan getRegularPlan() {
		return regularPlan;
	}

	public ResourcePlan getInventoryPlan() {
		return inventoryPlan;
	}

	public DiscoveryPlan getRegularDiscoveryPlan() {
		return regularDiscoveryPlan;
	}

	public DiscoveryPlan getInventoryDiscoveryPlan() {
		return inventoryDiscoveryPlan;
	}
}
