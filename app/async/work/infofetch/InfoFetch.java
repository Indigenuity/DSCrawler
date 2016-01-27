package async.work.infofetch;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import async.work.WorkOrder;
import async.work.WorkType;
import persistence.Site;
import persistence.SiteCrawl;
import persistence.UrlCheck;
import persistence.stateful.FetchJob;
import utilities.DSFormatter;

@Entity
public class InfoFetch extends WorkOrder {
	
	public InfoFetch(){
		super(WorkType.INFO_FETCH);
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long infoFetchId;
	
	@ManyToOne
	private FetchJob fetchJob;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String seed;
	
	private String placesId;
	
	private long siteId;
	private long siteCrawlId;
	private long urlCheckId;
	private long placesPageId;
	
	
	private boolean doUrlCheck = false;
	private boolean doSiteUpdate = false;
	private boolean doSiteCrawl = false;
	private boolean doPlacesPageFetch = false;
	
	private boolean siteUpdateCompleted = false;
	
	@Column(insertable = false, updatable = false)
	private boolean needUrlCheck = false;
	@Column(insertable = false, updatable = false)
	private boolean needSiteCrawl = false;
	@Column(insertable = false, updatable = false)
	private boolean needSiteUpdate = false;
	@Column(insertable = false, updatable = false)
	private boolean needPlacesPageFetch= false;
	
	public boolean hasMoreWork(){
		return needUrlCheck || needSiteCrawl || needSiteUpdate || needPlacesPageFetch;
	}
	
	
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
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}
	public long getUrlCheckId() {
		return urlCheckId;
	}
	public void setUrlCheckId(long urlCheckId) {
		this.urlCheckId = urlCheckId;
		System.out.println("just set urlcheck id : " + this.urlCheckId);
	}
	public long getSiteCrawlId() {
		return siteCrawlId;
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
	public boolean siteUpdateCompleted() {
		return siteUpdateCompleted;
	}
	public void setSiteUpdateCompleted(boolean siteUpdateCompleted) {
		this.siteUpdateCompleted = siteUpdateCompleted;
	}
	public boolean needUrlCheck() {
		return needUrlCheck;
	}
	public void setNeedUrlCheck(boolean needUrlCheck) {
		this.needUrlCheck = needUrlCheck;
	}
	public boolean needSiteCrawl() {
		return needSiteCrawl;
	}
	public void setNeedSiteCrawl(boolean needSiteCrawl) {
		this.needSiteCrawl = needSiteCrawl;
	}
	public boolean needSiteUpdate() {
		return needSiteUpdate;
	}
	public void setNeedSiteUpdate(boolean needSiteUpdate) {
		this.needSiteUpdate = needSiteUpdate;
	}
	public String getPlacesId() {
		return placesId;
	}
	public void setPlacesId(String placesId) {
		this.placesId = placesId;
	}
	public long getPlacesPageId() {
		return placesPageId;
	}
	public void setPlacesPageId(long placesPageId) {
		this.placesPageId = placesPageId;
	}
	public boolean isDoPlacesPageFetch() {
		return doPlacesPageFetch;
	}
	public void setDoPlacesPageFetch(boolean doPlacesPageFetch) {
		this.doPlacesPageFetch = doPlacesPageFetch;
	}
	public boolean isSiteUpdateCompleted() {
		return siteUpdateCompleted;
	}
	public boolean needPlacesPageFetch() {
		return needPlacesPageFetch;
	}
	
	
	
	
	

}
