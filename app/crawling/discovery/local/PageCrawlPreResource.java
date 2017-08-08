package crawling.discovery.local;

import java.net.URI;

import crawling.discovery.execution.PlanId;
import crawling.discovery.html.HttpResponseFile;
import crawling.discovery.planning.PreResource;
import datadefinitions.inventory.InvType;
import persistence.PageCrawl;

public class PageCrawlPreResource extends PreResource {

	protected final Long pageCrawlId;
	
	protected boolean inventory = false;

	protected boolean usedRoot = false;
	protected boolean newRoot = false;
	protected boolean generalRoot = false;
	
	protected boolean discoveredNewRoot = false;
	protected boolean discoveredUsedRoot = false;
	
	protected InvType invType = null;

	public PageCrawlPreResource(PageCrawl pageCrawl, HttpResponseFile responseFile, PreResource parent, PlanId planId, PlanId discoveredByPlanId) {
		super(pageCrawl.getUri(), responseFile, parent, planId, discoveredByPlanId);
		this.pageCrawlId = pageCrawl.getPageCrawlId();
		this.inventory = pageCrawl.getPagedInventory();
		this.usedRoot = pageCrawl.getUsedRoot();
		this.generalRoot = pageCrawl.getGeneralRoot();
		if(pageCrawl.getSiteCrawl().getNewInventoryRoot() != null && pageCrawl.getSiteCrawl().getNewInventoryRoot().equals(pageCrawl)){
			this.discoveredNewRoot = true;
		}
		if(pageCrawl.getSiteCrawl().getUsedInventoryRoot() != null && pageCrawl.getSiteCrawl().getUsedInventoryRoot().equals(pageCrawl)){
			this.discoveredUsedRoot = true;
		}
		this.invType = pageCrawl.getInvType();
	}

	public PageCrawlPreResource(URI uri, PreResource parent, PlanId planId, PlanId discoveredByPlanId) {
		super(uri, parent, planId, discoveredByPlanId);
		this.pageCrawlId = null;
	}

	public boolean isInventory() {
		return inventory;
	}

	public void setInventory(boolean inventory) {
		this.inventory = inventory;
	}

	public boolean isUsedRoot() {
		return usedRoot;
	}

	public void setUsedRoot(boolean usedRoot) {
		this.usedRoot = usedRoot;
	}

	public boolean isNewRoot() {
		return newRoot;
	}

	public void setNewRoot(boolean newRoot) {
		this.newRoot = newRoot;
	}

	public boolean isGeneralRoot() {
		return generalRoot;
	}

	public void setGeneralRoot(boolean generalRoot) {
		this.generalRoot = generalRoot;
	}

	public boolean isDiscoveredNewRoot() {
		return discoveredNewRoot;
	}

	public void setDiscoveredNewRoot(boolean discoveredNewRoot) {
		this.discoveredNewRoot = discoveredNewRoot;
	}

	public boolean isDiscoveredUsedRoot() {
		return discoveredUsedRoot;
	}

	public void setDiscoveredUsedRoot(boolean discoveredUsedRoot) {
		this.discoveredUsedRoot = discoveredUsedRoot;
	}

	public InvType getInvType() {
		return invType;
	}

	public void setInvType(InvType invType) {
		this.invType = invType;
	}

	public Long getPageCrawlId() {
		return pageCrawlId;
	}
}
