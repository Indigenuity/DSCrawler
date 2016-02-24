package async.work.infofetch;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import agarbagefolder.amalgamation.AmalgamationWorkOrder;
import agarbagefolder.amalgamation.AmalgamationWorkResult;
import agarbagefolder.crawlingnew.SiteCrawlWorkOrder;
import agarbagefolder.crawlingnew.SiteCrawlWorkResult;
import agarbagefolder.googleplaces.PlacesPageWorkOrder;
import agarbagefolder.googleplaces.PlacesPageWorkResult;
import agarbagefolder.siteupdate.SiteUpdateWorkOrder;
import agarbagefolder.siteupdate.SiteUpdateWorkResult;
import agarbagefolder.textanalysis.TextAnalysisWorkOrder;
import agarbagefolder.textanalysis.TextAnalysisWorkResult;
import agarbagefolder.urlresolve.UrlResolveWorkOrder;
import agarbagefolder.urlresolve.UrlResolveWorkResult;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import analysis.MobileCrawlAnalyzer;
import async.Asyncleton;
import async.docanalysis.DocAnalysisWorkOrder;
import async.docanalysis.DocAnalysisWorkResult;
import async.monitoring.AsyncMonitor;
import async.registration.WorkerRegistry;
import async.tools.AmalgamationTool;
import async.tools.DocAnalysisTool;
import async.tools.SiteCrawlTool;
import async.tools.SiteUpdateTool;
import async.tools.TextAnalysisTool;
import async.work.MultiStepJPAWorker;
import async.work.WorkItem;
import async.work.WorkOrder;
import async.work.WorkResult;
import async.work.WorkStatus;
import async.work.WorkType;
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
	public WorkOrder processWorkOrder(WorkOrder workOrder) {
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
				return infoFetch;
			}
		}catch(Exception e) {
			refreshInfoFetch();
			return null;
		}
		return null;
	}
	
	public void refreshInfoFetch(){
		JPA.withTransaction( () -> {
//			System.out.println("infoFetch before : " + infoFetch.getInfoFetchId());
			JPA.em().merge(infoFetch);
//			System.out.println("after merge");
			JPA.em().getTransaction().commit();
//			System.out.println("after commit");
			JPA.em().getTransaction().begin();
//			System.out.println("after begin");
			infoFetch = JPA.em().find(InfoFetch.class, infoFetch.getInfoFetchId());
//			System.out.println("after refetch");
//			JPA.em().detach(infoFetch);
//			System.out.println("infoFetch after refresh : " + infoFetch.getInfoFetchId());
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
			System.out.println("InfoFetchWorker doing urlCheck");
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
		
		try{
			if(!shouldSubtask(infoFetch.getSiteUpdate()))
				return false;
			if(infoFetch.getSiteId() == null) {
				throw new IllegalStateException("Need site to perform site update");
			}
			System.out.println("InfoFetchWorker doing SiteUpdate");
			SiteUpdateWorkResult workResult = null;
//			SiteUpdateTool.doWorkOrder(new SiteUpdateWorkOrder(infoFetch.getSiteId()));
			if(workResult.getWorkStatus() == WorkStatus.WORK_COMPLETED){
				infoFetch.getSiteUpdate().workStatus = WorkStatus.WORK_COMPLETED;
				infoFetch.setUrlCheckId(workResult.getUrlCheckId());
			}
			else {
				infoFetch.getSiteUpdate().workStatus = WorkStatus.NEEDS_REVIEW;
				infoFetch.setUrlCheckId(workResult.getUrlCheckId());
			}
			return true;
		}catch(IncompleteSubtaskException e){
			throw e;
		}catch(Exception e){
			
			String message = "siteUpdate : " + e.getMessage();
			infoFetch.getSiteUpdate().workStatus = WorkStatus.NEEDS_REVIEW;
			infoFetch.getSiteUpdate().note = message;
			throw new IncompleteSubtaskException(message);
		}
	}
	
	private boolean doSiteCrawl() throws IncompleteSubtaskException {
		
		try{
			if(!shouldSubtask(infoFetch.getSiteCrawl()))
				return false;
			if(infoFetch.getSiteId() == null) {
				throw new IncompleteSubtaskException("Need site to perform site crawl");
			}
			System.out.println("InfoFetchWorker doing SiteCrawl");			
			SiteCrawlWorkResult workResult = null;
//			SiteCrawlTool.doWorkOrder(new SiteCrawlWorkOrder(infoFetch.getSiteId()));
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
			System.out.println("InfoFetchWorker doing Amalgamation");
			AmalgamationWorkResult workResult = null;
//			AmalgamationTool.doWorkOrder(new AmalgamationWorkOrder(infoFetch.getSiteCrawlId()));
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
			System.out.println("InfoFetchWorker doing TextAnalysis");
			TextAnalysisWorkResult workResult = null;
//			TextAnalysisWorker.doWorkOrder(new TextAnalysisWorkOrder(infoFetch.getSiteCrawlId()));
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
			
			System.out.println("InfoFetchWorker doing DocAnalysis");
			DocAnalysisWorkResult workResult = null;
//			DocAnalysisWorker.doWorkOrder(new DocAnalysisWorkOrder(infoFetch.getSiteCrawlId()));
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