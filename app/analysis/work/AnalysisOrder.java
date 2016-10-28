package analysis.work;


import analysis.AnalysisConfig;
import newwork.WorkOrder;

public class AnalysisOrder extends WorkOrder {

	
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
	 
}
