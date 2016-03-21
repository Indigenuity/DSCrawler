package async.tools;

import persistence.tasks.Task;
import play.db.jpa.JPA;

public abstract class TransactionTool extends Tool {

	@Override
	protected Task safeDoTask(Task task) throws Exception{
//		System.out.println("InventoryTool processing task : " + task);
		Task[] temp = new Task[1];
		JPA.withTransaction( () -> {
			temp[0]= doTaskInTransaction(task);
			JPA.em().flush();
			JPA.em().clear();
		});
		System.gc();
		return temp[0];
	}
	
	protected abstract Task doTaskInTransaction(Task task) throws Exception;
}
