package crawling.discovery.planning;

import java.util.HashSet;
import java.util.Set;

import crawling.discovery.entities.Resource;
import crawling.discovery.entities.ResourceId;
import crawling.discovery.execution.PlanId;
import newwork.WorkStatus;

public class PreResource {
	
	protected Object value;
	protected final PlanId planId;
	protected final PlanId discoveredByPlanId;
	protected final Object source;
	protected final PreResource parent;
	protected final Set<PreResource> children = new HashSet<PreResource>();
	
	protected WorkStatus fetchStatus = WorkStatus.UNASSIGNED;
	protected Exception fetchException = null;
	protected WorkStatus discoveryStatus= WorkStatus.UNASSIGNED;
	protected Exception discoveryException = null;
	
	public PreResource(Object source, Object value, PreResource parent, PlanId planId, PlanId discoveredByPlanId) {
		this(source, parent, planId, discoveredByPlanId);
		this.value = value;
	}
	
	public PreResource(Object source, PreResource parent, PlanId planId, PlanId discoveredByPlanId) {
		this.planId = planId;
		this.discoveredByPlanId = discoveredByPlanId;
		this.source = source;
		this.parent = parent;
		if(this.parent != null){
			this.parent.addChild(this);
		}
	}
	
	public PreResource getRoot() {
		if(this.parent == null){
			return this;
		}
		return getParent().getRoot();
	}
	
	public int getDepth() {
		if(this.parent == null){
			return 0;
		}
		return 1 + parent.getDepth();
	}
	
	public boolean needDiscoveryWork(){
		return getDiscoveryStatus() == WorkStatus.UNASSIGNED || getDiscoveryStatus() == WorkStatus.STARTED;
	}
	
	public boolean needFetchWork(){
		return getFetchStatus() == WorkStatus.UNASSIGNED || getFetchStatus() == WorkStatus.STARTED;
	}
	
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public PreResource getParent() {
		return parent;
	}
	public Set<PreResource> getChildren() {
		return new HashSet<PreResource>(children);
	}
	protected boolean addChild(PreResource child) {
		return this.children.add(child);
	}
//	public boolean removeChild(Resource child) {
//		return this.children.remove(child);
//	}
	public Object getSource() {
		return source;
	}
	public WorkStatus getFetchStatus() {
		return fetchStatus;
	}

	public void setFetchStatus(WorkStatus fetchStatus) {
		this.fetchStatus = fetchStatus;
	}

	public Exception getFetchException() {
		return fetchException;
	}

	public void setFetchException(Exception fetchException) {
		this.fetchException = fetchException;
	}

	public WorkStatus getDiscoveryStatus() {
		return discoveryStatus;
	}

	public void setDiscoveryStatus(WorkStatus discoveryStatus) {
		this.discoveryStatus = discoveryStatus;
	}

	public Exception getDiscoveryException() {
		return discoveryException;
	}

	public void setDiscoveryException(Exception discoveryException) {
		this.discoveryException = discoveryException;
	}

	public PlanId getPlanId() {
		return planId;
	}

	public PlanId getDiscoveredByPlanId() {
		return discoveredByPlanId;
	}
}
