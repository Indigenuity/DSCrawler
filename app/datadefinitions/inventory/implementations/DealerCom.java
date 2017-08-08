package datadefinitions.inventory.implementations;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import datadefinitions.inventory.InventoryTool;
import sites.persistence.Vehicle;

public class DealerCom extends InventoryTool {
	
	// example https://www.kengarffvw.com/used-inventory/index.htm
	
	protected static final String NEW_PATH_STRING = "/new-inventory/index.htm";
	protected static final String USED_PATH_STRING = "/used-inventory/index.htm";
	protected static final Pattern PAGINATION_LINK_PATTERN= Pattern.compile(".*\\?start=([0-9]+)&");
	
	protected static final Pattern COUNT_PATTERN = Pattern.compile("vehicle-count[^0-9<(]+([0-9]+)");
	protected static final Pattern ALTERNATE_COUNT_PATTERN = Pattern.compile("([0-9]+) Items Matching");
	protected static final String NEXT_PAGE_SELECTOR = "a[rel=next]";

	//Jsoup can't handle relative urls starting with queries
//	@Override
//	public URI getNextPageLink(Document doc, URI currentUri) {
//		URI uri = super.getNextPageLink(doc, currentUri);
//		try {
//			
//			uri = new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), currentUri.getPath(), uri.getQuery(), uri.getFragment());
//		} catch (URISyntaxException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	@Override
	public boolean isNewPath(URI uri){
		return pathAndQueryContains(uri, NEW_PATH_STRING);
	}
	
	@Override
	public boolean isNewRoot(URI uri){
		return pathAndQueryEquals(uri, NEW_PATH_STRING);
	}
	
	@Override
	public boolean isUsedPath(URI uri){
		return pathAndQueryContains(uri, USED_PATH_STRING);
	}
	
	@Override
	public boolean isUsedRoot(URI uri){
		return pathAndQueryEquals(uri, USED_PATH_STRING);
	}
	
	@Override
	public boolean isGeneralPath(URI uri) {
		return false;
	}
	
	@Override
	public boolean isGeneralRoot(URI uri) {
		return false;
	}
	
	@Override
	public boolean isNewPath(Document doc, URI uri) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNewRoot(Document doc, URI uri) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isUsedPath(Document doc, URI uri) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isUsedRoot(Document doc, URI uri) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isGeneralPath(Document doc, URI uri) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isGeneralRoot(Document doc, URI uri) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public int getCount(Document doc){
		int count = findNumberMatch(doc.toString(), COUNT_PATTERN, 1);
		if(count == 0){
			count = findNumberMatch(doc.toString(), ALTERNATE_COUNT_PATTERN, 1);
		}
		return count;
	}
	
	@Override
	public URI getNextPageLink(Document doc, URI currentUri) {
		return getFirstAbsHref(NEXT_PAGE_SELECTOR, doc);
	}
	
	@Override
	public Set<URI> getPaginationLinks(Document doc, URI currentUri) {
		return getAllAHrefsContaining(PAGINATION_LINK_PATTERN, doc);
	}
	
	@Override
	public Set<Vehicle> getVehicles(Document doc) {
		Set<Vehicle> vehicles = new HashSet<Vehicle>();
		Elements listings = doc.select("ul.inventoryList li");
		for(Element listing : listings){
			try{
				Vehicle vehicle = new Vehicle();
				Element hProduct = listing.select(".hproduct").first();
				;
				vehicle.setVin(hProduct.attr("data-vin"));
				try{
					Element valueSpan = listing.select("span.msrp span.value").first();
					if(valueSpan == null){
						valueSpan = listing.select("span.retailValue span.value").first();
					}
					if(valueSpan != null){
						vehicle.setMsrp(doubleString(valueSpan.text()));
					}
				} catch(Exception e){
//					System.out.println("Error getting msrp of vehicle : " + vehicle.getVin());
//					System.out.println("e : " + e.getMessage());
				}
				try{
					Element priceSpan = listing.select("span.final-price span.value, span.stackedConditionalFinal span.value").first();
//					System.out.println("priceSpan.text : " + priceSpan.text());
					vehicle.setOfferedPrice(doubleString(priceSpan.text()));
				} catch(Exception e){
//					System.out.println("Error getting offered price of vehicle : " + vehicle.getVin());
//					System.out.println("e : " + e.getClass() + e.getMessage());
				}
				try{
					Element link = listing.select("a.url").first();
					vehicle.setUrl(link.absUrl("href"));
//					System.out.println("link : " + link.absUrl("href"));
				} catch(Exception e){
//					System.out.println("Error getting url for vehicle : " + vehicle.getVin());
				}
				
				try{
					Element odomElement = listing.select("dt:containsOwn(Mileage)").first();
					if(odomElement != null){
						odomElement = odomElement.nextElementSibling();
					}
					if(odomElement != null){
						vehicle.setMileage(doubleString(odomElement.text()));	
					}
					
				} catch(Exception e){
//					System.out.println("Error getting mileage for vehicle : " + vehicle.getVin());
				}
				vehicles.add(vehicle);
			} catch(Exception e){
				continue;
			}
		}
		return vehicles;
	}
	
	
	

}
