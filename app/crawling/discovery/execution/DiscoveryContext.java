package crawling.discovery.execution;

import java.util.HashSet;
import java.util.Set;

import crawling.discovery.entities.DiscoveredSource;
import crawling.discovery.entities.Resource;
import crawling.discovery.planning.DiscoveryPlan;
import crawling.discovery.planning.DiscoveryTool;

public class DiscoveryContext extends Context{

	protected final CrawlContext crawlContext;
	protected final DiscoveryTool discoveryTool;
	protected final PlanId primaryDestinationResourcePlanId;
	protected final Set<PlanId> destinationResourcePlanIds = new HashSet<PlanId>();
	protected final DiscoveryPool discoveryPool;
	
	public DiscoveryContext(CrawlContext crawlContext, DiscoveryPlan discoveryPlan){
		super(discoveryPlan);
		this.discoveryTool = discoveryPlan.getDiscoveryTool();
		this.crawlContext = crawlContext;
		this.primaryDestinationResourcePlanId = discoveryPlan.getPrimaryDestinationResourcePlanId();
		this.destinationResourcePlanIds.addAll(discoveryPlan.getDestinationResourcePlanIds());
		this.discoveryPool = crawlContext.getDiscoveryPool(discoveryPlan.getDiscoveryPoolPlan().getPlanId());
	}

	public CrawlContext getCrawlContext() {
		return crawlContext;
	}

	public boolean preDiscover(Object source) {
		return discover(source);
	}
	
	public boolean discover(Object source) {
		return discoveryPool.discoverSource(source);
	}
	
	public Set<DiscoveredSource> discoverSources(Resource parent) throws Exception{
		return discoveryTool.discover(parent, this);
	}

	public DiscoveryTool getDiscoveryTool() {
		return discoveryTool;
	}

	public Set<PlanId> getDestinationResourcePlanIds() {
		return new HashSet<PlanId>(destinationResourcePlanIds);
	}

	public DiscoveryPool getDiscoveryPool() {
		return discoveryPool;
	}

	public PlanId getPrimaryDestinationResourcePlanId() {
		return primaryDestinationResourcePlanId;
	}
	
	
}
