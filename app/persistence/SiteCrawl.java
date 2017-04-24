package persistence;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import analysis.SiteCrawlAnalysis;
import utilities.DSFormatter;
import datadefinitions.GeneralMatch;
import datadefinitions.OEM;
import datadefinitions.Scheduler;
import datadefinitions.WebProvider;
import datadefinitions.newdefinitions.InventoryType;
import datadefinitions.newdefinitions.WPAttribution;
import datadefinitions.newdefinitions.WPClue;

//Lazy fetch all collections

@Entity
@NamedEntityGraph(name="siteCrawlFull", 
	attributeNodes={
		@NamedAttributeNode(value="pageCrawls", subgraph="pageCrawlFull"),
		
	}, 
	subgraphs = {
		@NamedSubgraph(name="pageCrawlFull", attributeNodes = {
			@NamedAttributeNode("links"),
		})
}
	
)
public class SiteCrawl {
	
	public enum FileStatus {
		PRIMARY, SECONDARY, DELETED
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected long siteCrawlId;
	
	@ManyToOne(cascade=CascadeType.DETACH, fetch=FetchType.LAZY)
	@JoinTable(name="site_sitecrawl", 
			joinColumns={@JoinColumn(name="crawls_siteCrawlId")},
		    inverseJoinColumns={@JoinColumn(name="Site_siteId")})
	private Site site;
	
	/**************Crawl Basics and Config***********************************************/
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String seed;
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String resolvedSeed;
	
	private Date crawlDate = new Date();
	private int crawlDepth = 0;
	private Integer maxDepth = 1;
	private Integer maxPages = 5000;
	private boolean followNonUnique = true;
	private boolean homepageCrawl = false;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	@ElementCollection(fetch=FetchType.LAZY)
	private Set<String> uniqueCrawledPageUrls = new HashSet<String>();		//Involves chopping off query strings
	@Column(columnDefinition="varchar(4000)")
	@ElementCollection(fetch=FetchType.LAZY)
	private Set<String> crawledUrls = new HashSet<String>();		//Includes failed urls
	@Column(columnDefinition="varchar(4000)")
	@ElementCollection(fetch=FetchType.LAZY)
	private Set<String> crawledPaths = new HashSet<String>();
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
	
	@Column(nullable = true, columnDefinition="varchar(1000)")
	private String storageFolder;
	
	private String localFolderName;	//Guaranteed to be set in the constructor
	
	private boolean maxPagesReached = false;
	private int numRepeatedUrls = 0;
	private int numRetrievedFiles = 0;
	private int numLargeFiles = 0;
	private boolean smallCrawlApproved = false;
	
	/****************************************  Stateful metadata ***********************/
	
	protected FileStatus fileStatus = FileStatus.PRIMARY;
	
	
	/********************************************* Relationships *********************************/
	@OneToMany(fetch=FetchType.LAZY, cascade={CascadeType.DETACH, CascadeType.REMOVE}, orphanRemoval= true)
	@JoinTable(name="sitecrawl_pagecrawl",
		joinColumns={@JoinColumn(name="SiteCrawl_siteCrawlId")},
		inverseJoinColumns={@JoinColumn(name="pageCrawls_pageCrawlId")})
	@LazyCollection(LazyCollectionOption.EXTRA)
	private Set<PageCrawl> pageCrawls = new HashSet<PageCrawl>();
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinTable(name="sitecrawl_newInventorypage")
	private PageCrawl newInventoryPage;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinTable(name="sitecrawl_usedInventorypage")
	private PageCrawl usedInventoryPage;
	
	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinTable(name="sitecrawl_newInventoryroot")
	private PageCrawl newInventoryRoot;
	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinTable(name="sitecrawl_usedInventoryroot")
	private PageCrawl usedInventoryRoot;
	
	private Boolean inventoryCrawlSuccess = true;
	
//	@OneToOne(mappedBy="siteCrawl")
//	private SiteCrawlAnalysis siteCrawlAnalysis;
	
	
	/******************************************  Calculated Attributes Collections ***********************************/
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	@ElementCollection(fetch=FetchType.LAZY)
	private Set<String> allLinks = new HashSet<String>();
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	@ElementCollection(fetch=FetchType.LAZY)
	private Set<String> intrasiteLinks = new HashSet<String>();
	
	
	
	@SuppressWarnings("unused")
	private SiteCrawl () {}
	
	
	public SiteCrawl(String seed) {
		try {
			URI uri = new URI(seed);
			setLocalFolderName(URLEncoder.encode(uri.getHost(), "UTF-8"));
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Cannot run SiteCrawl on invalid seed : " + seed);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
		this.setSeed(seed);
		
	}
	
	public SiteCrawl(Site site) {
		this(site.getHomepage());
		this.setSite(site);
	}
	
	public PageCrawl getFirstRoot(){
		for(PageCrawl pageCrawl : getPageCrawls()){
			if(pageCrawl.getParentPage() == null){
				return pageCrawl;
			}
		}
		throw new IllegalStateException("SiteCrawl has no root PageCrawls : " + getSiteCrawlId());
	}
	
	public Set<PageCrawl> getRoots(){
		Set<PageCrawl> roots = new HashSet<PageCrawl>();
		for(PageCrawl pageCrawl : getPageCrawls()){
			if(pageCrawl.getParentPage() == null){
				roots.add(pageCrawl);
			}
		}
		return roots;
	}
	
	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public String getSeed() {
		return seed;
	}

	public void setSeed(String seed) {
		if(seed == null || seed.length() > 4000){
			throw new IllegalArgumentException("Can't set URL with length > 4000 as seed of SiteCrawl");
		}
		this.seed = seed;
	}

	public String getResolvedSeed() {
		return resolvedSeed;
	}

	public void setResolvedSeed(String resolvedSeed) {
		if(resolvedSeed == null || resolvedSeed.length() > 4000){
			throw new IllegalArgumentException("Can't set URL with length > 4000 as resolved seed of SiteCrawl");
		}
		this.resolvedSeed = resolvedSeed;
	}

	public Date getCrawlDate() {
		return crawlDate;
	}

	public void setCrawlDate(Date crawlDate) {
		this.crawlDate = crawlDate;
	}

	

	public String getStorageFolder() {
		return storageFolder;
	}

	public void setStorageFolder(String storageFolder) {
		this.storageFolder = storageFolder;
	}

	public boolean isMaxPagesReached() {
		return maxPagesReached;
	}

	public void setMaxPagesReached(boolean maxPagesReached) {
		this.maxPagesReached = maxPagesReached;
	}

	public int getNumRetrievedFiles() {
		return numRetrievedFiles;
	}

	public void setNumRetrievedFiles(int numRetrievedFiles) {
		this.numRetrievedFiles = numRetrievedFiles;
	}

	public int getNumLargeFiles() {
		return numLargeFiles;
	}

	public void setNumLargeFiles(int numLargeFiles) {
		this.numLargeFiles = numLargeFiles;
	}

	public int getNumRepeatedUrls() {
		return numRepeatedUrls;
	}

	public void setNumRepeatedUrls(int numRepeatedUrls) {
		this.numRepeatedUrls = numRepeatedUrls;
	}

	public long getSiteCrawlId() {
		return siteCrawlId;
	}

	public void setSiteCrawlId(long siteCrawlId) {
		this.siteCrawlId = siteCrawlId;
	}

	public boolean isHomepageCrawl() {
		return homepageCrawl;
	}

	public void setHomepageCrawl(boolean homepageCrawl) {
		this.homepageCrawl = homepageCrawl;
	}

	public boolean isSmallCrawlApproved() {
		return smallCrawlApproved;
	}

	public void setSmallCrawlApproved(boolean smallCrawlApproved) {
		this.smallCrawlApproved = smallCrawlApproved;
	}

	public boolean isFollowNonUnique() {
		return followNonUnique;
	}

	public void setFollowNonUnique(boolean followNonUnique) {
		this.followNonUnique = followNonUnique;
	}

	public Set<String> getAllLinks() {
		return allLinks;
	}

	public void setAllLinks(Set<String> allLinks) {
		this.allLinks.clear();
		for(String item : allLinks){
			this.allLinks.add(DSFormatter.truncate(item, 4000));
		}
	}
	
	public boolean addLink(String link) {
		return this.allLinks.add(DSFormatter.truncate(link, 4000));
	}

	public Set<String> getIntrasiteLinks() {
		return intrasiteLinks;
	}

	public void setIntrasiteLinks(Set<String> intrasiteLinks) {
		this.intrasiteLinks.clear();
		for(String item : intrasiteLinks){
			this.intrasiteLinks.add(DSFormatter.truncate(item, 4000));
		}
	}

	public boolean addIntrasiteLink(String intrasiteLink) {
		return this.intrasiteLinks.add(DSFormatter.truncate(intrasiteLink, 4000));
	}

	public Set<String> getUniqueCrawledPageUrls() {
		return uniqueCrawledPageUrls;
	}

	public void setUniqueCrawledPageUrls(Set<String> uniqueCrawledPageUrls) {
		this.uniqueCrawledPageUrls.clear();
		for(String item : uniqueCrawledPageUrls){
			this.uniqueCrawledPageUrls.add(DSFormatter.truncate(item, 4000));
		}
	}
	
	public boolean addUniqueCrawledPageUrl(String uniqueCrawledPageUrl) {
		return this.uniqueCrawledPageUrls.add(DSFormatter.truncate(uniqueCrawledPageUrl, 4000));
	}
	
	public Set<String> getCrawledUrls() {
		return crawledUrls;
	}

	public void setCrawledUrls(Set<String> crawledUrls) {
		this.crawledUrls.clear();
		for(String url : crawledUrls){
			this.crawledUrls.add(DSFormatter.truncate(url, 4000));
		}
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
	
	public Set<String> getCrawledPaths() {
		return crawledPaths;
	}

	public void setCrawledPaths(Set<String> crawledPaths) {
		this.crawledPaths.clear();
		for(String url : crawledPaths){
			this.crawledPaths.add(DSFormatter.truncate(url, 4000));
		}
	}
	
	public boolean addCrawledPath(String crawledPath) {
		return this.crawledPaths.add(DSFormatter.truncate(crawledPath, 4000));
	}

	public boolean addCrawledUrl(String crawledUrl) {
		return this.crawledUrls.add(DSFormatter.truncate(crawledUrl, 4000));
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

	public int getCrawlDepth() {
		return crawlDepth;
	}

	public void setCrawlDepth(int crawlDepth) {
		this.crawlDepth = crawlDepth;
	}

	public Set<PageCrawl> getPageCrawls() {
		return pageCrawls;
	}

	public void setPageCrawls(Set<PageCrawl> pageCrawls) {
		this.pageCrawls.clear();
		this.pageCrawls.addAll(pageCrawls);
	}
	
	public void addPageCrawl(PageCrawl pageCrawl) {
		this.pageCrawls.add(pageCrawl);
//		System.out.println("removed : " + this.unCrawledUrls.remove(pageCrawl.getUrl()));
//		System.out.println("removed : " + pageCrawl.getUrl());
	}
	
	public PageCrawl getNewInventoryPage() {
		return newInventoryPage;
	}

	public void setNewInventoryPage(PageCrawl newInventoryPage) {
		if(newInventoryPage != null)
			pageCrawls.add(newInventoryPage);
		this.newInventoryPage = newInventoryPage;
	}

	public PageCrawl getUsedInventoryPage() {
		return usedInventoryPage;
	}

	public void setUsedInventoryPage(PageCrawl usedInventoryPage) {
		if(usedInventoryPage != null)
			pageCrawls.add(usedInventoryPage);
		this.usedInventoryPage = usedInventoryPage;
	}
	

	public FileStatus getFileStatus() {
		return fileStatus;
	}


	public void setFileStatus(FileStatus fileStatus) {
		this.fileStatus = fileStatus;
	}


	public void initPageData() {
		pageCrawls.size();
	}
	
	public void initSiteCrawlData() {
		allLinks.size();
		intrasiteLinks.size();
		uniqueCrawledPageUrls.size();
		crawledUrls.size();
		failedUrls.size();
	}
	
	public void initAll() {
		initPageData();
		initSiteCrawlData();
	}


	public PageCrawl getNewInventoryRoot() {
		return newInventoryRoot;
	}


	public void setNewInventoryRoot(PageCrawl newInventoryRoot) {
		this.newInventoryRoot = newInventoryRoot;
	}


	public PageCrawl getUsedInventoryRoot() {
		return usedInventoryRoot;
	}


	public void setUsedInventoryRoot(PageCrawl usedInventoryRoot) {
		this.usedInventoryRoot = usedInventoryRoot;
	}


	public Integer getMaxDepth() {
		return maxDepth;
	}


	public void setMaxDepth(Integer maxDepth) {
		this.maxDepth = maxDepth;
	}


	public Integer getMaxPages() {
		return maxPages;
	}


	public void setMaxPages(Integer maxPages) {
		this.maxPages = maxPages;
	}


	public Boolean getInventoryCrawlSuccess() {
		return inventoryCrawlSuccess;
	}


	public void setInventoryCrawlSuccess(Boolean inventoryCrawlSuccess) {
		this.inventoryCrawlSuccess = inventoryCrawlSuccess;
	}


	public String getLocalFolderName() {
		return localFolderName;
	}


	public void setLocalFolderName(String localFolderName) {
		this.localFolderName = localFolderName;
	}
	
}
