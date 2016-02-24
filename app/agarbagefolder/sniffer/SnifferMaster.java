package agarbagefolder.sniffer;

import java.util.ArrayList;
import java.util.List;

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

public class SnifferMaster extends UntypedActor {
	private int numWorkers;
	private Router router; 
	private int numResults;
	
	public SnifferMaster(int numWorkers) {
		this.numWorkers = numWorkers;
		numResults = 0;
		
//		router = getContext().actorOf(new BalancingPool(3).props(Props.create(CrawlingWorker.class)), "router10");
		List<Routee> routees = new ArrayList<Routee>();
	    for (int i = 0; i < this.numWorkers; i++) {
	      ActorRef r = getContext().actorOf(Props.create(SnifferWorker.class));
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
				
				if(siteWork.getRedirectResolveWork() == SiteWork.DO_WORK){
					AsyncMonitor.instance().addWip("Sniffer", siteWork.getSiteId());
					siteWork.setRedirectResolveWork(SiteWork.WORK_IN_PROGRESS);
					router.route(siteWork, getSelf());
				}
				else if(siteWork.getRedirectResolveWork() == SiteWork.WORK_COMPLETED){
					Asyncleton.instance().getMainListener().tell(siteWork, getSelf());
					AsyncMonitor.instance().finishWip("Sniffer", siteWork.getSiteId());
				}
				else if(siteWork.getRedirectResolveWork() == SiteWork.WORK_IN_PROGRESS){
					Logger.error("Error while sniffing : " + siteWork.getSite());
					AsyncMonitor.instance().finishWip("Sniffer", siteWork.getSiteId());
				}
				
			}
			else if(work instanceof Terminated) {
				Logger.error("AnalysisMaster received terminated worker");
				router = router.removeRoutee(((Terminated) work).actor());
				ActorRef worker = getContext().actorOf(Props.create(SnifferWorker.class));
				getContext().watch(worker);
				router = router.addRoutee(new ActorRefRoutee(worker));
			}
		}
		catch(Exception e){
			Logger.error("Caught error in SnifferMaste : " + e);
		}
		
	}
}
