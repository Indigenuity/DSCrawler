package async.tasks;

import java.time.LocalDate;
import java.util.List;

import async.work.WorkStatus;
import async.work.WorkType;
import persistence.Site;
import persistence.Site.SiteStatus;
import persistence.tasks.Task;
import persistence.tasks.TaskSet;
import play.db.jpa.JPA;

public class TaskSetBuilder {

	public static TaskSet siteCheck(List<Site> sites) {
		TaskSet taskSet = new TaskSet();
		taskSet.setName("Site Checks " + LocalDate.now());
		
		System.out.println("Creating Site Check TaskSet for " + sites.size() + " sites");
		for(Site site : sites){
			Task supertask = new Task();
			supertask.setWorkStatus(WorkStatus.DO_WORK);
			supertask.setWorkType(WorkType.SUPERTASK);
			supertask.addContextItem("seed", site.getHomepage());
			supertask.addContextItem("siteId", site.getSiteId() + "");
			taskSet.addTask(supertask);
			
			Task urlTask = new Task();
			urlTask.setWorkType(WorkType.REDIRECT_RESOLVE);
			urlTask.setWorkStatus(WorkStatus.DO_WORK);
			supertask.addSubtask(urlTask);
		}
		
		JPA.em().persist(taskSet);
		return taskSet;
		
	}
	
	
	public static TaskSet siteCrawl(List<Site> sites) {
		TaskSet taskSet = new TaskSet();
		taskSet.setName("Site Crawls " + LocalDate.now());
		
		System.out.println("Creating Site Check TaskSet for " + sites.size() + " sites");
		for(Site site : sites){
			Task supertask = new Task();
			supertask.setWorkStatus(WorkStatus.DO_WORK);
			supertask.setWorkType(WorkType.SUPERTASK);
			supertask.addContextItem("seed", site.getHomepage());
			supertask.addContextItem("siteId", site.getSiteId() + "");
			taskSet.addTask(supertask);
			
//			Task urlTask = new Task();
//			urlTask.setWorkType(WorkType.REDIRECT_RESOLVE);
//			urlTask.setWorkStatus(WorkStatus.DO_WORK);
//			supertask.addSubtask(urlTask);
//			
//			Task updateTask = new Task();
//			updateTask.setWorkType(WorkType.SITE_UPDATE);
//			updateTask.setWorkStatus(WorkStatus.DO_WORK);
//			updateTask.addPrerequisite(urlTask);
//			supertask.addSubtask(updateTask);
//			
			Task crawlTask = new Task();
			crawlTask.setWorkType(WorkType.SITE_CRAWL);
			crawlTask.setWorkStatus(WorkStatus.DO_WORK);
			supertask.addSubtask(crawlTask);
			
			Task amalgTask = new Task();
			amalgTask.setWorkStatus(WorkStatus.DO_WORK);
			amalgTask.setWorkType(WorkType.AMALGAMATION);
			amalgTask.addPrerequisite(crawlTask);
			JPA.em().persist(amalgTask);
			
			
			Task analysisTask = new Task();
			analysisTask.setWorkType(WorkType.ANALYSIS);
			analysisTask.setWorkStatus(WorkStatus.DO_WORK);
			analysisTask.addPrerequisite(amalgTask);
			supertask.addSubtask(analysisTask);
			
			site.setHomepage(site.getHomepage());
		}
		
		JPA.em().persist(taskSet);
		return taskSet;
	}
}
