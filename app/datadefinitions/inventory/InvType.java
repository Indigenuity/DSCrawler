package datadefinitions.inventory;

import datadefinitions.inventory.implementations.Auction123;
import datadefinitions.inventory.implementations.Autofusion;
import datadefinitions.inventory.implementations.CdkGlobal;
import datadefinitions.inventory.implementations.DealerCom;

public enum InvType {
	
	AUTOFUSION		(new Autofusion()),
	CDK_GLOBAL		(new CdkGlobal()),
	DEALER_COM		(new DealerCom()),
	AUCTION_123		(new Auction123()),
	
	;

	private final InventoryTool tool;
	
	private InvType(InventoryTool tool){
		this.tool = tool;
	}

	public InventoryTool getTool() {
		return tool;
	}
	
}
