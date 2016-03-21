package controllers;


import java.util.List;

import javax.persistence.metamodel.EntityType;

import org.apache.commons.lang3.StringUtils;

import dao.SitesDAO;
import dao.StatsDAO;
import datatransfer.FileMover;
import async.monitoring.AsyncMonitor;
import async.work.WorkStatus;
import persistence.CrawlSet;
import persistence.Site;
import persistence.SiteCrawl;
import persistence.tasks.Task;
import persistence.tasks.TaskSet;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.*;
import reporting.DashboardStats;


public class DataView extends Controller { 
	
	@Transactional
	public static Result reviewTasks(long taskSetId, int count, int offset){
		List<Task> tasks = JPA.em().createQuery("select t from TaskSet ts join ts.tasks t where ts.taskSetId = :taskSetId and t.workStatus = :workStatus", Task.class)
				.setParameter("taskSetId", taskSetId).setParameter("workStatus", WorkStatus.NEEDS_REVIEW)
				.setFirstResult(offset).setMaxResults(count).getResultList();
		System.out.println("count : " + count);
		System.out.println("offset : " + offset);
		System.out.println("reviewing " + tasks.size() + " tasks");
		return ok(views.html.reviewing.lists.taskList.render(tasks));
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
	public static Result reviewDupDomains(int numToProcess, int offset) {
		List<String> dups = SitesDAO.getDuplicateDomains(numToProcess, offset);
		return ok(views.html.reviewDupDomains.render(dups));
	}
	
	
	@Transactional
	public static Result dashboardStats() {
		DashboardStats franchiseStats = StatsDAO.getSiteStats(true);
		DashboardStats independentStats = StatsDAO.getSiteStats(false);
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
	
	
}
