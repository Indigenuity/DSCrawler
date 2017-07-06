package sites.crawling;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import persistence.SiteCrawl;

@Entity
public class CrawlSession {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long crawlSessionId;
	
	
	private String name; 
	private Date startDate;
	
	@Column(columnDefinition="varchar(4000)")
	@ElementCollection
	private Set<String> seeds = new HashSet<String>();
	
	@OneToMany(fetch=FetchType.LAZY)
	private List<SiteCrawl> siteCrawls = new ArrayList<SiteCrawl>();
	
	
	public CrawlSession(){
		this.startDate = Calendar.getInstance().getTime();
	}


	public long getCrawlSessionId() {
		return crawlSessionId;
	}


	public void setCrawlSessionId(long crawlSessionId) {
		this.crawlSessionId = crawlSessionId;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public Date getStartDate() {
		return startDate;
	}


	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}


	public Set<String> getSeeds() {
		return seeds;
	}


	public void setSeeds(Set<String> seeds) {
		this.seeds = seeds;
	}


	public List<SiteCrawl> getSiteCrawls() {
		return siteCrawls;
	}


	public void setSiteCrawls(List<SiteCrawl> siteCrawls) {
		this.siteCrawls = siteCrawls;
	}
	
	public void addSiteCrawl(SiteCrawl siteCrawl) {
		this.siteCrawls.add(siteCrawl);
	}
	
	public void addSeed(String seed) {
		this.seeds.add(seed);
	}
	
	
}
