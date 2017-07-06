package analysis;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import javax.persistence.MapKeyEnumerated;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import datadefinitions.GeneralMatch;
import datadefinitions.OEM;
import datadefinitions.newdefinitions.LinkTextMatch;
import datadefinitions.newdefinitions.TestMatch;
import datadefinitions.newdefinitions.WPAttribution;
import persistence.ExtractedString;
import persistence.ExtractedUrl;
import persistence.InventoryNumber;
import persistence.PageCrawl;
import persistence.SiteCrawl;
import sites.persistence.Vehicle;

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
	
	@OneToMany(mappedBy="siteCrawlAnalysis", cascade=CascadeType.ALL, fetch=FetchType.EAGER, orphanRemoval=true)
	private Set<PageCrawlAnalysis> pageAnalyses = new HashSet<PageCrawlAnalysis>();
	
	@Transient
	protected AnalysisConfig config = new AnalysisConfig();
	
	protected Date analysisDate;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, orphanRemoval=true)
	protected Set<Vehicle> vehicles = new HashSet<Vehicle>();
	
	@ElementCollection(fetch=FetchType.LAZY)
	protected Set<String> vins = new HashSet<String>();
	
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
	
	
	protected int numHaveTitle = 0;
	protected int numTitleGoodLength = 0;
	protected int numTitleContainsCity = 0;
	protected int numTitleContainsState = 0;
	protected int numTitleContainsMake = 0;
	protected int numUniqueTitles = 0;
	
	protected int numUrlContainsCity = 0;
	protected int numUrlContainsState = 0;
	protected int numUrlContainsMake = 0;
	protected int numUniqueUrls = 0;
	
	protected int numHaveH1 = 0;
	protected int numH1ContainsCity = 0;
	protected int numH1ContainsState = 0;
	protected int numH1ContainsMake = 0;
	protected int numUniqueH1s = 0;
	
	protected int numHaveMetaDescription = 0;
	protected int numMetaDescriptionGoodLength = 0;
	protected int numMetaDescriptionContainsCity = 0;
	protected int numMetaDescriptionContainsState = 0;
	protected int numMetaDescriptionContainsMake = 0;
	protected int numUniqueMetaDescriptions = 0;
	
	protected int totalImages = 0;
	protected int totalAltImages = 0;
	
	
	//***************  Scores
	protected int urlUniqueScore = 0;
	protected int urlLocationScore = 0;
	protected int urlCleanScore = 0;
	
	protected int titleUniqueScore = 0;
	protected int titleLengthScore = 0;
	protected int titleContentScore = 0;
	
	protected int altImageScore = 0;
	
	protected int h1UniqueScore = 0;
	protected int h1ContentScore = 0;
	
	protected int metaDescriptionUniqueScore = 0;
	protected int metaDescriptionLengthScore = 0;
	protected int metaDescriptionContentScore = 0;
	
	@ElementCollection(fetch=FetchType.LAZY)
	@MapKeyEnumerated(EnumType.STRING)
	@Fetch(FetchMode.SELECT)
	protected Map<OEM, Integer> oemCounts = new HashMap<OEM, Integer>();
	@ElementCollection(fetch=FetchType.LAZY)
	@MapKeyEnumerated(EnumType.STRING)
	@Fetch(FetchMode.SELECT)
	protected Map<OEM, Integer> oemMetaCounts = new HashMap<OEM, Integer>();
	
	
	
	public PageCrawlAnalysis getForPageCrawl(PageCrawl pageCrawl) {
		for(PageCrawlAnalysis pageAnalysis : pageAnalyses) {
			if(pageCrawl == pageAnalysis.getPageCrawl()){
				return pageAnalysis;
			}
		}
		return null;
	}
	
	public boolean addPageCrawlAnalysis(PageCrawlAnalysis pageCrawlAnalysis){
		if(this.pageAnalyses.add(pageCrawlAnalysis)){
			pageCrawlAnalysis.setSiteCrawlAnalysis(this);
			return true;
		}
		return false;
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

	public int getNumHaveTitle() {
		return numHaveTitle;
	}

	public void setNumHaveTitle(int numHaveTitle) {
		this.numHaveTitle = numHaveTitle;
	}

	public int getNumTitleGoodLength() {
		return numTitleGoodLength;
	}

	public void setNumTitleGoodLength(int numTitleGoodLength) {
		this.numTitleGoodLength = numTitleGoodLength;
	}

	public int getNumTitleContainsCity() {
		return numTitleContainsCity;
	}

	public void setNumTitleContainsCity(int numTitleContainsCity) {
		this.numTitleContainsCity = numTitleContainsCity;
	}

	public int getNumTitleContainsState() {
		return numTitleContainsState;
	}

	public void setNumTitleContainsState(int numTitleContainsState) {
		this.numTitleContainsState = numTitleContainsState;
	}

	public int getNumTitleContainsMake() {
		return numTitleContainsMake;
	}

	public void setNumTitleContainsMake(int numTitleContainsMake) {
		this.numTitleContainsMake = numTitleContainsMake;
	}

	public int getNumUrlContainsCity() {
		return numUrlContainsCity;
	}

	public void setNumUrlContainsCity(int numUrlContainsCity) {
		this.numUrlContainsCity = numUrlContainsCity;
	}

	public int getNumUrlContainsState() {
		return numUrlContainsState;
	}

	public void setNumUrlContainsState(int numUrlContainsState) {
		this.numUrlContainsState = numUrlContainsState;
	}

	public int getNumUrlContainsMake() {
		return numUrlContainsMake;
	}

	public void setNumUrlContainsMake(int numUrlContainsMake) {
		this.numUrlContainsMake = numUrlContainsMake;
	}

	public int getNumHaveH1() {
		return numHaveH1;
	}

	public void setNumHaveH1(int numHaveH1) {
		this.numHaveH1 = numHaveH1;
	}

	public int getNumH1ContainsCity() {
		return numH1ContainsCity;
	}

	public void setNumH1ContainsCity(int numH1ContainsCity) {
		this.numH1ContainsCity = numH1ContainsCity;
	}

	public int getNumH1ContainsState() {
		return numH1ContainsState;
	}

	public void setNumH1ContainsState(int numH1ContainsState) {
		this.numH1ContainsState = numH1ContainsState;
	}

	public int getNumH1ContainsMake() {
		return numH1ContainsMake;
	}

	public void setNumH1ContainsMake(int numH1ContainsMake) {
		this.numH1ContainsMake = numH1ContainsMake;
	}

	public int getNumHaveMetaDescription() {
		return numHaveMetaDescription;
	}

	public void setNumHaveMetaDescription(int numHaveMetaDescription) {
		this.numHaveMetaDescription = numHaveMetaDescription;
	}

	public int getNumMetaDescriptionGoodLength() {
		return numMetaDescriptionGoodLength;
	}

	public void setNumMetaDescriptionGoodLength(int numMetaDescriptionGoodLength) {
		this.numMetaDescriptionGoodLength = numMetaDescriptionGoodLength;
	}

	public int getNumMetaDescriptionContainsCity() {
		return numMetaDescriptionContainsCity;
	}

	public void setNumMetaDescriptionContainsCity(int numMetaDescriptionContainsCity) {
		this.numMetaDescriptionContainsCity = numMetaDescriptionContainsCity;
	}

	public int getNumMetaDescriptionContainsState() {
		return numMetaDescriptionContainsState;
	}

	public void setNumMetaDescriptionContainsState(int numMetaDescriptionContainsState) {
		this.numMetaDescriptionContainsState = numMetaDescriptionContainsState;
	}

	public int getNumMetaDescriptionContainsMake() {
		return numMetaDescriptionContainsMake;
	}

	public void setNumMetaDescriptionContainsMake(int numMetaDescriptionContainsMake) {
		this.numMetaDescriptionContainsMake = numMetaDescriptionContainsMake;
	}

	public int getTotalImages() {
		return totalImages;
	}

	public void setTotalImages(int totalImages) {
		this.totalImages = totalImages;
	}

	public int getTotalAltImages() {
		return totalAltImages;
	}

	public void setTotalAltImages(int totalAltImages) {
		this.totalAltImages = totalAltImages;
	}

	public Map<OEM, Integer> getOemCounts() {
		return oemCounts;
	}

	public void setOemCounts(Map<OEM, Integer> oemCounts) {
		this.oemCounts = oemCounts;
	}

	public Map<OEM, Integer> getOemMetaCounts() {
		return oemMetaCounts;
	}

	public void setOemMetaCounts(Map<OEM, Integer> oemMetaCounts) {
		this.oemMetaCounts = oemMetaCounts;
	}

	public int getNumUniqueTitles() {
		return numUniqueTitles;
	}

	public void setNumUniqueTitles(int numUniqueTitles) {
		this.numUniqueTitles = numUniqueTitles;
	}

	public int getNumUniqueUrls() {
		return numUniqueUrls;
	}

	public void setNumUniqueUrls(int numUniqueUrls) {
		this.numUniqueUrls = numUniqueUrls;
	}

	public int getNumUniqueH1s() {
		return numUniqueH1s;
	}

	public void setNumUniqueH1s(int numUniqueH1s) {
		this.numUniqueH1s = numUniqueH1s;
	}

	public int getNumUniqueMetaDescriptions() {
		return numUniqueMetaDescriptions;
	}

	public void setNumUniqueMetaDescriptions(int numUniqueMetaDescriptions) {
		this.numUniqueMetaDescriptions = numUniqueMetaDescriptions;
	}

	public int getUrlUniqueScore() {
		return urlUniqueScore;
	}

	public void setUrlUniqueScore(int urlUniqueScore) {
		this.urlUniqueScore = urlUniqueScore;
	}

	public int getUrlLocationScore() {
		return urlLocationScore;
	}

	public void setUrlLocationScore(int urlLocationScore) {
		this.urlLocationScore = urlLocationScore;
	}

	public int getUrlCleanScore() {
		return urlCleanScore;
	}

	public void setUrlCleanScore(int urlCleanScore) {
		this.urlCleanScore = urlCleanScore;
	}

	public int getTitleUniqueScore() {
		return titleUniqueScore;
	}

	public void setTitleUniqueScore(int titleUniqueScore) {
		this.titleUniqueScore = titleUniqueScore;
	}

	public int getTitleLengthScore() {
		return titleLengthScore;
	}

	public void setTitleLengthScore(int titleLengthScore) {
		this.titleLengthScore = titleLengthScore;
	}

	public int getTitleContentScore() {
		return titleContentScore;
	}

	public void setTitleContentScore(int titleContentScore) {
		this.titleContentScore = titleContentScore;
	}

	public int getAltImageScore() {
		return altImageScore;
	}

	public void setAltImageScore(int altImageScore) {
		this.altImageScore = altImageScore;
	}

	public int getH1UniqueScore() {
		return h1UniqueScore;
	}

	public void setH1UniqueScore(int h1UniqueScore) {
		this.h1UniqueScore = h1UniqueScore;
	}

	public int getH1ContentScore() {
		return h1ContentScore;
	}

	public void setH1ContentScore(int h1ContentScore) {
		this.h1ContentScore = h1ContentScore;
	}

	public int getMetaDescriptionUniqueScore() {
		return metaDescriptionUniqueScore;
	}

	public void setMetaDescriptionUniqueScore(int metaDescriptionUniqueScore) {
		this.metaDescriptionUniqueScore = metaDescriptionUniqueScore;
	}

	public int getMetaDescriptionLengthScore() {
		return metaDescriptionLengthScore;
	}

	public void setMetaDescriptionLengthScore(int metaDescriptionLengthScore) {
		this.metaDescriptionLengthScore = metaDescriptionLengthScore;
	}

	public int getMetaDescriptionContentScore() {
		return metaDescriptionContentScore;
	}

	public void setMetaDescriptionContentScore(int metaDescriptionContentScore) {
		this.metaDescriptionContentScore = metaDescriptionContentScore;
	}

	public Set<Vehicle> getVehicles() {
		return vehicles;
	}

	public void setVehicles(Set<Vehicle> vehicles) {
		this.vehicles = vehicles;
	}
	public void addVehicles(Set<Vehicle> vehicles) {
		this.vehicles.addAll(vehicles);
	}

	public Set<String> getVins() {
		return vins;
	}

	public void setVins(Set<String> vins) {
		this.vins = vins;
	}
	
	public void addVins(Set<String> vins) {
		this.vins.addAll(vins);
	}
	
}
