package async;

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
import async.work.SiteWork;
import async.work.WorkItem;
import async.work.WorkSet;
import async.work.WorkType;
import async.work.infofetch.InfoFetch;
import experiment.Experiment;

public class MainMaster extends UntypedActor {

	private final int numWorkers;
	
	private final ActorRef listener;
	private Router router;
	
	private List<SiteWork> sites;
	
	private int numResults;

	public MainMaster(int numWorkers, ActorRef listener) {
		this.numWorkers = numWorkers;
		this.listener = listener;
		numResults = 0;
		
//		router = getContext().actorOf(new BalancingPool(3).props(Props.create(CrawlingWorker.class)), "router10");
		List<Routee> routees = new ArrayList<Routee>();
	    for (int i = 0; i < this.numWorkers; i++) {
	      ActorRef r = getContext().actorOf(Props.create(MainWorker.class));
	      getContext().watch(r);
	      routees.add(new ActorRefRoutee(r));
	    }
	    router = new Router(new RoundRobinRoutingLogic(), routees);
	}
	
	
	
	@Override
	public void onReceive(Object message) throws Exception {
		try{
			if(message instanceof InfoFetch) {
				Asyncleton.instance().getMaster(WorkType.INFO_FETCH).tell(message, getSelf());
			}
//			else if(message instanceof WorkSet) {
//				WorkSet workSet = (WorkSet) message;
//				WorkItem workItem = workSet.getNextWorkItem();
//				if(workItem == null){
//					System.out.println("Work completed for WorkSet : " + workSet.getUuid());
//					return;
//				}
//				
//				if(WaitingRoom.add(workItem, workSet)) {
//					Asyncleton.instance().getMaster(workItem.getWorkType()).tell(workItem, ActorRef.noSender());
//				}
//				else {
//					Logger.warn("Attempt to perform multiple tasks at same time was stopped on id " + workSet.getId() + " : " + workItem.getWorkType());
//				}
//			}
//			
//			else if(message instanceof WorkItem) {
//				WorkItem workItem = (WorkItem) message;
//				WorkSet workSet = null;
//				if(isMainWork(workItem)) {
//					router.route(message, getSelf());
//					return;
//				}
//				workSet = WaitingRoom.remove(workItem);
//				if(workSet == null) {
//					throw new IllegalStateException("Main Master received orhpaned work item : " + workItem.getId());
//				}
//				
//				getSelf().tell(workSet, getSelf());	//Send back to beginning of process to check for more work
//			}
			
			else if(message instanceof SiteWork) {
				router.route((SiteWork) message, getSelf());
			}
			
			else if(message instanceof Terminated) {
				Logger.error("MainMaster received terminated worker");
				router = router.removeRoutee(((Terminated) message).actor());
				ActorRef worker = getContext().actorOf(Props.create(MainWorker.class));
				getContext().watch(worker);
				router = router.addRoutee(new ActorRefRoutee(worker));
			}
			
			else if(message instanceof String) {
				numResults++;
				System.out.println("got a Main result : " + numResults);
			}
		}
		catch(Exception e){
			Logger.error("Exception caught in MainMaster : " + e);
		}
	}
	
	private static boolean isMainWork(WorkItem workItem) {
		return false;
	}
}
