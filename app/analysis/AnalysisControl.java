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
	
	public static void runFullAnalysis(SiteCrawl siteCrawl){
		AnalysisConfig config = new AnalysisConfig();
		config.setDoAll();
		runAnalysis(siteCrawl, config);
	}
	
	public static void runInventoryAnalysis(SiteCrawl siteCrawl){
		AnalysisConfig config = new AnalysisConfig();
		config.setDoVehicles(true);
		config.setDoInventoryNumbers(true);
		config.setDoPrices(true);
		runAnalysis(siteCrawl, config);
	}
	
	public static void runAggregationAnalysis(SiteCrawl siteCrawl){
		AnalysisConfig config = new AnalysisConfig();
		runAnalysis(siteCrawl, config);
	}
	
	public static void runAnalysis(SiteCrawl siteCrawl, AnalysisConfig config){
		SiteCrawlAnalysis analysis = AnalysisDao.getOrNew(siteCrawl);
		analysis.setConfig(config);
		SiteCrawlAnalyzer analyzer = new SiteCrawlAnalyzer(analysis);
		analyzer.runAnalysis();
	}
	
}
