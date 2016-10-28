package newwork;

import java.util.UUID;
import java.util.concurrent.Callable;

public abstract class TerminalWorkOrder<T> extends StandardWorkOrder {
	
	protected final Class<T> outputClass;
	
	public TerminalWorkOrder(Class<T> outputClass){
		this.outputClass = outputClass;
	}
	
	public abstract Callable<T> getInstructions();
	
	public Class<T> getOutputClass(){
		return outputClass;
	}
}
