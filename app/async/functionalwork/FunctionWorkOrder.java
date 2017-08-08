package async.functionalwork;

import java.util.function.Function;

import newwork.TypedWorkResult;

public class FunctionWorkOrder<T, R> extends FunctionalWorkOrder<Function<T, R>> {
	
	protected T input;
	protected Function<T, R> function;
	
	public FunctionWorkOrder(Function<T, R> function, T input){
		super();
		this.function = function;
		this.input = input;
	}

	@Override
	public TypedWorkResult<R> doWork() {
		return new TypedWorkResult<R>(this, function.apply(input));
	}

}
