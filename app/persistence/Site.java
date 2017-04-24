package persistence;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import datadefinitions.WebProvider;
import places.PlacesPage;
import sites.SiteLogic;
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
	
	private Date createdDate;
	
	/*********************** Validity **************************************/
	
	//*****URL structure
	private Boolean badUrlStructure = false;			// emails, non-url text, or just no http:// 
	private Boolean defunctDomain = false;				//
	private Boolean defunctPath = false;
	private Boolean uncrawlableDomain = false;
	private Boolean uncrawlablePath = false;
	private Boolean notStandardHomepagePath = false;
	private Boolean notStandardQuery = false;
	private Boolean isEmail = false;
	private Boolean approvedHomepagePath = false;
	private Boolean approvedQuery = false;
	
	private Boolean unapproved = false; 
	
	//*****Request results
	private Boolean httpError = false;
	private Boolean defunctContent = false;
	
	
	@NotAudited
	@Formula("badUrlStructure = 1 OR (notStandardHomepagePath = 1 AND approvedHomepagePath = 0)")
	private Boolean needsReview;
	
	
	/**************************************  Relationships **********************************/
	@ManyToOne
	@NotAudited
	private PlacesPage placesPage;
	
	
	//The most recent crawls for the site should be added to this collection via the "setMostRecentCrawl" method
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
	
	@Enumerated(EnumType.STRING)
	private RedirectType redirectReason;
	
	@ManyToOne
	private Site redirectsTo;
	
	@OneToOne
	private Site forwardsTo;
	
	@OneToOne
	private Site manualForwardsTo;
	
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval=true)
	@NotAudited 
	private UrlCheck urlCheck;
	
	@Formula("(SELECT max(sc.crawlDate) FROM site s "
			+ "join site_sitecrawl ssc on ssc.Site_siteId = s.siteId "
			+ "join sitecrawl sc on ssc.crawls_siteCrawlId = sc.sitecrawlid "
			+ "where s.siteId = siteId)")
	@NotAudited
	private Date mostRecentCrawl;
	
	
	
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
		UNVALIDATED, INVALID, ACTIVE, NEEDS_REVIEW, REDIRECTS, DEFUNCT, APPROVED, TEMP_DEFUNCT, SUSPECTED_DUPLICATE, OTHER_ISSUE, MANUALLY_REDIRECTS, DISAPPROVED;
	}
	public enum RedirectType {
		STANDARDIZATION, HTTP, CLERICAL, PATH_PARING, INFERENCE, QUERY_PARING;
	}
	
	public Site(){
		this.createdDate = new Date();
	}
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

	public void setDomain(String domain) { 
		this.domain = domain;
	}

	public String getHomepage() {
		return homepage;
	}

	public void setHomepage(String homepage) {
		if(homepage == null || homepage.length() > 4000){
			throw new IllegalArgumentException("Can't set URL with length > 4000 as homepage of Site");
		}
		this.homepage = homepage;
		SiteLogic.analyzeUrlStructure(this);
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
		
//		for(SiteCrawl siteCrawl : crawls) {
//			Date date = siteCrawl.getCrawlDate();
//			WebProvider wp = siteCrawl.getInferredWebProvider();
//			if(wp != null && wp != WebProvider.NONE && (mostRecent == null || date.compareTo(mostRecent) < 0)) {
//				mostRecent = date;
//				returned = siteCrawl;
//			}
//		}
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
		return null;
	}

	public void setStandardizedHomepage(String standardizedHomepage) {
//		this.standardizedHomepage = standardizedHomepage;
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
	public Date getMostRecentCrawl() {
		return mostRecentCrawl;
	}
	public void setMostRecentCrawl(Date mostRecentCrawl) {
		this.mostRecentCrawl = mostRecentCrawl;
	}
	public Boolean getBadUrlStructure() {
		return badUrlStructure;
	}
	public void setBadUrlStructure(Boolean badUrlStructure) {
		this.badUrlStructure = badUrlStructure;
	}
	public Boolean getDefunctDomain() {
		return defunctDomain;
	}
	public void setDefunctDomain(Boolean defunctDomain) {
		this.defunctDomain = defunctDomain;
	}
	public Boolean getDefunctPath() {
		return defunctPath;
	}
	public void setDefunctPath(Boolean defunctPath) {
		this.defunctPath = defunctPath;
	}
	public Boolean getUncrawlableDomain() {
		return uncrawlableDomain;
	}
	public void setUncrawlableDomain(Boolean uncrawlableDomain) {
		this.uncrawlableDomain = uncrawlableDomain;
	}
	public Boolean getUncrawlablePath() {
		return uncrawlablePath;
	}
	public void setUncrawlablePath(Boolean uncrawlablePath) {
		this.uncrawlablePath = uncrawlablePath;
	}
	public Boolean getHttpError() {
		return httpError;
	}
	public void setHttpError(Boolean httpError) {
		this.httpError = httpError;
	}
	public Boolean getDefunctContent() {
		return defunctContent;
	}
	public void setDefunctContent(Boolean defunctContent) {
		this.defunctContent = defunctContent;
	}
	public Boolean getNotStandardHomepagePath() {
		return notStandardHomepagePath;
	}
	public void setNotStandardHomepagePath(Boolean notStandardHomepagePath) {
		this.notStandardHomepagePath = notStandardHomepagePath;
	}
	public Boolean getApprovedHomepagePath() {
		return approvedHomepagePath;
	}
	public void setApprovedHomepagePath(Boolean approvedHomepagePath) {
		this.approvedHomepagePath = approvedHomepagePath;
	}
	public Boolean getNeedsReview() {
		return needsReview;
	}
	public void setNeedsReview(Boolean needsReview) {
		this.needsReview = needsReview;
	}
	public RedirectType getRedirectReason() {
		return redirectReason;
	}
	public void setRedirectReason(RedirectType redirectType) {
		this.redirectReason = redirectType;
	}
	public Site getRedirectsTo() {
		return redirectsTo;
	}
	public void setRedirectsTo(Site redirectsTo) {
		this.redirectsTo = redirectsTo;
	}
	public Boolean getNotStandardQuery() {
		return notStandardQuery;
	}
	public void setNotStandardQuery(Boolean notStandardQuery) {
		this.notStandardQuery = notStandardQuery;
	}
	public Boolean getIsEmail() {
		return isEmail;
	}
	public void setIsEmail(Boolean isEmail) {
		this.isEmail = isEmail;
	}
	public Boolean getUnapproved() {
		return unapproved;
	}
	public void setUnapproved(Boolean unapproved) {
		this.unapproved = unapproved;
	}
	public Boolean getApprovedQuery() {
		return approvedQuery;
	}
	public void setApprovedQuery(Boolean approvedQuery) {
		this.approvedQuery = approvedQuery;
	}
	
}
