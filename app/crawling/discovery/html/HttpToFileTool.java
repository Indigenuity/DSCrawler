package crawling.discovery.html;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;

import crawling.HttpFetcher;
import crawling.anansi.UriFetch;
import crawling.discovery.entities.Resource;
import crawling.discovery.entities.ResourceId;
import crawling.discovery.execution.ResourceContext;
import crawling.discovery.execution.ResourceWorkOrder;
import crawling.discovery.execution.ResourceWorkResult;
import crawling.discovery.planning.ResourceFetchTool;
import persistence.PageCrawl;
import persistence.SiteCrawl;
import play.Logger;
import play.db.jpa.JPA;

public class HttpToFileTool extends ResourceFetchTool{
	
	protected static int MAX_FILENAME_LENGTH = 259;		//For windows, it is 259 + a null character. Only 256 if you don't count drive names.

	@Override
	public Set<Resource> fetchResource(ResourceWorkOrder workOrder, ResourceContext context) throws Exception {
		Set<Resource> resources = new HashSet<Resource>();
		CloseableHttpClient httpClient = (CloseableHttpClient) workOrder.getResourceContext().getContextObject("httpClient");
		try(CloseableHttpResponse response = HttpFetcher.fetchUriRaw((URI)workOrder.getSource(), httpClient)){
			ResourceId resourceId = context.getNextResourceId();
			File storageFile = generateFile(resourceId, workOrder, context);
			HttpResponseFile responseFile = HttpResponseFile.create((URI)workOrder.getSource(), storageFile, response);
			Resource resource = new Resource(responseFile, resourceId);
			resources.add(resource);
		}
		
		return resources;
	}
	
	
	
	@Override
	public void postFetch(ResourceWorkResult workResult, ResourceContext context) throws Exception {
		// TODO Auto-generated method stub
		super.postFetch(workResult, context);
		workResult.getResources().forEach((resource) -> {
			HttpResponseFile responseFile = (HttpResponseFile) resource.getValue();
			JPA.withTransaction(() -> {
				PageCrawl pageCrawl = new PageCrawl();
				pageCrawl.setStatusCode(responseFile.getStatusCode());
				pageCrawl.setUrl(responseFile.getUri().toString());
				pageCrawl.setFilename(responseFile.getFile().getAbsolutePath());
				SiteCrawl siteCrawl = JPA.em().getReference(SiteCrawl.class, context.getContextObject("siteCrawlId"));
				pageCrawl.setSiteCrawl(siteCrawl);
			});
		});
		
	}



	protected File generateFile(ResourceId resourceId, ResourceWorkOrder workOrder, ResourceContext context) throws IOException {
		URI uri = (URI) workOrder.getSource();
		File crawlStorageFolder = (File) context.getContextObject("crawlStorageFolder");
		File hostStorageFolder = new File(crawlStorageFolder, URLEncoder.encode(uri.getHost(), "UTF-8"));
		String resourceIdString = resourceId.toString();
		if(hostStorageFolder.getAbsolutePath().length() > MAX_FILENAME_LENGTH - resourceIdString.length()){		//Leave enough room for at least the resourceId as filename
			throw new IOException("Cannot generate file when storage folder name is too long : " + hostStorageFolder.getAbsolutePath());
		}
		if(!hostStorageFolder.exists()){
			hostStorageFolder.mkdirs();
		}
		String path = uri.getPath();
		String query = uri.getQuery();
		String pathAndQuery = path;
		if(query != null){
			pathAndQuery = "?" + query;
		}
		String pathAndQueryEncoded = URLEncoder.encode(resourceId + pathAndQuery, "UTF-8");
		String filename = hostStorageFolder.getAbsolutePath() + "/" + pathAndQueryEncoded;
		return new File(truncate(filename, MAX_FILENAME_LENGTH));
			
	}
	
	public static String truncate(String original, int length) {
		if(original == null || !needsTruncation(original, length))
			return original;
		return original.substring(0, length); 
	}
	
	public static boolean needsTruncation(String original, int length) {
		if(original == null || original.length() <= length)
			return false;
		return true;
	}

}
