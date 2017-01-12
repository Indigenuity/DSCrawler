package newwork;

import java.io.PrintWriter;
import java.io.StringWriter;

import akka.actor.UntypedActor;

public abstract class Worker extends UntypedActor{

	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof WorkOrder){
			WorkOrder workOrder = (WorkOrder) message;
			
			try{
				reportStart();
				WorkResult workResult = processWorkOrder(workOrder);
				getSender().tell(workResult, getSelf());
			} catch(Exception e){
				endInError(workOrder, e);
			} finally {
				reportStop();
			}
		} else if(message instanceof WorkResult) {
			onReceiveWorkResult((WorkResult) message);
		}
	}
	
	public void onReceiveWorkResult(WorkResult workResult) throws Exception {
		throw new UnsupportedOperationException();
	}
	
	protected void endInError(WorkOrder workOrder, Exception e){
		WorkResult workResult = new WorkResult(workOrder);
		workResult.setWorkStatus(WorkStatus.ERROR);
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		workResult.setError(e.getClass().getSimpleName() + " : " + e.getMessage() + " : " +  sw);
		getSender().tell(workResult, getSelf());
		end();
	}
	
	private void end(){
		reportStop();
		
	}
	
	private void reportStart() {
		//TODO: do something here
	}
	
	private void reportStop() {
		//TODO: do something here
	}
	
	public abstract WorkResult processWorkOrder(WorkOrder workOrder) throws Exception;
	
}
