package async.functionalwork;

import newwork.WorkOrder;
import newwork.WorkResult;
import newwork.Worker;

public class FunctionalWorker extends Worker{

	@Override
	public WorkResult processWorkOrder(WorkOrder workOrder) throws Exception {
		WorkResult workResult = ((FunctionalWorkOrder<?>) workOrder).doWork();
		return workResult;
	}


}
