package crawling.discovery.html;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HttpResponseFile implements HtmlResource{

	public static int MAX_FILENAME_LENGTH = 260;
	
	private URI uri;
	private File file;
	private Locale locale;
	private Header[] headers;
	private int statusCode;
	
	private HttpResponseFile(){}
	
	public static HttpResponseFile create(URI uri, File storageFile, HttpResponse response) throws IOException{
		IOUtils.copy(response.getEntity().getContent(), new FileOutputStream(storageFile));
		HttpResponseFile responseFile = new HttpResponseFile();
		responseFile.setFile(storageFile);
		responseFile.setUri(uri);
		responseFile.setLocale(response.getLocale());
		responseFile.setHeaders(response.getAllHeaders());
		responseFile.setStatusCode(response.getStatusLine().getStatusCode());
		return responseFile;
	}
	
	public URI getUri() {
		return uri;
	}

	private void setUri(URI uri) {
		this.uri = uri;
	}

	public File getFile() {
		return file;
	}

	private void setFile(File file) {
		this.file = file;
	}

	public Locale getLocale() {
		return locale;
	}

	private void setLocale(Locale locale) {
		this.locale = locale;
	}

	public Header[] getHeaders() {
		return headers;
	}

	private void setHeaders(Header[] headers) {
		this.headers = headers;
	}

	public int getStatusCode() {
		return statusCode;
	}

	private void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	@Override
	public Document getDocument() throws Exception{
		String text = IOUtils.toString(new FileInputStream(file), "UTF-8");
		Document doc = Jsoup.parse(text);
		doc.setBaseUri(HtmlUtils.findBaseUriString(uri));
		return doc;
	}
}
