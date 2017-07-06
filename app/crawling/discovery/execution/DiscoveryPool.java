package crawling.discovery.execution;

import java.util.HashSet;
import java.util.Set;

import crawling.discovery.planning.DiscoveryPoolPlan;

public class DiscoveryPool {

	protected final PlanId planId;
	protected final CrawlContext crawlContext;
	protected final Set<Object> sources = new HashSet<Object>();
	
	public DiscoveryPool(CrawlContext crawlContext, DiscoveryPoolPlan plan){
		this.planId = plan.getPlanId();
		this.crawlContext = crawlContext;
		this.sources.addAll(plan.getStartingSources());
	}
	
	public boolean discoverSource(Object source){
		if(source == null){
			return false;
		}
		synchronized(sources){
//			System.out.println("known sources : " + knownSources);
			return sources.add(source);
		}
	}

	public PlanId getPlanId() {
		return planId;
	}

	public Set<Object> getSources() {
		return new HashSet<Object>(sources);
	}
}
