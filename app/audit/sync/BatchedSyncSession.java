package audit.sync;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import datatransfer.reports.Report;

//This SyncSession is performed in batches and utilized the PersistenceContext's flush strategy to maintain manageable memory
public class BatchedSyncSession<K, T, U> extends SyncSession<K, T, U>{

	
	
	protected Integer remoteFetchOffset = 0;
	protected Integer localFetchOffset = 0;
	
	public void runInserts(){
		List<K> currentBatch = new ArrayList<K>();
		Integer batchSize = Math.min(syncSessionConfig.getRemoteFetchBatchSize(), syncSessionConfig.getPersistBatchSize());

		int index = 0;
		for(K key : syncSessionKeys.getInsertKeys()){
			if(index >= remoteFetchOffset){
				currentBatch.add(key);
				if(currentBatch.size() % batchSize == 0 || index == syncSessionKeys.getInsertKeys().size() - 1){
					runInsertBatch(currentBatch);
					currentBatch.clear();
				}
			}
			index++;
		}
	}
	private void runInsertBatch(Collection<K> keys){
		for(K key : keys) {
			T remoteValue = remoteFetcher.apply(key);
			if(remoteValue == null) {
				failedRemoteFetch.add(key);
				continue;
			} 
			U insertedValue = supplier.get();
			insertedValue = updater.apply(remoteValue, insertedValue);
//			insert(insertedValue);	
		}
		applyPersistBatchStrategy();
	}
	
	public void runUpdates(){
		List<K> currentBatch = new ArrayList<K>();
		Integer batchSize = Math.min(syncSessionConfig.getRemoteFetchBatchSize(), syncSessionConfig.getLocalFetchBatchSize());
		batchSize = Math.min(batchSize, syncSessionConfig.getPersistBatchSize());

		int index = 0;
		for(K key : syncSessionKeys.getUpdateKeys()){
			if(index >= remoteFetchOffset){
				currentBatch.add(key);
				if(currentBatch.size() % batchSize == 0 || index == syncSessionKeys.getUpdateKeys().size() - 1){
					runUpdateBatch(currentBatch);
					currentBatch.clear();
				}
			}
			index++;
		}
	}
	
	private void runUpdateBatch(Collection<K> keys) {
		for(K key : keys) {
			T remoteValue = remoteFetcher.apply(key);
			U localValue = localFetcher.apply(key);
			if(remoteValue == null){
				failedRemoteFetch.add(key);
				continue;
			}
			if(localValue == null) {
				failedLocalFetch.add(key);
				continue;
			}
			U updatedValue = updater.apply(remoteValue, localValue);
//			update(updatedValue);
		}
		applyPersistBatchStrategy();
	}
	
	private void applyPersistBatchStrategy(){
		if(syncSessionConfig.getPersistBatchStrategy() == PersistBatchStrategy.FLUSH){
			persistenceContext.flush();
		} else if(syncSessionConfig.getPersistBatchStrategy() == PersistBatchStrategy.COMMIT){
			persistenceContext.commit();
		}
	}
	@Override
	public void runOutdates() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public List<Report> getReports() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void runSync() {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected U insert(K key) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected U update(K key) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected U outdate(K key) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected void commit() {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
	
}
