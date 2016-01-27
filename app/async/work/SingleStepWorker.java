package async.work;

import java.util.UUID;

import akka.actor.UntypedActor;
import async.monitoring.AsyncMonitor;
import play.Logger;
import play.db.jpa.JPA;

public class SingleStepWorker extends UntypedActor {
	
	protected Long uuid = UUID.randomUUID().getLeastSignificantBits();
	

	@Override
	public void onReceive(Object work) throws Exception {
		WorkOrder workOrder = (WorkOrder) work;
		System.out.println("Performing work : " + workOrder.getWorkType());
		AsyncMonitor.instance().addWip(workOrder.getWorkType().toString(), uuid);
		WorkResult workResult = processWorkOrder(workOrder);
		AsyncMonitor.instance().finishWip(workOrder.getWorkType().toString(), uuid);
		getSender().tell(workResult, getSelf());
		getContext().stop(getSelf());
	}
	
	@Override
	public void postRestart(Throwable reason) throws Exception {
		Logger.error("Worker restarting: " + reason);
		preStart();
	}
	
	public WorkResult processWorkOrder(WorkOrder workOrder) {
		return null;
	}
	
	

}