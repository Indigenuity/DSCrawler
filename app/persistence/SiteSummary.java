package persistence;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;

import persistence.converters.GeneralMatchConverter;
import persistence.converters.SchedulerConverter;
import persistence.converters.WebProviderConverter;
import datadefinitions.GeneralMatch;
import datadefinitions.Scheduler;
import datadefinitions.WebProvider;
import utilities.DSFormatter;

public class SiteSummary implements Serializable {
	@Transient
	private static final long serialVersionUID = 1L;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="SUMMARY_SITE_ID")
	private SiteInformationOld siteInfo;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long siteSummaryId;
	
	@Column(nullable = true, columnDefinition="varchar(300)")
	private String siteName;
	
	@Column(nullable = true, columnDefinition="varchar(15)")
	private String niada;
	@Column(nullable = true, columnDefinition="varchar(15)")
	private String capdb;
	@Column(nullable = false, columnDefinition="boolean default true")
	private boolean franchise = false;
	
	@Column(nullable = true, columnDefinition="varchar(300)")
	private String siteUrl;
	@Column(nullable = true, columnDefinition="varchar(300)")
	private String givenUrl;
	@Column(nullable = true, columnDefinition="varchar(300)")
	private String intermediateUrl;
	@Column(nullable = true, columnDefinition="varchar(300)")
	private String redirectUrl;
	
	
	private Date crawlDate;
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean crawlFromGivenUrl = false;
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean urlRequiresReview = false;
	@Column(nullable = true, columnDefinition="varchar(300)")
	private String crawlStorageFolder;
	@Column(nullable = false, columnDefinition = "boolean default false")
	private boolean emptySite = false;
	
	private int numPages;
	
	@Convert(converter = WebProviderConverter.class)
	@ElementCollection(fetch=FetchType.EAGER)
	protected List<WebProvider> webProviders = new ArrayList<WebProvider>();
	
	@Convert(converter = SchedulerConverter.class)
	@ElementCollection(fetch=FetchType.EAGER)
	protected List<Scheduler> schedulers = new ArrayList<Scheduler>();
	
	@Convert(converter = GeneralMatchConverter.class)
	@ElementCollection(fetch=FetchType.EAGER)
	protected List<GeneralMatch> generalMatches = new ArrayList<GeneralMatch>();
	
	@OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER, orphanRemoval=true)
	protected List<ExtractedString> extractedStrings = new ArrayList<ExtractedString>();
	@OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER, orphanRemoval=true)
	protected List<ExtractedUrl> extractedUrls = new ArrayList<ExtractedUrl>();
	
	@OneToMany(cascade={CascadeType.ALL}, fetch = FetchType.EAGER, orphanRemoval=true)
	protected List<Staff> allStaff = new ArrayList<Staff>();
	
	protected String websiteProvider;
	protected String scheduler;
	
	public SiteSummary(SiteInformationOld siteInfo) {
		this.siteInfo = siteInfo;
		initFields();
	}
	
	//Empty constructor for private deserialization
	private SiteSummary() {
		
	}
	
	private void initFields() {
		this.setNumPages(0);
	}
	
	public static SiteSummary Deserialize(String serialPath) throws IOException, ClassNotFoundException {
		SiteSummary siteInfo = new SiteSummary();
		System.out.println("Generating SiteSummary at : " + serialPath);
		FileInputStream fis = new FileInputStream(serialPath);
    	ObjectInputStream ois = new ObjectInputStream(fis);
    	siteInfo = (SiteSummary) ois.readObject();
    	ois.close();
    	fis.close();
		
    	return siteInfo;
	}
	
	
	public String stringify() {
		StringBuilder sb = new StringBuilder();
		sb.append("Site Summary for : ");
		sb.append(siteInfo.getSiteUrl());
		sb.append("\nGiven Url : ");
		sb.append(siteInfo.getGivenUrl());
		sb.append("\nIntermediate Url : ");
		sb.append(siteInfo.getIntermediateUrl());
		sb.append("\nRedirect Url : ");
		sb.append(siteInfo.getRedirectUrl());
		sb.append("\nNumber of pages : ");
		sb.append(numPages);
		sb.append("\n");
		return sb.toString();
	}
	
	private void stringifyList(StringBuilder sb, List list) {
		for(Object item : list) {
			sb.append("\t\t");
			sb.append(item);
			sb.append("\n");
		}
	}
	
	public void summarizeEmail() {
		
		
	}
	public SiteInformationOld getSiteInfo() {
		return siteInfo;
	}

	public void setSiteInfo(SiteInformationOld siteInfo) {
		this.siteInfo = siteInfo;
	}

	@Override
	public String toString() { 
		return this.stringify();
	}

	public int getNumPages() {
		return numPages;
	}

	public void setNumPages(int numPages) {
		this.numPages = numPages;
	}

	public boolean isFranchise() {
		return franchise;
	}

	public void setFranchise(boolean franchise) {
		this.franchise = franchise;
	}

	public String getSiteUrl() {
		return siteUrl;
	}

	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
	}

	public String getGivenUrl() {
		return givenUrl;
	}

	public void setGivenUrl(String givenUrl) {
		this.givenUrl = givenUrl;
	}

	public String getIntermediateUrl() {
		return intermediateUrl;
	}

	public void setIntermediateUrl(String intermediateUrl) {
		this.intermediateUrl = intermediateUrl;
	}

	public boolean isCrawlFromGivenUrl() {
		return crawlFromGivenUrl;
	}

	public void setCrawlFromGivenUrl(boolean crawlFromGivenUrl) {
		this.crawlFromGivenUrl = crawlFromGivenUrl;
	}

	public Date getCrawlDate() {
		return crawlDate;
	}

	public void setCrawlDate(Date crawlDate) {
		this.crawlDate = crawlDate;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public boolean isUrlRequiresReview() {
		return urlRequiresReview;
	}

	public void setUrlRequiresReview(boolean urlRequiresReview) {
		this.urlRequiresReview = urlRequiresReview;
	}

	public String getNiada() {
		return niada;
	}

	public void setNiada(String niada) {
		this.niada = niada;
	}

	public String getCapdb() {
		return capdb;
	}

	public void setCapdb(String capdb) {
		this.capdb = capdb;
	}

	public String getCrawlStorageFolder() {
		return crawlStorageFolder;
	}

	public void setCrawlStorageFolder(String crawlStorageFolder) {
		this.crawlStorageFolder = crawlStorageFolder;
	}

	public long getId() {
		return siteSummaryId;
	}

	public void setId(long id) {
		this.siteSummaryId = id;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public boolean isEmptySite() {
		return emptySite;
	}

	public void setEmptySite(boolean emptySite) {
		this.emptySite = emptySite;
	}

	public String getWebsiteProvider() {
		return websiteProvider;
	}

	public void setWebsiteProvider(String websiteProvider) {
		this.websiteProvider = websiteProvider;
	}

	public String getScheduler() {
		return scheduler;
	}

	public void setScheduler(String scheduler) {
		this.scheduler = scheduler;
	}
	
	public List<WebProvider> getWebProviders() {
		return webProviders;
	}

	public void setWebProviders(List<WebProvider> webProviders) {
		this.webProviders = webProviders;
	}

	public List<Scheduler> getSchedulers() {
		return schedulers;
	}

	public void setSchedulers(List<Scheduler> schedulers) {
		this.schedulers = schedulers;
	}

	public List<GeneralMatch> getGeneralMatches() {
		return generalMatches;
	}

	public void setGeneralMatches(List<GeneralMatch> generalMatches) {
		this.generalMatches = generalMatches;
	}

	public List<ExtractedString> getExtractedStrings() {
		return extractedStrings;
	}

	public void setExtractedStrings(List<ExtractedString> extractedStrings) {
		this.extractedStrings = extractedStrings;
	}

	public List<ExtractedUrl> getExtractedUrls() {
		return extractedUrls;
	}

	public void setExtractedUrls(List<ExtractedUrl> extractedUrls) {
		this.extractedUrls = extractedUrls;
	}

	
	
	public List<Staff> getAllStaff() {
		return allStaff;
	}

	public void setAllStaff(List<Staff> allStaff) {
		this.allStaff = allStaff;
	}

	@PreUpdate
	@PrePersist
	public void validation() {
		
		this.siteName = DSFormatter.truncate(this.siteName, 300);
		this.niada = DSFormatter.truncate(this.niada, 15);
		this.capdb = DSFormatter.truncate(this.capdb, 15);
		this.siteUrl = DSFormatter.truncate(this.siteUrl, 300);
		this.givenUrl = DSFormatter.truncate(this.givenUrl, 300);
		this.intermediateUrl = DSFormatter.truncate(this.intermediateUrl, 300);
		this.redirectUrl = DSFormatter.truncate(this.redirectUrl, 300);
		this.crawlStorageFolder = DSFormatter.truncate(this.crawlStorageFolder, 300);
	}
	
}
