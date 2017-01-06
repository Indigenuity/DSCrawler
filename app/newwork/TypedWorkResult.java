package newwork;

public class TypedWorkResult<U extends Object> extends WorkResult {

	private U result;
	
	public TypedWorkResult(Long workUuid, U result) {
		super(workUuid);
		this.result = result;
	}

	public TypedWorkResult(WorkOrder workOrder, U result) {
		super(workOrder);
		this.result = result;
	}

	@Override
	public U getResult() {
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setResult(Object result) {
		this.result = (U) result;
	}

	public void setTypedResult(U result) {
		this.result = result;
	}

}
