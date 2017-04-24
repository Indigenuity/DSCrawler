package crawling.discovery.html;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.impl.client.CloseableHttpClient;

import crawling.HttpFetcher;
import crawling.anansi.UriFetch;
import crawling.discovery.entities.Resource;
import crawling.discovery.execution.ResourceContext;
import crawling.discovery.execution.ResourceWorkOrder;
import crawling.discovery.planning.ResourceFetchTool;

public class HttpFetchTool extends ResourceFetchTool{

	@Override
	public Set<Object> fetchResources(ResourceWorkOrder workOrder, ResourceContext context) throws Exception {
//		System.out.println("Fetching URI : " + workOrder.getSource());
//		CloseableHttpClient httpClient = (CloseableHttpClient) workOrder.getResourceContext().getContextObject("httpClient");
//		UriFetch pageFetch = HttpFetcher.fetchPage((URI)workOrder.getSource(), httpClient);
		Set<Object> resources = new HashSet<Object>();
//		resources.add(new Resource(pageFetch));
		return resources;
	}
	

}
