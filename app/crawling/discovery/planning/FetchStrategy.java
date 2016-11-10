package crawling.discovery.planning;

import java.util.function.Function;

import crawling.discovery.entities.Endpoint;

public interface FetchStrategy<T extends Endpoint, R> extends Function<T, R>{

	public R fetch(T endpoint);
	
	@Override
	default R apply(T endpoint) {
		return fetch(endpoint);
	}
}
