package crawling.discovery;

import java.util.function.Function;

public interface Fetcher<T extends Endpoint, R> extends Function<T, R>{

	public R fetch(T endpoint);
	
	@Override
	default R apply(T endpoint) {
		return fetch(endpoint);
	}
}
