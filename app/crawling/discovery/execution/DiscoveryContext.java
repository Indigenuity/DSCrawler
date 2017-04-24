package crawling.discovery.execution;

import java.util.HashSet;
import java.util.Set;

import crawling.discovery.planning.DiscoveryPlan;
import crawling.discovery.planning.DiscoveryTool;

public class DiscoveryContext extends Context{

	protected final CrawlContext crawlContext;
	protected final DiscoveryTool discoveryTool;
	protected final PlanId defaultDestination;
	protected final Set<Object> knownSources = new HashSet<Object>();
	
	public DiscoveryContext(CrawlContext crawlContext, DiscoveryPlan discoveryPlan){
		this.contextObjects.putAll(discoveryPlan.getInitialContextObjects());
		this.rateLimiter = discoveryPlan.getRateLimiter();
		this.discoveryTool = discoveryPlan.getDiscoveryTool();
		this.crawlContext = crawlContext;
		this.defaultDestination = discoveryPlan.getDefaultDestination();
		this.knownSources.addAll(discoveryPlan.getStartingSources());
//		System.out.println("knownSources upon creation: " + knownSources);
	}

	public CrawlContext getCrawlContext() {
		return crawlContext;
	}

	public DiscoveryTool getDiscoveryTool() {
		return discoveryTool;
	}

	public PlanId getDefaultDestination() {
		return defaultDestination;
	}
	
	public boolean discoverSource(Object source){
		if(source == null){
			return false;
		}
		synchronized(knownSources){
//			System.out.println("known sources : " + knownSources);
			return knownSources.add(source);
		}
	}
	
	
	
	public Object getContextObject(String key){
		synchronized(contextObjects){
			if(contextObjects.containsKey(key)){
				return contextObjects.get(key);
			}
			return crawlContext.getContextObject(key);
		}
	}
}
