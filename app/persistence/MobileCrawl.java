package persistence;

import java.sql.Date;

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
	
	private int width;
	private int height;
	
	private int windowWidth;
	private int windowHeight;
	
	private int responseCode;
	
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
	
	
	
}
