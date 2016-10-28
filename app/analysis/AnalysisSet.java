package analysis;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;

import persistence.Site;


@Entity
public class AnalysisSet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected long analysisSetId;
	
	@ElementCollection
	@MapKeyColumn(name="siteCrawlId")
	@Column(name="analysisId", nullable=true)
	private Map<Long, Long> crawlAnalysisMap = new HashMap<Long, Long>();
	
	@ManyToOne
	private AnalysisConfig config;
	
	private Date dateStarted;
	
	private String name;
	
	@Enumerated(EnumType.STRING)
	private OperandType operandType = OperandType.SITE_CRAWL;
	
	public enum OperandType {
		SITE_CRAWL, SITE
	}
	
	public AnalysisSet(){
		this.dateStarted = Calendar.getInstance().getTime();
		this.name = "Default AnalysisSet";
	}
	
	public AnalysisSet(String name){
		this.dateStarted = Calendar.getInstance().getTime();
		this.name = name;
	}
	
	public Set<Long> getIncomplete(){
		Set<Long> siteCrawlIds = new HashSet<Long>();
		for(Entry<Long, Long> entry : crawlAnalysisMap.entrySet()){
			if(entry.getValue() == 0){
				siteCrawlIds.add(entry.getKey());
			}
		}
		return siteCrawlIds;
	}
	
	public Set<Long> getComplete(){
		Set<Long> analysisIds = new HashSet<Long>();
		for(Entry<Long, Long> entry : crawlAnalysisMap.entrySet()){
			if(entry.getValue() != 0){
				analysisIds.add(entry.getValue());
			}
		}
		return analysisIds;
	}
	
	public void reset() {
		for(Entry<Long, Long> entry : crawlAnalysisMap.entrySet()){
			entry.setValue(0L);
		}
	}
	
	public long getAnalysisSetId() {
		return analysisSetId;
	}

	public void setAnalysisSetId(long analysisSetId) {
		this.analysisSetId = analysisSetId;
	}

	public Date getDateStarted() {
		return dateStarted;
	}

	public void setDateStarted(Date dateStarted) {
		this.dateStarted = dateStarted;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<Long, Long> getCrawlAnalysisMap() {
		return crawlAnalysisMap;
	}

	public void setCrawlAnalysisMap(Map<Long, Long> crawlAnalysisMap) {
		this.crawlAnalysisMap = crawlAnalysisMap;
	}

	public AnalysisConfig getConfig() {
		return config;
	}

	public void setConfig(AnalysisConfig config) {
		this.config = config;
	}

	public OperandType getOperandType() {
		return operandType;
	}

	public void setOperandType(OperandType operandType) {
		this.operandType = operandType;
	}

	
	
}
