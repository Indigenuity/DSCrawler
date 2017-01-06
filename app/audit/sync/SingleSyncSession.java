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
	
	protected Sync sync;
	
	public SingleSyncSession(){
		super();
		this.sync = new Sync();
	}
	
	public Sync getSync() {
		return sync;
	}

	public void setSync(Sync sync) {
		this.sync = sync;
	}
	
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
		System.out.println("Running inserts : " + syncSessionKeys.getInsertKeys().size());
		int count = 0;
		for(K key : syncSessionKeys.getInsertKeys()){
			insert(key);
			if(count++ %100 == 0){
				System.out.println("Inserted : " + count);
			}
		}
	}

	@Override
	public void runUpdates() {
		System.out.println("Running updates: " + syncSessionKeys.getUpdateKeys().size());
		int count = 0;
		for(K key : syncSessionKeys.getUpdateKeys()){
			update(key);
			if(count++ %100 == 0){
				System.out.println("Updated : " + count);
			}
		}
	}

	@Override
	public void runOutdates() {
		System.out.println("Running outdates : " + syncSessionKeys.getOutdateKeys().size());
		int count = 0;
		for(K key : syncSessionKeys.getOutdateKeys()){
			outdate(key);
			if(count++ %100 == 0){
				System.out.println("Outdated : " + count);
			}
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
		System.out.println("Committing");
		this.sync = persistenceContext.insert(sync);
		persistenceContext.commit();
	}

	

}
