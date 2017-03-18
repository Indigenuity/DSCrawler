package crawling.anansi;

import java.net.URI;

public class UriFetch {

	private URI uri;
	private int statusCode;
	private byte[] result;
	
	public UriFetch(){}
	public UriFetch(URI uri) {
		this.uri = uri;
	}
	
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public byte[] getResult() {
		return result;
	}
	public void setResult(byte[] result) {
		this.result = result;
	}
	public URI getUri() {
		return uri;
	}
	public void setUri(URI uri) {
		this.uri = uri;
	}
	
	
	
}
