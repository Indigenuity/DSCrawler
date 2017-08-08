package async.callables;

import java.util.concurrent.Callable;

import newwork.WorkOrder;

public class CallableWorkOrder<R> extends WorkOrder {
	
	private final Callable<R> callable;
	
	public CallableWorkOrder(Callable<R> callable){
		this.callable = callable;
	}

	public Callable<R> getCallable() {
		return callable;
	}

}
