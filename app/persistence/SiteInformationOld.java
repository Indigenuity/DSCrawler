package persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;

import utilities.DSFormatter;


public class SiteInformationOld implements Serializable {
	private static final long serialVersionUID = 1L;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long siteInformationId;
	
	@Column(nullable = true, columnDefinition="varchar(300)")
	private String siteName;
	
	@Column(nullable = true, columnDefinition="varchar(15)")
	private String niada;
	@Column(nullable = true, columnDefinition="varchar(15)")
	private String capdb;
	@Column(nullable = false, columnDefinition="boolean default true")
	private boolean franchise;
	
	@OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.LAZY, orphanRemoval=true)
	private List<PageInformation> pages;
	
	@Column(nullable = true, columnDefinition="varchar(1000)")
	private String siteUrl;
	@Column(nullable = true, columnDefinition="varchar(1000)")
	private String givenUrl;
	@Column(nullable = true, columnDefinition="varchar(1000)")
	private String intermediateUrl; 
	@Column(nullable = true, columnDefinition="varchar(1000)")
	private String redirectUrl;
	
	private Date crawlDate;
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean crawlFromGivenUrl;
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean urlRequiresReview;
	@Column(nullable = true, columnDefinition="varchar(1000)")
	private String crawlStorageFolder;
	@Column(nullable = false, columnDefinition = "boolean default false")
	private boolean emptySite;
	
	@Column(nullable = true, columnDefinition="mediumtext")
	private String failedUrls;
	
	
	
	
	@Column(nullable = false, columnDefinition = "boolean default false")
	private boolean matchesAnalyzed;
	@Column(nullable = false, columnDefinition = "boolean default false")
	private boolean stringExtractionsAnalyzed;
	@Column(nullable = false, columnDefinition = "boolean default false")
	private boolean staffExtractionsAnalyzed;
	@Column(nullable = false, columnDefinition = "boolean default false")
	private boolean summaryCompleted;
	
	@Column(nullable = false, columnDefinition = "boolean default false")
	private boolean specialProject;
	
	
	
	@Transient
	private boolean urlSubmitted;
	@Transient
	private boolean redirectSubmitted;
	@Transient
	private boolean crawlSubmitted;
	@Transient
	private boolean analysisSubmitted;

	

	
	
	public SiteInformationOld() {
		pages = new ArrayList<PageInformation>();
		siteName = "Unknown";
		siteUrl = "Unknown";
		this.siteInformationId = 0;
	}
	
	public SiteInformationOld(String siteName, String siteUrl) {
		pages = new ArrayList<PageInformation>();
		this.siteName = siteName;
		this.siteUrl = siteUrl;
		this.siteInformationId = 0;
	}
	
	public SiteInformationOld(String siteUrl) {
		pages = new ArrayList<PageInformation>();
		this.siteName = "Unknown";
		this.siteUrl = siteUrl;
		this.siteInformationId = 0;
	}
	
	public static SiteInformationOld Deserialize(String serialPath) throws IOException, ClassNotFoundException {
		SiteInformationOld siteInfo = new SiteInformationOld();
		System.out.println("Generating SiteInformation at : " + serialPath);
		FileInputStream fis = new FileInputStream(serialPath);
    	ObjectInputStream ois = new ObjectInputStream(fis);
    	siteInfo = (SiteInformationOld) ois.readObject();
    	ois.close();
    	fis.close();
		
    	return siteInfo;
	}
	
	public List<PageInformation> getPages(){
		return this.pages;
	}
	
	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getSiteUrl() {
		return siteUrl;
	}

	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
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

	public void addPageInformation(PageInformation page) {
		this.pages.add(page);
	}
	
	public void addPageInformation(List<PageInformation> pages) {
		for(PageInformation page : pages) {
			this.pages.add(page);
		}
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
	
	
	public boolean isEmptySite() {
		return emptySite;
	}

	public void setEmptySite(boolean emptySite) {
		this.emptySite = emptySite;
	}

	public String stringify() {
		StringBuilder sb = new StringBuilder();
		sb.append("Site information for : ");
		sb.append(siteUrl);
		sb.append("\nSite Name : ");
		sb.append(siteName);
		sb.append("\nDatabase id : ");
		sb.append(siteInformationId);
		sb.append("\nNIADA : ");
		sb.append(niada);
		sb.append("\nCAPDB ID : ");
		sb.append(capdb);
		sb.append("\nFranchise:");
		sb.append(franchise);
		sb.append("\nGiven Url : ");
		sb.append(givenUrl);
		sb.append("\nIntermediate Url : ");
		sb.append(intermediateUrl);
		sb.append("\nCrawl from given url : ");
		sb.append(crawlFromGivenUrl);
		sb.append("\nCrawl Date : ");
		sb.append(crawlDate);
		sb.append("\nRedirect Url : ");
		sb.append(redirectUrl);
		;
		sb.append("\n");
		for(PageInformation page: pages) {
			sb.append(page.stringify());
		}
		
		return sb.toString();
	}

	
	public long getSiteInformationId() {
		return siteInformationId;
	}

	public void setInformationId(long id) {
		this.siteInformationId = id;
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

	public boolean isFranchise() {
		return franchise;
	}

	public void setFranchise(boolean fanchise) {
		this.franchise = fanchise;
	}

	public String getCrawlStorageFolder() {
		return crawlStorageFolder;
	}

	public void setCrawlStorageFolder(String crawlStorageFolder) {
		this.crawlStorageFolder = crawlStorageFolder;
	}

	public boolean isUrlSubmitted() {
		return urlSubmitted;
	}

	public void setUrlSubmitted(boolean urlSubmitted) {
		this.urlSubmitted = urlSubmitted;
	}

	public boolean isRedirectSubmitted() {
		return redirectSubmitted;
	}

	public void setRedirectSubmitted(boolean redirectSubmitted) {
		this.redirectSubmitted = redirectSubmitted;
	}

	public boolean isCrawlSubmitted() {
		return crawlSubmitted;
	}

	public void setCrawlSubmitted(boolean crawlSubmitted) {
		this.crawlSubmitted = crawlSubmitted;
	}

	public boolean isAnalysisSubmitted() {
		return analysisSubmitted;
	}

	public void setAnalysisSubmitted(boolean analysisSubmitted) {
		this.analysisSubmitted = analysisSubmitted;
	}

	public String getFailedUrls() {
		return failedUrls;
	}

	public void setFailedUrls(String failedUrls) {
		this.failedUrls = failedUrls;
	}

	public boolean isMatchesAnalyzed() {
		return matchesAnalyzed;
	}

	public void setMatchesAnalyzed(boolean matchesAnalyzed) {
		this.matchesAnalyzed = matchesAnalyzed;
	}

	public boolean isStringExtractionsAnalyzed() {
		return stringExtractionsAnalyzed;
	}

	public void setStringExtractionsAnalyzed(boolean stringExtractionsAnalyzed) {
		this.stringExtractionsAnalyzed = stringExtractionsAnalyzed;
	}

	public boolean isStaffExtractionsAnalyzed() {
		return staffExtractionsAnalyzed;
	}

	public void setStaffExtractionsAnalyzed(boolean staffExtractionsAnalyzed) {
		this.staffExtractionsAnalyzed = staffExtractionsAnalyzed;
	}
	

	public boolean isSpecialProject() {
		return specialProject;
	}

	public void setSpecialProject(boolean specialProject) {
		this.specialProject = specialProject;
	}

	public boolean isSummaryCompleted() {
		return summaryCompleted;
	}

	public void setSummaryCompleted(boolean summaryCompleted) {
		this.summaryCompleted = summaryCompleted;
	}

	@PreUpdate
	@PrePersist
	public void validation() {
		this.siteName = DSFormatter.truncate(this.siteName, 300);
		this.niada = DSFormatter.truncate(this.niada, 15);
		this.capdb = DSFormatter.truncate(this.capdb, 15);
		this.siteUrl = DSFormatter.truncate(this.siteUrl, 1000);
		this.givenUrl = DSFormatter.truncate(this.givenUrl, 1000);
		this.intermediateUrl = DSFormatter.truncate(this.intermediateUrl, 1000);
		this.redirectUrl = DSFormatter.truncate(this.redirectUrl, 1000);
		this.crawlStorageFolder = DSFormatter.truncate(this.crawlStorageFolder, 1000);
	}

}
