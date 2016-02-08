package async.work.infofetch;

import java.util.HashSet;
import java.util.Set;
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
import async.work.crawling.SiteCrawlWorkOrder;
import async.work.crawling.SiteCrawlWorkResult;
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
	private int count = 0;
	
	public void processWorkOrder(WorkOrder workOrder) {
		infoFetch = (InfoFetch) workOrder;
		System.out.println("InfoFetchWorker processing work order:"+ infoFetch.getInfoFetchId());
	}
	
	public void doNextStep(){
//		System.out.println("doing next step");
		if(needsReview()){
//			System.out.println("InfoFetchWorker going to finish because review is needed");
			finish();
		}
			WorkOrder nextOrder = null;
			if(infoFetch.getUrlCheck().workStatus == WorkStatus.DO_WORK){
//				System.out.println("InfoFetchWorker going to order url check work");
				nextOrder = new UrlResolveWorkOrder(infoFetch.getSeed());
			}
			else if(infoFetch.getSiteUpdate().workStatus == WorkStatus.DO_WORK){
//				System.out.println("InfoFetchWorker going to order site update work");
				nextOrder = new SiteUpdateWorkOrder(infoFetch.getSiteId(), infoFetch.getUrlCheckId());
			}
			else if(infoFetch.getSiteCrawl().workStatus == WorkStatus.DO_WORK){
				nextOrder = new SiteCrawlWorkOrder(infoFetch.getSiteId());
			}
			else if(infoFetch.getPlacesPageFetch().workStatus == WorkStatus.DO_WORK){
//				System.out.println("InfoFetchWorker going to order places page work");
				nextOrder = new PlacesPageWorkOrder(infoFetch.getPlacesId()); 
			}
			else {
				
			}
			
			if(nextOrder != null){
//				System.out.println("InfoFetchWorker sending out work order");
				Asyncleton.getInstance().getMaster(nextOrder.getWorkType()).tell(nextOrder, getSelf());
			}
			else {
//				System.out.println("InfoFetchWorker going to finish");
				finish();
			}
	}
	
	@Override
	public void processWorkResult(WorkResult workResult) {
//		System.out.println("InfoFetchWorker processing work result : " + workResult);
		JPA.withTransaction( () -> {
			infoFetch = JPA.em().find(InfoFetch.class, infoFetch.getInfoFetchId());
			if(workResult instanceof UrlResolveWorkResult) {
//				System.out.println("URLresolveworkresult");
				UrlResolveWorkResult result = (UrlResolveWorkResult) workResult;
//				System.out.println("get urlcheckid : " + result.getUrlCheckId());
				infoFetch.setUrlCheckId(result.getUrlCheckId());
				infoFetch.getUrlCheck().workStatus = WorkStatus.WORK_COMPLETED;
				
			}else if(workResult instanceof SiteUpdateWorkResult) {
//				System.out.println("SiteUpdateWorkresult");
				SiteUpdateWorkResult result = (SiteUpdateWorkResult) workResult;
				if(workResult.getWorkStatus() == WorkStatus.WORK_COMPLETED){
					infoFetch.getSiteUpdate().workStatus = WorkStatus.WORK_COMPLETED;
				}
				else {
					infoFetch.getSiteUpdate().workStatus = WorkStatus.NEEDS_REVIEW;
				}
			}
			else if(workResult instanceof PlacesPageWorkResult) {
//				System.out.println("PlacesPageWorkresult");
				PlacesPageWorkResult result = (PlacesPageWorkResult) workResult;
				infoFetch.setPlacesPageId(result.getPlacesPageId());
				infoFetch.getPlacesPageFetch().workStatus = WorkStatus.WORK_COMPLETED;
			}
			else if(workResult instanceof SiteCrawlWorkResult) {
//				System.out.println("SiteCrawlWorkresult");
				SiteCrawlWorkResult result = (SiteCrawlWorkResult) workResult;
				if(workResult.getWorkStatus() == WorkStatus.WORK_COMPLETED){
					infoFetch.getSiteCrawl().workStatus = WorkStatus.WORK_COMPLETED;
					infoFetch.setSiteCrawlId(result.getSiteCrawlId());
				}
				else {
					infoFetch.getSiteCrawl().workStatus = WorkStatus.NEEDS_REVIEW;
				}
			}
			
			JPA.em().getTransaction().commit();
			JPA.em().getTransaction().begin();
			JPA.em().refresh(infoFetch);
			JPA.em().detach(infoFetch);
		});
	}
	
	
	@Override
	public WorkResult generateWorkResult(){
//		System.out.println("InfoFetchWorker generating work result");
		WorkResult workResult = new WorkResult();
//		System.out.println("results uuid : " + this.getWorkOrderUuid());
		workResult.setUuid(this.getWorkOrderUuid());
		return workResult;
	}
	
	protected boolean needsReview() {
		boolean needsReview = false;
		Set<Subtask> subtasks = new HashSet<Subtask>();
		subtasks.add(infoFetch.getUrlCheck());
		subtasks.add(infoFetch.getSiteUpdate());
		subtasks.add(infoFetch.getSiteCrawl());
		subtasks.add(infoFetch.getPlacesPageFetch());
		
		for(Subtask subtask : subtasks) {
			needsReview |= (subtask.workStatus != WorkStatus.NO_WORK) && (subtask.workStatus != WorkStatus.DO_WORK) && (subtask.workStatus != WorkStatus.WORK_COMPLETED);
		}
//		System.out.println("needs review : " + needsReview);
		return needsReview;
	}
	
}