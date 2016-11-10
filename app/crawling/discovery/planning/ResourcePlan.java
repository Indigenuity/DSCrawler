package crawling.discovery.planning;


public class ResourcePlan<T, R> {

	protected final DerivationStrategy<T, R> derivationStrategy;
	protected final PersistStrategy<R> persistStrategy;
	
	public ResourcePlan(DerivationStrategy<T, R> derivationStrategy, PersistStrategy<R> persistStrategy) {
		super();
		this.derivationStrategy = derivationStrategy;
		this.persistStrategy = persistStrategy;
	}
	
	public DerivationStrategy<T, R> getDerivationStrategy(){
		return derivationStrategy;
	}
	
	public PersistStrategy<R> getPersistStrategy() {
		return persistStrategy;
	}
	
	
}
