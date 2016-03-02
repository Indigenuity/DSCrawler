package dao;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import async.work.WorkStatus;
import async.work.WorkType;
import datadefinitions.newdefinitions.WPAttribution;
import persistence.CrawlSet;
import persistence.Site;
import persistence.SiteCrawl;
import persistence.stateful.FetchJob;
import persistence.tasks.TaskSet;
import play.db.jpa.JPA;
import reporting.DashboardStats;

public class StatsDAO {
	
	public static DashboardStats getWpAttributionStats(Collection<SiteCrawl> siteCrawls) {
		DashboardStats stats = new DashboardStats();
		Map<WPAttribution, Integer> counts = new HashMap<WPAttribution, Integer>();
		int noneCount = 0;
		for(SiteCrawl siteCrawl : siteCrawls) {
			if(siteCrawl.getWpAttributions().size() == 0){
				noneCount++;
			}
			else{
				for(WPAttribution wp : siteCrawl.getWpAttributions()){
					counts.put(wp, counts.getOrDefault(wp, 0) + 1);
				}
			}
		}
	
		for(Entry<WPAttribution, Integer> entry : counts.entrySet()){
			stats.put(entry.getKey().name(), entry.getValue());
		}
		stats.put("No WPAttribution", noneCount);
		return stats;
	}
	
	public static DashboardStats getTaskSetStats(TaskSet taskSet) {
		DashboardStats stats = new DashboardStats();
		
		stats.put("Tasks", taskSet.getTasks().size());
		stats.put("Supertasks", TaskDAO.countWorkType(taskSet.getTaskSetId(), WorkType.SUPERTASK));
		stats.put("Tasks To Do", TaskDAO.countWorkStatus(taskSet.getTaskSetId(), WorkStatus.DO_WORK));
		stats.put("Tasks Completed", TaskDAO.countWorkStatus(taskSet.getTaskSetId(), WorkStatus.WORK_COMPLETED));
		stats.put("Tasks Need Review", TaskDAO.countWorkStatus(taskSet.getTaskSetId(), WorkStatus.NEEDS_REVIEW));
		
		return stats;
	}

	public DashboardStats getDashboardStats() {
		DashboardStats stats = new DashboardStats();
		EntityManager em = JPA.em();
		
		String query = "select count(*) from Dealer d";
		Query q = em.createQuery(query);
		stats.put("Total Dealers", Integer.parseInt(q.getSingleResult().toString()));
		
		query = "select count(*) from Dealer d where d.mainSite is null";
		q = em.createQuery(query);
		stats.setDealersWithoutSites(q.getSingleResult().toString());
		
		query = "select count(*) from Site s where s.crawls is empty";
		q = em.createQuery(query);
		stats.setUncrawledSites(q.getSingleResult().toString());
		
		query = "select count(*), "
				+ "count(case when s.redirectResolveDate is null then 1 end), "
				+ "count(case when s.homepageNeedsReview = 1 then 1 end) from Site s";
		q = em.createQuery(query);
		List<Object[]> rs  = em.createNativeQuery(query).getResultList();
		Object[] row = rs.get(0);
		stats.setTotalSites(row[0].toString());
		stats.setUnconfirmedHomepages(row[1].toString());
		stats.setHomepagesNeedReview(row[2].toString());
		
		
		query = "select count(*), count(case when sc.docAnalysisDone = 0 then 1 end), "
				+ "count(case when sc.amalgamationDone = 0 then 1 end), count(case when sc.textAnalysisDone = 0 then 1 end),"
				+ "count(case when sc.inferredWebProvider = null then 1 end), "
				+ "count(case when sc.inferredWebProvider = 0 then 1 end), "
				+ "count(case when sc.numRetrievedFiles = 0 then 1 end),"
				+ "count(case when sc.numRetrievedFiles < 8 then 1 end) from SiteCrawl sc";
		q = em.createQuery(query);
		rs  = em.createNativeQuery(query).getResultList();
		row = rs.get(0);
		stats.setTotalCrawls(row[0].toString());
		stats.setNeedDocAnalysis(row[1].toString());
		stats.setNeedAmalgamation(row[2].toString());
		stats.setNeedTextAnalysis(row[3].toString());
		stats.setNeedInference(row[4].toString());
		stats.setInferenceFailed(row[5].toString());
		stats.setEmptyCrawls(row[6].toString());
		stats.setSmallCrawls(row[7].toString());
		
		return stats;
	}
	
	public static DashboardStats getSiteStats(boolean franchise) {
		DashboardStats stats = new DashboardStats();
		
		stats.put("Total Sites", SitesDAO.getCount(franchise));
//		stats.put("Out Of Date Homepage URLs", SitesDAO.getOldHomepagesCount(franchise));
//		stats.put("Out of date Crawl", SitesDAO.getOldCrawlsCount(franchise));
		stats.put("No crawls", SitesDAO.getNoCrawlsCount(franchise));
		
		Map<String, Object> parameters;
		for(Field field : Site.class.getDeclaredFields()) {
			parameters = new HashMap<String, Object>();
			if(field.getType() == boolean.class){
				parameters.put("franchise", franchise);
				parameters.put(field.getName(), true);
				long count = SitesDAO.getCount(parameters);
				stats.put(field.getName(), count);
			}
		}
		
		
		return stats;
	}
	
	public static DashboardStats getFetchJobStats(FetchJob fetchJob) {
		DashboardStats stats = new DashboardStats();
		
		fillSubtaskStats(fetchJob, stats, "urlCheck");
		fillSubtaskStats(fetchJob, stats, "siteUpdate");
		fillSubtaskStats(fetchJob, stats, "siteCrawl");
		fillSubtaskStats(fetchJob, stats, "amalgamation");
		fillSubtaskStats(fetchJob, stats, "textAnalysis");
		fillSubtaskStats(fetchJob, stats, "docAnalysis");
		fillSubtaskStats(fetchJob, stats, "placesPageFetch");
//		stats.put("Total Sites", crawlSet.getSites().size());
//		stats.put("Need Crawl", crawlSet.getUncrawled().size());
//		stats.put("Need Mobile Crawl", crawlSet.getNeedMobile().size());
//		stats.put("Need Redirect Resolve", crawlSet.getNeedRedirectResolve().size());
//		stats.put("Site Crawls", crawlSet.getCompletedCrawls().size());
//		
//		for(Field field : Site.class.getDeclaredFields()) {
//			if(field.getType() == boolean.class){
//				long count = SitesDAO.getCrawlSetCount(crawlSet.getCrawlSetId(), field.getName(), true);
//				stats.put(field.getName(), count);
//			}
//		}
//		
//		for(Field field : SiteCrawl.class.getDeclaredFields()) {
//			if(field.getType() == boolean.class){
//				long count = SiteCrawlDAO.getCrawlSetCount(crawlSet.getCrawlSetId(), field.getName(), true);
//				stats.put(field.getName(), count);
//			}
//		}
		
		return stats;
	}
	
	private static void fillSubtaskStats(FetchJob fetchJob, DashboardStats stats, String subtaskName){
		String query = "select count(info) from FetchJob fj join fj.fetches info where fj.fetchJobId = :fetchJobId"
				+ " and info." + subtaskName + ".workStatus = :workStatus";
		Query q = JPA.em().createQuery(query);
		q.setParameter("fetchJobId", fetchJob.getFetchJobId());
		
		q.setParameter("workStatus", WorkStatus.DO_WORK);
		Integer value = Integer.parseInt(q.getSingleResult() + "");
		stats.put(subtaskName + " Not Done", value);
		
		q.setParameter("workStatus", WorkStatus.WORK_COMPLETED);
		value = Integer.parseInt(q.getSingleResult() + "");
		stats.put(subtaskName + " Completed", value);
		
		q.setParameter("workStatus", WorkStatus.NEEDS_REVIEW);
		value = Integer.parseInt(q.getSingleResult() + "");
		stats.put(subtaskName + " Needs Review", value);
	}
	
	public static DashboardStats getCrawlSetStats(CrawlSet crawlSet) {
		DashboardStats stats = new DashboardStats();
		
		stats.put("Total Sites", crawlSet.getSites().size());
		stats.put("Need Crawl", crawlSet.getUncrawled().size());
		stats.put("Need Mobile Crawl", crawlSet.getNeedMobile().size());
		stats.put("Need Redirect Resolve", crawlSet.getNeedRedirectResolve().size());
		stats.put("Site Crawls", crawlSet.getCompletedCrawls().size());
		
		for(Field field : Site.class.getDeclaredFields()) {
			if(field.getType() == boolean.class){
				long count = SitesDAO.getCrawlSetCount(crawlSet.getCrawlSetId(), field.getName(), true);
				stats.put(field.getName(), count);
			}
		}
		
		for(Field field : SiteCrawl.class.getDeclaredFields()) {
			if(field.getType() == boolean.class){
				long count = SiteCrawlDAO.getCrawlSetCount(crawlSet.getCrawlSetId(), field.getName(), true);
				stats.put(field.getName(), count);
			}
		}
		
		return stats;
	}
	
	public static Integer getSingleInt(String query, boolean isNative) {
		return Integer.parseInt(getSingleString(query, isNative));
	}
	
	public static String getSingleString(String query, boolean isNative) {
		return getSingleObject(query, isNative).toString();
	}

	public static Object getSingleObject(String query, boolean isNative) {
		if(isNative) {
			return JPA.em().createNativeQuery(query).getSingleResult();
		}
		return JPA.em().createQuery(query).getSingleResult();
	}
}
