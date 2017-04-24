package analysis;

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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import datadefinitions.GeneralMatch;
import datadefinitions.OEM;
import datadefinitions.newdefinitions.LinkTextMatch;
import datadefinitions.newdefinitions.TestMatch;
import datadefinitions.newdefinitions.WPAttribution;
import persistence.ExtractedString;
import persistence.ExtractedUrl;
import persistence.Metatag;
import persistence.PageCrawl;
import sites.persistence.Vehicle;

@Entity
public class PageCrawlAnalysis {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected long pageCrawlAnalysisId;
	
	@ManyToOne
	private SiteCrawlAnalysis siteCrawlAnalysis;
	
	@SuppressWarnings("unused")
	private PageCrawlAnalysis() {} //for JPA
	
	public PageCrawlAnalysis(PageCrawl pageCrawl){
		this.pageCrawl = pageCrawl;
	}
	
	@ManyToOne
	private PageCrawl pageCrawl;
	
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, orphanRemoval=true)
	protected Set<Vehicle> vehicles = new HashSet<Vehicle>();
	
	@ElementCollection(fetch=FetchType.LAZY)
	protected Set<String> vins = new HashSet<String>();
	
	//**************  Extracted Elements
	@Column(nullable = true, columnDefinition="varchar(4000)")
	@ElementCollection(fetch=FetchType.LAZY)
	@Fetch(FetchMode.SELECT)
	private Set<String> allLinks = new HashSet<String>();
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	@ElementCollection(fetch=FetchType.LAZY)
	@Fetch(FetchMode.SELECT)
	private Set<String> intrasiteLinks = new HashSet<String>();

	@ManyToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
	@Fetch(FetchMode.SELECT)
	private Set<Metatag> metatags = new HashSet<Metatag>();
	
	
	
	//****************  Extracted Strings
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, orphanRemoval=true)
	protected Set<ExtractedUrl> extractedUrls = new HashSet<ExtractedUrl>();
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, orphanRemoval=true)
	protected Set<ExtractedString> extractedStrings = new HashSet<ExtractedString>();
	
	
	//*************** Matches
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
	
	//*************** CapDB Flags and scores
	
	protected String titleText;
	protected boolean hasTitle;
	protected boolean titleGoodLength;
	protected boolean titleContainsCity;
	protected boolean titleContainsState;
	protected boolean titleContainsMake;
	
	protected boolean urlContainsCity;
	protected boolean urlContainsState;
	protected boolean urlContainsMake;
	
	protected String h1Text;
	protected boolean hasH1;
	protected boolean h1ContainsCity;
	protected boolean h1ContainsState;
	protected boolean h1ContainsMake;
	
	protected String metaDescriptionText;
	protected boolean hasMetaDescription;
	protected boolean metaDescriptionGoodLength;
	protected boolean metaDescriptionContainsCity;
	protected boolean metaDescriptionContainsState;
	protected boolean metaDescriptionContainsMake;
	
	protected int numImages = 0;
	protected int numAltImages = 0;
	
	@ElementCollection(fetch=FetchType.LAZY)
	@MapKeyEnumerated(EnumType.STRING)
	@Fetch(FetchMode.SELECT)
	protected Map<OEM, Integer> oemCounts = new HashMap<OEM, Integer>();
	@ElementCollection(fetch=FetchType.LAZY)
	@MapKeyEnumerated(EnumType.STRING)
	@Fetch(FetchMode.SELECT)
	protected Map<OEM, Integer> oemMetaCounts = new HashMap<OEM, Integer>();	//Should have been <OEM, Boolean>, but too annoying to fix
	
	

	public PageCrawl getPageCrawl() {
		return pageCrawl;
	}

	public void setPageCrawl(PageCrawl pageCrawl) {
		this.pageCrawl = pageCrawl;
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

	public long getPageCrawlAnalysisId() {
		return pageCrawlAnalysisId;
	}

	public void setPageCrawlAnalysisId(long pageCrawlAnalysisId) {
		this.pageCrawlAnalysisId = pageCrawlAnalysisId;
	}

	public boolean getTitleGoodLength() {
		return titleGoodLength;
	}

	public void setTitleGoodLength(boolean titleGoodLength) {
		this.titleGoodLength = titleGoodLength;
	}

	public boolean getTitleContainsCity() {
		return titleContainsCity;
	}

	public void setTitleContainsCity(boolean titleContainsCity) {
		this.titleContainsCity = titleContainsCity;
	}

	public boolean getTitleContainsState() {
		return titleContainsState;
	}

	public void setTitleContainsState(boolean titleContainsState) {
		this.titleContainsState = titleContainsState;
	}

	public boolean getTitleContainsMake() {
		return titleContainsMake;
	}

	public void setTitleContainsMake(boolean titleContainsMake) {
		this.titleContainsMake = titleContainsMake;
	}

	public boolean getUrlContainsCity() {
		return urlContainsCity;
	}

	public void setUrlContainsCity(boolean urlContainsCity) {
		this.urlContainsCity = urlContainsCity;
	}

	public boolean getUrlContainsState() {
		return urlContainsState;
	}

	public void setUrlContainsState(boolean urlContainsState) {
		this.urlContainsState = urlContainsState;
	}

	public boolean getUrlContainsMake() {
		return urlContainsMake;
	}

	public void setUrlContainsMake(boolean urlContainsMake) {
		this.urlContainsMake = urlContainsMake;
	}

	public SiteCrawlAnalysis getSiteCrawlAnalysis() {
		return siteCrawlAnalysis;
	}

	public void setSiteCrawlAnalysis(SiteCrawlAnalysis siteCrawlAnalysis) {
		this.siteCrawlAnalysis = siteCrawlAnalysis;
	}

	public boolean getH1ContainsCity() {
		return h1ContainsCity;
	}

	public void setH1ContainsCity(boolean h1ContainsCity) {
		this.h1ContainsCity = h1ContainsCity;
	}

	public boolean getH1ContainsState() {
		return h1ContainsState;
	}

	public void setH1ContainsState(boolean h1ContainsState) {
		this.h1ContainsState = h1ContainsState;
	}

	public boolean getH1ContainsMake() {
		return h1ContainsMake;
	}

	public void setH1ContainsMake(boolean h1ContainsMake) {
		this.h1ContainsMake = h1ContainsMake;
	}

	public boolean getHasTitle() {
		return hasTitle;
	}

	public void setHasTitle(boolean hasTitle) {
		this.hasTitle = hasTitle;
	}

	public boolean getHasH1() {
		return hasH1;
	}

	public void setHasH1(boolean hasH1) {
		this.hasH1 = hasH1;
	}

	public boolean getHasMetaDescription() {
		return hasMetaDescription;
	}

	public void setHasMetaDescription(boolean hasMetaDescription) {
		this.hasMetaDescription = hasMetaDescription;
	}

	public boolean getMetaDescriptionContainsCity() {
		return metaDescriptionContainsCity;
	}

	public void setMetaDescriptionContainsCity(boolean metaDescriptionContainsCity) {
		this.metaDescriptionContainsCity = metaDescriptionContainsCity;
	}

	public boolean getMetaDescriptionContainsState() {
		return metaDescriptionContainsState;
	}

	public void setMetaDescriptionContainsState(boolean metaDescriptionContainsState) {
		this.metaDescriptionContainsState = metaDescriptionContainsState;
	}

	public boolean getMetaDescriptionContainsMake() {
		return metaDescriptionContainsMake;
	}

	public void setMetaDescriptionContainsMake(boolean metaDescriptionContainsMake) {
		this.metaDescriptionContainsMake = metaDescriptionContainsMake;
	}

	public boolean getMetaDescriptionGoodLength() {
		return metaDescriptionGoodLength;
	}

	public void setMetaDescriptionGoodLength(boolean metaDescriptionGoodLength) {
		this.metaDescriptionGoodLength = metaDescriptionGoodLength;
	}

	public Integer getNumImages() {
		return numImages;
	}

	public void setNumImages(Integer numImages) {
		this.numImages = numImages;
	}

	public Integer getNumAltImages() {
		return numAltImages;
	}

	public void setNumAltImages(Integer numAltImages) {
		this.numAltImages = numAltImages;
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

	public String getTitleText() {
		return titleText;
	}

	public void setTitleText(String titleText) {
		this.titleText = titleText;
	}

	public String getH1Text() {
		return h1Text;
	}

	public void setH1Text(String h1Text) {
		this.h1Text = h1Text;
	}

	public String getMetaDescriptionText() {
		return metaDescriptionText;
	}

	public void setMetaDescriptionText(String metaDescriptionText) {
		this.metaDescriptionText = metaDescriptionText;
	}

	public Set<Vehicle> getVehicles() {
		return vehicles;
	}

	public void setVehicles(Set<Vehicle> vehicles) {
		this.vehicles.clear();
		this.vehicles.addAll(vehicles);
	}

	public Set<String> getVins() {
		return vins;
	}

	public void setVins(Set<String> vins) {
		this.vins.clear();
		this.vins = vins;
	}

	public void setNumImages(int numImages) {
		this.numImages = numImages;
	}

	public void setNumAltImages(int numAltImages) {
		this.numAltImages = numAltImages;
	}
	
	
	
}
