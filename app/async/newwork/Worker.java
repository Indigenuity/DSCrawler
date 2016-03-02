package async.newwork;


import java.util.HashSet;
import java.util.Set;

import akka.actor.UntypedActor;
import async.registration.ContextItem;
import async.registration.RegistryEntry;
import async.registration.WorkerRegistry;
import async.tasks.MissingContextItemException;
import async.tools.Tool;
import async.tools.ToolGuide;
import async.work.WorkOrder;
import async.work.WorkResult;
import async.work.WorkStatus;
import persistence.tasks.Task;
import play.Logger;
import play.db.jpa.JPA;

public class Worker extends UntypedActor {

	@Override
	public void onReceive(Object arg0) throws Exception {
//		System.out.println("Worker received message");
		WorkOrder workOrder = (WorkOrder) arg0;
		WorkResult workResult = doWorkOrder(workOrder);
		getSender().tell(workResult, getSelf());
	}
	
	public static WorkResult doWorkOrder(WorkOrder workOrder) {
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
			task = saveTask(task);
		}
		catch(Exception e) {
			Logger.error("Error in Worker doing work order: " + e);
			e.printStackTrace();
			if(task != null){
				task.setWorkStatus(WorkStatus.NEEDS_REVIEW);
				task.setNote(e.getClass().getSimpleName() + " : " + e.getMessage());
				saveTask(task);
			}
			result.setWorkStatus(WorkStatus.NEEDS_REVIEW);
			result.setNote(e.getClass().getSimpleName() + " : " + e.getMessage());
		}
		return result;
	}
	
	private static Task fetchTask(Long taskId){
		Task[] fetchedTask= new Task[1];
		JPA.withTransaction( () -> {
			fetchedTask[0] = JPA.em().find(Task.class, taskId);
			fetchedTask[0].initLazy();
		});
		return fetchedTask[0];
	}
	
	private static void initTask(Task task) {
		JPA.withTransaction( () -> {
			task.initLazy();
		});
	}
	
	private static Task saveTask(Task task) {
		
		JPA.withTransaction( () -> {
			JPA.em().merge(task);
		});
		return task;
	}
	
	private static Task doSingleTask(Task task) {
//		System.out.println("Worker doing single task : " + task.getTaskId());
		Tool tool = ToolGuide.findTool(task.getWorkType());
		task = tool.doTask(task);
		return task;
	}
	
	private static Task doSupertask(Task supertask) {
//		System.out.println("Worker doing supertask : " + supertask.getTaskId());
		
		while(doSingleStep(supertask));
		
		return supertask;
	}
	
	private static boolean doSingleStep(Task supertask){
//		System.out.println("Worker doing single step of supertask : " + supertask.getTaskId());
		if(needsReview(supertask)){	
			supertask.setWorkStatus(WorkStatus.NEEDS_REVIEW);
			supertask.setNote("Subtask(s) need review");
			return false;
		}
		if(!hasMoreWork(supertask)){ 
			supertask.setWorkStatus(WorkStatus.WORK_COMPLETED);
			supertask.setNote("No more work to complete");
			return false;
		}
		
		Task doableTask = getNextSubtask(supertask);
		if(doableTask == null){		//Means there is some tangle of prerequisites or something	
			supertask.setWorkStatus(WorkStatus.NEEDS_REVIEW);
			supertask.setNote("Subtasks are in prereq tangle");
			return false;
		}
		
		return doSubtask(doableTask);
	}
	
	private static boolean doSubtask(Task subtask) {
//		System.out.println("Worker doing subtask : " + subtask.getTaskId());
		Task supertask = subtask.getSupertask();
		try{
			loadContextItems(subtask);
			subtask = doSingleTask(subtask);
			if(subtask.getWorkStatus() != WorkStatus.WORK_COMPLETED){
				supertask.setWorkStatus(WorkStatus.NEEDS_REVIEW);
				supertask.setNote("Subtask did not complete : " + subtask.getNote());
				saveTask(subtask);
				saveTask(supertask);
				return false;
			}
			
			unloadContextItems(subtask);
			saveTask(subtask);
			saveTask(supertask);
			return true;
		}
		catch(Exception e) {
			Logger.error("Error in Worker doing subtask : " + e);
			e.printStackTrace();
			subtask.setWorkStatus(WorkStatus.NEEDS_REVIEW);
			subtask.setNote(e.getClass().getSimpleName() + " : " + e.getMessage());
			
			supertask.setWorkStatus(WorkStatus.NEEDS_REVIEW);
			supertask.setNote("Subtask needs Review");
			saveTask(subtask);
			saveTask(supertask);
			return false;
		}
		
	}
	
	private static void loadContextItems(Task subtask) {
		Task supertask = subtask.getSupertask();
		RegistryEntry entry = WorkerRegistry.getInstance().getRegistrant(subtask.getWorkType());
		for(ContextItem item : entry.getRequiredContextItems()){
			if(subtask.getContextItem(item.getName()) == null) {
				String superContextItem = supertask.getContextItem(item.getName());
				if(superContextItem == null && !item.isNullable()){
					throw new MissingContextItemException("Task missing required context item : " + item.getName());
				}
				subtask.addContextItem(item.getName(), superContextItem);
			}
		}
	}
	
	private static void unloadContextItems(Task subtask) {
		Task supertask = subtask.getSupertask();
		RegistryEntry entry = WorkerRegistry.getInstance().getRegistrant(subtask.getWorkType());
		for(ContextItem item : entry.getResultContextItems()){
			String resultContextItem = subtask.getContextItem(item.getName());
			if(resultContextItem == null && !item.isNullable()){
				throw new MissingContextItemException("Task missing result context item : " + item.getName());
			}
			supertask.addContextItem(item.getName(), resultContextItem);
		}
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
