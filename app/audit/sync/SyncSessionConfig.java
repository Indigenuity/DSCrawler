package audit.sync;

public class SyncSessionConfig {

	protected Integer remoteFetchBatchSize = 20;
	protected Integer localFetchBatchSize = 20;
	protected Integer persistBatchSize = 20;
	
	protected PersistBatchStrategy persistBatchStrategy = PersistBatchStrategy.FLUSH;

	public Integer getRemoteFetchBatchSize() {
		return remoteFetchBatchSize;
	}

	public void setRemoteFetchBatchSize(Integer remoteFetchBatchSize) {
		this.remoteFetchBatchSize = remoteFetchBatchSize;
	}

	public Integer getLocalFetchBatchSize() {
		return localFetchBatchSize;
	}

	public void setLocalFetchBatchSize(Integer localFetchBatchSize) {
		this.localFetchBatchSize = localFetchBatchSize;
	}

	public Integer getPersistBatchSize() {
		return persistBatchSize;
	}

	public void setPersistBatchSize(Integer persistFetchBatchSize) {
		this.persistBatchSize = persistFetchBatchSize;
	}

	public PersistBatchStrategy getPersistBatchStrategy() {
		return persistBatchStrategy;
	}

	public void setPersistBatchStrategy(PersistBatchStrategy persistBatchStrategy) {
		this.persistBatchStrategy = persistBatchStrategy;
	}
	
	
}
