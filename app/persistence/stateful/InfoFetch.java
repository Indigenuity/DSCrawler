package persistence.stateful;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import persistence.Site;
import persistence.SiteCrawl;
import persistence.UrlCheck;
import utilities.DSFormatter;

@Entity
public class InfoFetch {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long infoFetchId;
	
	@ManyToOne
	private FetchJob fetchJob;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String seed;
	
	private long siteId;
	private long siteCrawlId;
	private long urlCheckId;
	
	
	private boolean doUrlCheck = false;
	private boolean doSiteUpdate = false;
	private boolean doSiteCrawl = false;
	public long getInfoFetchId() {
		return infoFetchId;
	}
	public void setInfoFetchId(long infoFetchId) {
		this.infoFetchId = infoFetchId;
	}
	public String getSeed() {
		return seed;
	}
	public void setSeed(String seed) {
		this.seed = DSFormatter.truncate(seed, 4000);
	}
	public long getSiteId() {
		return siteId;
	}
	public void setSite(Site site) {
		this.siteId = site.getSiteId();
	}
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}
	public long getUrlCheckId() {
		return urlCheckId;
	}
	public void setUrlCheck(UrlCheck check) {
		this.urlCheckId= check.getUrlCheckId();
	}
	public void setUrlCheckId(long urlCheckId) {
		this.urlCheckId = urlCheckId;
	}
	public long getSiteCrawlId() {
		return siteCrawlId;
	}
	public void setSiteCrawl(SiteCrawl siteCrawl) {
		this.siteCrawlId = siteCrawl.getSiteCrawlId();
	}
	public void setSiteCrawlId(long siteCrawlId) {
		this.siteCrawlId = siteCrawlId;
	}
	public boolean isDoUrlCheck() {
		return doUrlCheck;
	}
	public void setDoUrlCheck(boolean doUrlCheck) {
		this.doUrlCheck = doUrlCheck;
	}
	public boolean isDoSiteUpdate() {
		return doSiteUpdate;
	}
	public void setDoSiteUpdate(boolean doSiteUpdate) {
		this.doSiteUpdate = doSiteUpdate;
	}
	public boolean isDoSiteCrawl() {
		return doSiteCrawl;
	}
	public void setDoSiteCrawl(boolean doSiteCrawl) {
		this.doSiteCrawl = doSiteCrawl;
	}
	public FetchJob getFetchJob() {
		return fetchJob;
	}
	public void setFetchJob(FetchJob fetchJob) {
		this.fetchJob = fetchJob;
	}
	
	
	

}
