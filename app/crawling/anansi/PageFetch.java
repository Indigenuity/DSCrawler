package crawling.anansi;

import java.net.URI;

public class PageFetch {

	private URI uri;
	private int statusCode;
	private String resultText;
	
	public PageFetch(){}
	public PageFetch(URI uri) {
		this.uri = uri;
	}
	
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public String getResultText() {
		return resultText;
	}
	public void setResultText(String resultText) {
		this.resultText = resultText;
	}
	public URI getUri() {
		return uri;
	}
	public void setUri(URI uri) {
		this.uri = uri;
	}
	
	
	
}
