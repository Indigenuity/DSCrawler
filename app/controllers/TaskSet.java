package controllers;

import async.work.WorkStatus;
import async.work.WorkType;
import dao.TaskSetDAO;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

public class TaskSet extends Controller {
	
	@Transactional
	 public static Result setSubtaskWorkStatus(Long taskSetId, String workTypeString, String currentStatusString, String targetStatusString){
		WorkStatus currentStatus = WorkStatus.valueOf(currentStatusString);
		WorkStatus targetStatus = WorkStatus.valueOf(targetStatusString);
		WorkType workType = WorkType.valueOf(workTypeString);
		TaskSetDAO.setSubtaskWorkStatus(taskSetId, workType, currentStatus, targetStatus);
		
		return DataView.taskSet(taskSetId);
	}
}

