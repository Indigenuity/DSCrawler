package agarbagefolder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import async.work.WorkOrder;
import async.work.WorkType;
import persistence.Site;
import persistence.SiteCrawl;
import persistence.UrlCheck;
import persistence.stateful.FetchJob;
import places.PlacesPage;
import play.db.jpa.JPA;
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
	private Long sfId;
	
	@Column(nullable = true)
	private Long siteId;
	@Column(nullable = true)
	private Long siteCrawlId;
	@Column(nullable = true)
	private Long urlCheckId;
	@Column(nullable = true)
	private Long placesPageId;
	@Column(nullable = true)
	private Long sfEntryId;
	
	@Transient
	private Site siteObject;
	@Transient
	private SiteCrawl siteCrawlObject;
	@Transient
	private UrlCheck urlCheckObject;
	@Transient
	private PlacesPage placesPageObject;
	
	private Subtask urlCheck = new Subtask();
	private Subtask siteUpdate = new Subtask();
	private Subtask siteCrawl = new Subtask();
	private Subtask amalgamation = new Subtask();
	private Subtask textAnalysis = new Subtask();
	private Subtask docAnalysis = new Subtask();
	private Subtask metaAnalysis = new Subtask();
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
	public Subtask getTextAnalysis() {
		return textAnalysis;
	}
	public void setTextAnalysis(Subtask textAnalysis) {
		this.textAnalysis = textAnalysis;
	}
	public Subtask getDocAnalysis() {
		return docAnalysis;
	}
	public void setDocAnalysis(Subtask docAnalysis) {
		this.docAnalysis = docAnalysis;
	}
	public Subtask getMetaAnalysis() {
		return metaAnalysis;
	}
	public void setMetaAnalysis(Subtask metaAnalysis) {
		this.metaAnalysis = metaAnalysis;
	}
	public Subtask getAmalgamation() {
		return amalgamation;
	}
	public void setAmalgamation(Subtask amalgamation) {
		this.amalgamation = amalgamation;
	}
	public Site getSiteObject() {
		return siteObject;
	}
	public void setSiteObject(Site siteObject) {
		this.siteObject = siteObject;
	}
	public SiteCrawl getSiteCrawlObject() {
		return siteCrawlObject;
	}
	public void setSiteCrawlObject(SiteCrawl siteCrawlObject) {
		this.siteCrawlObject = siteCrawlObject;
	}
	public UrlCheck getUrlCheckObject() {
		return urlCheckObject;
	}
	public void setUrlCheckObject(UrlCheck urlCheckObject) {
		this.urlCheckObject = urlCheckObject;
	}
	public PlacesPage getPlacesPageObject() {
		return placesPageObject;
	}
	public void setPlacesPageObject(PlacesPage placesPageObject) {
		this.placesPageObject = placesPageObject;
	}
	
	public Long getSfId() {
		return sfId;
	}
	public void setSfId(Long sfId) {
		this.sfId = sfId;
	}
	public void initObjects(){
		if(urlCheckId != null){
			urlCheckObject = JPA.em().find(UrlCheck.class, urlCheckId);
		}
		if(siteId != null){
			siteObject = JPA.em().find(Site.class, siteId);
		}
		if(siteCrawlId != null){
			siteCrawlObject = JPA.em().find(SiteCrawl.class, siteCrawlId);
		}
		if(placesPageId != null){
			placesPageObject = JPA.em().find(PlacesPage.class, placesPageId);
		}
	}
}
