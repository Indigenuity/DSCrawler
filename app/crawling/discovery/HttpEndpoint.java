package crawling.discovery;

import java.net.MalformedURLException;
import java.net.URL;

public class HttpEndpoint extends Endpoint {
	
	private URL url;

	public HttpEndpoint(URL url) throws MalformedURLException{
		this.setUrl(url);
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		if(url == null){
			throw new IllegalArgumentException("Cannot set null URL for HttpEndpoint");
		}
		this.url = url;
	}
}
