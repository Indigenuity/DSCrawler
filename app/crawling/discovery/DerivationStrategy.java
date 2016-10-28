package crawling.discovery;

import java.util.Objects;
import java.util.function.Function;

public interface DerivationStrategy<T, R>  extends Function<T, R>{

	public R derive(T source);
	
	default R apply(T source){
		return derive(source);
	}
	
	default <V> DerivationStrategy<V, R> compose(Function<? super V, ? extends T> before){
		Objects.requireNonNull(before);
		return(V v) -> apply(before.apply(v));
	}
	
	default <V> DerivationStrategy<T, V> andThen(Function<? super R, ? extends V> after) {
		Objects.requireNonNull(after);
		return (T t) -> after.apply(apply(t));
	}
	
	
	
}
