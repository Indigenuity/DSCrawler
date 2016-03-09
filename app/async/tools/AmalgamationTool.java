package async.tools;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import datatransfer.Amalgamater;
import global.Global;
import persistence.SiteCrawl;
import persistence.tasks.Task;
import play.Logger;
import play.db.jpa.JPA;
import async.registration.ContextItem;
import async.work.WorkStatus;
import async.work.WorkType;

public class AmalgamationTool extends Tool { 
	


	protected final static Set<ContextItem> requiredContextItems = new HashSet<ContextItem>();
	static{
		ContextItem item = new ContextItem("siteCrawlId", String.class, false);
		requiredContextItems.add(item);
	}
	
	protected final static Set<ContextItem> resultContextItems = new HashSet<ContextItem>();
	
	protected final static Set<WorkType> abilities = new HashSet<WorkType>();
	static{
		abilities.add(WorkType.AMALGAMATION);
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
		System.out.println("AmalgamationTool processing Task: " + task);
		
		try{
			Long siteCrawlId = Long.parseLong(task.getContextItem("siteCrawlId"));
			
			JPA.withTransaction( () -> {
				SiteCrawl siteCrawl = JPA.em().find(SiteCrawl.class, siteCrawlId);
				File storageFolder = new File(Global.getCrawlStorageFolder() + "/" + siteCrawl.getStorageFolder());
				File destination = new File(Global.getCombinedStorageFolder() + "/" + siteCrawl.getStorageFolder());
				Amalgamater.amalgamateFiles(storageFolder, destination);
				siteCrawl.setAmalgamationDone(true);
			});
			task.setWorkStatus(WorkStatus.WORK_COMPLETED);
		}
		catch(Exception e) {
			Logger.error("Error in Amalgamation: " + e);
			e.printStackTrace();
			task.setWorkStatus(WorkStatus.NEEDS_REVIEW);
			task.setNote(e.getClass().getSimpleName() + " : " + e.getMessage());
		}
		return task;
	}

}