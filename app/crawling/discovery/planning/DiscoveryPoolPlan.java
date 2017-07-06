package crawling.discovery.planning;

import java.util.HashSet;
import java.util.Set;

import crawling.discovery.execution.CrawlContext;
import crawling.discovery.execution.DiscoveryPool;
import crawling.discovery.execution.PlanId;

public class DiscoveryPoolPlan {

	protected final PlanId planId = new PlanId();
	protected final Set<Object> startingSources = new HashSet<Object>();

	public DiscoveryPool generatePool(CrawlContext crawlContext){
		return new DiscoveryPool(crawlContext, this);
	}
	
	public PlanId getPlanId() {
		return planId;
	}
	
	public synchronized Set<Object> getStartingSources() {
		return startingSources;
	}

	public synchronized boolean addSource(Object source) {
		return startingSources.add(source);
	}

	public synchronized boolean removeSource(Object source) {
		return startingSources.remove(source);
	}
}
