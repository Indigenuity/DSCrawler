package async.metaanalysis;

import java.util.ArrayList;
import java.util.List;

import persistence.SiteCrawl;
import play.Logger;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;
import async.Asyncleton;
import async.docanalysis.DocAnalysisWorker;
import async.monitoring.AsyncMonitor;
import async.work.SiteWork;

public class MetaAnalysisMaster extends UntypedActor {

	private final int numWorkers;
	
	private Router router;
	
	private int numResults;

	public MetaAnalysisMaster(int numWorkers) {
		this.numWorkers = numWorkers;
		numResults = 0;
		
		List<Routee> routees = new ArrayList<Routee>();
	    for (int i = 0; i < this.numWorkers; i++) {
	      ActorRef r = getContext().actorOf(Props.create(MetaAnalysisWorker.class));
	      getContext().watch(r);
	      routees.add(new ActorRefRoutee(r));
	    }
	    router = new Router(new RoundRobinRoutingLogic(), routees);
	}
	
	@Override
	public void onReceive(Object work) throws Exception {
		try{
			if(work instanceof SiteWork) {
				SiteWork siteWork = (SiteWork) work;
				if(siteWork.getMetaAnalysisWork() == SiteWork.DO_WORK){
					AsyncMonitor.instance().addWip("Meta Analysis", siteWork.getSiteCrawlId());
					siteWork.setMetaAnalysisWork(SiteWork.WORK_IN_PROGRESS);
					router.route(siteWork, getSelf());
				}
				else if(siteWork.getMetaAnalysisWork() == SiteWork.WORK_COMPLETED){
					AsyncMonitor.instance().finishWip("Meta Analysis", siteWork.getSiteCrawlId());
					Asyncleton.instance().getMainListener().tell(siteWork, getSelf());
				}
				else if(siteWork.getMetaAnalysisWork() == SiteWork.WORK_IN_PROGRESS){	//Worker ended in error
					AsyncMonitor.instance().finishWip("Meta Analysis", siteWork.getSiteCrawlId());
				}
				
			}
			else if(work instanceof SiteCrawl) {
				router.route(work, getSelf());
			}
			else if(work instanceof Terminated) {
				Logger.error("MetaAnalysisMaster received terminated worker");
				router = router.removeRoutee(((Terminated) work).actor());
				ActorRef worker = getContext().actorOf(Props.create(MetaAnalysisWorker.class));
				getContext().watch(worker);
				router = router.addRoutee(new ActorRefRoutee(worker));
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			Logger.error("Caught Exception in MetaAnalysisMaster : " + e);
		}
	}
}