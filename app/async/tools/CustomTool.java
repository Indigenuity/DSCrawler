package async.tools;

import java.util.HashSet;
import java.util.Set;

import async.registration.ContextItem;
import async.work.WorkStatus;
import async.work.WorkType;
import persistence.tasks.Task;
import play.Logger;

public class CustomTool extends Tool { 

	protected final static Set<ContextItem> requiredContextItems = new HashSet<ContextItem>();
	static{
		ContextItem item = new ContextItem("seed", String.class, false);
		requiredContextItems.add(item);
	}
	
	protected final static Set<ContextItem> resultContextItems = new HashSet<ContextItem>();
	static{
		ContextItem item = new ContextItem("resultSeed", String.class, false);
		resultContextItems.add(item);
	}
	
	protected final static Set<WorkType> abilities = new HashSet<WorkType>();
	static{
		abilities.add(WorkType.CUSTOM);
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
		System.out.println("Custom Tool processing task: " + task);
		
		try{
			String seed = task.getContextItem("seed");
//			JPA.withTransaction( () -> {
//				SiteCrawl siteCrawl = JPA.em().find(SiteCrawl.class, siteCrawlId);
//				siteCrawl.initAll();
//				SiteCrawlAnalyzer.textAnalysis(siteCrawl);
//				siteCrawl.setTextAnalysisDone(true);
//			});
			System.out.println("did work on seed : " + seed);
			task.addContextItem("resultSeed", seed);
			task.setWorkStatus(WorkStatus.WORK_COMPLETED);
		}
		catch(Exception e) {
			Logger.error("Error in Custom Tool: " + e);
			e.printStackTrace();
			task.setWorkStatus(WorkStatus.NEEDS_REVIEW);
			task.setNote("Exception : " + e.getMessage());
		}
		return task;
	}
}