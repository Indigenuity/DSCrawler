package crawling.discovery.planning;


import java.util.HashSet;
import java.util.Set;

import crawling.discovery.execution.CrawlContext;
import crawling.discovery.execution.DiscoveryContext;
import crawling.discovery.execution.PlanId;

public class DiscoveryPlan extends Plan {

	protected PlanId defaultDestination;
	protected DiscoveryTool discoveryTool;
	
	protected Set<Object> startingSources = new HashSet<Object>();
	
	public DiscoveryPlan(){
	}

	public DiscoveryTool getDiscoveryTool() {
		return discoveryTool;
	}

	public void setDiscoveryTool(DiscoveryTool discoveryTool) {
		this.discoveryTool = discoveryTool;
	}

	public synchronized DiscoveryContext generateContext(CrawlContext crawlContext){
		return new DiscoveryContext(crawlContext, this);
	}

	public PlanId getDefaultDestination() {
		return defaultDestination;
	}

	public void setDefaultDestination(PlanId defaultDestination) {
		this.defaultDestination = defaultDestination;
	}
	
	public void setDefaultDestination(ResourcePlan defaultDestinationPlan) {
		this.defaultDestination = defaultDestinationPlan.getPlanId();
	}
	public Object putContextObject(String key, Object value){
		synchronized(initialContextObjects){
			return initialContextObjects.put(key, value);
		}
	}

	public Set<Object> getStartingSources() {
		return startingSources;
	}
	public boolean addStartingSource(Object source){
		return startingSources.add(source);
	}
}
