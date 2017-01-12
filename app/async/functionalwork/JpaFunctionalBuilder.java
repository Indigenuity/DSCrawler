package async.functionalwork;

import java.util.function.Consumer;
import java.util.function.Function;

import play.db.jpa.JPA;

public class JpaFunctionalBuilder {

	public <T, U> Function<Long, U> wrapKeyFunctionWithTransaction(Function<T, U> function, Class<T> clazz){
		return function.compose((key) -> {
			try {
				return JPA.withTransaction(() ->{
					T item = JPA.em().find(clazz, key);
					return item;
				});
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		});
	}
	
	public static <T, U> Function<Long, U> wrapFunctionInFind(Function<T, U> function, Class<T> clazz){
		return (key) -> {
			return function.apply(JPA.em().find(clazz, key));
		};
	}
	
	public static <T> Consumer<Long> wrapConsumerInFind(Consumer<T> consumer, Class<T> clazz){
		return ((key) -> {
			consumer.accept(JPA.em().find(clazz, key));
		});
	} 
	
}
