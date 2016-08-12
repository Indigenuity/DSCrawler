package controllers;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import async.tasks.TaskReviewer;
import async.work.WorkStatus;
import async.work.WorkType;
import dao.TaskSetDAO;
import persistence.UrlCheck;
import persistence.tasks.Task;
import persistence.tasks.TaskSet;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

public class TaskSetController extends Controller {
	
	@Transactional
	public static Result createSiteCheckTaskSet() {
		return ok();
	}
	
	@Transactional
	public static Result submitTaskReview(){
		DynamicForm form = Form.form().bindFromRequest();
		Long taskId = Long.parseLong(form.get("taskId"));
		Task task = JPA.em().find(Task.class, taskId);
		String action = form.get("action");
		System.out.println("action : " + action);
		Map<String, String> contextItems = new HashMap<String, String>();
		contextItems.putAll(form.data());
		TaskReviewer.reviewTask(task, contextItems);
		
//		UrlCheck urlCheck = JPA.em().find(UrlCheck.class, Long.parseLong(task.getContextItem("urlCheckId")));
//		urlCheck.setManuallyApproved(true);
//		task.setWorkStatus(WorkStatus.WORK_COMPLETED);
		return ok();
	}
	
	@Transactional
	 public static Result setSubtaskWorkStatus() throws SQLException{
		DynamicForm requestData = Form.form().bindFromRequest();
		Long taskSetId = Long.parseLong(requestData.get("taskSetId"));
		System.out.println("workType : " + requestData.get("workType"));
		System.out.println("currentWorkStatus : " + requestData.get("currentWorkStatus"));
		System.out.println("targetWorkStatus : " + requestData.get("targetWorkStatus"));
		WorkType workType = WorkType.valueOf(requestData.get("workType"));
		WorkStatus currentStatus = WorkStatus.valueOf(requestData.get("currentWorkStatus"));
		WorkStatus targetStatus = WorkStatus.valueOf(requestData.get("targetWorkStatus"));
		String supertask = requestData.get("supertaskTargetWorkStatus");
		System.out.println("supertask:  " + supertask);
		if("NO_CHANGE".equals(supertask)){
			TaskSetDAO.setSubtaskWorkStatus(taskSetId, workType, currentStatus, targetStatus);
		} else{
			WorkStatus supertaskTargetWorkStatus = WorkStatus.valueOf(supertask);
			System.out.println("supertask converted : " + supertaskTargetWorkStatus);
			TaskSetDAO.setSubtaskWorkStatus(taskSetId, workType, currentStatus, targetStatus, supertaskTargetWorkStatus);
		}
		
		
		
		
		
		return Results.redirect("taskSet?taskSetId="+taskSetId);
	}
	
	@Transactional
	 public static Result addSubtask(String prereqWorkTypeString) throws SQLException{
		DynamicForm requestData = Form.form().bindFromRequest();
		Long taskSetId = Long.parseLong(requestData.get("taskSetId"));
		WorkType workType = WorkType.valueOf(requestData.get("workType"));
		WorkType prereqWorkType = WorkType.valueOf(requestData.get("prereqWorkType"));
		
		TaskSet taskSet = JPA.em().find(TaskSet.class, taskSetId);
		
		for(Task supertask : taskSet.getTasks()){
		}
		
		return DataView.taskSet(taskSetId);
	}
}

