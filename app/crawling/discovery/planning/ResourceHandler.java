package crawling.discovery.planning;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import crawling.discovery.entities.Resource;
import crawling.discovery.execution.CrawlContext;

public abstract class ResourceHandler<S, R extends Resource<?>> {

	protected final Set<S> crawledSources = new HashSet<S>();
	protected final Set<S> qualifiedSources = new HashSet<S>();
	protected final Set<S> unqualifiedSources = new HashSet<S>();
	protected final Map<S, Exception> failedSources = new HashMap<S, Exception>();
	
	protected final Object sourceMutex = new Object();
	
	protected final CrawlContext context;
	
	public ResourceHandler(CrawlContext context) {
		this.context = context;
	}
	
	/************** sub classes should implement these methods ********************/
	public abstract R fetchResource(S source) throws Exception;
	
	public boolean isValidSource(S source){
		return true;
	}
	
	public void preCrawl(S source){
		
	}
	public void postCrawl(S source){
		
	}
	
	/*************** end sub class encouraged implementations *******************/
	
	public boolean qualifySource(S source){
		synchronized(sourceMutex){
			if(isValidSource(source) && isUncrawled(source)){
				return markQualified(source);
			} 
			markUnqualified(source);
			return false;
		}
	}
	
	public R crawlSource(S source) throws Exception{
		try{
			synchronized(sourceMutex){
				if(!isQualified(source)){
					throw new UnqualifiedSourceException("Unqualified Source : " + source);
				}
				markCrawled(source);
			}
			return fetchResource(source);
		}catch(Exception e){
			markFailed(source, e);
			throw e;
		}
	}
	
	private boolean markCrawled(S source) {
		synchronized(sourceMutex) {
			return this.crawledSources.add(source);
		}
	}
	
	private void markFailed(S source, Exception e){
		synchronized(sourceMutex){
			this.failedSources.put(source, e);
		}
	}
	
	private boolean markUnqualified(S source){
		synchronized(sourceMutex){
			return this.unqualifiedSources.add(source);
		}
	}
	
	private boolean markQualified(S source){
		synchronized(sourceMutex){
			return this.qualifiedSources.add(source);
		}
	}
	
	public boolean isQualified(S source){
		synchronized(sourceMutex){
			return qualifiedSources.contains(source);
		}
	}

	public boolean isUncrawled(S source){
		synchronized(sourceMutex){
			return crawledSources.contains(source);
		}
	}
	public Set<S> crawledSources(){
		synchronized(sourceMutex){
			return new HashSet<S>(crawledSources);
		}
	}
	public Set<S> unqualifiedSources(){
		synchronized(sourceMutex){
			return new HashSet<S>(unqualifiedSources);
		}
	}
	public Set<S> qualifiedSources(){
		synchronized(sourceMutex){
			return new HashSet<S>(qualifiedSources);
		}
	}
	public Map<S, Exception> failedSources(){
		synchronized(sourceMutex){
			return new HashMap<S, Exception>(failedSources);
		}
	}
	
	@SuppressWarnings("serial")
	public static class UnqualifiedSourceException extends RuntimeException{
		public UnqualifiedSourceException(String message){
			super(message);
		}
		
	}
	
}
