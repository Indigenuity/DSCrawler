package crawling.anansi;

import java.net.URI;

import org.apache.http.impl.client.CloseableHttpClient;

import async.functionalwork.FunctionWorkOrder;
import crawling.HttpFetcher;

public class PageFetchWorkOrder extends FunctionWorkOrder<URI, PageFetch>{

	public PageFetchWorkOrder(URI input, CloseableHttpClient httpClient) {
		super((uri) -> HttpFetcher.fetchPage(uri, httpClient), input);
	}
	
	@Override
	public PageFetchWorkResult doWork() {
		return new PageFetchWorkResult(this, function.apply(input));
	}

}
