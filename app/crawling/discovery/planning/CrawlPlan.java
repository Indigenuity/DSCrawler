package crawling.discovery.planning;

import java.util.ArrayList;
import java.util.List;

public class CrawlPlan {

//	protected List<FetchPlan<?, ?>> fetchPlans = new ArrayList<FetchPlan<?, ?>>();
	
	protected final List<PrimaryResourcePlan<?>> resourcePlans = new ArrayList<PrimaryResourcePlan<?>>();
}
