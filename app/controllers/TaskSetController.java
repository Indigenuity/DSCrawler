package controllers;

import async.work.WorkStatus;
import async.work.WorkType;
import dao.TaskSetDAO;
import persistence.tasks.Task;
import persistence.tasks.TaskSet;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

public class TaskSetController extends Controller {
	
	@Transactional
	 public static Result setSubtaskWorkStatus(){
		DynamicForm requestData = Form.form().bindFromRequest();
		Long taskSetId = Long.parseLong(requestData.get("taskSetId"));
		WorkType workType = WorkType.valueOf(requestData.get("workType"));
		WorkStatus currentStatus = WorkStatus.valueOf(requestData.get("currentStatus"));
		WorkStatus targetStatus = WorkStatus.valueOf(requestData.get("targetStatus"));
		
		TaskSetDAO.setSubtaskWorkStatus(taskSetId, workType, currentStatus, targetStatus);
		
		return DataView.taskSet(taskSetId);
	}
	
	@Transactional
	 public static Result addSubtask(String prereqWorkTypeString){
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

