package persistence;

import java.sql.Date;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
public class CrawlSet {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long crawlSetId;
	 
	private String name; 
	private Date startDate;
	
	@ManyToMany
	@JoinTable(name="crawlset_completedSiteCrawls")
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.EXTRA)
	private Set<SiteCrawl> completedCrawls = new HashSet<SiteCrawl>();

	@ManyToMany
	@JoinTable(name="crawlset_uncrawledSites")
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.EXTRA)
	private Set<Site> uncrawled = new HashSet<Site>();
	
	@ManyToMany
	@JoinTable(name="crawlset_sites")
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.EXTRA)
	private Set<Site> sites = new HashSet<Site>();
	
	@ManyToMany
	@JoinTable(name="crawlset_needMobile")
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.EXTRA)
	private Set<Site> needMobile = new HashSet<Site>();
	
	@ManyToMany
	@JoinTable(name="crawlset_mobileCrawls")
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.EXTRA)
	private Set<MobileCrawl> mobileCrawls = new HashSet<MobileCrawl>();
	
	@ManyToMany
	@JoinTable(name="crawlset_needRedirectResolve")
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.EXTRA)
	private Set<Site> needRedirectResolve = new HashSet<Site>();
	
	@ManyToMany
	@JoinTable(name="crawlset_badSites")
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.EXTRA)
	private Set<Site> badSites = new HashSet<Site>();

	public long getCrawlSetId() {
		return crawlSetId;
	}

	public void setCrawlSetId(long crawlSetId) {
		this.crawlSetId = crawlSetId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Set<SiteCrawl> getCompletedCrawls() {
		return completedCrawls;
	}

	public void setCompletedCrawls(Collection<SiteCrawl> completedCrawls) {
		this.completedCrawls.clear();
		this.completedCrawls.addAll(completedCrawls);
	}
	
	public void addCompletedCrawl(SiteCrawl completedCrawl) {
		this.completedCrawls.add(completedCrawl);
	}

	public Set<Site> getUncrawled() {
		return uncrawled;
	}

	public void setUncrawled(Collection<Site> uncrawled) {
		this.uncrawled.clear();
		this.uncrawled.addAll(uncrawled);
	}
	
	public Set<Site> getSites() {
		return sites;
	}

	public void setSites(Collection<Site> sites) {
		this.sites.clear();
		this.sites.addAll(sites);
	}
	
	public Set<Site> getNeedMobile() {
		return needMobile;
	}

	public void setNeedMobile(Collection<Site> needMobile) {
		this.needMobile.clear();
		this.needMobile.addAll(needMobile);
	}

	public Set<MobileCrawl> getMobileCrawls() {
		return mobileCrawls;
	}

	public void setMobileCrawls(Set<MobileCrawl> mobileCrawls) {
		this.mobileCrawls.clear();
		this.mobileCrawls.addAll(mobileCrawls);
	}
	
	public void addMobileCrawl(MobileCrawl mobileCrawl) {
		this.mobileCrawls.add(mobileCrawl);
	}
	
	public Set<Site> getNeedRedirectResolve() {
		return needRedirectResolve;
	}

	public void setNeedRedirectResolve(Collection<Site> needRedirectResolve) {
		this.needRedirectResolve.clear();
		this.needRedirectResolve.addAll(needRedirectResolve);
	}
	
	public void finishCrawl(Site site, SiteCrawl siteCrawl) {
		if(site == null || siteCrawl == null)
			return;
		this.uncrawled.remove(site);
		this.completedCrawls.add(siteCrawl);
	}
	
	public void finishMobileCrawl(Site site, MobileCrawl mobileCrawl) {
		if(site == null || mobileCrawl == null)
			return;
		this.needMobile.remove(site);
		this.mobileCrawls.add(mobileCrawl);
	}
	
	public void finishRedirectResolve(Site site) {
		if(site == null)
			return;
		this.needRedirectResolve.remove(site);
	}
	
	
}
