package persistence;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

public class UrlResolveSet {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long urlResolveSetId;
	 
	private String name; 
	private Date startDate;
	
	@ManyToMany
	@JoinTable(name="urlResolveSet_finishedResolveSites")
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.EXTRA)
	private Set<Site> finishedResolveSites = new HashSet<Site>();
	
	@ManyToMany
	@JoinTable(name="urlResolveSet_sites")
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.EXTRA)
	private Set<Site> sites = new HashSet<Site>();

	public long getUrlResolveSetId() {
		return urlResolveSetId;
	}

	public void setUrlResolveSetId(long urlResolveSetId) {
		this.urlResolveSetId = urlResolveSetId;
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

	public Set<Site> getFinishedResolveSites() {
		return finishedResolveSites;
	}

	public void setFinishedResolveSites(Set<Site> finishedResolveSites) {
		this.finishedResolveSites.clear();
		this.finishedResolveSites.addAll(finishedResolveSites);
	}

	public Set<Site> getSites() {
		return sites;
	}

	public void setSites(Set<Site> sites) {
		this.sites.clear();
		this.sites.addAll(sites);
	}
	
	public void finishSite(Site site) {
		if(this.sites.remove(site)) {
			this.finishedResolveSites.add(site);
		}
	}
	
	

}
