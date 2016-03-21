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
		}else {
			supertask.setWorkStatus(WorkStatus.NEEDS_REVIEW);
			supertask.setNote("Subtask changed to status unknown");
		}
	}

	public static void reviewTask(Task task, Map<String, String> contextItems) {
		if(task.getWorkType() == WorkType.SITE_IMPORT){
			reviewSiteImportTask(task, contextItems);
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
		Long urlCheckId = Long.parseLong(contextItems.get("urlCheckId"));
		String action = contextItems.get("action");
		UrlCheck urlCheck = JPA.em().find(UrlCheck.class, urlCheckId);
		
		if("approve".equals(action)){
			urlCheck.setManuallyApproved(true);
			task.setWorkStatus(WorkStatus.WORK_COMPLETED);
		} else if("manualSeed".equals(action)){
			String manualSeed = contextItems.get("manualSeed");
//			task.addContextItem("seed", manualSeed);
//			task.removeContextItems("urlCheckId");
//			if(task.getSupertask() != null) {
//				task.getSupertask().removeContextItems("urlCheckId");
//			}
//			urlCheck.setManualSeed(manualSeed);
		}
	}
}
