package crawling.discovery.planning;

import java.util.function.BiConsumer;

import crawling.discovery.entities.Resource;

public interface PersistStrategy<R> extends BiConsumer<R, Resource>{

	default PersistStrategy<R> andThen(PersistStrategy<R> after) {
		return (r, parent) -> {
			this.accept(r, parent);
			after.accept(r, parent);
		};
	}
	
	public static <T> PersistStrategy<T> emptyStrategy(Class<T> clazz){
		return (thing, resource) -> {};
	}
}
