package newwork;

import java.io.PrintWriter;
import java.io.StringWriter;

import akka.actor.UntypedActor;

public abstract class Worker extends UntypedActor{

	protected StandardWorkOrder currentWorkOrder;		//Each Worker should only be working on a single WorkOrder at a time
	protected WorkResult workResult;			//This WorkOrder will result in a single WorkResult

	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof StandardWorkOrder){
			currentWorkOrder = (StandardWorkOrder) message;
			workResult = new WorkResult(currentWorkOrder.getUuid());
			try{
				reportStart();
				onReceiveWorkOrder();
			} catch(Exception e){
				endInError(e);
			}
		} else if(message instanceof WorkResult) {
			onReceiveWorkResult((WorkResult) message);
		}
	}
	
	public void onReceiveWorkResult(WorkResult workResult) throws Exception {
		throw new UnsupportedOperationException();
	}
	
	protected void endInError(Exception e){
		workResult.setWorkStatus(WorkStatus.ERROR);
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		workResult.setError(e.getClass().getSimpleName() + " : " + e.getMessage() + " : " +  sw);
		end();
	}
	
	protected void endInSuccess(Object result) {
		workResult.setResult(result);
		workResult.setWorkStatus(WorkStatus.COMPLETE);
		end();
	}
	
	private void end(){
		reportStop();
		getSender().tell(workResult, getSelf());
	}
	
	private void reportStart() {
		
	}
	
	private void reportStop() {
		
	}
	
	public abstract void onReceiveWorkOrder() throws Exception;
	
}
