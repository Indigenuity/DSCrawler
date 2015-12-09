package async.crawling;

import java.util.ArrayList;
import java.util.List;

import experiment.Experiment;
import persistence.SiteCrawl;
import persistence.SiteInformationOld;
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
import async.monitoring.AsyncMonitor;
import async.monitoring.AsyncMonitor.CompletedWork;
import async.monitoring.AsyncMonitor.WorkInProgress;
import async.work.SiteWork;

public class CrawlingMaster extends UntypedActor {

	private final int numWorkers;
	
	private Router router;
	
	private int numResults;

	public CrawlingMaster(int numWorkers) {
		this.numWorkers = numWorkers;
		numResults = 0;
		
//		router = getContext().actorOf(new BalancingPool(3).props(Props.create(CrawlingWorker.class)), "router10");
		List<Routee> routees = new ArrayList<Routee>();
	    for (int i = 0; i < this.numWorkers; i++) {
	      ActorRef r = getContext().actorOf(Props.create(CrawlingWorker.class));
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
				
				if(siteWork.getCrawlWork() == SiteWork.DO_WORK){
					AsyncMonitor.instance().addWip("Crawl", siteWork.getSiteId());
					siteWork.setCrawlWork(SiteWork.WORK_IN_PROGRESS);
					router.route(siteWork, getSelf());
				}
				else if(siteWork.getCrawlWork() == SiteWork.WORK_COMPLETED){
					AsyncMonitor.instance().finishWip("Crawl", siteWork.getSiteId());
					Asyncleton.instance().getMainListener().tell(siteWork, getSelf());
				}
				else if(siteWork.getCrawlWork() == SiteWork.WORK_IN_PROGRESS){	//Worker ended in error
					AsyncMonitor.instance().finishWip("Crawl", siteWork.getSiteId());
				}
					
				
			}
			else if(work instanceof SiteCrawl) {
				router.route(work, getSelf());
			}
			else if(work instanceof Terminated) {
				Logger.error("CrawlingMaster received terminated worker");
				router = router.removeRoutee(((Terminated) work).actor());
				ActorRef worker = getContext().actorOf(Props.create(CrawlingWorker.class));
				getContext().watch(worker);
				router = router.addRoutee(new ActorRefRoutee(worker));
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			Logger.error("Caught Exception in CrawlingMaster : " + e);
		}
	}
}
