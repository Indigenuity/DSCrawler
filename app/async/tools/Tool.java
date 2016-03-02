package async.tools;

import java.util.Set;

import async.registration.ContextItem;
import async.work.WorkStatus;
import async.work.WorkType;
import persistence.tasks.Task;

public abstract class Tool {

	public Task doTask(Task task){
		if(!hasRequiredItems(task)){
			return incompleteTask(task, "Task doesn't have all the required context items");
		}
		return safeDoTask(task);
	}
	
	protected abstract Task safeDoTask(Task task);
		
	public abstract Set<ContextItem> getRequiredItems(WorkType workType);
	
	public abstract Set<ContextItem> getResultItems(WorkType workType);
	
	public abstract Set<WorkType> getAbilities();
	
	public boolean hasRequiredItems(Task task) {
		for(ContextItem item : getRequiredItems(task.getWorkType())){
			if(task.getContextItem(item.getName()) == null){
				return false;
			}
		}
		return true;
	}
	
	public Task incompleteTask(Task task, String note) {
		
		task.setNote(note);
		task.setWorkStatus(WorkStatus.NEEDS_REVIEW);
		return task;
	}
	
}
