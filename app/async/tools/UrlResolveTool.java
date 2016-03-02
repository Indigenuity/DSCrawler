package async.tools;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import async.registration.ContextItem;
import async.work.WorkStatus;
import async.work.WorkType;
import persistence.UrlCheck;
import persistence.tasks.Task;
import play.Logger;
import play.db.jpa.JPA;
import utilities.UrlSniffer;

public class UrlResolveTool extends Tool { 
	


	protected final static Set<ContextItem> requiredContextItems = new HashSet<ContextItem>();
	static{
		ContextItem item = new ContextItem("seed", String.class, false);
		requiredContextItems.add(item);
	}
	
	protected final static Set<ContextItem> resultContextItems = new HashSet<ContextItem>();
	static{
		ContextItem item = new ContextItem("urlCheckId", Long.class, false);
		resultContextItems.add(item);
	}
	
	protected final static Set<WorkType> abilities = new HashSet<WorkType>();
	static{
		abilities.add(WorkType.REDIRECT_RESOLVE);
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
		System.out.println("UrlResolveTool processing Task : " + task.getTaskId());
		try{
			String seed = task.getContextItem("seed");
			UrlCheck urlCheck = UrlSniffer.checkUrl(seed);
			urlCheck.setCheckDate(new java.sql.Date(Calendar.getInstance().getTimeInMillis()));
			JPA.withTransaction( () -> {
				JPA.em().persist(urlCheck);				
			});
//			System.out.println("id after persist : " + urlCheck.getUrlCheckId());
			task.addContextItem("urlCheckId", urlCheck.getUrlCheckId() + "");
			task.setWorkStatus(WorkStatus.WORK_COMPLETED);
//			System.out.println("UrlResolveWorker done processing work order");
		}
		catch(Exception e) {
			Logger.error("Error in Url Resolve: " + e);
			e.printStackTrace();
			task.setWorkStatus(WorkStatus.NEEDS_REVIEW);
			task.setNote("Exception : " + e.getMessage());
		}
		return task;
	}


	
	

}