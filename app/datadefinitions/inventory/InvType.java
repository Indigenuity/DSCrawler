package datadefinitions.inventory;

public enum InvType {
	
	AUTOFUSION		(new Autofusion()),
	CDK_GLOBAL		(new CdkGlobal()),
	DEALER_COM		(new DealerCom()),
	
	;

	private final InventoryTool tool;
	
	private InvType(InventoryTool tool){
		this.tool = tool;
	}

	public InventoryTool getTool() {
		return tool;
	}
	
}
