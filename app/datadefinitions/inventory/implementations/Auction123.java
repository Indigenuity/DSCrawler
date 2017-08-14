package datadefinitions.inventory.implementations;

import java.net.URI;
import java.util.Set;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;

import datadefinitions.inventory.InventoryTool;
import sites.persistence.Vehicle;

public class Auction123 extends InventoryTool{
	
	
	protected static final String NEW_PATH_STRING = "/new-inventory/index.htm";
	protected static final String USED_PATH_STRING = "/used-inventory/index.htm";
	protected static final Pattern PAGINATION_LINK_PATTERN= Pattern.compile(".*\\?start=([0-9]+)&");
	
	protected static final String GENERAL_ROOT_PATTERN = "showroom.auction123.com/.+/(detectmobile.html|index.html)";
	protected static final Pattern COUNT_PATTERN = Pattern.compile("(?s)a123InventoryCount.+ of ([0-9]+) Total");
	protected static final String NEXT_PAGE_SELECTOR = "a[class=a123PagerLink]";

	@Override
	public boolean isNewPath(URI uri) {
		return false;
	}

	@Override
	public boolean isNewRoot(URI uri) {
		return false;
	}

	@Override
	public boolean isUsedPath(URI uri) {
		return false;
	}

	@Override
	public boolean isUsedRoot(URI uri) {
		return false;
	}

	@Override
	public boolean isGeneralPath(URI uri) {
		return false;
	}

	@Override
	public boolean isGeneralRoot(URI uri) {
		return uriContains(uri, GENERAL_ROOT_PATTERN);
	}
	
	@Override
	public boolean isNewPath(Document doc, URI uri) {
		return false;
	}

	@Override
	public boolean isNewRoot(Document doc, URI uri) {
		return false;
	}

	@Override
	public boolean isUsedPath(Document doc, URI uri) {
		return false;
	}

	@Override
	public boolean isUsedRoot(Document doc, URI uri) {
		return false;
	}

	@Override
	public boolean isGeneralPath(Document doc, URI uri) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isGeneralRoot(Document doc, URI uri) {
		return isGeneralRoot(uri);
	}

	@Override
	public int getCount(Document doc) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public URI getNextPageLink(Document doc, URI currentUri) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<URI> getPaginationLinks(Document doc, URI currentUri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Vehicle> getVehicles(Document doc) {
		// TODO Auto-generated method stub
		return null;
	}

	

	

	

}
