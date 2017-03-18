package persistence;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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
	
	private int httpStatus;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String redirectedUrl;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String errorMessage;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	@ElementCollection(fetch=FetchType.LAZY)
	@Fetch(FetchMode.SELECT)
	private Set<String> links = new HashSet<String>();
	
	@OneToOne(cascade=CascadeType.ALL, orphanRemoval=false)
	private InventoryNumber inventoryNumber = new InventoryNumber();

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

	public int getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(int httpStatus) {
		this.httpStatus = httpStatus;
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

	public void setSiteCrawl(SiteCrawl siteCrawl) {
		this.siteCrawl = siteCrawl;
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
}
