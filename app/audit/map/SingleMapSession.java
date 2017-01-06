package audit.map;

import audit.sync.Sync;

//Intended to run all in memory with no intermediate flushes
public class SingleMapSession<T, U> extends MapSession<T, U> {

	protected SingleMapSession(Class<T> keyClazz, Class<U> valueClazz) {
		super(keyClazz, valueClazz);
		this.sync = new Sync();
	}

	@Override
	public void runAssignments(){ 
		T key;
		while((key = keySupplier.get()) != null){
			U value = valueFetcher.apply(key);
			if(value == null) {
				value = valueCreator.apply(key);
				if(value == null) {		//Value can still be null if there is no valid mapping for the given key
					continue;
				}
				value = persistenceContext.insert(value);
			}
			key = assigner.apply(key, value);
			persistenceContext.update(key);
			flush();
		}
		preCommit();
		commit();
	}
}
