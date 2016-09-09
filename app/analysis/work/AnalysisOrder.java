package analysis.work;

import java.util.UUID;

import analysis.AnalysisConfig;

public class AnalysisOrder {

	protected final Long uuid = UUID.randomUUID().getLeastSignificantBits();
	
	private Long siteCrawlId;
	private AnalysisConfig analysisConfig;

	public Long getSiteCrawlId() {
		return siteCrawlId;
	}
	public void setSiteCrawlId(Long siteCrawlId) {
		this.siteCrawlId = siteCrawlId;
	}
	public AnalysisConfig getAnalysisConfig() {
		return analysisConfig;
	}
	public void setAnalysisConfig(AnalysisConfig analysisConfig) {
		this.analysisConfig = analysisConfig;
	}
	public Long getUuid() {
		return uuid;
	}
	 
}
