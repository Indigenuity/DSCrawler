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
	private URI redirectedUri;
	private File file;
	private Locale locale;
	private Header[] headers;
	private int statusCode;
	
	public  HttpResponseFile(URI uri, File storageFile){
		this.uri = uri;
		this.file = storageFile;
	}
	
	@Override
	public Document getDocument() throws Exception{
		String text = IOUtils.toString(new FileInputStream(file), "UTF-8");
		Document doc = Jsoup.parse(text);
		doc.setBaseUri(uri.toString());
		return doc;
	}
	
	@Override
	public boolean docExists() {
		return file != null && file.exists();
	}
	
	
	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public Header[] getHeaders() {
		return headers;
	}

	public void setHeaders(Header[] headers) {
		this.headers = headers;
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public URI getRedirectedUri() {
		return redirectedUri;
	}

	public void setRedirectedUri(URI redirectedUri) {
		this.redirectedUri = redirectedUri;
	}

}
