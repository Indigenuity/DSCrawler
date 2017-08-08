package crawling.discovery.planning;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import crawling.discovery.control.BasicIdGenerator;
import crawling.discovery.control.CrawlUtil;
import crawling.discovery.control.IdGenerator;
import crawling.discovery.entities.FlushableResource;
import crawling.discovery.entities.Resource;
import crawling.discovery.execution.CrawlContext;
import crawling.discovery.execution.DiscoveryContext;
import crawling.discovery.execution.EmptyCrawlTool;
import crawling.discovery.execution.PlanId;
import crawling.discovery.execution.ResourceContext;

public class CrawlPlan extends ContextWithResourcesPlan{
	
	protected CrawlTool crawlTool = new EmptyCrawlTool();
	protected IdGenerator idGenerator = new BasicIdGenerator();
	
	protected final Set<ResourcePlan> resourcePlans = new HashSet<ResourcePlan>();
	protected final Set<DiscoveryPlan> discoveryPlans = new HashSet<DiscoveryPlan>();
	protected final Set<PreResource> resources = new HashSet<PreResource>();
	
	
	public CrawlPlan(){
	}
	
	public synchronized CrawlContext generateContext() throws Exception{
		CrawlContext crawlContext = new CrawlContext(this);
		return crawlContext;
	}
	
	public synchronized void processPreResources(CrawlContext crawlContext) throws Exception{
//		System.out.println("CrawlPlan processing PreResources");
		for(PreResource root : getRootResources()){
//			System.out.println("Root PreResource : " + root.getSource());
			processPreResource(root, null, crawlContext);
		}
	}
	
	protected void processPreResource(PreResource preResource, Resource parent, CrawlContext crawlContext) throws Exception{
		Resource resource = crawlContext.preGenerateResource(preResource, parent);
		
		for(PreResource child : preResource.getChildren()){
			processPreResource(child, resource, crawlContext);
		}
		CrawlUtil.flush(resource);
	}
	
	public Set<DiscoveryPoolPlan> getDiscoveryPoolPlans() {
		Set<DiscoveryPoolPlan> poolPlans = new HashSet<DiscoveryPoolPlan>();
		for(DiscoveryPlan discoveryPlan : discoveryPlans){
			poolPlans.add(discoveryPlan.getDiscoveryPoolPlan());
		}
		return poolPlans;
	}
	
	protected boolean isRegistered(ResourcePlan resourcePlan) {
		return resourcePlans.contains(resourcePlan);
	}
	
	protected boolean isRegistered(DiscoveryPlan discoveryPlan) {
		return discoveryPlans.contains(discoveryPlan);
	}
	
	public CrawlPlan registerResourcePlan(ResourcePlan resourcePlan){
		resourcePlans.add(resourcePlan);
		return this;
	}
	
	public CrawlPlan registerDiscoveryPlan(DiscoveryPlan discoveryPlan){
		discoveryPlans.add(discoveryPlan);
		return this;
	}
	
	public Set<PreResource> getResources() {
		return resources;
	}
	
	public boolean addResource(PreResource resource){
		return resources.add(resource);
	}
	
	public Set<PreResource> getRootResources(){
		Set<PreResource> roots = new HashSet<PreResource>();
		for(PreResource resource : resources){
			if(resource.getParent() == null){
				roots.add(resource);
			}
		}
		return roots;
	}

	public Set<ResourcePlan> getResourcePlans() {
		return resourcePlans;
	}

	public Set<DiscoveryPlan> getDiscoveryPlans() {
		return discoveryPlans;
	}

	public CrawlTool getCrawlTool() {
		return crawlTool;
	}

	public void setCrawlTool(CrawlTool crawlTool) {
		this.crawlTool = crawlTool;
	}
	

	public IdGenerator getIdGenerator() {
		return idGenerator;
	}

	public void setIdGenerator(IdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}
	
}
