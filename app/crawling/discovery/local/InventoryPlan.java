package crawling.discovery.local;

import crawling.discovery.html.HttpConfig;
import crawling.discovery.html.HttpToFilePlan;

public class InventoryPlan extends HttpToFilePlan {

	public final static int INVENTORY_DEFAULT_MAX_DEPTH_OF_CRAWLING = 10000;
	public final static int INVENTORY_DEFAULT_MAX_PAGES_TO_FETCH = 10001;
	
	public InventoryPlan(HttpConfig config) {
		super(config);
		this.setFetchTool(new DSToFileTool(true));
		this.setMaxDepth(INVENTORY_DEFAULT_MAX_DEPTH_OF_CRAWLING);
		this.setMaxPages(INVENTORY_DEFAULT_MAX_PAGES_TO_FETCH);
	}
}
