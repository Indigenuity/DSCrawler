package crawling.discovery.local;

import crawling.discovery.entities.Resource;
import crawling.discovery.execution.PlanId;
import crawling.discovery.execution.ResourceWorkOrder;

public class InventoryWorkOrder extends ResourceWorkOrder{

	protected InvOrderType invOrderType;
	
	public InventoryWorkOrder(Object source, Resource parent, PlanId planId) {
		super(source, parent, planId);
	}
	
	public InvOrderType getInvOrderType() {
		return invOrderType;
	}

	public void setInvOrderType(InvOrderType invOrderType) {
		this.invOrderType = invOrderType;
	}

	public static enum InvOrderType{
		NEW, USED
	}
}
