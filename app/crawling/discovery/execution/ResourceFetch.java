package crawling.discovery.execution;

import crawling.discovery.entities.Resource;

public class ResourceFetch<T, U extends Resource> {

	private T source;
	private U resource;
	private FetchStatus fetchStatus;
	public T getSource() {
		return source;
	}
	public void setSource(T source) {
		this.source = source;
	}
	public U getResource() {
		return resource;
	}
	public void setResource(U resource) {
		this.resource = resource;
	}
	public FetchStatus getFetchStatus() {
		return fetchStatus;
	}
	public void setFetchStatus(FetchStatus fetchStatus) {
		this.fetchStatus = fetchStatus;
	}
	
	
	
}
