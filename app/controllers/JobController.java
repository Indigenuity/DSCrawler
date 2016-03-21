package controllers;

import global.Global;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;

import agarbagefolder.InfoFetch;
import agarbagefolder.SiteWork;
import agarbagefolder.WorkSet;
import akka.actor.ActorRef;
import analysis.SiteCrawlAnalyzer;
import analysis.LogAnalyzer;
import async.async.Asyncleton;
import async.tasks.TaskMaster;
import async.work.WorkItem;
import async.work.WorkStatus;
import async.work.WorkType;
import dao.InfoFetchDAO;
import dao.SiteCrawlDAO;
import persistence.CrawlSet;
import persistence.MobileCrawl;
import persistence.PageInformation;
import persistence.Site;
import persistence.SiteCrawl;
import persistence.SiteInformationOld;
import persistence.stateful.FetchJob;
import persistence.tasks.Task;
import persistence.tasks.TaskSet;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

public class JobController extends Controller {

	
	@Transactional
	public static Result taskSetWork() {
		try{
			System.out.println("received task set work"); 
			DynamicForm requestData = Form.form().bindFromRequest();
			Long taskSetId = Long.parseLong(requestData.get("taskSetId"));
			Integer numToProcess = Integer.parseInt(requestData.get("numToProcess"));
			Integer offset = Integer.parseInt(requestData.get("offset"));
			Integer numWorkers = Integer.parseInt(requestData.get("numWorkers"));
			
			
			TaskSet taskSet = JPA.em().find(TaskSet.class, taskSetId);
			
			TaskMaster.doTaskSetWork(taskSet, numWorkers, numToProcess, offset);
			
			return DataView.dashboard("Submitted Task Set Work");
		}
		catch(Exception e) {
			return internalServerError(e.getMessage());
		}
		
	}
	
	
	@Transactional
	public static Result fillFailedUrls() throws IOException{
		LogAnalyzer.nullUrls();
		return ok();
	}
	
}
