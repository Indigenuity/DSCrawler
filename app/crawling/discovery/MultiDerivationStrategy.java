package crawling.discovery;

import java.util.Collection;
import java.util.List;

import crawling.discovery.planning.DerivationStrategy;

public interface MultiDerivationStrategy<T, R> extends DerivationStrategy<T, Collection<R>>{

}
