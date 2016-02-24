package async.newwork;


import java.util.HashSet;
import java.util.Set;

import async.work.WorkOrder;
import async.work.WorkResult;
import async.work.WorkStatus;
import persistence.tasks.Task;
import play.Logger;
import play.db.jpa.JPA;

public class Worker {

	public static WorkResult doWorkOrder(WorkOrder workOrder) {
		System.out.println("Doing supertask work");
		WorkResult result = new WorkResult(workOrder);
		Task task = null;
		try{
			Long taskId = Long.parseLong(workOrder.getContextItem("taskId"));
			task = fetchTask(taskId);
			
			if(task.getSubtasks().size() > 0){
				task = doSupertask(task);
			}
			else{
				task = doSingleTask(task);
			}
		}
		catch(Exception e) {
			Logger.error("Error in Worker: " + e);
			e.printStackTrace();
			if(task != null){
				result.addContextItem("taskId", task.getTaskId() + "");
			}
			result.setWorkStatus(WorkStatus.NEEDS_REVIEW);
			result.setNote("Exception : " + e.getMessage());
		}
		return result;
	}
	
	private static void doSubtask(Task subtask) {
		
	}
	
	
	
	private static Task fetchTask(Long taskId){
		Task[] fetchedTask= new Task[1];
		JPA.withTransaction( () -> {
			fetchedTask[0] = JPA.em().find(Task.class, taskId);
		});
		return fetchedTask[0];
	}
	
	private static Task doSupertask(Task task) {
		Set<Task> doableTasks = getDoableTasks(task);
		
		if(doableTasks.size() < 1){	//No valid subtasks to do
			if(needsReview(task)){	//The reason is because a subtask needs review	
				task.setWorkStatus(WorkStatus.NEEDS_REVIEW);
				task.setNote("Subtask(s) need review");
			}
			else if(hasMoreWork(task)){	// The reason is because prereq tangle or some other reason 
				task.setWorkStatus(WorkStatus.NEEDS_REVIEW);
				task.setNote("Subtasks are in prereq tangle");
			}
		}
		else {
			for(Task subtask: doableTasks) {
				doSubtask(subtask);
			}
		}
		
		return task;
	}
	
	private static Task doSingleTask(Task task) {
		
		return task;
	}
	
	private static Set<Task> getDoableTasks(Task supertask) {
		Set<Task> doableTasks = new HashSet<Task>();
		if(supertask.isSerialTask()){
			doableTasks.add(getNextSubtask(supertask));
		}
		else {
			doableTasks.addAll(getNextSubtasks(supertask));
		}
		return doableTasks;
	}
	
	private static Task getNextSubtask(Task supertask){
		for(Task subtask : supertask.getSubtasks()){
			if(subtask.getWorkStatus() == WorkStatus.DO_WORK && subtask.prereqsSatisfied()){
				return subtask;
			}
		}
		return null;
	}
	
	private static Set<Task> getNextSubtasks(Task supertask){
		Set<Task> subtasks = new HashSet<Task>();
		for(Task subtask : supertask.getSubtasks()){
			if(subtask.getWorkStatus() == WorkStatus.DO_WORK && subtask.prereqsSatisfied()){
				subtasks.add(subtask);
			}
		}
		return subtasks;
	}
	
	private static boolean needsReview(Task supertask) {
		for(Task subtask : supertask.getSubtasks()){
			if(subtask.getWorkStatus() == WorkStatus.NEEDS_REVIEW){
				return true;
			}
		}
		return false;
	}
	
	private static boolean hasMoreWork(Task supertask) {
		for(Task subtask : supertask.getSubtasks()){
			if(subtask.getWorkStatus() == WorkStatus.DO_WORK){
				return true;
			}
		}
		return false;
	}
	
}
