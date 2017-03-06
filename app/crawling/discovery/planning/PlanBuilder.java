package crawling.discovery.planning;

import java.util.function.Function;

import crawling.discovery.entities.Resource;
import crawling.discovery.execution.CrawlContext;

public class PlanBuilder<S, R extends Resource<?>> {
	
	private DerivationStrategy<S, R> derivationStrategy;
	private Function<S, Boolean> qualificationStrategy = (s) -> true;

	private PlanBuilder(DerivationStrategy<S, R> derivationStrategy){
		this.derivationStrategy = derivationStrategy;
	}
	
	public static <S, R extends Resource<?>> PlanBuilder<S, R> create(DerivationStrategy<S, R> derivationStrategy){
		return new PlanBuilder<S, R>(derivationStrategy);
	}
	
	public ResourceHandler<S, R> build(CrawlContext context){
		ResourceHandler<S, R> resourcePlan = new ResourceHandler<S, R>(context) {

			@Override
			public R fetchResource(S source) {
				return derivationStrategy.derive(source);
			}

			@Override
			public boolean qualifySource(S source) {
				return qualificationStrategy.apply(source);
			}
			
		};
		return resourcePlan;
	}
	
	
}
