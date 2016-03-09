package persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import datadefinitions.OEM;
import utilities.DSFormatter;


// Eager fetch all collections.  Any time you're dealing with individual pages, assume you need data

@Entity
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
	
	private String title;
	private String h1;
	
	private int numImages;
	private int numAltImages;
	

	@Column(nullable = true, columnDefinition="varchar(4000)")
	@ElementCollection(fetch=FetchType.EAGER)
	private Set<String> links = new HashSet<String>();
	
//	@Transient 
	@ManyToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
	private Set<Metatag> metatags = new HashSet<Metatag>();
	
	@ManyToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
	private Set<ImageTag> imageTags = new HashSet<ImageTag>();
	
	@OneToOne(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
	private Metatag metaTitle = null;
	 
	@OneToOne(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
	private Metatag metaDescription = null;
	
	@ElementCollection
	@MapKeyEnumerated(EnumType.STRING)
	private Map<OEM, Integer> brandMatchCounts = new HashMap<OEM, Integer>();
	
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean urlCityQualifier = false;
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean urlStateQualifier = false;
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean urlMakeQualifier = false;
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean titleCityQualifier = false;
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean titleStateQualifier = false;
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean titleMakeQualifier = false;
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean h1CityQualifier = false;
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean h1StateQualifier = false;
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean h1MakeQualifier = false;
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean metaDescriptionCityQualifier = false;
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean metaDescriptionStateQualifier = false;
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean metaDescriptionMakeQualifier = false;
	
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean descriptionLength = false;
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean titleLength = false;
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean titleKeywordStuffing = false;
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean urlClean = false;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER, orphanRemoval=true)
	private Set<InventoryNumber> inventoryNumbers = new HashSet<InventoryNumber>();
	

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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = DSFormatter.truncate(title, 255);
	}

	public String getH1() {
		return h1;
	}

	public void setH1(String h1) {
		this.h1 = DSFormatter.truncate(h1, 255);
	}

	public int getNumImages() {
		return numImages;
	}

	public void setNumImages(int numImages) {
		this.numImages = numImages;
	}

	public int getNumAltImages() {
		return numAltImages;
	}

	public void setNumAltImages(int numAltImages) {
		this.numAltImages = numAltImages;
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
	
	public Set<Metatag> getMetatags() {
		return metatags;
	}

	public void setMetatags(Collection<Metatag> metatags) {
		this.metatags.clear();
		this.metatags.addAll(metatags);
	}
	
	public boolean addMetatag(Metatag metatag) {
		return this.metatags.add(metatag);
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

	public Set<ImageTag> getImageTags() {
		return imageTags;
	}

	public void setImageTags(Collection<ImageTag> imageTags) {
		this.imageTags.clear();
		this.imageTags.addAll(imageTags);
	}
	
	public boolean addImageTag(ImageTag imageTag) {
		return imageTags.add(imageTag);
	}

	public Metatag getMetaTitle() {
		return metaTitle;
	}

	public void setMetaTitle(Metatag metaTitle) {
		this.metaTitle = metaTitle;
	}

	public Metatag getMetaDescription() {
		return metaDescription;
	}

	public void setMetaDescription(Metatag metaDescription) {
		this.metaDescription = metaDescription;
	}

	public boolean isUrlMakeQualifier() {
		return urlMakeQualifier;
	}

	public void setUrlMakeQualifier(boolean urlMakeQualifier) {
		this.urlMakeQualifier = urlMakeQualifier;
	}

	public boolean isTitleMakeQualifier() {
		return titleMakeQualifier;
	}

	public void setTitleMakeQualifier(boolean titleMakeQualifier) {
		this.titleMakeQualifier = titleMakeQualifier;
	}

	public boolean isH1MakeQualifier() {
		return h1MakeQualifier;
	}

	public void setH1MakeQualifier(boolean h1MakeQualifier) {
		this.h1MakeQualifier = h1MakeQualifier;
	}

	public boolean isDescriptionLength() {
		return descriptionLength;
	}

	public void setDescriptionLength(boolean descriptionLength) {
		this.descriptionLength = descriptionLength;
	}

	public boolean isTitleLength() {
		return titleLength;
	}

	public void setTitleLength(boolean titleLength) {
		this.titleLength = titleLength;
	}

	public boolean isTitleKeywordStuffing() {
		return titleKeywordStuffing;
	}

	public void setTitleKeywordStuffing(boolean titleKeywordStuffing) {
		this.titleKeywordStuffing = titleKeywordStuffing;
	}

	public boolean isUrlClean() {
		return urlClean;
	}

	public void setUrlClean(boolean urlClean) {
		this.urlClean = urlClean;
	}

	public boolean isUrlCityQualifier() {
		return urlCityQualifier;
	}

	public void setUrlCityQualifier(boolean urlCityQualifier) {
		this.urlCityQualifier = urlCityQualifier;
	}

	public boolean isUrlStateQualifier() {
		return urlStateQualifier;
	}

	public void setUrlStateQualifier(boolean urlStateQualifier) {
		this.urlStateQualifier = urlStateQualifier;
	}

	public boolean isTitleCityQualifier() {
		return titleCityQualifier;
	}

	public void setTitleCityQualifier(boolean titleCityQualifier) {
		this.titleCityQualifier = titleCityQualifier;
	}

	public boolean isTitleStateQualifier() {
		return titleStateQualifier;
	}

	public void setTitleStateQualifier(boolean titleStateQualifier) {
		this.titleStateQualifier = titleStateQualifier;
	}

	public boolean isH1CityQualifier() {
		return h1CityQualifier;
	}

	public void setH1CityQualifier(boolean h1CityQualifier) {
		this.h1CityQualifier = h1CityQualifier;
	}

	public boolean isH1StateQualifier() {
		return h1StateQualifier;
	}

	public void setH1StateQualifier(boolean h1StateQualifier) {
		this.h1StateQualifier = h1StateQualifier;
	}

	public boolean isMetaDescriptionCityQualifier() {
		return metaDescriptionCityQualifier;
	}

	public void setMetaDescriptionCityQualifier(boolean metaDescriptionCityQualifier) {
		this.metaDescriptionCityQualifier = metaDescriptionCityQualifier;
	}

	public boolean isMetaDescriptionStateQualifier() {
		return metaDescriptionStateQualifier;
	}

	public void setMetaDescriptionStateQualifier(
			boolean metaDescriptionStateQualifier) {
		this.metaDescriptionStateQualifier = metaDescriptionStateQualifier;
	}

	public boolean isMetaDescriptionMakeQualifier() {
		return metaDescriptionMakeQualifier;
	}

	public void setMetaDescriptionMakeQualifier(boolean metaDescriptionMakeQualifier) {
		this.metaDescriptionMakeQualifier = metaDescriptionMakeQualifier;
	}

	public Set<InventoryNumber> getInventoryNumbers() {
		return inventoryNumbers;
	}

	public void setInventoryNumbers(Set<InventoryNumber> inventoryNumbers) {
		this.inventoryNumbers.clear();
		this.inventoryNumbers.addAll(inventoryNumbers);
	}

	public Map<OEM, Integer> getBrandMatchCounts() {
		return brandMatchCounts;
	}

	public void setBrandMatchCounts(Map<OEM, Integer> brandMatchCounts) {
		this.brandMatchCounts.clear();
		this.brandMatchCounts.putAll(brandMatchCounts);
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
	
	
	
	
	
	
}
