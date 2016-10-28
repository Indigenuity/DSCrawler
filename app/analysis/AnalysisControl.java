package analysis;

import java.util.List;

import akka.actor.ActorRef;
import analysis.work.AnalysisOrder;
import analysis.work.AnalysisWorker;
import async.async.Asyncleton;
import persistence.SiteCrawl;
import play.db.jpa.JPA;

public class AnalysisControl {

	public static void runAnalysisExperiment() throws Exception{
			
			String queryString = "select sc.siteCrawlId from SiteCrawl sc where sc.fileStatus = 'PRIMARY'";
//		String queryString = "from SiteCrawl sc where sc.crawlDate > '2016-09-08'";
		List<Long> ids= JPA.em().createQuery(queryString, Long.class).getResultList();
		
		ActorRef master = Asyncleton.getInstance().getGenericMaster(10, AnalysisWorker.class);
		System.out.println("siteCrawls : " + ids.size());
		
		AnalysisConfig config = new AnalysisConfig();
		config.setDoGeneralMatches(true);
		config.setDoLinkTextMatches(true);
		
		int count = 0;
		for(Long id : ids) {
			try{
				AnalysisOrder order = new AnalysisOrder();
				order.setAnalysisConfig(config);
				order.setSiteCrawlId(id);
				master.tell(order, ActorRef.noSender());
				
			} catch(Exception e) {
				System.out.println("caught excweption (sitecrawl " + id + ") : " + e.getMessage());
			}
			
			
			count++;
			if(count %500 == 0){
				System.out.println("count : " + count);
			}
		}
	}
	
	public static void independentAnalysisReport() {
//		String queryString = "select sca from SiteCrawlAnalysis sca join sca.siteCrawl sc join sc.site s where s.franchise is null";
//		List<SiteCrawlAnalysis> existing = JPA.em().createQuery(queryString, SiteCrawlAnalysis.class)
//				.setParameter("siteCrawl", siteCrawl).getResultList();
	}

}
