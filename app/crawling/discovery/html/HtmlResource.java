package crawling.discovery.html;


import org.jsoup.nodes.Document;

import crawling.discovery.entities.Endpoint;
import crawling.discovery.entities.GenericPrimaryResource;
import crawling.discovery.entities.Resource;

public class HtmlResource extends GenericPrimaryResource {

	protected Document doc;
	public HtmlResource(String name, Resource parent, Endpoint endpoint) {
		super(name, parent, endpoint);
	}
	
	


	
	
	
}
