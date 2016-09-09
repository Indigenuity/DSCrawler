package analysis;

import java.util.List;

import akka.actor.ActorRef;
import analysis.work.AnalysisWorker;
import async.async.Asyncleton;
import persistence.SiteCrawl;
import play.db.jpa.JPA;

public class AnalysisControl {

	public static void runAnalysisExperiment() throws Exception{
			
			String queryString = "from SiteCrawl sc where sc.fileStatus = 'PRIMARY'";
			List<SiteCrawl> siteCrawls = JPA.em().createQuery(queryString, SiteCrawl.class).getResultList();
			
			ActorRef master = Asyncleton.getInstance().getGenericMaster(10, AnalysisWorker.class);
			System.out.println("siteCrawls : " + siteCrawls.size());
			
			int count = 0;
			for(SiteCrawl siteCrawl : siteCrawls) {
				try{
					master.tell(siteCrawl.getSiteCrawlId(), ActorRef.noSender());
					JPA.em().detach(siteCrawl);
				} catch(Exception e) {
					System.out.println("caught excweption (site " + siteCrawl.getSite().getSiteId() + ") : " + e.getMessage());
				}
				
				
				count++;
				if(count %500 == 0){
					System.out.println("count : " + count);
				}
			}
		}

}
