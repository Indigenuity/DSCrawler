package crawling.discovery.planning;

import java.util.HashSet;
import java.util.Set;

import crawling.discovery.execution.CrawlContext;
import crawling.discovery.execution.DiscoveryContext;
import crawling.discovery.execution.PlanId;

public class DiscoveryPlan extends ContextPlan {

	protected PlanId sourceResourcePlanId;
	protected PlanId primaryDestinationResourcePlanId;
	
	protected final Set<PlanId> destinationResourcePlanIds = new HashSet<PlanId>();
	
	protected DiscoveryTool discoveryTool;
	protected DiscoveryPoolPlan discoveryPoolPlan = new DiscoveryPoolPlan();
	
	public DiscoveryPlan(PlanId sourceResourcePlanId){
		super();
		this.sourceResourcePlanId = sourceResourcePlanId;
	}
	
	public DiscoveryPlan(PlanId sourceResourcePlanId, DiscoveryTool discoveryTool) {
		this(sourceResourcePlanId);
		this.discoveryTool = discoveryTool;
	}

	public DiscoveryPlan(PlanId sourceResourcePlanId, DiscoveryTool discoveryTool, DiscoveryPoolPlan discoveryPoolPlan) {
		this(sourceResourcePlanId, discoveryTool);
		this.discoveryPoolPlan = discoveryPoolPlan;
	}
	
	public synchronized DiscoveryContext generateContext(CrawlContext crawlContext){
		return new DiscoveryContext(crawlContext, this);
	}
	
	public PlanId getPrimaryDestinationResourcePlanId() {
		return primaryDestinationResourcePlanId;
	}

	public void setPrimaryDestinationResourcePlanId(PlanId primaryDestinationResourcePlanId) {
		this.primaryDestinationResourcePlanId = primaryDestinationResourcePlanId;
		addDestinationResourcePlanId(primaryDestinationResourcePlanId);
	}

	public DiscoveryTool getDiscoveryTool() {
		return discoveryTool;
	}

	public void setDiscoveryTool(DiscoveryTool discoveryTool) {
		this.discoveryTool = discoveryTool;
	}

	public DiscoveryPoolPlan getDiscoveryPoolPlan() {
		return discoveryPoolPlan;
	}

	public void setDiscoveryPoolPlan(DiscoveryPoolPlan discoveryPoolPlan) {
		this.discoveryPoolPlan = discoveryPoolPlan;
	}

	public Set<PlanId> getDestinationResourcePlanIds() {
		return new HashSet<PlanId>(destinationResourcePlanIds);
	}
	
	public boolean addDestinationResourcePlanId(PlanId resourcePlanId){
		return destinationResourcePlanIds.add(resourcePlanId);
	}
	
	public boolean removeDestinationResourcePlanId(PlanId resourcePlanId){
		return destinationResourcePlanIds.remove(resourcePlanId);
	}

	public PlanId getSourceResourcePlanId() {
		return sourceResourcePlanId;
	}

	public void setSourceResourcePlanId(PlanId sourceResourcePlanId) {
		this.sourceResourcePlanId = sourceResourcePlanId;
	}
}
