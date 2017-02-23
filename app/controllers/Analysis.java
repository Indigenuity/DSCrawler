package controllers;

import java.util.List;
import java.util.Map.Entry;

import akka.actor.ActorRef;
import analysis.AnalysisConfig;
import analysis.AnalysisSet;
import analysis.AnalysisConfig.AnalysisMode;
import analysis.AnalysisSet.OperandType;
import analysis.work.AnalysisOrder;
import analysis.work.AnalysisWorker;
import async.async.Asyncleton;
import play.Logger;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

public class Analysis extends Controller {
	
	final static Logger.ALogger dsLogger = Logger.of("ds");

	@Transactional
    public static Result dashboard() {

    	String queryString = "from AnalysisSet ans order by ans.dateStarted desc";
    	List<AnalysisSet> analysisSets = JPA.em().createQuery(queryString, AnalysisSet.class).getResultList();
    	return ok(views.html.analysis.analysisDashboard.render(analysisSets));
    }
	
	@Transactional
	public static Result resetAnalysisSet(Long analysisSetId) {
		AnalysisSet analysisSet = JPA.em().find(AnalysisSet.class, analysisSetId);
		analysisSet.reset();
		return ok("AnalysisSet " + analysisSetId + " reset succesfully."); 
	}
	
	@Transactional
	public static Result runAnalysisSet(Long analysisSetId) {
		AnalysisSet analysisSet = JPA.em().find(AnalysisSet.class, analysisSetId);
		ActorRef master = Asyncleton.getInstance().getMonotypeMaster(25, AnalysisWorker.class);
//		analysisSet.setOperandType(OperandType.SITE_CRAWL);
		System.out.println("Operand Type : " + analysisSet.getOperandType());
		
//		AnalysisConfig config = new AnalysisConfig();
//		config.setAnalysisMode(AnalysisMode.PAGED);
//		config.setDoTestMatches(true);
//		config.setDoWpAttributionMatches(true);
//		
//		JPA.em().persist(config);
//		
//		analysisSet.setConfig(config);
		
		for(Entry<Long, Long> entry : analysisSet.getCrawlAnalysisMap().entrySet()){
			if(entry.getValue() == 0 && analysisSet.getOperandType() == OperandType.SITE_CRAWL){
//				System.out.println("Sending analysis order");
				AnalysisOrder order = new AnalysisOrder();
				order.setAnalysisConfig(analysisSet.getConfig());
				order.setSiteCrawlId(entry.getKey());
				master.tell(order, ActorRef.noSender());
			}
		}
		return ok("AnalysisSet " + analysisSetId + " queued succesfully for processing."); 
	}

}
