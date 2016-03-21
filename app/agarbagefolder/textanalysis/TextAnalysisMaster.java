package agarbagefolder.textanalysis;

import java.util.ArrayList;
import java.util.List;

import agarbagefolder.SiteWork;
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
import async.async.Asyncleton;
import async.monitoring.AsyncMonitor;
import async.tools.TextAnalysisTool;

public class TextAnalysisMaster extends UntypedActor {

	private final int numWorkers;
	
	private Router router;
	
	private int numResults;

	public TextAnalysisMaster(int numWorkers) {
		this.numWorkers = numWorkers;
		numResults = 0;
		
		List<Routee> routees = new ArrayList<Routee>();
	    for (int i = 0; i < this.numWorkers; i++) {
	      ActorRef r = getContext().actorOf(Props.create(TextAnalysisTool.class));
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
				if(siteWork.getTextAnalysisWork() == SiteWork.DO_WORK){
					AsyncMonitor.instance().addWip("Text Analysis", siteWork.getSiteCrawlId());
					siteWork.setTextAnalysisWork(SiteWork.WORK_IN_PROGRESS);
					router.route(siteWork, getSelf());
				}
				else if(siteWork.getTextAnalysisWork() == SiteWork.WORK_COMPLETED){
					AsyncMonitor.instance().finishWip("Text Analysis", siteWork.getSiteCrawlId());
					Asyncleton.instance().getMainListener().tell(siteWork, getSelf());
				}
				else if(siteWork.getTextAnalysisWork() == SiteWork.WORK_IN_PROGRESS){	//Worker ended in error
					AsyncMonitor.instance().finishWip("Text Analysis", siteWork.getSiteCrawlId());
				}
				
			}
			else if(work instanceof SiteCrawl) {
				router.route(work, getSelf());
			}
			else if(work instanceof Terminated) {
				Logger.error("TextAnalysisMaster received terminated worker");
				router = router.removeRoutee(((Terminated) work).actor());
				ActorRef worker = getContext().actorOf(Props.create(TextAnalysisTool.class));
				getContext().watch(worker);
				router = router.addRoutee(new ActorRefRoutee(worker));
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			Logger.error("Caught Exception in TextAnalysisMaster : " + e);
		}
	}
}