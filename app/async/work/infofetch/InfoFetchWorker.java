package async.work.infofetch;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import analysis.MobileCrawlAnalyzer;
import async.Asyncleton;
import async.amalgamation.AmalgamationWorkOrder;
import async.amalgamation.AmalgamationWorkResult;
import async.amalgamation.AmalgamationWorker;
import async.docanalysis.DocAnalysisWorkOrder;
import async.docanalysis.DocAnalysisWorkResult;
import async.docanalysis.DocAnalysisWorker;
import async.monitoring.AsyncMonitor;
import async.textanalysis.TextAnalysisWorkOrder;
import async.textanalysis.TextAnalysisWorkResult;
import async.textanalysis.TextAnalysisWorker;
import async.work.MultiStepJPAWorker;
import async.work.WorkItem;
import async.work.WorkOrder;
import async.work.WorkResult;
import async.work.WorkStatus;
import async.work.WorkType;
import async.work.WorkerRegistry;
import async.work.crawling.CrawlingWorker;
import async.work.crawling.SiteCrawlWorkOrder;
import async.work.crawling.SiteCrawlWorkResult;
import async.work.googleplaces.PlacesPageWorkOrder;
import async.work.googleplaces.PlacesPageWorkResult;
import async.work.siteupdate.SiteUpdateWorkOrder;
import async.work.siteupdate.SiteUpdateWorkResult;
import async.work.siteupdate.SiteUpdateWorker;
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
	
	@Override
	public boolean processWorkOrder(WorkOrder workOrder) {
		infoFetch = (InfoFetch) workOrder;
		System.out.println("InfoFetchWorker processing work order:"+ infoFetch.getInfoFetchId());
		
		try{
			if(doUrlCheck() ||
			doSiteUpdate() ||
			doSiteCrawl() ||
			doAmalgamation() ||
			doTextAnalysis() ||
			doDocAnalysis() ||
			doMetaAnalysis() ||
			doPlacesPageFetch()){
				refreshInfoFetch();
				return true;
			}
		}catch(Exception e) {
			refreshInfoFetch();
			return false;
		}
		return false;
	}
	
	public void refreshInfoFetch(){
		JPA.withTransaction( () -> {
			JPA.em().merge(infoFetch);
			JPA.em().getTransaction().commit();
			JPA.em().getTransaction().begin();
			JPA.em().refresh(infoFetch);
			JPA.em().detach(infoFetch);
		});
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
				
			}
			else if(workResult instanceof PlacesPageWorkResult) {
//				System.out.println("PlacesPageWorkresult");
				PlacesPageWorkResult result = (PlacesPageWorkResult) workResult;
				infoFetch.setPlacesPageId(result.getPlacesPageId());
				infoFetch.getPlacesPageFetch().workStatus = WorkStatus.WORK_COMPLETED;
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
	
	private static boolean shouldSubtask(Subtask subtask) throws IncompleteSubtaskException {
		WorkStatus workStatus = subtask.workStatus;
		if(workStatus == WorkStatus.NO_WORK || workStatus == WorkStatus.WORK_COMPLETED){
			return false;
		} 
		if(workStatus != WorkStatus.DO_WORK){
			throw new IncompleteSubtaskException("Subtask is not in a state to do work");
		}
		return true;
	}
	
	private boolean doUrlCheck() throws IncompleteSubtaskException{
		WorkStatus workStatus = infoFetch.getUrlCheck().workStatus;
		if(workStatus == WorkStatus.NO_WORK || workStatus == WorkStatus.WORK_COMPLETED){
			return false;
		} 
		else if(workStatus == WorkStatus.DO_WORK){
			String seed = infoFetch.getSeed();
			if(seed == null){
				infoFetch.getUrlCheck().workStatus = WorkStatus.NEEDS_REVIEW;
				infoFetch.getUrlCheck().note = "Need seed to perform URL Check";
				throw new IncompleteSubtaskException("urlCheck");
			}
			placeWorkOrder(new UrlResolveWorkOrder(infoFetch.getSeed()));
			return true;
		}
		else {
			throw new IncompleteSubtaskException("urlCheck");
		}
	}
	
	private boolean doSiteUpdate() throws IncompleteSubtaskException{
		WorkStatus workStatus = infoFetch.getSiteUpdate().workStatus;
		if(workStatus == WorkStatus.NO_WORK || workStatus == WorkStatus.WORK_COMPLETED){
			return false;
		} 
		else if(workStatus == WorkStatus.DO_WORK){
			if(infoFetch.getSiteId() == null) {
				infoFetch.getSiteUpdate().workStatus = WorkStatus.NEEDS_REVIEW;
				infoFetch.getSiteUpdate().note = "Need site to perform site update";
				throw new IncompleteSubtaskException("urlCheck");
			}
			SiteUpdateWorkResult workResult = SiteUpdateWorker.doWorkOrder((new SiteUpdateWorkOrder(infoFetch.getSiteId(), infoFetch.getUrlCheckId())));
			
			if(workResult.getWorkStatus() == WorkStatus.WORK_COMPLETED){
				infoFetch.setUrlCheckId(workResult.getUrlCheckId());
				infoFetch.getSiteUpdate().workStatus = WorkStatus.WORK_COMPLETED;
			}
			else {
				infoFetch.getSiteUpdate().workStatus = WorkStatus.NEEDS_REVIEW;
			}
			return true;
		}
		else {
			throw new IncompleteSubtaskException("siteUpdate");
		}
	}
	
	private boolean doSiteCrawl() throws IncompleteSubtaskException {
		try{
			if(!shouldSubtask(infoFetch.getSiteCrawl()))
				return false;
			if(infoFetch.getSiteId() == null) {
				throw new IncompleteSubtaskException("Need site to perform site crawl");
			}
			
			SiteCrawlWorkResult workResult = CrawlingWorker.doWorkOrder(new SiteCrawlWorkOrder(infoFetch.getSiteId()));
			if(workResult.getWorkStatus() == WorkStatus.WORK_COMPLETED){
				infoFetch.getSiteCrawl().workStatus = WorkStatus.WORK_COMPLETED;
				infoFetch.setSiteCrawlId(workResult.getSiteCrawlId());
			}
			else {
				infoFetch.getSiteCrawl().workStatus = WorkStatus.NEEDS_REVIEW;
			}
			return true;
		}catch(Exception e){
			
			String message = "siteCrawl : " + e.getMessage();
			infoFetch.getSiteCrawl().workStatus = WorkStatus.NEEDS_REVIEW;
			infoFetch.getSiteCrawl().note = message;
			throw new IncompleteSubtaskException(message);
		}
	}
	
	private boolean doAmalgamation() throws IncompleteSubtaskException{
		try{
			if(!shouldSubtask(infoFetch.getAmalgamation()))
				return false;
			if(infoFetch.getSiteCrawlId() == null) {
				throw new IncompleteSubtaskException("Need sitecrawl to perform amalgamation");
			}
			
			AmalgamationWorkResult workResult = AmalgamationWorker.doWorkOrder(new AmalgamationWorkOrder(infoFetch.getSiteCrawlId()));
			if(workResult.getWorkStatus() == WorkStatus.WORK_COMPLETED){
				infoFetch.getAmalgamation().workStatus = WorkStatus.WORK_COMPLETED;
			}
			else {
				infoFetch.getAmalgamation().workStatus = WorkStatus.NEEDS_REVIEW;
			}
			return true;
		}catch(Exception e){
			String message = "amalgamation : " + e.getMessage();
			infoFetch.getAmalgamation().workStatus = WorkStatus.NEEDS_REVIEW;
			infoFetch.getAmalgamation().note = message;
			throw new IncompleteSubtaskException(message);
		}
	}
	
	private boolean doTextAnalysis() throws IncompleteSubtaskException{
		try{
			if(!shouldSubtask(infoFetch.getTextAnalysis()))
				return false;
			if(infoFetch.getSiteCrawlId() == null) {
				throw new IncompleteSubtaskException("Need sitecrawl to perform TextAnalysis");
			}
			
			TextAnalysisWorkResult workResult = TextAnalysisWorker.doWorkOrder(new TextAnalysisWorkOrder(infoFetch.getSiteCrawlId()));
			if(workResult.getWorkStatus() == WorkStatus.WORK_COMPLETED){
				infoFetch.getTextAnalysis().workStatus = WorkStatus.WORK_COMPLETED;
			}
			else {
				infoFetch.getTextAnalysis().workStatus = WorkStatus.NEEDS_REVIEW;
			}
			return true;
		}catch(Exception e){
			String message = "TextAnalysis : " + e.getMessage();
			infoFetch.getTextAnalysis().workStatus = WorkStatus.NEEDS_REVIEW;
			infoFetch.getTextAnalysis().note = message;
			throw new IncompleteSubtaskException(message);
		}
	}
	private boolean doDocAnalysis() throws IncompleteSubtaskException{
		try{
			if(!shouldSubtask(infoFetch.getDocAnalysis()))
				return false;
			if(infoFetch.getSiteCrawlId() == null) {
				throw new IncompleteSubtaskException("Need sitecrawl to perform DocAnalysis");
			}
			
			DocAnalysisWorkResult workResult = DocAnalysisWorker.doWorkOrder(new DocAnalysisWorkOrder(infoFetch.getSiteCrawlId()));
			if(workResult.getWorkStatus() == WorkStatus.WORK_COMPLETED){
				infoFetch.getDocAnalysis().workStatus = WorkStatus.WORK_COMPLETED;
			}
			else {
				infoFetch.getDocAnalysis().workStatus = WorkStatus.NEEDS_REVIEW;
			}
			return true;
		}catch(Exception e){
			String message = "DocAnalysis : " + e.getMessage();
			infoFetch.getDocAnalysis().workStatus = WorkStatus.NEEDS_REVIEW;
			infoFetch.getDocAnalysis().note = message;
			throw new IncompleteSubtaskException(message);
		}
	}
	private boolean doMetaAnalysis() throws IncompleteSubtaskException{
//		WorkStatus workStatus = infoFetch.getMetaAnalysis().workStatus;
//		if(workStatus == WorkStatus.NO_WORK){
//			return false;
//		} 
//		else if(workStatus == WorkStatus.DO_WORK || workStatus == WorkStatus.WORK_COMPLETED){
//			if(infoFetch.getSiteId() == null) {
//				infoFetch.getMetaAnalysis().workStatus = WorkStatus.NEEDS_REVIEW;
//				infoFetch.getMetaAnalysis().note = "Need sitecrawl to perform MetaAnalysis";
//				throw new IncompleteSubtaskException("metaAnalysis");
//			}
//			placeWorkOrder(new MetaAnalysisWorkOrder(infoFetch.getSiteCrawlId()));
//			return true;
//		}
//		else {
//			throw new IncompleteSubtaskException("metaAnalysis");
//		}
		return false;
	}
	
	private boolean doPlacesPageFetch() throws IncompleteSubtaskException{
		WorkStatus workStatus = infoFetch.getPlacesPageFetch().workStatus;
		if(workStatus == WorkStatus.NO_WORK || workStatus == WorkStatus.WORK_COMPLETED){
			return false;
		} 
		else if(workStatus == WorkStatus.DO_WORK){
			if(infoFetch.getPlacesId() == null) {
				infoFetch.getPlacesPageFetch().workStatus = WorkStatus.NEEDS_REVIEW;
				infoFetch.getPlacesPageFetch().note = "Need places ID to perform places page fetch";
				throw new IncompleteSubtaskException("placesPageFetch");
			}
			placeWorkOrder(new PlacesPageWorkOrder(infoFetch.getPlacesId()));
			return true;
		}
		else {
			throw new IncompleteSubtaskException("placesPageFetch");
		}
	}
	
	private void placeWorkOrder(WorkOrder workOrder) {
		Asyncleton.getInstance().getMaster(workOrder.getWorkType()).tell(workOrder, getSelf());
	}
	
	
}