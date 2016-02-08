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
		try{
			WorkOrder workOrder = (WorkOrder) work;
			System.out.println("Performing work : " + workOrder.getWorkType() + " Thread name : " + Thread.currentThread().getName());
			AsyncMonitor.instance().addWip(workOrder.getWorkType().toString(), uuid);
			WorkResult workResult = processWorkOrder(workOrder);
			AsyncMonitor.instance().finishWip(workOrder.getWorkType().toString(), uuid);
			getSender().tell(workResult, getSelf());
		}
		catch(Exception e){
			Logger.error("Error in Single Step Worker : " + e);
			System.out.println("Error in Single Step Worker : " + e);
			e.printStackTrace();
		}
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