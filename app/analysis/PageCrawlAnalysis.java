package analysis;

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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import datadefinitions.GeneralMatch;
import datadefinitions.newdefinitions.LinkTextMatch;
import datadefinitions.newdefinitions.TestMatch;
import datadefinitions.newdefinitions.WPAttribution;
import persistence.ExtractedString;
import persistence.ExtractedUrl;
import persistence.Metatag;
import persistence.PageCrawl;

@Entity
public class PageCrawlAnalysis {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected long pageCrawlAnalysisId;
	
	private PageCrawlAnalysis() {} //for JPA
	
	public PageCrawlAnalysis(PageCrawl pageCrawl){
		this.pageCrawl = pageCrawl;
	}
	
	@ManyToOne
	private PageCrawl pageCrawl;
	
	
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
	
	
	
	
	
}
