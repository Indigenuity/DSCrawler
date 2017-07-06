package crawling.discovery.html;


import java.net.URI;

import org.jsoup.nodes.Document;

import crawling.discovery.entities.Endpoint;
import crawling.discovery.entities.GenericPrimaryResource;
import crawling.discovery.entities.Resource;

public interface HtmlResource  {

	public Document getDocument() throws Exception;
	public URI getUri();
	public URI getRedirectedUri();
	public Integer getStatusCode();
	
}
