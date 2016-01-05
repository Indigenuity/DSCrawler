package persistence;


import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;

import utilities.DSFormatter;

@Entity
public class MobileCrawl {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected long siteCrawlId;
	
	@ManyToOne(cascade=CascadeType.DETACH)
	@JoinTable(name="site_mobilecrawl", 
			joinColumns={@JoinColumn(name="mobileCrawls_mobileCrawlId")},
		    inverseJoinColumns={@JoinColumn(name="Site_siteId")})
	private Site site;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String seed;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String resolvedSeed;
	
	private Date crawlDate;
	
	//These two values come from the CSS values for width of the body tag
	private int width;
	private int height;
	
	private int scrollWidth;
	private int scrollHeight;
	
	private int windowWidth;
	private int windowHeight;
	
	private int responseCode;
	
	private boolean detected400 = false;
	private boolean detected401 = false;
	private boolean detected402 = false;
	private boolean detected403 = false;
	private boolean detected404 = false;
	private boolean detected500 = false;
	private boolean detected501 = false;
	private boolean detected502 = false;
	private boolean detected503 = false;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String fauxResolvedSeed;
	
	//These two values come from the CSS values for width of the body tag
	private int fauxWidth;
	private int fauxHeight;
	
	private int fauxScrollWidth;
	private int fauxScrollHeight;
	
	private int fauxWindowWidth;
	private int fauxWindowHeight;
	
	private int fauxResponseCode;
	
	private boolean fauxDetected400 = false;
	private boolean fauxDetected401 = false;
	private boolean fauxDetected402 = false;
	private boolean fauxDetected403 = false;
	private boolean fauxDetected404 = false;
	private boolean fauxDetected500 = false;
	private boolean fauxDetected501 = false;
	private boolean fauxDetected502 = false;
	private boolean fauxDetected503 = false;
	
	private boolean isMobiSite = false;
	private boolean isResponsive = true;
	private boolean isAdaptive = true;
	private boolean mostlyResponsive = false;
	private boolean mostlyAdaptive = false;
	
	private boolean mobileAnalysisDone = false;
	

	
	public long getSiteCrawlId() {
		return siteCrawlId;
	}
	public void setSiteCrawlId(long siteCrawlId) {
		this.siteCrawlId = siteCrawlId;
	}
	public String getSeed() {
		return seed;
	}
	public void setSeed(String seed) {
		this.seed = DSFormatter.truncate(seed, 4000);
	}
	public String getResolvedSeed() {
		return resolvedSeed;
	}
	public void setResolvedSeed(String resolvedSeed) {
		this.resolvedSeed = DSFormatter.truncate(resolvedSeed, 4000);
	}
	public String getFauxResolvedSeed() {
		return fauxResolvedSeed;
	}
	public void setFauxResolvedSeed(String fauxResolvedSeed) {
		this.fauxResolvedSeed = DSFormatter.truncate(fauxResolvedSeed, 4000);
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWindowWidth() {
		return windowWidth;
	}
	public void setWindowWidth(int windowWidth) {
		this.windowWidth = windowWidth;
	}
	public int getWindowHeight() {
		return windowHeight;
	}
	public void setWindowHeight(int windowHeight) {
		this.windowHeight = windowHeight;
	}
	public Site getSite() {
		return site;
	}
	public void setSite(Site site) {
		this.site = site;
	}
	public Date getCrawlDate() {
		return crawlDate;
	}
	public void setCrawlDate(Date crawlDate) {
		this.crawlDate = crawlDate;
	}
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	public int getScrollWidth() {
		return scrollWidth;
	}
	public void setScrollWidth(int scrollWidth) {
		this.scrollWidth = scrollWidth;
	}
	public int getScrollHeight() {
		return scrollHeight;
	}
	public void setScrollHeight(int scrollHeight) {
		this.scrollHeight = scrollHeight;
	}
	public boolean isDetected400() {
		return detected400;
	}
	public void setDetected400(boolean detected400) {
		this.detected400 = detected400;
	}
	public boolean isDetected401() {
		return detected401;
	}
	public void setDetected401(boolean detected401) {
		this.detected401 = detected401;
	}
	public boolean isDetected402() {
		return detected402;
	}
	public void setDetected402(boolean detected402) {
		this.detected402 = detected402;
	}
	public boolean isDetected403() {
		return detected403;
	}
	public void setDetected403(boolean detected403) {
		this.detected403 = detected403;
	}
	public boolean isDetected404() {
		return detected404;
	}
	public void setDetected404(boolean detected404) {
		this.detected404 = detected404;
	}
	public boolean isDetected500() {
		return detected500;
	}
	public void setDetected500(boolean detected500) {
		this.detected500 = detected500;
	}
	public boolean isDetected501() {
		return detected501;
	}
	public void setDetected501(boolean detected501) {
		this.detected501 = detected501;
	}
	public boolean isDetected502() {
		return detected502;
	}
	public void setDetected502(boolean detected502) {
		this.detected502 = detected502;
	}
	public boolean isDetected503() {
		return detected503;
	}
	public void setDetected503(boolean detected503) {
		this.detected503 = detected503;
	}
	public int getFauxWidth() {
		return fauxWidth;
	}
	public void setFauxWidth(int fauxWidth) {
		this.fauxWidth = fauxWidth;
	}
	public int getFauxHeight() {
		return fauxHeight;
	}
	public void setFauxHeight(int fauxHeight) {
		this.fauxHeight = fauxHeight;
	}
	public int getFauxScrollWidth() {
		return fauxScrollWidth;
	}
	public void setFauxScrollWidth(int fauxScrollWidth) {
		this.fauxScrollWidth = fauxScrollWidth;
	}
	public int getFauxScrollHeight() {
		return fauxScrollHeight;
	}
	public void setFauxScrollHeight(int fauxScrollHeight) {
		this.fauxScrollHeight = fauxScrollHeight;
	}
	public int getFauxWindowWidth() {
		return fauxWindowWidth;
	}
	public void setFauxWindowWidth(int fauxWindowWidth) {
		this.fauxWindowWidth = fauxWindowWidth;
	}
	public int getFauxWindowHeight() {
		return fauxWindowHeight;
	}
	public void setFauxWindowHeight(int fauxWindowHeight) {
		this.fauxWindowHeight = fauxWindowHeight;
	}
	public int getFauxResponseCode() {
		return fauxResponseCode;
	}
	public void setFauxResponseCode(int fauxResponseCode) {
		this.fauxResponseCode = fauxResponseCode;
	}
	public boolean isFauxDetected400() {
		return fauxDetected400;
	}
	public void setFauxDetected400(boolean fauxDetected400) {
		this.fauxDetected400 = fauxDetected400;
	}
	public boolean isFauxDetected401() {
		return fauxDetected401;
	}
	public void setFauxDetected401(boolean fauxDetected401) {
		this.fauxDetected401 = fauxDetected401;
	}
	public boolean isFauxDetected402() {
		return fauxDetected402;
	}
	public void setFauxDetected402(boolean fauxDetected402) {
		this.fauxDetected402 = fauxDetected402;
	}
	public boolean isFauxDetected403() {
		return fauxDetected403;
	}
	public void setFauxDetected403(boolean fauxDetected403) {
		this.fauxDetected403 = fauxDetected403;
	}
	public boolean isFauxDetected404() {
		return fauxDetected404;
	}
	public void setFauxDetected404(boolean fauxDetected404) {
		this.fauxDetected404 = fauxDetected404;
	}
	public boolean isFauxDetected500() {
		return fauxDetected500;
	}
	public void setFauxDetected500(boolean fauxDetected500) {
		this.fauxDetected500 = fauxDetected500;
	}
	public boolean isFauxDetected501() {
		return fauxDetected501;
	}
	public void setFauxDetected501(boolean fauxDetected501) {
		this.fauxDetected501 = fauxDetected501;
	}
	public boolean isFauxDetected502() {
		return fauxDetected502;
	}
	public void setFauxDetected502(boolean fauxDetected502) {
		this.fauxDetected502 = fauxDetected502;
	}
	public boolean isFauxDetected503() {
		return fauxDetected503;
	}
	public void setFauxDetected503(boolean fauxDetected503) {
		this.fauxDetected503 = fauxDetected503;
	}
	public boolean isMobiSite() {
		return isMobiSite;
	}
	public void setMobiSite(boolean isMobiSite) {
		this.isMobiSite = isMobiSite;
	}
	public boolean isResponsive() {
		return isResponsive;
	}
	public void setResponsive(boolean isResponsive) {
		this.isResponsive = isResponsive;
	}
	public boolean isAdaptive() {
		return isAdaptive;
	}
	public void setAdaptive(boolean isAdaptive) {
		this.isAdaptive = isAdaptive;
	}
	public boolean isMostlyResponsive() {
		return mostlyResponsive;
	}
	public void setMostlyResponsive(boolean mostlyResponsive) {
		this.mostlyResponsive = mostlyResponsive;
	}
	public boolean isMostlyAdaptive() {
		return mostlyAdaptive;
	}
	public void setMostlyAdaptive(boolean mostlyAdaptive) {
		this.mostlyAdaptive = mostlyAdaptive;
	}
	public boolean isMobileAnalysisDone() {
		return mobileAnalysisDone;
	}
	public void setMobileAnalysisDone(boolean mobileAnalysisDone) {
		this.mobileAnalysisDone = mobileAnalysisDone;
	}
	
	
	
}
