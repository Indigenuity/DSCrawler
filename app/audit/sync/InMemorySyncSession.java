package audit.sync;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class InMemorySyncSession<K, T, U> {
	
	private Map<K, T> remote;
	private Map<K, U> local;
	private BiFunction<T, U, U> updater;
	private Supplier<U> supplier;
	
	public InMemorySyncSession(Map<K, T> remote, Map<K, U> local, BiFunction<T, U, U> updater, Supplier<U> supplier){
		this.remote = remote;
		this.local = local;
		this.updater = updater;
		this.supplier = supplier;
	}
	
	public List<U> added(){
		return remote.keySet().stream()
				.filter( (key) -> {
					return !local.containsKey(key);
				})
				.map(remote::get)
				.map((element) -> {
					return updater.apply(element, supplier.get());
				})
				.collect(Collectors.toList());
	}
	
	public List<U> modified(){
		return remote.keySet().stream()
				.filter(local::containsKey)
				.map((key) -> {
					return updater.apply(remote.get(key), local.get(key));
				})
				.collect(Collectors.toList());
	}
	
	public List<U> notFound() {
		return local.keySet().stream()
				.filter( (key) -> {
					return !remote.containsKey(key);
				})
				.map(local::get)
				.collect(Collectors.toList());
	}
	
}
