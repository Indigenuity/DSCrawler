package crawling.discovery.planning;

import java.util.ArrayList;
import java.util.List;

import com.google.common.util.concurrent.RateLimiter;

public class ResourceConfig extends Config {
	
	public static final float DEFAULT_PERMITS_PER_SECOND = Float.MAX_VALUE;
	
	protected final List<DiscoveryConfig> discoveryConfigs = new ArrayList<DiscoveryConfig>();
	
	
	protected float permitsPerSecond = DEFAULT_PERMITS_PER_SECOND;
	
	public boolean addDiscoveryConfig(DiscoveryConfig config){
		return this.discoveryConfigs.add(config);
	}

}
