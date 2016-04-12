package reporting;

import java.util.LinkedHashMap;
import java.util.Map;


public class DashboardStats {
	
	private Map<String, Object> stats = new LinkedHashMap<String, Object>();
	private Map<String, DashboardStats> sections = new LinkedHashMap<String, DashboardStats>();
	
	private String title;

	private String totalDealers;
	private String	totalSites;
	private String dealersWithoutSites;
	private String uncrawledSites;
	private String unconfirmedHomepages;
	private String homepagesNeedReview;
	private String totalCrawls;
	private String emptyCrawls;
	private String smallCrawls;
	private String needDocAnalysis;
	private String needAmalgamation;
	private String needTextAnalysis;
	private String needInference;
	private String inferenceFailed; 
	
	public DashboardStats(Map<String, Object> stats) {
		this.stats.putAll(stats);
	}
	
	public DashboardStats(String title) {
		this.title = title;
	}
	
	public DashboardStats() {}
	
	public void put(String key, Object value) {
		this.stats.put(key, value);
	}
	
	public Map<String, Object> getStats() {
		return stats;
	}
	
	public String getTotalDealers() {
		return totalDealers;
	}
	public void setTotalDealers(String totalDealers) {
		this.totalDealers = totalDealers;
	}
	public String getTotalSites() {
		return totalSites;
	}
	public void setTotalSites(String totalSites) {
		this.totalSites = totalSites;
	}
	public String getDealersWithoutSites() {
		return dealersWithoutSites;
	}
	public void setDealersWithoutSites(String dealersWithoutSites) {
		this.dealersWithoutSites = dealersWithoutSites;
	}
	public String getUncrawledSites() {
		return uncrawledSites;
	}
	public void setUncrawledSites(String uncrawledSites) {
		this.uncrawledSites = uncrawledSites;
	}
	public String getUnconfirmedHomepages() {
		return unconfirmedHomepages;
	}
	public void setUnconfirmedHomepages(String unconfirmedHomepages) {
		this.unconfirmedHomepages = unconfirmedHomepages;
	}
	public String getHomepagesNeedReview() {
		return homepagesNeedReview;
	}
	public void setHomepagesNeedReview(String homepagesNeedReview) {
		this.homepagesNeedReview = homepagesNeedReview;
	}
	public String getNeedDocAnalysis() {
		return needDocAnalysis;
	}
	public void setNeedDocAnalysis(String needDocAnalysis) {
		this.needDocAnalysis = needDocAnalysis;
	}
	public String getNeedAmalgamation() {
		return needAmalgamation;
	}
	public void setNeedAmalgamation(String needAmalgamation) {
		this.needAmalgamation = needAmalgamation;
	}
	public String getNeedTextAnalysis() {
		return needTextAnalysis;
	}
	public void setNeedTextAnalysis(String needTextAnalysis) {
		this.needTextAnalysis = needTextAnalysis;
	}
	public String getNeedInference() {
		return needInference;
	}
	public void setNeedInference(String needInference) {
		this.needInference = needInference;
	}
	public String getTotalCrawls() {
		return totalCrawls;
	}
	public void setTotalCrawls(String totalCrawls) {
		this.totalCrawls = totalCrawls;
	}
	public String getEmptyCrawls() {
		return emptyCrawls;
	}
	public void setEmptyCrawls(String emptyCrawls) {
		this.emptyCrawls = emptyCrawls;
	}
	public String getSmallCrawls() {
		return smallCrawls;
	}
	public void setSmallCrawls(String smallCrawls) {
		this.smallCrawls = smallCrawls;
	}
	public String getInferenceFailed() {
		return inferenceFailed;
	}
	public void setInferenceFailed(String inferenceFailed) {
		this.inferenceFailed = inferenceFailed;
	}

	public Map<String, DashboardStats> getSections() {
		return sections;
	}

	public DashboardStats addSection(String name, DashboardStats section) {
		return this.sections.put(name, section);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}
