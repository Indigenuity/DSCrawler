package analysis.work;

import akka.actor.UntypedActor;
import analysis.AnalysisConfig;
import analysis.AnalysisDao;
import analysis.SiteCrawlAnalysis;
import analysis.SiteCrawlAnalyzer;
import persistence.SiteCrawl;
import play.db.jpa.JPA;

public class AnalysisWorker extends UntypedActor {

	@Override
	public void onReceive(Object message) throws Exception {
		try{
			AnalysisOrder order = (AnalysisOrder) message;
			Long siteCrawlId = order.getSiteCrawlId();
			AnalysisConfig config = order.getAnalysisConfig();
			
			System.out.println("Doing Analysis work on SiteCrawl : " + siteCrawlId);
			JPA.withTransaction(() -> {
			
				SiteCrawl siteCrawl = JPA.em().find(SiteCrawl.class, siteCrawlId);
				
				SiteCrawlAnalysis analysis = AnalysisDao.getOrNew(siteCrawl);
				analysis.setConfig(config);
//				SiteCrawlAnalyzer.runSiteCrawlAnalysis(analysis);
				JPA.em().getTransaction().commit();
				JPA.em().getTransaction().begin();
				JPA.em().clear();
			
			});
		
		} catch(Exception e) {
			System.out.println(e.getClass().getSimpleName() + " exception in AnalysisWorker : " + e.getMessage());
		}
		
		
	}

}
