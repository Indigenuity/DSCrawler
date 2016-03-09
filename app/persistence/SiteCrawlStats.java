package persistence;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class SiteCrawlStats {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected long siteCrawlStatsId;
	
	@OneToOne(mappedBy="siteCrawlStats")
	SiteCrawl siteCrawl;
	
	private Integer totalPages = 0;
	
	private Integer urlCityQualifier = 0;
	private Integer urlStateQualifier = 0;
	private Integer urlMakeQualifier = 0;
	
	private Integer titleCityQualifier = 0;
	private Integer titleStateQualifier = 0;
	private Integer titleMakeQualifier = 0;
	
	private Integer h1CityQualifier = 0;
	private Integer h1StateQualifier = 0;
	private Integer h1MakeQualifier = 0;
	
	private Integer metaDescriptionCityQualifier = 0;
	private Integer metaDescriptionStateQualifier = 0;
	private Integer metaDescriptionMakeQualifier = 0;
	
	private Integer descriptionLength = 0;
	private Integer titleLength = 0;
	private Integer titleKeywordStuffing = 0;
	private Integer urlClean = 0;
	
	private Integer totalImages = 0;
	private Integer altImages = 0;
	
	
	
	private int uniqueUrlScore = 0;
	private int uniqueTitleScore = 0;
	private int uniqueH1Score = 0;
	private int uniqueMetaDescriptionScore = 0;
	
	private int contentUrlScore = 0;
	private int contentTitleScore = 0;
	private int contentH1Score = 0;
	private int contentSRPH1Score = 0;
	private int contentMetaDescriptionScore = 0;
	
	private int lengthTitleScore = 0;
	private int lengthMetaDescriptionScore = 0;
	
	private int altImageScore = 0;
	private int urlReadableScore = 0;
	
	
	
	
	
	
	public long getSiteCrawlStatsId() {
		return siteCrawlStatsId;
	}
	public void setSiteCrawlStatsId(long siteCrawlStatsId) {
		this.siteCrawlStatsId = siteCrawlStatsId;
	}
	public SiteCrawl getSiteCrawl() {
		return siteCrawl;
	}
	public void setSiteCrawl(SiteCrawl siteCrawl) {
		this.siteCrawl = siteCrawl;
	}
	public Integer getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}
	public Integer getUrlCityQualifier() {
		return urlCityQualifier;
	}
	public void setUrlCityQualifier(Integer urlCityQualifier) {
		this.urlCityQualifier = urlCityQualifier;
	}
	public Integer getUrlStateQualifier() {
		return urlStateQualifier;
	}
	public void setUrlStateQualifier(Integer urlStateQualifier) {
		this.urlStateQualifier = urlStateQualifier;
	}
	public Integer getUrlMakeQualifier() {
		return urlMakeQualifier;
	}
	public void setUrlMakeQualifier(Integer urlMakeQualifier) {
		this.urlMakeQualifier = urlMakeQualifier;
	}
	public Integer getTitleCityQualifier() {
		return titleCityQualifier;
	}
	public void setTitleCityQualifier(Integer titleCityQualifier) {
		this.titleCityQualifier = titleCityQualifier;
	}
	public Integer getTitleStateQualifier() {
		return titleStateQualifier;
	}
	public void setTitleStateQualifier(Integer titleStateQualifier) {
		this.titleStateQualifier = titleStateQualifier;
	}
	public Integer getTitleMakeQualifier() {
		return titleMakeQualifier;
	}
	public void setTitleMakeQualifier(Integer titleMakeQualifier) {
		this.titleMakeQualifier = titleMakeQualifier;
	}
	public Integer getH1CityQualifier() {
		return h1CityQualifier;
	}
	public void setH1CityQualifier(Integer h1CityQualifier) {
		this.h1CityQualifier = h1CityQualifier;
	}
	public Integer getH1StateQualifier() {
		return h1StateQualifier;
	}
	public void setH1StateQualifier(Integer h1StateQualifier) {
		this.h1StateQualifier = h1StateQualifier;
	}
	public Integer getH1MakeQualifier() {
		return h1MakeQualifier;
	}
	public void setH1MakeQualifier(Integer h1MakeQualifier) {
		this.h1MakeQualifier = h1MakeQualifier;
	}
	public Integer getMetaDescriptionCityQualifier() {
		return metaDescriptionCityQualifier;
	}
	public void setMetaDescriptionCityQualifier(Integer metaDescriptionCityQualifier) {
		this.metaDescriptionCityQualifier = metaDescriptionCityQualifier;
	}
	public Integer getMetaDescriptionStateQualifier() {
		return metaDescriptionStateQualifier;
	}
	public void setMetaDescriptionStateQualifier(Integer metaDescriptionStateQualifier) {
		this.metaDescriptionStateQualifier = metaDescriptionStateQualifier;
	}
	public Integer getMetaDescriptionMakeQualifier() {
		return metaDescriptionMakeQualifier;
	}
	public void setMetaDescriptionMakeQualifier(Integer metaDescriptionMakeQualifier) {
		this.metaDescriptionMakeQualifier = metaDescriptionMakeQualifier;
	}
	public Integer getDescriptionLength() {
		return descriptionLength;
	}
	public void setDescriptionLength(Integer descriptionLength) {
		this.descriptionLength = descriptionLength;
	}
	public Integer getTitleLength() {
		return titleLength;
	}
	public void setTitleLength(Integer titleLength) {
		this.titleLength = titleLength;
	}
	public Integer getTitleKeywordStuffing() {
		return titleKeywordStuffing;
	}
	public void setTitleKeywordStuffing(Integer titleKeywordStuffing) {
		this.titleKeywordStuffing = titleKeywordStuffing;
	}
	public Integer getUrlClean() {
		return urlClean;
	}
	public void setUrlClean(Integer urlClean) {
		this.urlClean = urlClean;
	}
	public Integer getTotalImages() {
		return totalImages;
	}
	public void setTotalImages(Integer totalImages) {
		this.totalImages = totalImages;
	}
	public Integer getAltImages() {
		return altImages;
	}
	public void setAltImages(Integer altImages) {
		this.altImages = altImages;
	}
	public int getUniqueUrlScore() {
		return uniqueUrlScore;
	}
	public void setUniqueUrlScore(int uniqueUrlScore) {
		this.uniqueUrlScore = uniqueUrlScore;
	}
	public int getUniqueTitleScore() {
		return uniqueTitleScore;
	}
	public void setUniqueTitleScore(int uniqueTitleScore) {
		this.uniqueTitleScore = uniqueTitleScore;
	}
	public int getUniqueH1Score() {
		return uniqueH1Score;
	}
	public void setUniqueH1Score(int uniqueH1Score) {
		this.uniqueH1Score = uniqueH1Score;
	}
	public int getUniqueMetaDescriptionScore() {
		return uniqueMetaDescriptionScore;
	}
	public void setUniqueMetaDescriptionScore(int uniqueMetaDescriptionScore) {
		this.uniqueMetaDescriptionScore = uniqueMetaDescriptionScore;
	}
	public int getContentUrlScore() {
		return contentUrlScore;
	}
	public void setContentUrlScore(int contentUrlScore) {
		this.contentUrlScore = contentUrlScore;
	}
	public int getContentTitleScore() {
		return contentTitleScore;
	}
	public void setContentTitleScore(int contentTitleScore) {
		this.contentTitleScore = contentTitleScore;
	}
	public int getContentH1Score() {
		return contentH1Score;
	}
	public void setContentH1Score(int contentH1Score) {
		this.contentH1Score = contentH1Score;
	}
	public int getContentSRPH1Score() {
		return contentSRPH1Score;
	}
	public void setContentSRPH1Score(int contentSRPH1Score) {
		this.contentSRPH1Score = contentSRPH1Score;
	}
	public int getContentMetaDescriptionScore() {
		return contentMetaDescriptionScore;
	}
	public void setContentMetaDescriptionScore(int contentMetaDescriptionScore) {
		this.contentMetaDescriptionScore = contentMetaDescriptionScore;
	}
	public int getLengthTitleScore() {
		return lengthTitleScore;
	}
	public void setLengthTitleScore(int lengthTitleScore) {
		this.lengthTitleScore = lengthTitleScore;
	}
	public int getLengthMetaDescriptionScore() {
		return lengthMetaDescriptionScore;
	}
	public void setLengthMetaDescriptionScore(int lengthMetaDescriptionScore) {
		this.lengthMetaDescriptionScore = lengthMetaDescriptionScore;
	}
	public int getAltImageScore() {
		return altImageScore;
	}
	public void setAltImageScore(int altImageScore) {
		this.altImageScore = altImageScore;
	}
	public int getUrlReadableScore() {
		return urlReadableScore;
	}
	public void setUrlReadableScore(int urlReadableScore) {
		this.urlReadableScore = urlReadableScore;
	}
	
	
	
}
