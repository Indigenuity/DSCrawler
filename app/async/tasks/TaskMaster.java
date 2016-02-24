package async.tasks;

import akka.actor.ActorRef;
import async.Asyncleton;
import async.work.WorkOrder;
import async.work.WorkStatus;
import async.work.WorkType;
import persistence.tasks.Task;
import persistence.tasks.TaskSet;

public class TaskMaster {

	public static void doTaskSetWork(TaskSet taskSet, int numWorkers, int numToProcess, int offset) {
		int count = 0;
		System.out.println("TaskMaster initializing master with " + numWorkers + " workers");
		ActorRef master = Asyncleton.getInstance().getGenericMaster(numWorkers);
		for(Task task : taskSet.getTasks()) {
			if(count < numToProcess && task.getWorkStatus() == WorkStatus.DO_WORK){
				count++;
				if(count < offset){
					continue;
				}
				WorkOrder workOrder = new WorkOrder(WorkType.TASK);
				workOrder.addContextItem("taskId", task.getTaskId() + "");
				master.tell(workOrder, Asyncleton.getInstance().getMainListener());
			}
		}
	}
}
