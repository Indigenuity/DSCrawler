package audit.sync;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SyncSessionKeys<K> {

	private final Set<K> remoteKeys;
	private final Set<K> localKeys;
	
	private final Set<K> insertKeys;
	private final Set<K> updateKeys;
	private final Set<K> outdateKeys;
	
	public SyncSessionKeys(Set<K> remoteKeys, Set<K> localKeys){
		System.out.print("Generating key sets for SyncSession....");
		this.remoteKeys = remoteKeys;
		this.localKeys = localKeys;
		
		insertKeys = remoteKeys.stream()
				.filter( (key) -> !localKeys.contains(key))
				.collect(Collectors.toSet());
		updateKeys = remoteKeys.stream()
				.filter(localKeys::contains)
				.collect(Collectors.toSet());
		outdateKeys = localKeys.stream()
				.filter( (key) -> !remoteKeys.contains(key))
				.collect(Collectors.toSet());
		
		System.out.println("Finished generating key sets");
	}

	public Set<K> getRemoteKeys() {
		return new HashSet<K>(remoteKeys);
	}

	public Set<K> getLocalKeys() {
		return new HashSet<K>(localKeys);
	}

	public Set<K> getInsertKeys() {
		return new HashSet<K>(insertKeys);
	}

	public Set<K> getUpdateKeys() {
		return new HashSet<K>(updateKeys);
	}

	public Set<K> getOutdateKeys() {
		return new HashSet<K>(outdateKeys);
	}
	
	
}
