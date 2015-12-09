package persistence.views;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import persistence.Site;

@Entity
@Table(name="sites_with_duplicate_domains")
@Immutable
public class DupDomainSite {
	
	@Id
	@Column(name="siteId")
	private long viewId;
	
	@OneToOne
	@PrimaryKeyJoinColumn
	@JoinColumn(name="siteId")
	private Site site;

	public long getViewId() {
		return viewId;
	}

	public void setViewId(long viewId) {
		this.viewId = viewId;
	}

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}
	
	
	

}
