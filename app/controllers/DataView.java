package controllers;


import java.util.List;

import javax.persistence.metamodel.EntityType;

import org.apache.commons.lang3.StringUtils;

import dao.SitesDAO;
import dao.StatsDAO;
import datatransfer.FileMover;
import async.monitoring.Lobby;
import persistence.SiteCrawl;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.*;
import reporting.DashboardStats;
import reporting.StatsBuilder;
import salesforce.persistence.SalesforceAccount;


public class DataView extends Controller {
	
	@Transactional
	public static Result salesforceAccount(long salesforceAccountId) {
		SalesforceAccount account = JPA.em().find(SalesforceAccount.class, salesforceAccountId);
		return ok();
	}
	
	@Transactional
	public static Result sitesDashboardStats() {
		return ok(views.html.viewstats.viewStats.render(StatsBuilder.sitesDashboard()));
	}
	
	@Transactional
	public static Result salesforceDashboardStats() {
		return ok(views.html.viewstats.viewStats.render(StatsBuilder.salesforceDashboard()));
	}
	
	@Transactional
	public static Result analysisDashboardStats() {
		return ok(views.html.viewstats.viewStats.render(StatsBuilder.salesforceDashboard()));
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
	public static Result reviewDupDomains(int numToProcess, int offset) {
		List<String> dups = SitesDAO.getDuplicateDomains(numToProcess, offset);
		return ok(views.html.reviewDupDomains.render(dups));
	}
	
	
	@Transactional
	public static Result dashboardStats() {
		DashboardStats franchiseStats = StatsDAO.getSiteStats(true);
		DashboardStats independentStats = StatsDAO.getSiteStats(false);
		return ok(views.html.viewstats.dashboardstats.render(franchiseStats, independentStats)); 
	}
	
	public static Result getMonitoringQueues() {
		return ok(views.html.home.waitingRoomList.render(Lobby.getRoomSummaries()));
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
