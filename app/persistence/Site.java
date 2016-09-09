package persistence;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.hibernate.annotations.CollectionId;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import datadefinitions.WebProvider;
import places.PlacesPage;
import utilities.DSFormatter;

@Entity
@Audited(withModifiedFlag=true)
@Table(indexes = {@Index(name = "domain_index", columnList="domain",     unique = false)})
public class Site {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long siteId;
	
	/************************* Basics ***************************************/
	
	private String domain;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String homepage;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String standardizedHomepage;
	
	private Date createdDate;
	
	/**************************************  Relationships **********************************/
	@ManyToOne
	@NotAudited
	private PlacesPage placesPage;
	
	@OneToMany(fetch=FetchType.LAZY)
	@NotAudited
	@JoinTable(name="site_sitecrawl", 
			joinColumns={@JoinColumn(name="Site_siteId")},
		    inverseJoinColumns={@JoinColumn(name="crawls_siteCrawlId")})
	private List<SiteCrawl> crawls = new ArrayList<SiteCrawl>();
	
	@OneToMany(fetch=FetchType.LAZY)
	@NotAudited
	@JoinTable(name="site_mobilecrawl", 
			joinColumns={@JoinColumn(name="Site_siteId")},
		    inverseJoinColumns={@JoinColumn(name="mobileCrawls_mobileCrawlId")})
	private List<MobileCrawl> mobileCrawls = new ArrayList<MobileCrawl>();
	
	
	@OneToOne
	private Site forwardsTo;
	
	@OneToOne
	private Site manualForwardsTo;
	
	@OneToOne
	@NotAudited
	private UrlCheck urlCheck;
	
	
	
	/******************************* Collections *************************************************/
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	@ElementCollection(fetch=FetchType.LAZY)
	@NotAudited
	private Set<String> redirectUrls = new HashSet<String>();
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	@ElementCollection(fetch=FetchType.LAZY)
	@NotAudited
	private Set<String> groupUrls = new HashSet<String>();
	
	@Column(nullable = true, columnDefinition="varchar(255)")
	@ElementCollection(fetch=FetchType.LAZY)
	@NotAudited
	private Set<String> cities = new HashSet<String>();
	
	/************************* Single Attributes *****************************************/
	
	private boolean sharedSite = false;	//If this site is the only one on the domain, as opposed to PAACO sites 
	
	@Enumerated(EnumType.STRING)
	private SiteStatus siteStatus = SiteStatus.UNVALIDATED;
	
	
	
	private Date redirectResolveDate;
	
	private boolean maybeDefunct = false;
	private boolean defunct = false;
	private boolean notInterested = false;
	private boolean oemSite = false;
	private boolean franchise = false;
	private boolean groupSite = false;
	private boolean crawlerProtected = false;
	private Boolean languagePath = false;
	private Boolean languageQuery = false;
	
	public enum SiteStatus {
		UNVALIDATED, INVALID, ACTIVE, NEEDS_REVIEW, REDIRECTS, DEFUNCT, APPROVED, TEMP_DEFUNCT, SUSPECTED_DUPLICATE, OTHER_ISSUE, MANUALLY_REDIRECTS;
	}
	
	public Site(){}
	public Site(String homepage){
		this.setHomepage(homepage);
	}
	
	
	public long getSiteId() {
		return siteId;
	}

	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}

	public Boolean getLanguagePath() {
		return languagePath;
	}

	public void setLanguagePath(Boolean languagePath) {
		this.languagePath = languagePath;
	}

	public Boolean getLanguageQuery() {
		return languageQuery;
	}

	public void setLanguageQuery(Boolean languageQuery) {
		this.languageQuery = languageQuery;
	}

	public String getDomain() {
		return domain;
	}

	private void setDomain(String domain) { 
		this.domain = domain;
	}

	public String getHomepage() {
		return homepage;
	}

	public void setHomepage(String homepage) {
		if(homepage == null || homepage.length() > 4000){
			throw new IllegalArgumentException("Can't set URL with length > 4000 as homepage of Site");
		}
		try {
			URL url = new URL(homepage);
			this.setDomain(DSFormatter.removeWww(url.getHost()));
		} catch (MalformedURLException e) {
//			System.out.println();
//			throw new IllegalArgumentException("Can't have malformed url as homepage : " + homepage);
		}
		this.homepage = homepage;
	}

	public List<SiteCrawl> getCrawls() {
		return crawls;
	}

	public void setCrawls(List<SiteCrawl> crawls) {
		this.crawls.clear();
		this.crawls.addAll(crawls);
	}
	
	public void addCrawl(SiteCrawl crawl) {
		this.crawls.add(crawl);
	}
	
	public SiteCrawl getLatestCrawl() {
		SiteCrawl returned = null;
		Date mostRecent = null;
		for(SiteCrawl siteCrawl : crawls) {
			Date date = siteCrawl.getCrawlDate();
			if(mostRecent == null || date.compareTo(mostRecent) < 0) {
				mostRecent = date;
				returned = siteCrawl;
			}
		}
		return returned;
	}
	
	public SiteCrawl getLatestWithWebProvider() {
		Date mostRecent = null;
		SiteCrawl returned = null;
		
		for(SiteCrawl siteCrawl : crawls) {
			Date date = siteCrawl.getCrawlDate();
			WebProvider wp = siteCrawl.getInferredWebProvider();
			if(wp != null && wp != WebProvider.NONE && (mostRecent == null || date.compareTo(mostRecent) < 0)) {
				mostRecent = date;
				returned = siteCrawl;
			}
		}
		return returned;
	}
	
	public List<MobileCrawl> getMobileCrawls() {
		return mobileCrawls;
	}

	public void setMobileCrawls(List<MobileCrawl> mobileCrawls) {
		this.mobileCrawls.clear();
		this.mobileCrawls.addAll(mobileCrawls);
	}
	
	public void addMobileCrawl(MobileCrawl mobileCrawl) {
		this.mobileCrawls.add(mobileCrawl);
	}
	
	public MobileCrawl getLatestMobileCrawl() {
		MobileCrawl returned = null;
		java.util.Date mostRecent = null;
		for(MobileCrawl mobileCrawl : mobileCrawls) {
			java.util.Date date = mobileCrawl.getCrawlDate();
			if(mostRecent == null || date.compareTo(mostRecent) < 0) {
				mostRecent = date;
				returned = mobileCrawl;
			}
		}
		return returned;
	}

	public Date getRedirectResolveDate() {
		return redirectResolveDate;
	}

	public void setRedirectResolveDate(Date homepageConfirmed) {
		this.redirectResolveDate = homepageConfirmed;
	}


	public Set<String> getRedirectUrls() {
		return redirectUrls;
	}

	public void setRedirectUrls(Set<String> redirectUrls) {
		this.redirectUrls.clear();
		for(String item : redirectUrls){
			this.redirectUrls.add(DSFormatter.truncate(item, 4000));
		}
	}
	
	public void addRedirectUrl(String redirectUrl) {
		this.redirectUrls.add(DSFormatter.truncate(redirectUrl, 4000));
	}
	public Set<String> getGroupUrls() {
		return groupUrls;
	}

	public void setGroupUrls(Set<String> groupUrls) {
		this.groupUrls.clear();
		for(String item : groupUrls){
			this.groupUrls.add(DSFormatter.truncate(item, 4000));
		}
	}
	
	public void addGroupUrl(String groupUrl) {
		this.groupUrls.add(DSFormatter.truncate(groupUrl, 4000));
	}

	public boolean isMaybeDefunct() {
		return maybeDefunct;
	}

	public void setMaybeDefunct(boolean maybeDefunct) {
		this.maybeDefunct = maybeDefunct;
	}

	public boolean isDefunct() {
		return defunct;
	}

	public void setDefunct(boolean defunct) {
		this.defunct = defunct;
	}

	public String getStandardizedHomepage() {
		return standardizedHomepage;
	}

	public void setStandardizedHomepage(String standardizedHomepage) {
		this.standardizedHomepage = standardizedHomepage;
	}

	public void addCrawls(List<SiteCrawl> siteCrawls) {
		this.crawls.addAll(siteCrawls);
	}

	public boolean isNotInterested() {
		return notInterested;
	}

	public void setNotInterested(boolean notInterested) {
		this.notInterested = notInterested;
	}

	public boolean isOemSite() {
		return oemSite;
	}

	public void setOemSite(boolean oemSite) {
		this.oemSite = oemSite;
	}

	public boolean isGroupSite() {
		return groupSite;
	}

	public void setGroupSite(boolean groupSite) {
		this.groupSite = groupSite;
	}

	public boolean isCrawlerProtected() {
		return crawlerProtected;
	}

	public void setCrawlerProtected(boolean crawlerProtected) {
		this.crawlerProtected = crawlerProtected;
	}

	public boolean isFranchise() {
		return franchise;
	}

	public void setFranchise(boolean franchise) {
		this.franchise = franchise;
	}


	public PlacesPage getPlacesPage() {
		return placesPage;
	}

	public void setPlacesPage(PlacesPage placesPage) {
		this.placesPage = placesPage;
	}

	public Set<String> getCities() {
		return cities;
	}

	public void setCities(Set<String> cities) {
		this.cities.clear();
		this.cities.addAll(cities);
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public boolean isSharedSite() {
		return sharedSite;
	}

	public void setSharedSite(boolean sharedSite) {
		this.sharedSite = sharedSite;
	}

	public static boolean isBoolean(String fieldName) {
		for(Field field : Site.class.getDeclaredFields()) {
			if(field.getType() == boolean.class && field.getName().equals(fieldName)){
				return true;
			}
		}
		return false;
	}

	public SiteStatus getSiteStatus() {
		return siteStatus;
	}

	public void setSiteStatus(SiteStatus siteStatus) {
		this.siteStatus = siteStatus;
	}

	public Boolean isCrawlable() {
		return siteStatus == SiteStatus.APPROVED && !sharedSite;
	}
	public Site getForwardsTo() {
		return forwardsTo;
	}
	public void setForwardsTo(Site forwardsTo) {
		this.forwardsTo = forwardsTo;
	}
	public Site getManualForwardsTo() {
		return manualForwardsTo;
	}
	public void setManualForwardsTo(Site manualForwardsTo) {
		this.manualForwardsTo = manualForwardsTo;
	}
	public UrlCheck getUrlCheck() {
		return urlCheck;
	}
	public void setUrlCheck(UrlCheck urlCheck) {
		this.urlCheck = urlCheck;
	}

	
}
