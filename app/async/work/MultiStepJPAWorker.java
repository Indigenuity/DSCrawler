package async.work;

import java.util.UUID;

import akka.actor.UntypedActor;
import async.monitoring.AsyncMonitor;
import play.Logger;
import play.db.jpa.JPA;

public class MultiStepJPAWorker extends UntypedActor {
	
	protected Long uuid = UUID.randomUUID().getLeastSignificantBits();
	protected WorkOrder workOrder;

	@Override
	public void onReceive(Object work) throws Exception {
		
		if(work instanceof WorkOrder) {
			if(workOrder != null){
				throw new IllegalStateException("Received second work order : " + work);
			}
			workOrder = (WorkOrder) work;
			System.out.println("Performing work : " + workOrder.getWorkType());
			AsyncMonitor.instance().addWip(workOrder.getWorkType().toString(), uuid);
			processWorkOrder(workOrder);
			proceedWithWork();
		}
		else if(work instanceof WorkResult){
			WorkResult workResult = (WorkResult)work;
			processWorkResult(workResult);
			proceedWithWork();
		}
		else {
			Logger.error("got unknown work in Multi Step worker : " + work);
		}
		
	}
	
	@Override
	public void postRestart(Throwable reason) throws Exception {
		Logger.error("Worker restarting: " + reason);
		preStart();
	}
	
	public void processWorkOrder(WorkOrder workOrder) {
		
	}
	
	public boolean hasNextStep() {
		return false;
	}
	
	public void doNextStep(){
		
	}
	
	public void proceedWithWork(){
		if(hasNextStep()){
			doNextStep();
		}
		else {
			finish();
		}
	}
	
	public void processWorkResult(WorkResult workResult) {
		
	}
	
	public void finish() {
		AsyncMonitor.instance().finishWip(workOrder.getWorkType().toString(), uuid);
		WorkResult workResult = generateWorkResult();
		getSender().tell(workResult, getSelf());
		getContext().stop(getSelf());
	}
	
	public WorkResult generateWorkResult(){
		return null;
	}
	

}