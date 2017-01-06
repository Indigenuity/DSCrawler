package async.functionalwork;

import java.util.function.Function;

import newwork.TypedWorkResult;

public class FunctionWorkOrder<T, U> extends FunctionalWorkOrder<Function<T, U>> {
	
	protected T input;
	protected Function<T, U> function;
	
	public FunctionWorkOrder(Function<T, U> function, T input){
		super();
		this.function = function;
		this.input = input;
	}

	@Override
	public TypedWorkResult<U> doWork() {
		return new TypedWorkResult<U>(this, function.apply(input));
	}

}
