package async.tasks;

import java.util.Map;

import async.work.WorkStatus;
import async.work.WorkType;
import persistence.Site;
import persistence.UrlCheck;
import persistence.tasks.Task;
import play.db.jpa.JPA;

public class TaskReviewer {
	
	public static void resetTask(Task task){
		task.setWorkStatus(WorkStatus.DO_WORK);
		task.setNote("");
		matchSupertaskToSubtask(task);
	}
	
	public static void matchSupertaskToSubtask(Task subtask){
		Task supertask = subtask.getSupertask();
		if(supertask == null){
			return;
		}
		if(subtask.getWorkStatus() == WorkStatus.DO_WORK){
			supertask.setWorkStatus(WorkStatus.DO_WORK);
			supertask.setNote("");
		}else if(subtask.getWorkStatus() == WorkStatus.NEEDS_REVIEW){
			supertask.setWorkStatus(WorkStatus.NEEDS_REVIEW);
			supertask.setNote("Subtask needs review");
		}else if(subtask.getWorkStatus() == WorkStatus.WORK_COMPLETED){
			supertask.setWorkStatus(WorkStatus.DO_WORK);
			supertask.setNote("");
		}else if(subtask.getWorkStatus() == WorkStatus.MORE_WORK){
			supertask.setWorkStatus(WorkStatus.DO_WORK);
			supertask.setNote("");
		}else {
			supertask.setWorkStatus(WorkStatus.NEEDS_REVIEW);
			supertask.setNote("Subtask changed to status unknown");
		}
	}

	public static void reviewTask(Task task, Map<String, String> contextItems) {
		String action = contextItems.get("action");
		System.out.println("reviewing task " + task.getTaskId() + " with action " + action);
		if("MORE_WORK".equals(action)){
			System.out.println("Sending back for more work : " + task.getTaskId());
			task.setWorkStatus(WorkStatus.MORE_WORK);
			task.setNote("");
		}else if(task.getWorkType() == WorkType.SITE_IMPORT){
			reviewSiteImportTask(task, contextItems);
		}else if(task.getWorkType() == WorkType.REDIRECT_RESOLVE){
			reviewUrlCheckTask(task, contextItems);
		}
		
		matchSupertaskToSubtask(task);
	}
	
	public static void reviewSiteImportTask(Task task, Map<String, String> contextItems){
		Long urlCheckId = Long.parseLong(contextItems.get("urlCheckId"));
		String siteIdString = contextItems.get("siteId");
		String action = contextItems.get("action");
		Site site = null;
		UrlCheck urlCheck = JPA.em().find(UrlCheck.class, urlCheckId);
		if(siteIdString != null) {
			site = JPA.em().find(Site.class, Long.parseLong(siteIdString));
		}
		
		if(action == "accept"){
			
		}
		
	}
	
	public static void reviewUrlCheckTask(Task task, Map<String, String> contextItems) {
		Long urlCheckId = Long.parseLong(task.getContextItem("urlCheckId"));
		String action = contextItems.get("action");
		UrlCheck urlCheck = JPA.em().find(UrlCheck.class, urlCheckId);
		
		if("APPROVE_RESOLVED".equals(action)){
			Boolean sharedSite = Boolean.parseBoolean(contextItems.get("sharedSite"));
			System.out.println("Approving resolved of task " + task.getTaskId() + " and urlcheck " + urlCheck.getUrlCheckId() + " sharedSite : " + sharedSite);
			urlCheck.setManuallyApproved(true);
			urlCheck.setSharedSite(sharedSite);
			
			task.setWorkStatus(WorkStatus.WORK_COMPLETED);
		} else if("MANUAL_SEED".equals(action)){
			String manualSeed = contextItems.get("manualSeed");
			System.out.println("manual seed : " + manualSeed);
			JPA.em().remove(urlCheck);
			task.addContextItem("seed", manualSeed);
			task.removeContextItems("urlCheckId");
			if(task.getSupertask() != null) {
				task.getSupertask().removeContextItems("urlCheckId");
				task.getSupertask().addContextItem("seed", manualSeed);
			}
			task.setWorkStatus(WorkStatus.DO_WORK);
		} else if("MARK_DEFUNCT".equals(action)){
			urlCheck.setMarkedDefunct(true);
			task.setWorkStatus(WorkStatus.WORK_COMPLETED);
		}else if("RECHECK".equals(action)){
			JPA.em().remove(urlCheck);
			task.removeContextItems("urlCheckId");
			if(task.getSupertask() != null) {
				task.getSupertask().removeContextItems("urlCheckId");
			}
			task.setWorkStatus(WorkStatus.DO_WORK);
		}
	}
}
