package async.tools;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import async.registration.ContextItem;
import async.work.WorkStatus;
import async.work.WorkType;
import dao.SitesDAO;
import persistence.Site;
import persistence.UrlCheck;
import persistence.tasks.Task;
import play.Logger;
import play.db.jpa.JPA;

public class SiteImportTool extends Tool {

	protected final static Set<ContextItem> requiredContextItems = new HashSet<ContextItem>();
	static{
		ContextItem item = new ContextItem("urlCheckId", String.class, false);
		requiredContextItems.add(item);
		item = new ContextItem("franchise", String.class, false);
		requiredContextItems.add(item);
	}
	
	protected final static Set<ContextItem> resultContextItems = new HashSet<ContextItem>();
	static{
		ContextItem item = new ContextItem("siteId", Long.class, false);
		resultContextItems.add(item);
	}
	
	protected final static Set<WorkType> abilities = new HashSet<WorkType>();
	static{
		abilities.add(WorkType.SITE_IMPORT);
	}
	
	@Override
	public  Set<WorkType> getAbilities() {
		return abilities;
	}
	
	@Override
	public Set<ContextItem> getRequiredItems(WorkType workType) {
		return requiredContextItems;
	}


	@Override
	public Set<ContextItem> getResultItems(WorkType workType) {
		return resultContextItems;
	}	
	
	@Override
	protected Task safeDoTask(Task task) {
		System.out.println("SiteImportTool importing with task : " + task.getTaskId());
		Long urlCheckId = Long.parseLong(task.getContextItem("urlCheckId"));
		Boolean franchise = Boolean.parseBoolean(task.getContextItem("franchise"));
		
		JPA.withTransaction( () -> {
			UrlCheck urlCheck = JPA.em().find(UrlCheck.class, urlCheckId);
			Site existingSite = SitesDAO.getFirst("homepage", urlCheck.getResolvedSeed(), 0);
			if(existingSite == null) {
				if(urlCheck.isAllApproved() || urlCheck.isManuallyApproved()){
					Site site = new Site();
					site.setHomepage(urlCheck.getResolvedSeed());
//					site.setDomain(urlCheck.getResolvedHost());
					site.setFranchise(franchise);
					site.setCreatedDate(new java.sql.Date(Calendar.getInstance().getTime().getTime()));
					site.setRedirectResolveDate(new java.sql.Date(Calendar.getInstance().getTime().getTime()));
					site.setLanguagePath(urlCheck.isLanguagePath());
					site.setLanguageQuery(urlCheck.isLanguageQuery());
					site.setSharedSite(urlCheck.isSharedSite());
					urlCheck.setAccepted(true);
					
					JPA.em().persist(site);
					task.setWorkStatus(WorkStatus.WORK_COMPLETED);
					task.addContextItem("siteId", site.getSiteId() + "");
				}
				else {
					task.setNote("URL not fully approved");
					task.setWorkStatus(WorkStatus.NEEDS_REVIEW);
				}
			}
			else {
				task.setNote("Site already exists with this homepage");
				task.setWorkStatus(WorkStatus.WORK_COMPLETED);
				task.addContextItem("siteId", existingSite.getSiteId() + "");
			}
		});
			
		return task;
	}

	@Override
	protected Task safeDoMore(Task task){
		return safeDoTask(task);
	}

}