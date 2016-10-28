package crawling.discovery;

import java.util.Map;

public class ResourcePlan<T, R> {

	protected DerivationStrategy<T, R> derivationStrategy;
	protected PersistStrategy<R> persistStrategy;
	
	public DerivationStrategy<T, R> getDerivationStrategy(){
		return derivationStrategy;
	}
	public ResourcePlan<T, R> setDerivationStrategy(DerivationStrategy<T, R> strategy){
		this.derivationStrategy = strategy;
		return this;
	}
	public PersistStrategy<R> getPersistStrategy() {
		return persistStrategy;
	}
	public ResourcePlan<T, R> setPersistStrategy(PersistStrategy<R> persistStrategy) {
		this.persistStrategy = persistStrategy;
		return this;
	}
	
	
}
