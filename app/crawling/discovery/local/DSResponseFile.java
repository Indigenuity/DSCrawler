package crawling.discovery.local;

import java.io.File;
import java.net.URI;

import crawling.discovery.html.HttpResponseFile;
import datadefinitions.inventory.InvType;

public class DSResponseFile extends HttpResponseFile {

	protected boolean inventory = false;

	protected boolean usedRoot = false;
	protected boolean newRoot = false;
	
	protected boolean discoveredNewRoot = false;
	protected boolean discoveredUsedRoot = false;
	
	protected InvType invType = null;
	
	protected Long pageCrawlId;
	
	public DSResponseFile(URI uri, File storageFile) {
		super(uri, storageFile);
		// TODO Auto-generated constructor stub
	}
	
	public DSResponseFile(HttpResponseFile responseFile){
		this(responseFile.getUri(), responseFile.getFile());
		setRedirectedUri(responseFile.getRedirectedUri());
		setFile(responseFile.getFile());
		setLocale(responseFile.getLocale());
		setHeaders(responseFile.getHeaders());
		setStatusCode(responseFile.getStatusCode());
		
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

	public Long getPageCrawlId() {
		return pageCrawlId;
	}

	public void setPageCrawlId(Long pageCrawlId) {
		this.pageCrawlId = pageCrawlId;
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
	
	

}
