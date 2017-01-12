package analysis;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import datadefinitions.GeneralMatch;
import datadefinitions.newdefinitions.LinkTextMatch;
import datadefinitions.newdefinitions.TestMatch;
import datadefinitions.newdefinitions.WPAttribution;
import persistence.ExtractedString;
import persistence.ExtractedUrl;
import persistence.InventoryNumber;
import persistence.PageCrawl;
import persistence.SiteCrawl;

@Entity
public class SiteCrawlAnalysis {
	
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected long siteCrawlAnalysisId;
	
	private SiteCrawlAnalysis() {} // for JPA
	
	public SiteCrawlAnalysis(SiteCrawl siteCrawl) {
		this.siteCrawl = siteCrawl;
	}
	
	// *********** Basics and Configuration
	
	@OneToOne
	SiteCrawl siteCrawl;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER, orphanRemoval=true)
	private Set<PageCrawlAnalysis> pageAnalyses = new HashSet<PageCrawlAnalysis>();
	
	@Transient
	protected AnalysisConfig config = new AnalysisConfig();
	
	protected Date analysisDate;
	
	//*************** Matching
	@Enumerated(EnumType.STRING)
	@ElementCollection(fetch=FetchType.LAZY)
	protected Set<GeneralMatch> generalMatches = new HashSet<GeneralMatch>();
	
	@Enumerated(EnumType.STRING)
	@ElementCollection(fetch=FetchType.LAZY)
	protected Set<LinkTextMatch> linkTextMatches = new HashSet<LinkTextMatch>();
	
	@Enumerated(EnumType.STRING)
	@ElementCollection(fetch=FetchType.LAZY)
	protected Set<TestMatch> testMatches = new HashSet<TestMatch>();
	
	@Enumerated(EnumType.STRING)
	@ElementCollection(fetch=FetchType.LAZY)
	protected Set<WPAttribution> wpAttributions = new HashSet<WPAttribution>();
	
//	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, orphanRemoval=true)
//	private Set<InventoryNumber> inventoryNumbers = new HashSet<InventoryNumber>();
	
	

	
//	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, orphanRemoval=true)
//	protected Set<ExtractedUrl> extractedUrls = new HashSet<ExtractedUrl>();
//	
//	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, orphanRemoval=true)
//	protected Set<ExtractedString> extractedStrings = new HashSet<ExtractedString>();
//	
//	@Column(nullable = true, columnDefinition="varchar(4000)")
//	@ElementCollection(fetch=FetchType.LAZY)
//	private Set<String> allLinks = new HashSet<String>();
//	
//	@Column(nullable = true, columnDefinition="varchar(4000)")
//	@ElementCollection(fetch=FetchType.LAZY)
//	private Set<String> intrasiteLinks = new HashSet<String>();
	
	
	
	
	
	
	
	
	public PageCrawlAnalysis getForPageCrawl(PageCrawl pageCrawl) {
		for(PageCrawlAnalysis pageAnalysis : pageAnalyses) {
			if(pageCrawl == pageAnalysis.getPageCrawl()){
				return pageAnalysis;
			}
		}
		return null;
	}
	
	public SiteCrawl getSiteCrawl() {
		return siteCrawl;
	}

	public void setSiteCrawl(SiteCrawl siteCrawl) {
		this.siteCrawl = siteCrawl;
	}

	public Set<PageCrawlAnalysis> getPageAnalyses() {
		return pageAnalyses;
	}

	public void setPageAnalyses(Set<PageCrawlAnalysis> pageAnalyses) {
		this.pageAnalyses = pageAnalyses;
	}

	public Set<GeneralMatch> getGeneralMatches() {
		return generalMatches;
	}

	public void setGeneralMatches(Set<GeneralMatch> generalMatches) {
		this.generalMatches = generalMatches;
	}

	public Set<LinkTextMatch> getLinkTextMatches() {
		return linkTextMatches;
	}

	public void setLinkTextMatches(Set<LinkTextMatch> linkTextMatches) {
		this.linkTextMatches = linkTextMatches;
	}

	public long getSiteCrawlAnalysisId() {
		return siteCrawlAnalysisId;
	}

	public void setSiteCrawlAnalysisId(long siteCrawlAnalysisId) {
		this.siteCrawlAnalysisId = siteCrawlAnalysisId;
	}

	public AnalysisConfig getConfig() {
		return config;
	}

	public void setConfig(AnalysisConfig config) {
		this.config = config;
	}

	public Date getAnalysisDate() {
		return analysisDate;
	}

	public void setAnalysisDate(Date analysisDate) {
		this.analysisDate = analysisDate;
	}

	public Set<TestMatch> getTestMatches() {
		return testMatches;
	}

	public void setTestMatches(Set<TestMatch> testMatches) {
		this.testMatches = testMatches;
	}

	public Set<WPAttribution> getWpAttributions() {
		return wpAttributions;
	}

	public void setWpAttributions(Set<WPAttribution> wpAttributions) {
		this.wpAttributions = wpAttributions;
	}
	
	
	
}
