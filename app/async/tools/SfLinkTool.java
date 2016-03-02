package async.tools;

import java.util.HashSet;
import java.util.Set;

import async.registration.ContextItem;
import async.work.WorkStatus;
import async.work.WorkType;
import persistence.SFEntry;
import persistence.Site;
import persistence.tasks.Task;
import play.Logger;
import play.db.jpa.JPA;

public class SfLinkTool extends Tool {

	protected final static Set<ContextItem> requiredContextItems = new HashSet<ContextItem>();
	static{
		ContextItem item = new ContextItem("siteId", String.class, false);
		requiredContextItems.add(item);
		item = new ContextItem("sfEntryId", String.class, false);
		requiredContextItems.add(item);
	}
	
	protected final static Set<ContextItem> resultContextItems = new HashSet<ContextItem>();
	
	protected final static Set<WorkType> abilities = new HashSet<WorkType>();
	static{
		abilities.add(WorkType.SF_LINK);
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
		System.out.println("SfLinkTool working on task : " + task.getTaskId());
		try{
			Long siteId = Long.parseLong(task.getContextItem("siteId"));
			Long sfEntryId = Long.parseLong(task.getContextItem("sfEntryId"));
			
			JPA.withTransaction( () -> {
				Site site = JPA.em().find(Site.class, siteId);
				SFEntry sf = JPA.em().find(SFEntry.class, sfEntryId);
				
				sf.setMainSite(site);
			});
			
			task.setWorkStatus(WorkStatus.WORK_COMPLETED);
		}
		catch(Exception e) {
			Logger.error("Error in SfLinkTool: " + e);
			e.printStackTrace();
			task.setWorkStatus(WorkStatus.NEEDS_REVIEW);
			task.setNote(e.getClass().getSimpleName() + " : " + e.getMessage());
		}
		return task;
	}


}