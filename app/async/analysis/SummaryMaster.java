package async.analysis;

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
import async.work.SiteWork;

public class SummaryMaster extends UntypedActor {

	private final int numWorkers;
	
	private Router router;
	
	
	private int numResults;

	public SummaryMaster(int numWorkers) {
		this.numWorkers = numWorkers;
		numResults = 0;
		
		List<Routee> routees = new ArrayList<Routee>();
	    for (int i = 0; i < this.numWorkers; i++) {
	      ActorRef r = getContext().actorOf(Props.create(SummaryWorker.class));
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
				SiteInformationOld siteInfo= siteWork.getSiteInfo();
				
				if(siteWork.getSummaryWork() == SiteWork.DO_WORK){
					siteWork.setSummaryWork(SiteWork.WORK_IN_PROGRESS);
					router.route(siteWork, getSelf());
				}
				else if(siteWork.getSummaryWork() == SiteWork.WORK_IN_PROGRESS){
					siteWork.setSummaryWork(SiteWork.WORK_COMPLETED);
					Asyncleton.instance().getMainListener().tell(siteWork, getSelf());
				}
				
			}
			else if(work instanceof Terminated) {
				Logger.error("SummaryMaster received terminated worker");
				router = router.removeRoutee(((Terminated) work).actor());
				ActorRef worker = getContext().actorOf(Props.create(SummaryWorker.class));
				getContext().watch(worker);
				router = router.addRoutee(new ActorRefRoutee(worker));
			}
		}
		catch(Exception e){
			Logger.error("Caught Exception in Summary Master : " + e);
		}
		
	}
}