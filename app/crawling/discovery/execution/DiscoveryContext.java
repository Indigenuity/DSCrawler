package crawling.discovery.execution;

import java.util.HashSet;
import java.util.Set;

import crawling.discovery.entities.Resource;
import crawling.discovery.planning.DiscoveryPlan;
import crawling.discovery.planning.DiscoveryTool;

public class DiscoveryContext extends Context{

	protected final CrawlContext crawlContext;
	protected final DiscoveryTool discoveryTool;
	protected final PlanId destinationPlanId;
	protected final DiscoveryPool discoveryPool;
	
	public DiscoveryContext(CrawlContext crawlContext, DiscoveryPlan discoveryPlan){
		super(discoveryPlan);
		this.discoveryTool = discoveryPlan.getDiscoveryTool();
		this.crawlContext = crawlContext;
		this.destinationPlanId = discoveryPlan.getDestinationPlanId();
		this.discoveryPool = crawlContext.getDiscoveryPool(discoveryPlan.getDiscoveryPoolPlan().getPlanId());
	}

	public CrawlContext getCrawlContext() {
		return crawlContext;
	}

	public PlanId getDestinationPlanId() {
		return destinationPlanId;
	}
	
	public boolean preDiscover(Object source) {
		return discover(source);
	}
	
	public boolean discover(Object source) {
		return discoveryPool.discoverSource(source);
	}
	
	public Set<Object> discoverSources(Resource parent) throws Exception{
		return discoveryTool.discover(parent, this);
	}
}
