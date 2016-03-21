package async.tools;

import java.util.HashSet;
import java.util.Set;

import analysis.SiteCrawlAnalyzer;
import async.registration.ContextItem;
import async.work.WorkStatus;
import async.work.WorkType;
import persistence.SiteCrawl;
import persistence.tasks.Task;
import play.db.jpa.JPA;

public class AnalysisTool extends TransactionTool { 
	


	protected final static Set<ContextItem> requiredContextItems = new HashSet<ContextItem>();
	static{
		ContextItem item = new ContextItem("siteCrawlId", String.class, false);
		requiredContextItems.add(item);
	}
	
	protected final static Set<ContextItem> resultContextItems = new HashSet<ContextItem>();
	
	protected final static Set<WorkType> abilities = new HashSet<WorkType>();
	static{
		abilities.add(WorkType.ANALYSIS);
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
	
	public static Task doathing(Task task) throws Exception{
		return (new InventoryTool()).safeDoTask(task);
	}
	
	@Override
	protected Task doTaskInTransaction(Task task) throws Exception{
		System.out.println("AnalysisTool processing task: " + task.getTaskId());
		Long siteCrawlId = Long.parseLong(task.getContextItem("siteCrawlId"));
		SiteCrawl siteCrawl = JPA.em().find(SiteCrawl.class, siteCrawlId);
		siteCrawl.initAll();
		SiteCrawlAnalyzer.doFull(siteCrawl);
		JPA.em().getTransaction().commit();
		JPA.em().getTransaction().begin();
		JPA.em().detach(siteCrawl);
		task.setWorkStatus(WorkStatus.WORK_COMPLETED);
		return task;
	}

}