package crawling.discovery.execution;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.util.concurrent.RateLimiter;

import crawling.discovery.entities.Resource;
import crawling.discovery.entities.ResourceId;
import crawling.discovery.planning.PreResource;
import crawling.discovery.planning.FetchTool;
import crawling.discovery.planning.ResourcePlan;
import crawling.discovery.planning.ResourcePreOrder;
import crawling.discovery.planning.ResourceTool;
import newwork.WorkStatus;

public class ResourceContext extends ContextWithResources{
	
	protected final CrawlContext crawlContext;
	protected final ResourceTool resourceTool;
	protected final int numWorkers;
	
	public ResourceContext(CrawlContext crawlContext, ResourcePlan resourcePlan){
		super(resourcePlan);
		this.resourceTool = resourcePlan.getResourceTool();
		this.crawlContext = crawlContext;
		this.numWorkers = resourcePlan.getNumWorkers();
//		System.out.println("resource context preorders : " + preOrders.size());
	}
	
	public CrawlContext getCrawlContext() {
		return crawlContext;
	}

	public int getNumWorkers() {
		return numWorkers;
	}

	public PlanId getPlanId() {
		return planId;
	}

	public ResourceTool getResourceTool() {
		return resourceTool;
	}

}
