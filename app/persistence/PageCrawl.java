package persistence;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Date;
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
import org.jsoup.nodes.Document;

import crawling.discovery.html.HtmlResource;
import datadefinitions.inventory.InvType;
import sites.utilities.PageCrawlLogic;
import utilities.DSFormatter;


// Eager fetch all collections.  Any time you're dealing with individual pages, assume you need data

@Entity
public class PageCrawl implements HtmlResource{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long pageCrawlId;
	
	@ManyToOne()
	@JoinTable(name="sitecrawl_pagecrawl",
			joinColumns={@JoinColumn(name="pageCrawls_pageCrawlId")},
			inverseJoinColumns={@JoinColumn(name="SiteCrawl_siteCrawlId")})
	private SiteCrawl siteCrawl;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private PageCrawl parentPage;
	
	@OneToMany(mappedBy="parentPage", fetch=FetchType.LAZY)
	private Set<PageCrawl> childPages = new HashSet<PageCrawl>();
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String url;
	
	private Date crawlDate = new Date();
	
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
	
	public Date getCrawlDate() {
		return crawlDate;
	}

	public void setCrawlDate(Date crawlDate) { 
		this.crawlDate = crawlDate;
	}

	@Override
	public Document getDocument() throws Exception { 
		return PageCrawlLogic.getDocument(this);
	}
	
	@Override
	public URI getRedirectedUri(){
		if(getRedirectedUrl() == null){
			return null;
		}
		try {
			return new URI(getRedirectedUrl());
		} catch (URISyntaxException e) {
			throw new UnsupportedOperationException("Can't generate URI for bad redirect uri : " + getRedirectedUrl());
		}
	}
}
