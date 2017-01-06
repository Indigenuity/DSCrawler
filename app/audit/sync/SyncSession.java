package audit.sync;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import datatransfer.reports.Report;

public abstract class SyncSession<K, T, U> {
	
	protected PersistenceContext persistenceContext;
	
	protected Class<K> keyClazz;
	protected Class<T> remoteClazz;
	protected Class<U> localClazz;
	
	protected Supplier<U> supplier;
	protected BiFunction<T, U, U> updater;
	protected Function<U, U> outdater;
	protected Function<K, T> remoteFetcher = (key) -> persistenceContext.fetch(key, remoteClazz);
	protected Function<K, U> localFetcher = (key) -> persistenceContext.fetch(key, localClazz);
	
	protected SyncSessionKeys<K> syncSessionKeys;
	protected SyncSessionConfig syncSessionConfig = new SyncSessionConfig();
	
	protected Set<K> failedRemoteFetch = new HashSet<K>();
	protected Set<K> failedLocalFetch = new HashSet<K>();
	
	public abstract void runSync();
	
	public abstract void runInserts();
	public abstract void runUpdates();
	public abstract void runOutdates();
	
	public abstract List<Report> getReports();
	
	protected abstract U insert(K key);
	protected abstract U update(K key);
	protected abstract U outdate(K key);
	
	protected abstract void commit();
	
	
	
	
	
	protected void preCommit() {
		System.out.println("in original");
	}

	public PersistenceContext getPersistenceContext() {
		return persistenceContext;
	}

	public void setPersistenceContext(PersistenceContext persistenceContext) {
		this.persistenceContext = persistenceContext;
	}

	public Class<K> getKeyClazz() {
		return keyClazz;
	}

	public Class<T> getRemoteClazz() {
		return remoteClazz;
	}

	public Class<U> getLocalClazz() {
		return localClazz;
	}

	public Supplier<U> getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier<U> supplier) {
		this.supplier = supplier;
	}

	public BiFunction<T, U, U> getUpdater() {
		return updater;
	}

	public void setUpdater(BiFunction<T, U, U> updater) {
		this.updater = updater;
	}

	public Function<U, U> getOutdater() {
		return outdater;
	}

	public void setOutdater(Function<U, U> outdater) {
		this.outdater = outdater;
	}

	public Function<K, T> getRemoteFetcher() {
		return remoteFetcher;
	}

	public void setRemoteFetcher(Function<K, T> remoteFetcher) {
		this.remoteFetcher = remoteFetcher;
	}

	public Function<K, U> getLocalFetcher() {
		return localFetcher;
	}

	public void setLocalFetcher(Function<K, U> localFetcher) {
		this.localFetcher = localFetcher;
	}

	public SyncSessionKeys<K> getSyncSessionKeys() {
		return syncSessionKeys;
	}

	public void setSyncSessionKeys(SyncSessionKeys<K> syncSessionKeys) {
		this.syncSessionKeys = syncSessionKeys;
	}

	public SyncSessionConfig getSyncSessionConfig() {
		return syncSessionConfig;
	}

	public void setSyncSessionConfig(SyncSessionConfig syncSessionConfig) {
		this.syncSessionConfig = syncSessionConfig;
	}

	public Set<K> getFailedRemoteFetch() {
		return failedRemoteFetch;
	}

	public Set<K> getFailedLocalFetch() {
		return failedLocalFetch;
	}
}
