package crawling.discovery.execution;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import crawling.discovery.entities.Resource;
import newwork.WorkResult;

public class ResourceWorkResult extends WorkResult{

	public final Set<DiscoveredSource> discoveredSources = new HashSet<DiscoveredSource>();
	public final Set<Resource> resources = new HashSet<Resource>();
	public final Object source;
	public final Resource parent;
	public final PlanId planId;
	public ResourceWorkResult(ResourceWorkOrder workOrder) {
		super(workOrder);
		this.source = workOrder.getSource();
		this.parent = workOrder.getParent();
		this.planId = workOrder.getPlanId();
	}
	
	public ResourceWorkResult(ResourceWorkOrder workOrder, Set<Resource> resources) {
		this(workOrder);
		this.resources.addAll(resources);
	}
	
	public void addDiscoveredSources(Set<DiscoveredSource> discoveredSources){
		this.discoveredSources.addAll(discoveredSources);
	}

	public Set<DiscoveredSource> getDiscoveredSources() {
		return discoveredSources;
	}

	public Set<Resource> getResources() {
		return new HashSet<Resource>(resources);
	}
	
	public void addResources(Set<Resource> resources){
		this.resources.addAll(resources);
	}

	public void addResource(Resource resource){
		this.resources.add(resource);
	}
	
	public Object getSource() {
		return source;
	}

	public Resource getParent() {
		return parent;
	}

	public PlanId getPlanId() {
		return planId;
	}
}
