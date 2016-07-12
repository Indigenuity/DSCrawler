package newwork;

public abstract class MultiStepWorker extends Worker {

	
	@Override
	public void onReceiveWorkOrder() throws Exception {
		begin();
	}
	
	public void doSteps(){
		
	}
	
	public abstract void begin();
	public abstract void finish();

}
