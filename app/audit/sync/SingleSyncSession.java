package audit.sync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import datatransfer.reports.Report;
import datatransfer.reports.ReportFactory;
import datatransfer.reports.ReportRow;

/* This class is for use when the SyncSession shouldn't care about memory management.  It assumes everything is in memory at once.
 * SingleSyncSessions are not made to be persisted.  They are single-shot syncs.  For later tracking of what was edited,
 * the Sync instance sync should be saved whenever a commit is made
 */

public class SingleSyncSession<K, T, U> extends SyncSession<K, T, U> {
	
	protected Map<K, ReportRow> insertedRows = new HashMap<K, ReportRow>();
	protected Map<K, ReportRow> beforeUpdateRows = new HashMap<K, ReportRow>();
	protected Map<K, ReportRow> afterUpdateRows = new HashMap<K, ReportRow>();
	protected Map<K, ReportRow> outdatedRows = new HashMap<K, ReportRow>();
	
	@Override
	public void runSync(){
		runInserts();
		runUpdates();
		runOutdates();
		preCommit();
		commit();
	}
	
	@Override
	public void runInserts() {
		for(K key : syncSessionKeys.getInsertKeys()){
			insert(key);
		}
	}

	@Override
	public void runUpdates() {
		for(K key : syncSessionKeys.getUpdateKeys()){
			update(key);
		}
	}

	@Override
	public void runOutdates() {
		for(K key : syncSessionKeys.getOutdateKeys()){
			outdate(key);
		}
	}
	
	@Override
	public List<Report> getReports() {
		List<Report> reports = new ArrayList<Report>();
		reports.add(ReportFactory.fromGenericKeyMap(insertedRows).setName(localClazz.getSimpleName() + " inserted"));
		reports.add(ReportFactory.fromGenericKeyMap(beforeUpdateRows).setName(localClazz.getSimpleName() + " before update"));
		reports.add(ReportFactory.fromGenericKeyMap(afterUpdateRows).setName(localClazz.getSimpleName() + " after update"));
		reports.add(ReportFactory.fromGenericKeyMap(outdatedRows).setName(localClazz.getSimpleName() + " outdated"));
		return reports;
	}
	
	@Override
	protected U insert(K key){
		T remoteValue = remoteFetcher.apply(key);
		if(remoteValue == null) {
			failedRemoteFetch.add(key);
			return null;
		}
		U localValue = supplier.get();
		localValue = updater.apply(remoteValue, localValue);
		localValue = persistenceContext.insert(localValue);
		insertedRows.put(key, ReportFactory.fromObject(localValue));
		return localValue;
	}
	
	@Override
	protected U update(K key){
		T remoteValue = remoteFetcher.apply(key);
		U localValue = localFetcher.apply(key);
		if(remoteValue == null) {
			failedRemoteFetch.add(key);
			return null;
		}
		if(localValue == null) {
			failedLocalFetch.add(key);
			return null;
		}
		beforeUpdateRows.put(key, ReportFactory.fromObject(localValue));
		localValue = updater.apply(remoteValue, localValue);
		localValue = persistenceContext.update(localValue);
		afterUpdateRows.put(key, ReportFactory.fromObject(localValue));
		return localValue;
	}
	
	@Override
	protected U outdate(K key){
		U localValue = localFetcher.apply(key);
		if(localValue == null) {
			failedLocalFetch.add(key);
		}
		localValue = outdater.apply(localValue);
		localValue = persistenceContext.outdate(localValue);
		outdatedRows.put(key, ReportFactory.fromObject(localValue));
		return localValue;
	}

	@Override
	protected void commit() {
		persistenceContext.commit();
	}

	

}
