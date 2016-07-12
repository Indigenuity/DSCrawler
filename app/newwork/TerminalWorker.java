package newwork;

public class TerminalWorker extends Worker {

	protected TerminalWorkOrder<?> workOrder;
	
	@Override
	public void onReceiveWorkOrder() throws Exception {
		workOrder = (TerminalWorkOrder<?>) currentWorkOrder;
		Object result = workOrder.getInstructions().call();
		endInSuccess(result);
	}
}
