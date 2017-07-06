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
import javax.persistence.Transient;

import org.hibernate.annotations.Formula;
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
	
	private Date crawlDate = new Date();
	private int crawlDepth = 0;
	private Integer maxDepth = 1;
	private Integer maxPages = 5000;
	private boolean followNonUnique = true;
	
	@Column(nullable = true, columnDefinition="varchar(1000)")
	private String storageFolder;
	
	private String localFolderName;	//Guaranteed to be set in the constructor
	
	private boolean maxPagesReached = false;
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
	private PageCrawl newInventoryRoot;
	@ManyToOne(fetch=FetchType.LAZY)
	private PageCrawl usedInventoryRoot;
	
	private Boolean inventoryCrawlSuccess = true;
	@Formula("inventoryCrawlSuccess AND usedInventoryRoot_pageCrawlId is not null AND newInventoryRoot_pageCrawlId is not null")
	private Boolean satisfactoryInventoryCrawl;
	
//	@OneToOne(mappedBy="siteCrawl")
//	private SiteCrawlAnalysis siteCrawlAnalysis;
	
	
	/******************************************  Calculated Attributes Collections ***********************************/
	
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

	public long getSiteCrawlId() {
		return siteCrawlId;
	}

	public void setSiteCrawlId(long siteCrawlId) {
		this.siteCrawlId = siteCrawlId;
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
	
	public FileStatus getFileStatus() { 
		return fileStatus;
	}

	public void setFileStatus(FileStatus fileStatus) {
		this.fileStatus = fileStatus;
	}

	public void initPageData() {
		pageCrawls.size();
	}
	
	public void initAll() {
		initPageData();
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

	public Boolean getSatisfactoryInventoryCrawl() {
		return satisfactoryInventoryCrawl;
	}
}
