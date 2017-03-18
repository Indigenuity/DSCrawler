package crawling.discovery.entities;

import java.util.HashSet;
import java.util.Set;

import com.google.common.util.concurrent.RateLimiter;


public class SourcePool {
	protected final Set<Object> knownSources = new HashSet<Object>();
	protected final Set<Object> crawledSources = new HashSet<Object>();
	protected RateLimiter rateLimiter;
	
	public boolean isKnown(Object source){
		synchronized(knownSources){
			return knownSources.contains(source);
		}
	}
	
	public boolean discover(Object source){
		synchronized(knownSources){
			return knownSources.add(source);
		}
	}
	
	public boolean acquireCrawlPermit(Object source){
		if(!crawledSources.add(source)){
			return false;
		}
		if(rateLimiter != null){
			rateLimiter.acquire();
		}
		return true;
	}
	
	public void setRateLimiter(RateLimiter rateLimiter) {
		this.rateLimiter = rateLimiter;
	}
}
