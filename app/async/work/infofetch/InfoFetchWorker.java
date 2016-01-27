package async.work.infofetch;

import java.util.UUID;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import analysis.MobileCrawlAnalyzer;
import async.Asyncleton;
import async.monitoring.AsyncMonitor;
import async.work.MultiStepJPAWorker;
import async.work.WorkItem;
import async.work.WorkOrder;
import async.work.WorkResult;
import async.work.WorkStatus;
import async.work.WorkType;
import async.work.WorkerRegistry;
import async.work.googleplaces.PlacesPageWorkOrder;
import async.work.googleplaces.PlacesPageWorkResult;
import async.work.siteupdate.SiteUpdateWorkOrder;
import async.work.siteupdate.SiteUpdateWorkResult;
import async.work.urlresolve.UrlResolveWorkOrder;
import async.work.urlresolve.UrlResolveWorkResult;
import persistence.MobileCrawl;
import persistence.UrlCheck;
import play.Logger;
import play.db.jpa.JPA;

public class InfoFetchWorker extends MultiStepJPAWorker {

	private Long uuid = UUID.randomUUID().getLeastSignificantBits();
	private InfoFetch infoFetch = null;
	private Long infoFetchId = null;
	
	public void processWorkOrder(WorkOrder workOrder) {
		infoFetch = (InfoFetch) workOrder;
		System.out.println("processing work order for info fetch : "+ infoFetch.getInfoFetchId());
	}
	
	public boolean hasNextStep() {
		return infoFetch.hasMoreWork();
	}
	
	public void doNextStep(){ 
//		System.out.println("doing next step");
		if(infoFetch.needUrlCheck()){
			Class<?> clazz = WorkerRegistry.getInstance().getRegistrant(WorkType.REDIRECT_RESOLVE);
			UrlResolveWorkOrder workOrder = new UrlResolveWorkOrder(infoFetch.getSeed());
			ActorRef r = getContext().actorOf(Props.create(clazz));
		    r.tell(workOrder, getSelf());
		}
		else if(infoFetch.needPlacesPageFetch()){
//			System.out.println("sending places page fetch work"); 
			Class<?> clazz = WorkerRegistry.getInstance().getRegistrant(WorkType.PLACES_PAGE_FETCH);
			PlacesPageWorkOrder workOrder = new PlacesPageWorkOrder(infoFetch.getPlacesId());
			ActorRef r = getContext().actorOf(Props.create(clazz));
		    r.tell(workOrder, getSelf());
		}
		else if(infoFetch.needSiteUpdate()){
			Class<?> clazz = WorkerRegistry.getInstance().getRegistrant(WorkType.SITE_UPDATE);
			SiteUpdateWorkOrder workOrder = new SiteUpdateWorkOrder(infoFetch.getSiteId(), infoFetch.getUrlCheckId());
			ActorRef r = getContext().actorOf(Props.create(clazz));
		    r.tell(workOrder, getSelf());
		}
		else {
			Logger.error("In doNextStep in InfoFetchWorker with no next step");
		}
	}
	
	public void proceedWithWork(){
//		System.out.println("proceed with work");
		if(hasNextStep()){
			doNextStep();
		}
		else {
			finish();
		}
	}
	
	public void processWorkResult(WorkResult workResult) {
//		System.out.println("processing work result : " + workResult);
		JPA.withTransaction( () -> {
			infoFetch = JPA.em().find(InfoFetch.class, infoFetch.getInfoFetchId());
			if(workResult instanceof UrlResolveWorkResult) {
				UrlResolveWorkResult result = (UrlResolveWorkResult) workResult;
//				System.out.println("get urlcheckid : " + result.getUrlCheckId());
				infoFetch.setUrlCheckId(result.getUrlCheckId());
				
			}
			else if(workResult instanceof PlacesPageWorkResult) {
				PlacesPageWorkResult result = (PlacesPageWorkResult) workResult;
//				System.out.println("get places page id : " + result.getPlacesPageId());
				infoFetch.setPlacesPageId(result.getPlacesPageId());
			}else if(workResult instanceof SiteUpdateWorkResult) {
				SiteUpdateWorkResult result = (SiteUpdateWorkResult) workResult;
//				if(workResult.getWorkStatus() == WorkStatus.WORK_COMPLETED){
					infoFetch.setSiteUpdateCompleted(true);
//				}
			}
			
			JPA.em().getTransaction().commit();
			JPA.em().getTransaction().begin();
			JPA.em().refresh(infoFetch);
			JPA.em().detach(infoFetch);
		});
	}
	
	
	
	public WorkResult generateWorkResult(){
		System.out.println("generating work result");
		return new WorkResult();
	}
	
}