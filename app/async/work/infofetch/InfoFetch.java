package async.work.infofetch;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import async.work.WorkOrder;
import async.work.WorkStatus;
import async.work.WorkType;
import async.work.urlresolve.UrlResolveWorkOrder;
import persistence.stateful.FetchJob;
import utilities.DSFormatter;

@Entity
public class InfoFetch extends WorkOrder {
	
	
	
	public InfoFetch(){
		super(WorkType.INFO_FETCH); 
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long infoFetchId;
	
	@ManyToOne
	private FetchJob fetchJob;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String seed;
	
	private String placesId;
	
	@Column(nullable = true)
	private Long siteId;
	@Column(nullable = true)
	private Long siteCrawlId;
	@Column(nullable = true)
	private Long urlCheckId;
	@Column(nullable = true)
	private Long placesPageId;
	
	@Transient
	private List<Subtask> subtasks;
	
	private Subtask urlCheck = new Subtask();
	private Subtask siteUpdate = new Subtask();
	private Subtask siteCrawl = new Subtask();
	private Subtask placesPageFetch = new Subtask();
	
	public Long getInfoFetchId() {
		return infoFetchId;
	}
	public void setInfoFetchId(Long infoFetchId) {
		this.infoFetchId = infoFetchId;
	}
	public String getSeed() {
		return seed;
	}
	public void setSeed(String seed) {
		this.seed = DSFormatter.truncate(seed, 4000);
	}
	public Long getSiteId() {
		return siteId;
	}
	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}
	public Long getUrlCheckId() {
		return urlCheckId;
	}
	public void setUrlCheckId(Long urlCheckId) {
		this.urlCheckId = urlCheckId;
//		System.out.println("just set urlcheck id : " + this.urlCheckId);
	}
	public Long getSiteCrawlId() {
		return siteCrawlId;
	}
	public void setSiteCrawlId(Long siteCrawlId) {
		this.siteCrawlId = siteCrawlId;
	}
	public FetchJob getFetchJob() {
		return fetchJob;
	}
	public void setFetchJob(FetchJob fetchJob) {
		this.fetchJob = fetchJob;
	}
	public String getPlacesId() {
		return placesId;
	}
	public void setPlacesId(String placesId) {
		this.placesId = placesId;
	}
	public Long getPlacesPageId() {
		return placesPageId;
	}
	public void setPlacesPageId(Long placesPageId) {
		this.placesPageId = placesPageId;
	}
	public Subtask getUrlCheck() {
		return urlCheck;
	}
	public void setUrlCheck(Subtask urlCheck) {
		this.urlCheck = urlCheck;
	}
	public Subtask getSiteUpdate() {
		return siteUpdate;
	}
	public void setSiteUpdate(Subtask siteUpdate) {
		this.siteUpdate = siteUpdate;
	}
	public Subtask getSiteCrawl() {
		return siteCrawl;
	}
	public void setSiteCrawl(Subtask siteCrawl) {
		this.siteCrawl = siteCrawl;
	}
	public Subtask getPlacesPageFetch() {
		return placesPageFetch;
	}
	public void setPlacesPageFetch(Subtask placesPageFetch) {
		this.placesPageFetch = placesPageFetch;
	}
	
}
