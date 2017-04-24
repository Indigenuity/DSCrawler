package persistence;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import datadefinitions.inventory.InvType;
import utilities.DSFormatter;


// Eager fetch all collections.  Any time you're dealing with individual pages, assume you need data

@Entity
@NamedEntityGraph(name="pageCrawlFull", attributeNodes={ 
		@NamedAttributeNode("links"),
})
public class PageCrawl {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long pageCrawlId;
	
	@ManyToOne
	@JoinTable(name="sitecrawl_pagecrawl",
			joinColumns={@JoinColumn(name="pageCrawls_pageCrawlId")},
			inverseJoinColumns={@JoinColumn(name="SiteCrawl_siteCrawlId")})
	private SiteCrawl siteCrawl;
	
	@ManyToOne
	private PageCrawl parentPage;
	
	@OneToMany(mappedBy="parentPage")
	private Set<PageCrawl> childPages = new HashSet<PageCrawl>();
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String url;
	
	private Integer statusCode;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String path;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String query;
	
	@Column(nullable = true, columnDefinition="varchar(1000)")
	private String filename;
	
	private boolean largeFile = false;
	
	private Boolean newPath = false;
	private Boolean newRoot = false;
	private Boolean usedPath = false;
	private Boolean usedRoot = false;
	private Boolean pagedInventory = false;
	
	@Enumerated(EnumType.STRING)
	private InvType invType;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String redirectedUrl;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String errorMessage;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	@ElementCollection(fetch=FetchType.LAZY)
	@Fetch(FetchMode.SELECT)
	private Set<String> links = new HashSet<String>();
	
	@Column(columnDefinition="varchar(4000)")
	@ElementCollection(fetch=FetchType.LAZY)
	private Set<String> unCrawledUrls = new HashSet<String>();		
	@Column(columnDefinition="varchar(4000)")
	@ElementCollection(fetch=FetchType.LAZY)
	private Set<String> failedUrls = new HashSet<String>();
	
	@ElementCollection(fetch=FetchType.LAZY)
	private Set<String> unCrawledInventoryUrls = new HashSet<String>();		
	@Column(columnDefinition="varchar(4000)")
	@ElementCollection(fetch=FetchType.LAZY)
	private Set<String> failedInventoryUrls = new HashSet<String>();
	
	
	
	@OneToOne(cascade=CascadeType.ALL, orphanRemoval=false)
	private InventoryNumber inventoryNumber = new InventoryNumber();

	public URI getUri(){
		try {
			return new URI(getUrl());
		} catch (URISyntaxException e) {
			throw new IllegalStateException("PageCrawl with invalid uri : " + getPageCrawlId());
		}
	}
	
	public int getDepth(){
		if(this.getParentPage() == null){
			return 0;
		}
		return 1 + this.getParentPage().getDepth();
	}
	
	public long getPageCrawlId() {
		return pageCrawlId;
	}

	public void setPageCrawlId(long pageCrawlId) {
		this.pageCrawlId = pageCrawlId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = DSFormatter.truncate(url, 4000);
	}

	public String getRedirectedUrl() {
		return redirectedUrl;
	}

	public void setRedirectedUrl(String redirectedUrl) {
		this.redirectedUrl = DSFormatter.truncate(redirectedUrl, 400);
	}


	public Set<String> getLinks() {
		return links;
	}

	public void setLinks(Collection<String> links) {
		this.links.clear();
		for(String item : links){
			this.links.add(DSFormatter.truncate(item, 4000));
		}
	}
	
	public boolean addLink(String link) {
		return this.links.add(DSFormatter.truncate(link, 4000));
	}
	
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = DSFormatter.truncate(filename, 1000);
	}

	public SiteCrawl getSiteCrawl() {
		return siteCrawl;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = DSFormatter.truncate(errorMessage, 4000);
	}


	public boolean isLargeFile() {
		return largeFile;
	}

	public void setLargeFile(boolean largeFile) {
		this.largeFile = largeFile;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = DSFormatter.truncate(path);
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = DSFormatter.truncate(query);
	}

	public InventoryNumber getInventoryNumber() {
		return inventoryNumber;
	}

	public void setInventoryNumber(InventoryNumber inventoryNumber) {
		this.inventoryNumber = inventoryNumber;
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public PageCrawl getParentPage() {
		return parentPage;
	}

	public void setParentPage(PageCrawl parentPage) {
		if(parentPage != null && (parentPage.equals(this) || parentPage.getPageCrawlId() == this.getPageCrawlId())){
			throw new IllegalArgumentException("A PageCrawl cannot be a parent to itself");
		}
		this.parentPage = parentPage;
	}

	public Boolean getNewPath() {
		return newPath;
	}

	public void setNewPath(Boolean newPath) {
		this.newPath = newPath;
	}

	public Boolean getNewRoot() {
		return newRoot;
	}

	public void setNewRoot(Boolean newRoot) {
		this.newRoot = newRoot;
	}

	public Boolean getUsedPath() {
		return usedPath;
	}

	public void setUsedPath(Boolean usedPath) {
		this.usedPath = usedPath;
	}

	public Boolean getUsedRoot() {
		return usedRoot;
	}

	public void setUsedRoot(Boolean usedRoot) {
		this.usedRoot = usedRoot;
	}

	public Boolean getPagedInventory() {
		return pagedInventory;
	}

	public void setPagedInventory(Boolean pagedInventory) {
		this.pagedInventory = pagedInventory;
	}

	public InvType getInvType() {
		return invType;
	}

	public void setInvType(InvType invType) {
		this.invType = invType;
	}

	public Set<PageCrawl> getChildPages() {
		return childPages;
	}

	public void addChildPage(PageCrawl childPage) {
		this.childPages.add(childPage);
	}
	
	public Set<String> getUnCrawledUrls() {
		return unCrawledUrls;
	}

	public void setUnCrawledUrls(Set<String> unCrawledUrls) {
		this.unCrawledUrls.clear();
		for(String url : unCrawledUrls){
			this.unCrawledUrls.add(DSFormatter.truncate(url, 4000));
		}
	}
	
	public boolean addUncrawledInventoryUrl(String uncrawledInventoryUrl) {
		return this.unCrawledInventoryUrls.add(DSFormatter.truncate(uncrawledInventoryUrl, 4000));
	}
	public Set<String> getUnCrawledInventoryUrls() {
		return unCrawledInventoryUrls;
	}

	public void setUnCrawledInventoryUrls(Set<String> unCrawledInventoryUrls) {
		this.unCrawledInventoryUrls.clear();
		for(String url : unCrawledInventoryUrls){
			this.unCrawledInventoryUrls.add(DSFormatter.truncate(url, 4000));
		}
	}
	
	public boolean addUncrawledUrl(String uncrawledUrl) {
		return this.unCrawledUrls.add(DSFormatter.truncate(uncrawledUrl, 4000));
	}

	public void setFailedInventoryUrls(Set<String> failedInventoryUrls) {
		this.failedInventoryUrls.clear();
		for(String url : failedInventoryUrls){
			this.failedInventoryUrls.add(DSFormatter.truncate(url, 4000));
		}
	}
	public boolean addFailedInventoryUrl(String failedInventoryUrl) {
		return this.failedInventoryUrls.add(DSFormatter.truncate(failedInventoryUrl, 4000));
	}
	
	public Set<String> getFailedInventoryUrls() {
		return failedInventoryUrls;
	}
	
	public Set<String> getFailedUrls() {
		return failedUrls;
	}

	public void setFailedUrls(Set<String> failedUrls) {
		this.failedUrls.clear();
		for(String url : failedUrls){
			this.failedUrls.add(DSFormatter.truncate(url, 4000));
		}
	}
	
	public boolean addFailedUrl(String failedUrl) {
		return this.failedUrls.add(DSFormatter.truncate(failedUrl, 4000));
	}
	
}
