package async.functionalwork;

import newwork.WorkOrder;
import newwork.WorkResult;
import play.db.jpa.JPA;

public class JpaFunctionalWorker extends FunctionalWorker {

	@Override
	public WorkResult processWorkOrder(WorkOrder workOrder) {
		WorkResult[] workResult = new WorkResult[1];
		JPA.withTransaction(() ->{
			workResult[0] = super.processWorkOrder(workOrder);
		});
		return workResult[0];
	}
}
