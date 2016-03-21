package agarbagefolder;

import java.util.UUID;

import akka.actor.UntypedActor;
import async.monitoring.AsyncMonitor;
import async.work.WorkOrder;
import async.work.WorkResult;
import play.Logger;
import play.db.jpa.JPA;

public class SingleStepJPAWorker extends UntypedActor {
	
	protected Long uuid = UUID.randomUUID().getLeastSignificantBits();
	

	@Override
	public void onReceive(Object work) throws Exception {
		WorkOrder workOrder = (WorkOrder) work;
//		System.out.println("Performing work : " + workOrder.getWorkType());
		AsyncMonitor.instance().addWip(workOrder.getWorkType().toString(), uuid);
		WorkResult workResult = processWorkOrderWithWrapper(workOrder);
		AsyncMonitor.instance().finishWip(workOrder.getWorkType().toString(), uuid);
		getSender().tell(workResult, getSelf());
	}
	
	@Override
	public void postRestart(Throwable reason) throws Exception {
		Logger.error("Worker restarting: " + reason);
		preStart();
	}
	
	protected WorkResult processWorkOrder(WorkOrder workOrder) {
		return null;
	}
	
	protected WorkResult processWorkOrderWithWrapper(WorkOrder workOrder){
		WorkResult[] workResult = new WorkResult[1];
		JPA.withTransaction( () -> {
			workResult[0] = processWorkOrder(workOrder);
		});
		System.out.println("workResult[0] : " + workResult[0]);
		return workResult[0];
	}
	
	

}