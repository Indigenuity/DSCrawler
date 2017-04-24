package crawling.discovery.html;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
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
import utilities.HttpUtils;

public class HttpToFileTool extends ResourceFetchTool{
	
	protected static int MAX_FILENAME_LENGTH = 259;		//For windows, it is 259 + a null character. Only 256 if you don't count drive names.

	@Override
	public Set<Object> fetchResources(ResourceWorkOrder workOrder, ResourceContext context) throws Exception {
		System.out.println("fetching resource : " + workOrder.getSource());
		
		Set<Object> resources = new HashSet<Object>();
		URI currentUri = (URI)workOrder.getSource();
		ResourceId resourceId = context.getNextResourceId();
		File storageFile = generateFile(resourceId, currentUri, context);
		HttpResponseFile responseFile = new HttpResponseFile(currentUri, storageFile);
		
		CloseableHttpClient httpClient = (CloseableHttpClient) context.getContextObject("httpClient");
		HttpGet request = new HttpGet(currentUri);
		try(CloseableHttpResponse response = httpClient.execute(request);){
			responseFile.setHeaders(response.getAllHeaders());
			responseFile.setLocale(response.getLocale());
			responseFile.setStatusCode(response.getStatusLine().getStatusCode());
			if(HttpUtils.isRedirect(response.getStatusLine().getStatusCode())){
				setRedirectUri(currentUri, responseFile, response);
			} else {
				IOUtils.copy(response.getEntity().getContent(), new FileOutputStream(storageFile));
			}
			resources.add(responseFile);
		}
		
		return resources;
	}
	
	protected void setRedirectUri(URI currentUri, HttpResponseFile responseFile, CloseableHttpResponse response){
		try {
			String redirectUriString = response.getFirstHeader("Location").getValue();
			URI redirectedUri = new URI(redirectUriString);
			if(!redirectedUri.isAbsolute()){
				redirectedUri = currentUri.resolve(redirectedUri);	
			}
			responseFile.setRedirectedUri(redirectedUri);
		} catch (URISyntaxException | NullPointerException e) {
			throw new IllegalStateException("Received redirect without a valid Location");
		}
	}
	
	protected File generateFile(ResourceId resourceId, URI uri, ResourceContext context) throws IOException {
		File crawlStorageFolder = (File) context.getContextObject("crawlStorageFolder");
		String resourceIdString = resourceId.toString();
		if(crawlStorageFolder.getAbsolutePath().length() + resourceIdString.length() > MAX_FILENAME_LENGTH){		//Leave enough room for at least the resourceId as filename
			throw new IOException("Cannot generate file when storage folder name is too long : " + crawlStorageFolder.getAbsolutePath());
		}
		String filename = constructFilename(resourceIdString, uri);
		filename = crawlStorageFolder.getAbsolutePath() + "/" + filename;
		filename = truncate(filename, MAX_FILENAME_LENGTH);
		File file = new File(filename);
		return makeFile(crawlStorageFolder, file);
	}
	
	protected String constructFilename(String resourceIdString, URI uri) throws UnsupportedEncodingException{
		String path = uri.getPath();
		String query = uri.getQuery();
		String pathAndQuery = path;
		if(query != null){
			pathAndQuery = "?" + query;
		}
		return URLEncoder.encode(resourceIdString + "-" + pathAndQuery, "UTF-8");
	}
	
	protected File makeFile(File storageFolder, File file) throws IOException {
		file.createNewFile();
		return file;
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
