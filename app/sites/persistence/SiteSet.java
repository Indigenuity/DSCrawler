package sites.persistence;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import persistence.Site;

@Entity
public class SiteSet {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long siteSetId;
	
	private String description;
	
	@OneToMany(fetch=FetchType.LAZY)
	@LazyCollection(LazyCollectionOption.EXTRA)
	private Set<Site> sites = new HashSet<Site>();

	
	public SiteSet() {}
	public SiteSet(String description){
		this.setDescription(description);
	}
	
	public long getSiteSetId() {
		return siteSetId;
	}

	public void setSiteSetId(long siteSetId) {
		this.siteSetId = siteSetId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<Site> getSites() {
		return new HashSet<Site>(sites);
	}
	
	public synchronized boolean addSite(Site site){
		return sites.add(site);
	}
	
	public synchronized boolean addSites(Collection<Site> sites){
		return this.sites.addAll(sites);
	}
	
	public synchronized boolean removeSite(Site site){
		return sites.remove(site);
	}
	
	public synchronized boolean removeSites(Collection<Site> sites){
		return this.sites.remove(sites);
	}

	
}
