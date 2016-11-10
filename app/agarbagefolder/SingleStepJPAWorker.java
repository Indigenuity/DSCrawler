package agarbagefolder;

import java.util.UUID;

import akka.actor.UntypedActor;
import async.monitoring.AsyncMonitor;
import async.work.TypedWorkOrder;
import async.work.TypedWorkResult;
import play.Logger;
import play.db.jpa.JPA;

public class SingleStepJPAWorker extends UntypedActor {
	
	protected Long uuid = UUID.randomUUID().getLeastSignificantBits();
	

	@Override
	public void onReceive(Object work) throws Exception {
		TypedWorkOrder workOrder = (TypedWorkOrder) work;
//		System.out.println("Performing work : " + workOrder.getWorkType());
		AsyncMonitor.instance().addWip(workOrder.getWorkType().toString(), uuid);
		TypedWorkResult workResult = processWorkOrderWithWrapper(workOrder);
		AsyncMonitor.instance().finishWip(workOrder.getWorkType().toString(), uuid);
		getSender().tell(workResult, getSelf());
	}
	
	@Override
	public void postRestart(Throwable reason) throws Exception {
		Logger.error("Worker restarting: " + reason);
		preStart();
	}
	
	protected TypedWorkResult processWorkOrder(TypedWorkOrder workOrder) {
		return null;
	}
	
	protected TypedWorkResult processWorkOrderWithWrapper(TypedWorkOrder workOrder){
		TypedWorkResult[] workResult = new TypedWorkResult[1];
		JPA.withTransaction( () -> {
			workResult[0] = processWorkOrder(workOrder);
		});
		System.out.println("workResult[0] : " + workResult[0]);
		return workResult[0];
	}
	
	

}