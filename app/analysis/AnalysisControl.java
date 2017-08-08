package analysis;

import java.util.List;

import akka.actor.ActorRef;
import analysis.work.AnalysisOrder;
import analysis.work.AnalysisWorker;
import async.async.Asyncleton;
import async.functionalwork.JpaFunctionalBuilder;
import persistence.SiteCrawl;
import play.db.jpa.JPA;

public class AnalysisControl {
	
	public static SiteCrawlAnalysis runFullAnalysis(SiteCrawl siteCrawl){
		AnalysisConfig config = new AnalysisConfig();
		config.setDoAll();
		return runAnalysis(siteCrawl, config);
	}
	
	public static SiteCrawlAnalysis runInventoryAnalysis(SiteCrawl siteCrawl){
		AnalysisConfig config = new AnalysisConfig();
		config.setDoVehicles(true);
		config.setDoInventoryNumbers(true);
		config.setDoPrices(true);
		return runAnalysis(siteCrawl, config);
	}
	
	public static SiteCrawlAnalysis runAggregationAnalysis(SiteCrawl siteCrawl){
		AnalysisConfig config = new AnalysisConfig();
		return runAnalysis(siteCrawl, config);
	}
	
	public static SiteCrawlAnalysis runAnalysis(SiteCrawl siteCrawl, AnalysisConfig config){
		SiteCrawlAnalysis analysis = AnalysisDao.getOrNew(siteCrawl);
		analysis.setConfig(config);
		SiteCrawlAnalyzer analyzer = new SiteCrawlAnalyzer(analysis);
		return analyzer.runAnalysis();
	}
	
}
