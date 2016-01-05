package controllers;

import global.Global;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;

import akka.actor.ActorRef;
import analysis.SiteCrawlAnalyzer;
import analysis.LogAnalyzer;
import async.Asyncleton;
import async.work.SiteWork;
import async.work.WorkItem;
import async.work.WorkSet;
import async.work.WorkType;
import dao.SiteCrawlDAO;
import persistence.CrawlSet;
import persistence.MobileCrawl;
import persistence.PageInformation;
import persistence.Site;
import persistence.SiteCrawl;
import persistence.SiteInformationOld;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

public class JobController extends Controller {

	
	@Transactional 
	public static Result crawlSetWork()  {
		try{
			System.out.println("received crawl set work"); 
			DynamicForm requestData = Form.form().bindFromRequest();
			CrawlSet crawlSet = JPA.em().find(CrawlSet.class, Long.parseLong(requestData.get("crawlSetId")));
			WorkType workType = WorkType.valueOf(requestData.get("workType"));
			Integer numToProcess = Integer.parseInt(requestData.get("numToProcess"));
			Integer offset = Integer.parseInt(requestData.get("offset"));
			System.out.println("Crawl set : "  + crawlSet);
	    	System.out.println("workType : " + workType);
	    	System.out.println("numToProcess : " + numToProcess);
	    	System.out.println("offset : " + offset);
	    	
			int count = 0;
			Set<Site> sites = null;
			if(workType == WorkType.MOBILE_TEST) {
				Hibernate.initialize(crawlSet.getNeedMobile());
				sites = crawlSet.getNeedMobile();
			}
			else if(workType == WorkType.CRAWL) {
				Hibernate.initialize(crawlSet.getUncrawled());
				sites = crawlSet.getUncrawled();
			}
			else if(workType == WorkType.REDIRECT_RESOLVE) {
				Hibernate.initialize(crawlSet.getNeedRedirectResolve());
				sites = crawlSet.getNeedRedirectResolve();
			}
			
			if(sites != null) {
				for(Site site : sites) {
					if( count++ < numToProcess) {
						WorkSet workSet = new WorkSet();
						workSet.setCrawlSetId(crawlSet.getCrawlSetId());
						workSet.setSiteId(site.getSiteId());
						WorkItem workItem = new WorkItem(workType);
						workSet.addWorkItem(workItem);
						System.out.println("submitting work for site : " + site.getSiteId());
						JPA.em().detach(site);
						Asyncleton.instance().getMainMaster().tell(workSet, ActorRef.noSender());
					}
				}
			}
			else if(workType == WorkType.CUSTOM) {
				Set<SiteCrawl> siteCrawls = crawlSet.getCompletedCrawls();
				for(SiteCrawl siteCrawl : siteCrawls) {
					if( count++ < numToProcess) {
						WorkSet workSet = new WorkSet();
						workSet.setCrawlSetId(crawlSet.getCrawlSetId());
						workSet.setSiteCrawlId(siteCrawl.getSiteCrawlId());
						WorkItem workItem;
						if(!siteCrawl.isAmalgamationDone()){
							workItem = new WorkItem(WorkType.AMALGAMATION);
							workSet.addWorkItem(workItem);
						}
						if(!siteCrawl.isDocAnalysisDone()) {
							workItem = new WorkItem(WorkType.DOC_ANALYSIS);
							workSet.addWorkItem(workItem);
						}
						if(!siteCrawl.isTextAnalysisDone()){
							workItem = new WorkItem(WorkType.TEXT_ANALYSIS);
							workSet.addWorkItem(workItem);
						}
						if(!siteCrawl.isMetaAnalysisDone()){
							workItem = new WorkItem(WorkType.META_ANALYSIS);
							workSet.addWorkItem(workItem);
						}
						System.out.println("submitting work for sitecrawl : " + siteCrawl.getSiteCrawlId());
						JPA.em().detach(siteCrawl);	
						Asyncleton.instance().getMainMaster().tell(workSet, ActorRef.noSender());
					}
				}
			}
			else if(workType == WorkType.MOBILE_ANALYSIS) {
				for(MobileCrawl crawl : crawlSet.getMobileCrawls()){
					WorkSet workSet = new WorkSet();
					workSet.setCrawlSetId(crawlSet.getCrawlSetId());
					workSet.setMobileCrawlId(crawl.getSiteCrawlId());
					WorkItem workItem = new WorkItem(workType);
					workSet.addWorkItem(workItem);
					JPA.em().detach(crawl);
					Asyncleton.instance().getMainMaster().tell(workSet, ActorRef.noSender());
				}
			}
			else{		//Do site crawl work
				List<SiteCrawl> siteCrawls = new ArrayList<SiteCrawl>();
				if(workType == WorkType.META_ANALYSIS){
					siteCrawls = SiteCrawlDAO.getCrawlSetList(crawlSet.getCrawlSetId(), "metaAnalysisDone", false, numToProcess, offset);
					
				}
				else if(workType == WorkType.DOC_ANALYSIS){
					siteCrawls = SiteCrawlDAO.getCrawlSetList(crawlSet.getCrawlSetId(), "docAnalysisDone", false, numToProcess, offset);
					
				}
				else if(workType == WorkType.TEXT_ANALYSIS){
					siteCrawls = SiteCrawlDAO.getCrawlSetList(crawlSet.getCrawlSetId(), "textAnalysisDone", false, numToProcess, offset);
					
				}
				else{
					throw new UnsupportedOperationException("Unknown Work Type for Crawl Set : " + workType);
				}
				
				for(SiteCrawl siteCrawl : siteCrawls) {
					WorkSet workSet = new WorkSet();
					workSet.setCrawlSetId(crawlSet.getCrawlSetId());
					workSet.setSiteCrawlId(siteCrawl.getSiteCrawlId());
					WorkItem workItem = new WorkItem(workType);
					workSet.addWorkItem(workItem);
					JPA.em().detach(siteCrawl);
					Asyncleton.instance().getMainMaster().tell(workSet, ActorRef.noSender());
				}
			}
			
			
	    	
	    	return DataView.dashboard("Submitted Crawl Set Job");
		}
		catch(Exception e) {
			return internalServerError(e.getMessage());
		}
	}
	
	@Transactional
	public static Result fillFailedUrls() throws IOException{
		
		LogAnalyzer.nullUrls();
		return ok();
	}
	
	@Transactional
	public static Result smallCrawl(int numToProcess){
		System.out.println("Global : " + Global.CRAWL_STORAGE_FOLDER);
		System.out.println("about to launch query");
		System.out.println("numToProcess : " + numToProcess);
		String query = "from Site s where s.maybeDefunct = false and "
				+ "s.defunct = false and s.homepageNeedsReview = false and s.reviewLater = false and "
				+ "invalidUrl = false and redirectResolveDate is not null";
		List<Site> sites = JPA.em().createQuery(query, Site.class).setMaxResults(numToProcess).getResultList();
		System.out.println("sites size : " + sites.size());
		for(Site site : sites){ 
			JPA.em().detach(site);
			SiteWork work = new SiteWork();
			work.setSite(site);
			work.setCrawlWork(SiteWork.DO_WORK);
			work.setCrawlType(SiteWork.SMALL_CRAWL);
			work.setDocAnalysisWork(SiteWork.DO_WORK);
			work.setAmalgamationWork(SiteWork.DO_WORK);
			work.setTextAnalysisWork(SiteWork.DO_WORK);
			System.out.println("site id " + site.getSiteId());
			Asyncleton.instance().getMainMaster().tell(work, ActorRef.noSender());
		}
		return ok("Crawl submitted");
	}
	
	@Transactional
	public static Result recrawlEmpties(int numToProcess) {
		System.out.println("Global : " + Global.CRAWL_STORAGE_FOLDER);
		System.out.println("about to launch query");
		System.out.println("numtocrawl : " + numToProcess);
		String query = "from SiteCrawl sc where sc.numRetrievedFiles = 0";
		List<SiteCrawl> siteCrawls = JPA.em().createQuery(query, SiteCrawl.class).setMaxResults(numToProcess).getResultList();
		System.out.println("sites size : " + siteCrawls.size());
		for(SiteCrawl siteCrawl : siteCrawls){ 
			JPA.em().detach(siteCrawl);
			Site site = siteCrawl.getSite();
			SiteWork work = new SiteWork();
			work.setSite(site);
			work.setCrawlWork(SiteWork.DO_WORK);
			work.setDocAnalysisWork(SiteWork.DO_WORK);
			work.setAmalgamationWork(SiteWork.DO_WORK);
			work.setTextAnalysisWork(SiteWork.DO_WORK);
			System.out.println("site id " + site.getSiteId());
			Asyncleton.instance().getMainMaster().tell(work, ActorRef.noSender());
		}
		return ok("Crawl submitted");
	}
	@Transactional
	public static Result inferUninferred(int numToProcess){
		System.out.println("Global : " + Global.CRAWL_STORAGE_FOLDER);
		System.out.println("about to launch query");
		System.out.println("numToProcess: " + numToProcess);
		String query = "from SiteCrawl sc where (sc.inferredWebProvider is null or sc.inferredWebProvider = 0) "
				+ "and sc.crawlDate > '2015-07-23'";
		List<SiteCrawl> siteCrawls = JPA.em().createQuery(query, SiteCrawl.class).setMaxResults(numToProcess).getResultList();
		System.out.println("siteCrawls size : " + siteCrawls.size());
		int count = 0;
		for(SiteCrawl siteCrawl : siteCrawls){
			siteCrawl.setInferredWebProvider(SiteCrawlAnalyzer.inferWebProvider(siteCrawl));
			System.out.println("setting webprovider (" + ++count + ") : " + siteCrawl.getInferredWebProvider());
		}
		return ok("Inference job submitted");
	}
	 
	@Transactional
	public static Result crawlUncrawled(int numToCrawl){
		System.out.println("Global : " + Global.CRAWL_STORAGE_FOLDER);
		System.out.println("about to launch query");
		System.out.println("numtocrawl : " + numToCrawl);
		String query = "from Site s where (s.crawls is empty or s.recrawl = true) and (s.maybeDefunct = false and "
				+ "s.defunct = false and s.homepageNeedsReview = false and s.reviewLater = false and "
				+ "invalidUrl = false and redirectResolveDate is not null)";
		List<Site> sites = JPA.em().createQuery(query, Site.class).setMaxResults(numToCrawl).getResultList();
		System.out.println("sites size : " + sites.size());
		for(Site site : sites){ 
			JPA.em().detach(site);
			SiteWork work = new SiteWork();
			work.setSite(site);
			work.setCrawlWork(SiteWork.DO_WORK);
			work.setDocAnalysisWork(SiteWork.DO_WORK);
			work.setAmalgamationWork(SiteWork.DO_WORK);
			work.setTextAnalysisWork(SiteWork.DO_WORK);
			System.out.println("site id " + site.getSiteId());
			Asyncleton.instance().getMainMaster().tell(work, ActorRef.noSender());
		}
		return ok("Crawl submitted");
	}
		
	@Transactional
	public static Result fixUnfixedUrls(int numToProcess){
		System.out.println("about to launch query");
		System.out.println("numToProcess : " + numToProcess);
//		String query = "from Site s where "
		String query = "from Site s where s.redirectResolveDate is null and s.homepageNeedsReview = false and s.reviewLater = false and s.invalidUrl = false and s.maybeDefunct = false and s.defunct = false";
//		String query = "from Site s where exists (from SiteCrawl sc where sc.site = s and sc.numRetrievedFiles = 0)";
		List<Site> sites = JPA.em().createQuery(query, Site.class).setMaxResults(numToProcess).getResultList();
		System.out.println("sites size for fixing urls: " + sites.size());
		for(Site site : sites){
			JPA.em().detach(site);
			SiteWork work = new SiteWork();
			work.setSite(site);
			work.setRedirectResolveWork(SiteWork.DO_WORK);
//			System.out.println("going to fix url : " + site.getHomepage());
			Asyncleton.instance().getMainMaster().tell(work, ActorRef.noSender());
		}
		return ok("Url fixing submitted");
	}
	
	@Transactional
	public static Result resolveUnresolvedUrls(int numToProcess){
		System.out.println("about to launch query");
		System.out.println("numToProcess : " + numToProcess);
		String query = "from Site s where s.homepageConfirmed is null and s.homepageNeedsReview = false and s.reviewLater = false and "
				+ "s.maybeDefunct = false and s.defunct = false and s.suggestedHomepage is null";
		List<Site> sites = JPA.em().createQuery(query, Site.class).setMaxResults(numToProcess).getResultList();
		System.out.println("sites size : " + sites.size());
		for(Site site : sites){
			JPA.em().detach(site);
			SiteWork work = new SiteWork();
			work.setSite(site);
			work.setRedirectResolveWork(SiteWork.DO_WORK);
//			System.out.println("going to resolve redirects for url : " + site.getSiteId());
			Asyncleton.instance().getMainMaster().tell(work, ActorRef.noSender());
		}
		return ok("Redirect resolve submitted");
	}
	
	@Transactional
	public static Result matchUnmatched(int numToProcess){
		System.out.println("about to launch query");
		System.out.println("numToProcess : " + numToProcess);
		String query = "from SiteCrawl sc where sc.crawlDate is not null and sc.storageFolder is not null and sc.inferredWebProvider = 0"
				+ " and sc.crawlDate > '2015-07-23'";
		List<SiteCrawl> sites = JPA.em().createQuery(query, SiteCrawl.class).setMaxResults(numToProcess).getResultList();
		System.out.println("sites size : " + sites.size());
		for(SiteCrawl siteCrawl : sites){
			siteCrawl.lazyInit();
			JPA.em().detach(siteCrawl);
//			System.out.println("extracted strings : " + siteCrawl.getExtractedStrings().size());
			SiteWork work = new SiteWork();
			work.setSiteCrawl(siteCrawl);
			work.setTextAnalysisWork(SiteWork.DO_WORK);
//			System.out.println("going to do text analysis for : " + siteInfo.getSiteInformationId());
			Asyncleton.instance().getMainMaster().tell(work, ActorRef.noSender());
		}
		return ok("Text analysis submitted");
	}
	
	@Transactional
	public static Result stringExtractions(int numToExtract){
//		numToExtract = 5;
		System.out.println("about to launch query");
		System.out.println("numtoextract : " + numToExtract);
		String query = "from SiteInformation si where si.crawlDate is not null and si.crawlStorageFolder is not null and si.stringExtractionsAnalyzed = 0";
		List<SiteInformationOld> sites = JPA.em().createQuery(query, SiteInformationOld.class).setMaxResults(numToExtract).getResultList();
		System.out.println("sites size : " + sites.size());
		for(SiteInformationOld siteInfo : sites){
			System.out.println("redirect url : " + siteInfo.getRedirectUrl());
			JPA.em().detach(siteInfo);
			SiteWork work = new SiteWork(siteInfo);
			work.setStringExtractionWork(SiteWork.DO_WORK);
//			System.out.println("going to do String Extraction for : " + siteInfo.getId());
			Asyncleton.instance().getMainMaster().tell(work, ActorRef.noSender());
		}
		return ok("Extraction submitted");
	}
	
	@Transactional
	public static Result staffExtractions(int numToExtract){
		System.out.println("about to launch query");
		System.out.println("numtoextract : " + numToExtract);
		String query = "from SiteInformation si where si.crawlDate is not null and si.crawlStorageFolder is not null and si.staffExtractionsAnalyzed = 0";
//		String query = "from SiteInformation si where si.siteInformationId = 16523";
		List<SiteInformationOld> sites = JPA.em().createQuery(query, SiteInformationOld.class).setMaxResults(numToExtract).getResultList();
		System.out.println("sites size : " + sites.size());
		for(SiteInformationOld siteInfo : sites){
			System.out.println("redirect url : " + siteInfo.getRedirectUrl());
			JPA.em().detach(siteInfo);
			SiteWork work = new SiteWork(siteInfo);
			work.setStaffExtractionWork(SiteWork.DO_WORK);
			System.out.println("going to do StaffExtraction for : " + siteInfo.getSiteInformationId());
			Asyncleton.instance().getMainMaster().tell(work, ActorRef.noSender());
		}
		return ok("Extraction submitted"); 
	}
	
	@Transactional
	public static Result summarizeUnsummarized(int numToSummarize){
		System.out.println("about to launch query");
		System.out.println("numToSummarize : " + numToSummarize);
		String query = "from SiteInformation si where si.crawlDate is not null and si.crawlStorageFolder is not null and si.summaryCompleted = 0";
//		String query = "from SiteInformation si where si.siteInformationId = 16523";
		List<SiteInformationOld> sites = JPA.em().createQuery(query, SiteInformationOld.class).setMaxResults(numToSummarize).getResultList();
		System.out.println("sites size : " + sites.size());
		for(SiteInformationOld siteInfo : sites){
			System.out.println("redirect url : " + siteInfo.getRedirectUrl());
			JPA.em().detach(siteInfo);
			SiteWork work = new SiteWork(siteInfo);
			work.setSummaryWork(SiteWork.DO_WORK);
			System.out.println("going to do Summary work for : " + siteInfo.getSiteInformationId());
			Asyncleton.instance().getMainMaster().tell(work, ActorRef.noSender());
		}
		return ok("Summaries submitted"); 
	}
	
	@Transactional
	public static Result doAllWork(int numToWork){
		System.out.println("about to launch query");
		System.out.println("numToWork : " + numToWork);
		String query = "from SiteInformation si where si.urlRequiresReview = 0 and"
				+ " redirectUrl != 'TIMEOUT' and redirectUrl != 'ERROR' and (si.intermediateUrl is null "  
				+ " or si.redirectUrl is null or si.crawlDate is null or si.matchesAnalyzed = 0 "
				+ " or si.stringExtractionsAnalyzed = 0 or si.staffExtractionsAnalyzed = 0 "
				+ " or si.summaryCompleted = 0)";
//		String query = "from SiteInformation si where si.siteInformationId = 3";
		List<SiteInformationOld> sites = JPA.em().createQuery(query, SiteInformationOld.class).setMaxResults(numToWork).getResultList();
		System.out.println("sites size : " + sites.size());
		for(SiteInformationOld siteInfo : sites){
			System.out.println("redirect url : " + siteInfo.getRedirectUrl());
			JPA.em().detach(siteInfo);
			SiteWork work = new SiteWork(siteInfo);
			work.setAllWorkNeeded(SiteWork.DO_WORK);
			System.out.println("going to do all work for : " + siteInfo.getSiteInformationId());
			Asyncleton.instance().getMainMaster().tell(work, ActorRef.noSender());
		}
		return ok("Summaries submitted");   
	}
	
	
	
	
//	@Transactional
//	public static Result doStringExtractions(int numToWork) {
//		System.out.println("About to launch query");
//		System.out.println("numtowork : " + numToWork);
//		String query = "from SiteInformation si where si.crawlDate = '";
//		return ok();
//	}
//	
	
	
}
