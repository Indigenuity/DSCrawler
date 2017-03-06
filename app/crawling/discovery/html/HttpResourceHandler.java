package crawling.discovery.html;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.impl.client.CloseableHttpClient;

import crawling.HttpFetcher;
import crawling.anansi.PageFetch;
import crawling.discovery.entities.Resource;
import crawling.discovery.execution.CrawlContext;
import crawling.discovery.planning.ResourceHandler;

public class HttpResourceHandler extends ResourceHandler<URI, Resource<PageFetch>> {

	private HttpConfig config;
	private CloseableHttpClient httpClient;
	
	public HttpResourceHandler(CrawlContext context, HttpConfig config) {
		super(context);
		this.config = config;
		this.httpClient = this.config.buildHttpClient();
	}
	
	@Override
	public Resource<PageFetch> fetchResource(URI source) throws Exception{
		PageFetch pageFetch = HttpFetcher.fetchPage(source, httpClient);
		return new Resource<PageFetch>(pageFetch);
	}

	@Override
	public boolean isValidSource(URI source) {
		// TODO Auto-generated method stub
		return super.isValidSource(source);
	}

	@Override
	public void preCrawl(URI source) {
		// TODO Auto-generated method stub
		super.preCrawl(source);
	}
	

}
