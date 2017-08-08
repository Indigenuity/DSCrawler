package crawling.discovery.html;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import crawling.discovery.entities.Resource;
import crawling.discovery.entities.ResourceId;
import crawling.discovery.execution.CrawlContext;
import crawling.discovery.execution.ResourceContext;
import crawling.discovery.local.PageCrawlResource;
import crawling.discovery.planning.PreResource;
import crawling.discovery.planning.ResourceTool;
import crawling.discovery.planning.FetchTool;
import persistence.PageCrawl;
import play.db.jpa.JPA;
import utilities.DSFormatter;
import utilities.HttpUtils;

public class HttpToFileTool implements ResourceTool{
	
	protected static int MAX_FILENAME_LENGTH = 259;		//For windows, it is 259 + a null character. Only 256 if you don't count drive names.

	@Override
	public Object fetchValue(Resource resource, CrawlContext context) throws Exception {
//		System.out.println("fetching from source in HttpToFileTool: " + resource.getSource());
		
		URI currentUri = (URI)resource.getSource();
		ResourceId resourceId = resource.getResourceId();
		File storageFile = findStorageFile(resourceId, currentUri, context);
		//TODO better handle and log errors of httpClient not being initialized
		CloseableHttpClient httpClient = (CloseableHttpClient) context.get("httpClient");
		HttpResponseFile responseFile = fetchResponse(currentUri, storageFile, httpClient);
		
		return responseFile;
	}
	
	protected HttpResponseFile fetchResponse(URI currentUri, File storageFile, CloseableHttpClient httpClient) throws Exception{
		HttpResponseFile responseFile = new HttpResponseFile(currentUri, storageFile);
		HttpGet request = new HttpGet(responseFile.getUri());
		try(CloseableHttpResponse response = httpClient.execute(request);){
			responseFile.setHeaders(response.getAllHeaders());
			responseFile.setLocale(response.getLocale());
			responseFile.setStatusCode(response.getStatusLine().getStatusCode());
			if(HttpUtils.isRedirect(response.getStatusLine().getStatusCode())){
				setRedirectUri(responseFile.getUri(), responseFile, response);
			} else if(HttpUtils.isSuccessful(response.getStatusLine().getStatusCode())){
				recordResponse(storageFile, response.getEntity().getContent());
			} else {
			}
		}
		return responseFile;
	}
	
	protected void recordResponse(File storageFile, InputStream content) throws IOException{
		IOUtils.copy(content, new FileOutputStream(storageFile));
	}
	
	@Override
	public Resource generateResource(Object source, Resource parent, ResourceId resourceId, CrawlContext context) throws Exception{
		return new Resource(source, parent, resourceId);
	}
	
	@Override
	public Resource generateResource(PreResource preResource, Resource parent, ResourceId resourceId , CrawlContext context) throws Exception{
		return new Resource(preResource, parent, resourceId);
	}
	
	protected void setRedirectUri(URI currentUri, HttpResponseFile responseFile, CloseableHttpResponse response){
		String redirectUriString = null;
		try {
			redirectUriString = response.getFirstHeader("Location").getValue();
			URI redirectedUri = new URI(redirectUriString);
			if(!redirectedUri.isAbsolute()){
				redirectedUri = currentUri.resolve(redirectedUri);	
			}
			responseFile.setRedirectedUri(redirectedUri);
		} catch(URISyntaxException e) {
			throw new IllegalStateException("Received redirect with invalid Location : " + redirectUriString);
		} catch(NullPointerException e){
			throw new IllegalStateException("Received redirect with no Location.");
		}
	}
	
	protected File findStorageFile(ResourceId resourceId, URI uri, CrawlContext context) throws IOException {
		File crawlStorageFolder = (File) context.get("crawlStorageFolder");
		String resourceIdString = resourceId.toString();
		if(crawlStorageFolder.getAbsolutePath().length() + resourceIdString.length() > MAX_FILENAME_LENGTH){		//Leave enough room for at least the resourceId as filename
			throw new IOException("Cannot generate file when storage folder name is too long : " + crawlStorageFolder.getAbsolutePath());
		}
		String filename = constructFilename(resourceIdString, uri);
		filename = crawlStorageFolder.getAbsolutePath() + "/" + filename;
		filename = truncate(filename, MAX_FILENAME_LENGTH);
		return new File(filename);
	}
	
	protected String constructFilename(String resourceIdString, URI uri) throws UnsupportedEncodingException{
		String path = uri.getPath();
		String query = uri.getQuery();
		String pathAndQuery = path;
		if(query != null){
			pathAndQuery += "?" + query;
		}
		return URLEncoder.encode(resourceIdString + "-" + pathAndQuery, "UTF-8");
	}
	
	protected File makeFile(File file) throws IOException {
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
