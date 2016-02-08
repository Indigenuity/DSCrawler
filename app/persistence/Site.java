package persistence;

import java.lang.reflect.Field;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import datadefinitions.WebProvider;
import utilities.DSFormatter;

@Entity
public class Site {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long siteId;
	
	private String domain;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String homepage;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String standardizedHomepage;
	
	@Column(nullable = false, columnDefinition="boolean default true")
	private boolean standaloneSite = true;	//If this site is the only one on the domain, as opposed to PAACO sites
	
	@ManyToOne
	private PlacesPage placesPage;
	
	@OneToMany(fetch=FetchType.LAZY)
	@JoinTable(name="site_sitecrawl", 
			joinColumns={@JoinColumn(name="Site_siteId")},
		    inverseJoinColumns={@JoinColumn(name="crawls_siteCrawlId")})
	private List<SiteCrawl> crawls = new ArrayList<SiteCrawl>();
	
	@OneToMany(fetch=FetchType.LAZY)
	@JoinTable(name="site_mobilecrawl", 
			joinColumns={@JoinColumn(name="Site_siteId")},
		    inverseJoinColumns={@JoinColumn(name="mobileCrawls_mobileCrawlId")})
	private List<MobileCrawl> mobileCrawls = new ArrayList<MobileCrawl>();
	
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean homepageNeedsReview = false;
	
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean hompageValidUrlConfirmed = false;
	
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean queryStringApproved = false;
	
	private Date redirectResolveDate;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String suggestedHomepage; //For when the redirect resolver finds a different homepage
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	@ElementCollection(fetch=FetchType.LAZY)
	private Set<String> redirectUrls = new HashSet<String>();
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	@ElementCollection(fetch=FetchType.LAZY)
	private Set<String> groupUrls = new HashSet<String>();
	
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean notableChange = false;
	
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean maybeDefunct = false;
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean defunct = false;
	
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean reviewLater = false;
	
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean invalidUrl = false;
	
	private String reviewReason;
	
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean notInterested = false;
	
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean oemSite = false;
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean franchise = false;
	
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean groupSite = false;
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean crawlerProtected = false;
	
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean recrawl = false;
	
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean locationPage = false;
	
	@Column(nullable = false, columnDefinition="boolean default true")
	private boolean showToMatt = true;

	@Column(nullable = true, columnDefinition="varchar(255)")
	@ElementCollection(fetch=FetchType.LAZY)
	private Set<String> cities = new HashSet<String>();
	
	@Formula("homepageNeedsReview | reviewLater | invalidUrl | maybeDefunct | defunct | crawlerProtected | groupSite ")
	private boolean invalid;
	
	@Formula("month(current_date) - month(redirectResolveDate) > 2 or year(current_date) - year(redirectResolveDate) > 0")
	public boolean staleRedirect;
	
	
	public long getSiteId() {
		return siteId;
	}

	public void setSiteId(long siteId) {
		this.siteId = siteId;
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
	}

	public boolean isStandaloneSite() {
		return standaloneSite;
	}

	public void setStandaloneSite(boolean standaloneSite) {
		this.standaloneSite = standaloneSite;
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

	public boolean isHomepageNeedsReview() {
		return homepageNeedsReview;
	}

	public void setHomepageNeedsReview(boolean homepageNeedsReview) {
		this.homepageNeedsReview = homepageNeedsReview;
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

	public String getSuggestedHomepage() {
		return suggestedHomepage;
	}

	public void setSuggestedHomepage(String suggestedHomepage) {
		this.suggestedHomepage = DSFormatter.truncate(suggestedHomepage, 4000);
	}

	public boolean isNotableChange() {
		return notableChange;
	}

	public void setNotableChange(boolean notableChange) {
		this.notableChange = notableChange;
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

	public boolean isReviewLater() {
		return reviewLater;
	}

	public void setReviewLater(boolean reviewLater) {
		this.reviewLater = reviewLater;
	}

	public String getStandardizedHomepage() {
		return standardizedHomepage;
	}

	public void setStandardizedHomepage(String standardizedHomepage) {
		this.standardizedHomepage = standardizedHomepage;
	}

	public boolean isHompageValidUrlConfirmed() {
		return hompageValidUrlConfirmed;
	}

	public void setHompageValidUrlConfirmed(boolean hompageValidUrlConfirmed) {
		this.hompageValidUrlConfirmed = hompageValidUrlConfirmed;
	}

	public boolean isInvalidUrl() {
		return invalidUrl;
	}

	public void setInvalidUrl(boolean invalidUrl) {
		this.invalidUrl = invalidUrl;
	}

	public boolean isQueryStringApproved() {
		return queryStringApproved;
	}

	public void setQueryStringApproved(boolean queryStringApproved) {
		this.queryStringApproved = queryStringApproved;
	}

	public String getReviewReason() {
		return reviewReason;
	}

	public void setReviewReason(String reviewReason) {
		this.reviewReason = reviewReason;
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

	public boolean isRecrawl() {
		return recrawl;
	}

	public void setRecrawl(boolean recrawl) {
		this.recrawl = recrawl;
	}

	public boolean isShowToMatt() {
		return showToMatt;
	}

	public void setShowToMatt(boolean showToMatt) {
		this.showToMatt = showToMatt;
	}

	public boolean isFranchise() {
		return franchise;
	}

	public void setFranchise(boolean franchise) {
		this.franchise = franchise;
	}

	public boolean isInvalid() {
		return invalid;
	}

	public void setInvalid(boolean isValid) {
		this.invalid = isValid;
	}
	
	
	
	
	
	public PlacesPage getPlacesPage() {
		return placesPage;
	}

	public void setPlacesPage(PlacesPage placesPage) {
		this.placesPage = placesPage;
	}

	public boolean isLocationPage() {
		return locationPage;
	}

	public void setLocationPage(boolean locationPage) {
		this.locationPage = locationPage;
	}

	public Set<String> getCities() {
		return cities;
	}

	public void setCities(Set<String> cities) {
		this.cities.clear();
		this.cities.addAll(cities);
	}

	public boolean isStaleRedirect() {
		return staleRedirect;
	}

	public void setStaleRedirect(boolean staleRedirect) {
		this.staleRedirect = staleRedirect;
	}

	public static boolean isBoolean(String fieldName) {
		for(Field field : Site.class.getDeclaredFields()) {
			if(field.getType() == boolean.class && field.getName().equals(fieldName)){
				return true;
			}
		}
		return false;
	}
	
	
}
