package controllers;

import forms.CrawlSetJob;
import global.Global;

import java.io.File;
import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.metamodel.EntityType;

import org.apache.commons.lang3.StringUtils;

import dao.InfoFetchDAO;
import dao.SitesDAO;
import dao.StatsDAO;
import datatransfer.FileMover;
import async.monitoring.AsyncMonitor;
import async.work.infofetch.InfoFetch;
import persistence.CrawlSet;
import persistence.Site;
import persistence.SiteCrawl;
import persistence.SiteInformationOld;
import persistence.stateful.FetchJob;
import persistence.tasks.TaskSet;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.*;
import reporting.DashboardStats;
import views.html.*;

import org.apache.commons.beanutils.*;

public class DataView extends Controller { 
	
	@Transactional
	public static Result wpTesting() {
		List<SiteCrawl> siteCrawls = JPA.em().createQuery("from SiteCrawl sc", SiteCrawl.class).getResultList();
		
		DashboardStats stats = StatsDAO.getWpAttributionStats(siteCrawls);
		System.out.println("stats : " + stats.getStats().size());
		return ok(views.html.persistence.viewStats.render(stats));
	}
	
	@Transactional
	public static Result wpTestingNone() {
		List<SiteCrawl> siteCrawls = JPA.em().createQuery("from SiteCrawl sc where wp is empty", SiteCrawl.class).getResultList();
		
		DashboardStats stats = StatsDAO.getWpAttributionStats(siteCrawls);
		System.out.println("stats : " + stats.getStats().size());
		return ok(views.html.persistence.viewStats.render(stats));
	}
	
	@Transactional
	public static Result taskSets() {
		List<TaskSet> taskSets = JPA.em().createQuery("from TaskSet ts", TaskSet.class).getResultList();
		System.out.println("taskSets : " + taskSets.size());
		return ok(views.html.persistence.taskSets.render(taskSets));
	}
	
	@Transactional
	public static Result taskSet(long taskSetId) {
		TaskSet taskSet = JPA.em().find(TaskSet.class, taskSetId);
		DashboardStats stats = StatsDAO.getTaskSetStats(taskSet);
		return ok(views.html.persistence.taskSet.render(taskSet, stats));
	}
	
	@Transactional
	public static Result reviewInfoFetches(String subtaskName, Long fetchJobId, int numToProcess, int offset) {
		List<InfoFetch> fetches = InfoFetchDAO.getNeedReview(fetchJobId, subtaskName, numToProcess, offset);
		for(InfoFetch fetch : fetches) {
			fetch.initObjects();
		}
		return ok(views.html.reviewing.lists.infoFetchList.render(fetches)); 
	}
	
	@Transactional
	public static Result fetchJobs() {
		List<FetchJob> fetchJobs = JPA.em().createQuery("from FetchJob fj", FetchJob.class).getResultList();
		System.out.println("fetchJobs : " + fetchJobs.size());
		return ok(views.html.fetchJobs.render(fetchJobs));
	}
	
	@Transactional
	public static Result fetchJob(long fetchJobId) {
		FetchJob fetchJob = JPA.em().find(FetchJob.class, fetchJobId);
		DashboardStats stats = StatsDAO.getFetchJobStats(fetchJob);
		return ok(views.html.persistence.fetchJob.render(fetchJob, stats));
	}
	
	@Transactional
	public static Result viewEntity(String entityClass, long entityId){
		
		//Only accept JPA Entity classes
		for(EntityType<?> type : JPA.em().getMetamodel().getEntities()) {
			if(StringUtils.equals(type.getName(), entityClass)){
				Object entity = JPA.em().find(type.getJavaType(), entityId);
				System.out.println("entity : " + entity);
				return ok(views.html.scaffolding.viewEntity.render(entity));
			}
		}
		
		return badRequest("Can't view non-entity object with name " + entityClass);
	}
	
	@Transactional
	public static Result crawlSet(long crawlSetId) {
		CrawlSet crawlSet = JPA.em().find(CrawlSet.class, crawlSetId);
		DashboardStats stats = StatsDAO.getCrawlSetStats(crawlSet);
		
		return ok(views.html.persistence.crawlSet.render(crawlSet, stats));
	}

	public static Result getUncrawled(){
		int offset = 0;
		int maxResults = 0;
		List<SiteInformationOld> sites = JPA.em().createQuery("from SiteInformation si where si.crawlDate is not null", SiteInformationOld.class).setFirstResult(offset).setMaxResults(maxResults).getResultList();
		
		return ok();
	}
	
	public static Result dashboard(String message) {
		return ok(views.html.dashboard.render(message));
	}
	
	@Transactional
	public static Result viewSiteCrawl(long siteCrawlId) {
		SiteCrawl siteCrawl = JPA.em().find(SiteCrawl.class, siteCrawlId); 
		return ok(views.html.persistence.siteCrawlFull.render(siteCrawl));
	}
	
	@Transactional 
	public static Result viewSiteList() {
		DynamicForm requestData = Form.form().bindFromRequest();
		CrawlSet crawlSet = JPA.em().find(CrawlSet.class, Long.parseLong(requestData.get("crawlSetId")));
		String field = requestData.get("field");
		int count = Integer.parseInt(requestData.get("count"));
		int offset = Integer.parseInt(requestData.get("offset"));
		List<Site> sites = SitesDAO.getCrawlSetList(crawlSet.getCrawlSetId(), field, true, count, offset);
		
		return ok(views.html.reviewSites.render(sites, crawlSet));
	}
	
	@Transactional
	public static Result reviewSites(long crawlSetId, int numToProcess, int offset) {
		List<Site> sites = SitesDAO.getCrawlSetList(crawlSetId, "homepageNeedsReview", true, numToProcess, offset);
		CrawlSet crawlSet = JPA.em().find(CrawlSet.class, crawlSetId);
		
		return ok(views.html.reviewSites.render(sites, crawlSet));
	}
	
	@Transactional
	public static Result reviewDupDomains(int numToProcess, int offset) {
		List<String> dups = SitesDAO.getDuplicateDomains(numToProcess, offset);
		return ok(views.html.reviewDupDomains.render(dups));
	}
	
	
	@Transactional
	public static Result crawlSetStats(long crawlSetId) {
		CrawlSet crawlSet = JPA.em().find(CrawlSet.class, crawlSetId);
		DashboardStats stats = StatsDAO.getCrawlSetStats(crawlSet);
		
		return ok(views.html.persistence.crawlSetStats.render(stats));
	}
	
	@Transactional
	public static Result dashboardStats() {
//		EntityManager em = JPA.em();
//		DashboardStats stats = new DashboardStats();
//		
//		String query = "select count(*) from Dealer d";
//		Query q = em.createQuery(query);
//		stats.setTotalDealers(q.getSingleResult().toString());
//		
//		query = "select count(*) from Dealer d where d.mainSite is null";
//		q = em.createQuery(query);
//		stats.setDealersWithoutSites(q.getSingleResult().toString());
//		
//		query = "select count(*) from Site s where s.crawls is empty";
//		q = em.createQuery(query);
//		stats.setUncrawledSites(q.getSingleResult().toString());
//		
//		query = "select count(*), "
//				+ "count(case when s.redirectResolveDate is null then 1 end), "
//				+ "count(case when s.homepageNeedsReview = 1 then 1 end) from Site s";
//		q = em.createQuery(query);
//		List<Object[]> rs  = em.createNativeQuery(query).getResultList();
//		Object[] row = rs.get(0);
//		stats.setTotalSites(row[0].toString());
//		stats.setUnconfirmedHomepages(row[1].toString());
//		stats.setHomepagesNeedReview(row[2].toString());
//		
//		
//		query = "select count(*), count(case when sc.docAnalysisDone = 0 then 1 end), "
//				+ "count(case when sc.amalgamationDone = 0 then 1 end), count(case when sc.textAnalysisDone = 0 then 1 end),"
//				+ "count(case when sc.inferredWebProvider = null then 1 end), "
//				+ "count(case when sc.inferredWebProvider = 0 then 1 end), "
//				+ "count(case when sc.numRetrievedFiles = 0 then 1 end),"
//				+ "count(case when sc.numRetrievedFiles < 8 then 1 end) from SiteCrawl sc";
//		q = em.createQuery(query);
//		rs  = em.createNativeQuery(query).getResultList();
//		row = rs.get(0);
//		stats.setTotalCrawls(row[0].toString());
//		stats.setNeedDocAnalysis(row[1].toString());
//		stats.setNeedAmalgamation(row[2].toString());
//		stats.setNeedTextAnalysis(row[3].toString());
//		stats.setNeedInference(row[4].toString());
//		stats.setInferenceFailed(row[5].toString());
//		stats.setEmptyCrawls(row[6].toString());
//		stats.setSmallCrawls(row[7].toString());
		DashboardStats franchiseStats = StatsDAO.getSiteStats(true);
		DashboardStats independentStats = StatsDAO.getSiteStats(false);
//		for(String key : franchiseStats.getStats().keySet()) {
//			System.out.println(key + " : " + franchiseStats.getStats().get(key));
//		}
		return ok(views.html.dashboardstats.render(franchiseStats, independentStats)); 
	}
	
	public static Result getMonitoringQueues() {
		return ok(views.html.allWips.render(AsyncMonitor.instance().getWipLists()));
	} 
	
	public static Result getMonitoringQueue(String queueName) {
		return ok();  
	}
	
	public static Result mainUsableSpace() {
		return ok(FileMover.getMainUsableGb() + " GB");
	}
	
	public static Result secondaryUsableSpace() {
		return ok(FileMover.getSecondaryUsableGb() + " GB");
	}
	
//	public static Result checkSniffingQueue() {
//		return ok(AsyncMonitor.instance().getSnifferWIP().size() + "");
//	}
//	public static Result checkCrawlQueue() {
//		return ok(AsyncMonitor.instance().getCrawlWIP().size() + "");
//	}
//	public static Result checkDocAnalysisQueue() {
//		return ok(AsyncMonitor.instance().getDocAnalysisWIP().size() + "");
//	}
//	public static Result checkMetaAnalysisQueue() {
//		return ok(AsyncMonitor.instance().getMetaAnalysisWIP().size() + "");
//	}
//	public static Result checkAmalgamationQueue() {
//		return ok(AsyncMonitor.instance().getAmalgamationWIP().size() + "");
//	}
//	public static Result checkTextAnalysisQueue() {
//		return ok(AsyncMonitor.instance().getTextAnalysisWIP().size() + "");
//	}
//	public static Result checkInferenceQueue() {
//		return ok(AsyncMonitor.instance().getInferenceWIP().size() + "");
//	}
//	
//	public static Result getSniffingQueue() {
//		return ok(views.html.wiplist.render(AsyncMonitor.instance().getSnifferWIP()));
//	}
//	public static Result getCrawlQueue() {
//		return ok(views.html.wiplist.render(AsyncMonitor.instance().getCrawlWIP()));
//	}
//	public static Result getDocAnalysisQueue() {
//		return ok(views.html.wiplist.render(AsyncMonitor.instance().getDocAnalysisWIP()));
//	}
//	public static Result getMetaAnalysisQueue() {
//		return ok(views.html.wiplist.render(AsyncMonitor.instance().getMetaAnalysisWIP()));
//	}
//	public static Result getAmalgamationQueue() {
//		return ok(views.html.wiplist.render(AsyncMonitor.instance().getAmalgamationWIP()));
//	}
//	public static Result getTextAnalysisQueue() {
//		return ok(views.html.wiplist.render(AsyncMonitor.instance().getTextAnalysisWIP()));
//	}
//	public static Result getInferenceQueue() {
//		return ok(views.html.wiplist.render(AsyncMonitor.instance().getInferenceWIP()));
//	}
	
	
}
