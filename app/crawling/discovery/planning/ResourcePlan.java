package crawling.discovery.planning;


import java.util.HashSet;
import java.util.Set;

import crawling.discovery.entities.Resource;
import crawling.discovery.execution.CrawlContext;
import crawling.discovery.execution.PlanId;
import crawling.discovery.execution.ResourceContext;

public abstract class ResourcePlan extends ContextWithResourcesPlan { 
	
	public final static int DEFAULT_NUM_WORKERS = 5;
	
	protected int numWorkers = DEFAULT_NUM_WORKERS;
	
	protected final Set<PreResource> resources = new HashSet<PreResource>();
	
	protected ResourceTool resourceTool;
	
	public ResourcePlan(ResourceTool resourceTool) {
		this.resourceTool = resourceTool;
	}
	
	public ResourcePlan() {
	}
	
	public synchronized ResourceContext generateContext(CrawlContext crawlContext) throws Exception{
		ResourceContext context =  new ResourceContext(crawlContext, this);
		return context;
	}
	
	public int getNumWorkers() {
		return numWorkers;
	}

	public void setNumWorkers(int numWorkers) {
		this.numWorkers = numWorkers;
	}

	public ResourceTool getResourceTool() {
		return resourceTool;
	}

	public void setResourceTool(ResourceTool resourceTool) {
		this.resourceTool = resourceTool;
	}


	public boolean addResource(PreResource resource){
		return this.resources.add(resource);
	}
	
	public Set<PreResource> getResources() {
		return resources;
	}
	
}
