package async.functionalwork;

import newwork.WorkOrder;
import newwork.WorkResult;

public abstract class FunctionalWorkOrder<T> extends WorkOrder {

	protected T functionalWork;
	
	public abstract WorkResult doWork();
	
}
