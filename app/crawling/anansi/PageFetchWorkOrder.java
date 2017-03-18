package crawling.anansi;

import java.io.IOException;
import java.net.URI;
import java.util.function.Function;

import org.apache.http.impl.client.CloseableHttpClient;

import async.functionalwork.FunctionWorkOrder;
import crawling.HttpFetcher;

public class PageFetchWorkOrder extends FunctionWorkOrder<URI, UriFetch>{

	public PageFetchWorkOrder(Function<URI, UriFetch> function, URI input) {
		super(function, input);
		// TODO Auto-generated constructor stub
	}


//	public PageFetchWorkOrder(URI input, CloseableHttpClient httpClient) {
//		super((uri) -> {
//			try {
//				return HttpFetcher.fetchUri(uri, httpClient);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}, input);
//	}
//	
//	@Override
//	public PageFetchWorkResult doWork() {
//		return new PageFetchWorkResult(this, function.apply(input));
//	}

}
