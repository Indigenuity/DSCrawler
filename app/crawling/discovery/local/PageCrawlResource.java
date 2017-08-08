package crawling.discovery.local;

import java.net.URI;

import org.jsoup.nodes.Document;

import crawling.discovery.entities.FlushableResource;
import crawling.discovery.entities.Resource;
import crawling.discovery.entities.ResourceId;
import crawling.discovery.execution.PlanId;
import crawling.discovery.html.HttpResponseFile;
import crawling.discovery.planning.PreResource;
import datadefinitions.inventory.InvType;
import persistence.PageCrawl;
import play.db.jpa.JPA;
import sites.utilities.PageCrawlLogic;

public class PageCrawlResource extends Resource {
	
	protected final Long pageCrawlId;
	
	protected boolean inventory = false;

	protected boolean usedRoot = false;
	protected boolean newRoot = false;
	protected boolean generalRoot = false;
	
	protected InvType invType = null;
	
	public PageCrawlResource(Object source, Resource parent, ResourceId resourceId) {
		super(source, parent, resourceId);
		pageCrawlId = null;
	}
	
	public PageCrawlResource(PageCrawlPreResource preResource, Resource parent, ResourceId resourceId){
		super(preResource, parent, resourceId);
		
		if(preResource.getValue() != null){
			this.pageCrawlId = preResource.getPageCrawlId();
			this.inventory = preResource.isInventory();
			this.usedRoot = preResource.isUsedRoot();
			this.generalRoot = preResource.isGeneralRoot();
			this.invType = preResource.getInvType();
		} else {
			pageCrawlId = null;
		}
	}
	
	public HttpResponseFile getResponseFile(){
		return (HttpResponseFile)getValue();
	}
	
	public URI getUri(){
		return (URI) getSource();
	}

	public Long getPageCrawlId() {
		return pageCrawlId;
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

	public InvType getInvType() {
		return invType;
	}

	public void setInvType(InvType invType) {
		this.invType = invType;
	}

	public boolean isGeneralRoot() {
		return generalRoot;
	}

	public void setGeneralRoot(boolean generalRoot) {
		this.generalRoot = generalRoot;
	}
	
}
