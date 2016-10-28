package crawling.discovery;

import java.util.function.Consumer;

public interface PersistStrategy<R> extends Consumer<R>{

	default PersistStrategy<R> andThen(PersistStrategy<R> after) {
		return (r) -> {
			this.accept(r);
			after.accept(r);
		};
	}
	
}
