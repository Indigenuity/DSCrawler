package audit.map;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import audit.sync.PersistenceContext;

public abstract class MapSession<T, U> {

	protected PersistenceContext persistenceContext;
	
	protected Class<T> keyClazz;
	protected Class<U> valueClazz;
	
	protected Supplier<T> keySupplier;
	protected Function<T, U> valueCreator;
	protected BiFunction<T, U, T> assigner;
	protected Function<T, U> valueFetcher;

	protected MapSession(Class<T> keyClazz, Class<U> valueClazz){
		this.keyClazz = keyClazz;
		this.valueClazz = valueClazz;
	}
	
	public abstract void runAssignments();
	
	protected void preCommit() {
		
	}
	
	protected  void commit(){
		persistenceContext.commit();
	}
	
	protected void flush() {
		persistenceContext.flush();
	}

	public PersistenceContext getPersistenceContext() {
		return persistenceContext;
	}

	public void setPersistenceContext(PersistenceContext persistenceContext) {
		this.persistenceContext = persistenceContext;
	}

	public Class<T> getKeyClazz() {
		return keyClazz;
	}

	public Class<U> getValueClazz() {
		return valueClazz;
	}

	public Supplier<T> getKeySupplier() {
		return keySupplier;
	}

	public void setKeySupplier(Supplier<T> keySupplier) {
		this.keySupplier = keySupplier;
	}

	public Function<T, U> getValueCreator() {
		return valueCreator;
	}

	public void setValueCreator(Function<T, U> valueCreator) {
		this.valueCreator = valueCreator;
	}

	public BiFunction<T, U, T> getAssigner() {
		return assigner;
	}

	public void setAssigner(BiFunction<T, U, T> assigner) {
		this.assigner = assigner;
	}

	public Function<T, U> getAssignmentFetcher() {
		return valueFetcher;
	}

	public void setAssignmentFetcher(Function<T, U> assignmentFetcher) {
		this.valueFetcher = assignmentFetcher;
	}
	
	
}
