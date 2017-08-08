package crawling.discovery.planning;

import com.google.common.util.concurrent.RateLimiter;

public class ContextWithResourcesPlan extends ContextPlan {
	
	public final static int DEFAULT_MAX_DEPTH_OF_CRAWLING = Integer.MAX_VALUE;
	public final static int DEFAULT_MAX_PAGES_TO_FETCH = Integer.MAX_VALUE;
	
	protected RateLimiter rateLimiter = null;
	
	protected int maxDepth;
	protected int maxFetches;

	public RateLimiter getRateLimiter() {
		return rateLimiter;
	}
	public void setRateLimiter(RateLimiter rateLimiter) {
		this.rateLimiter = rateLimiter;
	}
	public int getMaxDepth() {
		return maxDepth;
	}
	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}
	public int getMaxFetches() {
		return maxFetches;
	}
	public void setMaxFetches(int maxFetches) {
		this.maxFetches = maxFetches;
	}
}
