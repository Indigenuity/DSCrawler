package crawling.discovery.execution;

import java.util.HashSet;
import java.util.Set;

import crawling.discovery.planning.DiscoveryPlan;
import crawling.discovery.planning.DiscoveryTool;

public class DiscoveryContext extends Context{

	protected final CrawlContext crawlContext;
	protected final DiscoveryTool discoveryTool;
	protected final PlanReference defaultDestination;
	protected final Set<Object> knownSources = new HashSet<Object>();
	
	public DiscoveryContext(CrawlContext crawlContext, DiscoveryPlan discoveryPlan){
		this.contextObjects.putAll(discoveryPlan.getInitialContextObjects());
		this.rateLimiter = discoveryPlan.getRateLimiter();
		this.discoveryTool = discoveryPlan.getDiscoveryTool();
		this.crawlContext = crawlContext;
		this.defaultDestination = discoveryPlan.getDefaultDestination();
	}

	public CrawlContext getCrawlContext() {
		return crawlContext;
	}

	public DiscoveryTool getDiscoveryTool() {
		return discoveryTool;
	}

	public PlanReference getDefaultDestination() {
		return defaultDestination;
	}
	
	public boolean discoverSource(Object source){
		synchronized(knownSources){
			return knownSources.add(source);
		}
	}
}
