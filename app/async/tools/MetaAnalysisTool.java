package async.tools;

import persistence.SiteCrawl;
import persistence.tasks.Task;
import play.Logger;
import play.db.jpa.JPA;

import java.util.HashSet;
import java.util.Set;

import analysis.SiteCrawlAnalyzer;
import async.registration.ContextItem;
import async.work.WorkStatus;
import async.work.WorkType;

public class MetaAnalysisTool  extends Tool { 

	protected final static Set<ContextItem> requiredContextItems = new HashSet<ContextItem>();
	static{
		ContextItem item = new ContextItem("siteCrawlId", String.class, false);
		requiredContextItems.add(item);
	}
	
	protected final static Set<ContextItem> resultContextItems = new HashSet<ContextItem>();
	
	protected final static Set<WorkType> abilities = new HashSet<WorkType>();
	static{
		abilities.add(WorkType.META_ANALYSIS);
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
		System.out.println("MetaAnalysisTool processing task: " + task);
		
		try{
			Long siteCrawlId = Long.parseLong(task.getContextItem("siteCrawlId"));
			JPA.withTransaction( () -> {
				SiteCrawl siteCrawl = JPA.em().find(SiteCrawl.class, siteCrawlId);
				siteCrawl.initAll();
				SiteCrawlAnalyzer.metaAnalysis(siteCrawl);
				siteCrawl.setMetaAnalysisDone(true);
			});
			task.setWorkStatus(WorkStatus.WORK_COMPLETED);
		}
		catch(Exception e) {
			Logger.error("Error in Meta Analysis: " + e);
			e.printStackTrace();
			task.setWorkStatus(WorkStatus.NEEDS_REVIEW);
			task.setNote("Exception : " + e.getMessage());
		}
		return task;
	}
}