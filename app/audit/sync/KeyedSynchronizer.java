package audit.sync;

import java.util.Set;
import java.util.stream.Collectors;

public abstract class KeyedSynchronizer<K, L, R> {
	
	public abstract L getLocal(K key);
	public abstract R getRemote(K key);
	
	public abstract void update(L local, R remote);
	public abstract void insert(R remote);
	public abstract void outdate(L local);
	
	public void updateByKey(K key){
		update(getLocal(key), getRemote(key));
	}
	
	public void insertByKey(K key) {
		insert(getRemote(key));
	}
	
	public void outdateByKey(K key) {
		outdate(getLocal(key));
	}
	
	public void insertAllKeys(Set<K> remoteKeys){
		remoteKeys.forEach(this::insertByKey);
	}
	
	public void updateAllKeys(Set<K> keys){
		keys.forEach(this::updateByKey);
	}
	
	public void outdateAllKeys(Set<K> localKeys){
		localKeys.forEach(this::outdateByKey);
	}
	
	public void sync(Set<K> localKeys, Set<K> remoteKeys){
		insertAllKeys(remoteKeys.stream()
				.filter((key) -> !localKeys.contains(key))
				.collect(Collectors.toSet()));
		updateAllKeys(remoteKeys.stream()
				.filter(localKeys::contains)
				.collect(Collectors.toSet()));
		outdateAllKeys(localKeys.stream()
				.filter( (key) -> !remoteKeys.contains(key))
				.collect(Collectors.toSet()));
	}
}
