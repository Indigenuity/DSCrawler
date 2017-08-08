package datadefinitions.inventory.implementations;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import datadefinitions.inventory.InventoryTool;
import datadefinitions.newdefinitions.WebProvider;
import sites.persistence.Vehicle;

public class CdkGlobal extends InventoryTool{
	//example http://www.gregbell.com/VehicleSearchResults?pageContext=VehicleSearch&search=new&make=Chevrolet&offset=0&limit=20&bodyType=TRUCK
	
	protected static final String NEW_PATH = "/VehicleSearchResults?search=new";
	protected static final String USED_PATH = "/VehicleSearchResults?search=preowned";
	protected static final String ALTERNATE_USED_PATH = "/VehicleSearchResults?search=used";
	protected static final Pattern PAGINATION_LINK_PATTERN= Pattern.compile(".*/VehicleSearchResults\\?limit=[0-9]+&offset=([0-9]+)");
	
	protected static final Pattern COUNT_PATTERN = Pattern.compile("nv_search_count_container[^0-9<]+([0-9]+)");
	protected static final Pattern ALTERNATE_COUNT_PATTERN = Pattern.compile(">([0-9]+) Vehicles Found");
	protected static final String NEXT_PAGE_SELECTOR = "a[data-action=next]";
	
	@Override
	public boolean isNewPath(URI uri){
		return uri != null && pathAndQueryContains(uri, NEW_PATH);
	}
	
	@Override
	public boolean isNewRoot(URI uri){
		return pathAndQueryEquals(uri, NEW_PATH);
	}
	
	@Override
	public boolean isUsedPath(URI uri){
		return pathAndQueryContains(uri, USED_PATH) || pathAndQueryContains(uri, ALTERNATE_USED_PATH); 
	}
	
	@Override
	public boolean isUsedRoot(URI uri){
		return pathAndQueryEquals(uri, USED_PATH) || pathAndQueryEquals(uri, ALTERNATE_USED_PATH);
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
		int count = findNumberMatch(doc.toString(), ALTERNATE_COUNT_PATTERN, 1);
		if(count == 0){
			count = findNumberMatch(doc.toString(), COUNT_PATTERN, 1);
		}
		return count;
	}
	
	@Override
	public URI getNextPageLink(Document doc, URI currentUri) {
		URI uri = getTraditionalLink(doc, currentUri);
		if(uri == null){
			uri = scrapeAjaxNextPageButton(doc, currentUri);
		}
		return uri;
	}
	
	public URI getTraditionalLink(Document doc, URI currentUri){
		try{
			URI uri = getFirstAbsHref(NEXT_PAGE_SELECTOR, doc);
			if(currentUri.toString().contains("search=new")){
				return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(), "search=new&" + uri.getQuery(), uri.getFragment());
			} else if(currentUri.toString().contains("search=used")){
				return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(), "search=used&" + uri.getQuery(), uri.getFragment());
			} else if(currentUri.toString().contains("search=preowned")){
				return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(), "search=preowned&" + uri.getQuery(), uri.getFragment());
			}
			return null;
		} catch(Exception e){
			return null;
		}
	}

	public URI scrapeAjaxNextPageButton(Document doc, URI currentUri){
		Elements buttons = doc.select("button.loadMore");
		if(buttons.size() < 1){
			return null;
		}
		String path = buttons.get(0).attr("abs:data-link");
		try {
			URI uri = new URI(path);
			return uri;
		} catch (URISyntaxException e) {
			return null;
		}
	}
	
	@Override
	public Set<URI> getPaginationLinks(Document doc, URI currentUri){
		return getAllAHrefsContaining(PAGINATION_LINK_PATTERN, doc);
	}

	@Override
	public Set<Vehicle> getVehicles(Document doc) {
		Set<Vehicle> vehicles = new HashSet<Vehicle>();
		Elements classicListings = doc.select("section[itemtype=http://schema.org/Car]");
		Elements alternateListings = doc.select("section.vehicleListWrapper article[itemtype=http://schema.org/IndividualProduct]");
		vehicles.addAll(getClassicVehicles(classicListings));
		vehicles.addAll(getAlternateVehicles(alternateListings));
		
		return vehicles;
	}
	
	private Set<Vehicle> getClassicVehicles(Elements listings){
		Set<Vehicle> vehicles = new HashSet<Vehicle>();
		for(Element listing : listings){
			try{
				Vehicle vehicle = new Vehicle();
				Element vinDd = listing.select("dd[itemprop=vehicleIdentificationNumber]").first();
				vehicle.setVin(vinDd.text());
				try{
					Element valueSpan = listing.select("li[if=prices.msrp] span[itemprop=price]").first();
					vehicle.setMsrp(doubleString(valueSpan.text()));
				} catch(Exception e){
//					System.out.println("Error getting price of vehicle : " + vehicle.getVin());
				}
				try{
					Element priceSpan = listing.select("li.total-sale-price span[itemprop=price]").first();
					vehicle.setOfferedPrice(doubleString(priceSpan.text()));
				} catch(Exception e){
//					System.out.println("Error getting price of vehicle : " + vehicle.getVin());
				}
				try{
					Element link = listing.select("a[itemprop=url]").first();
					vehicle.setUrl(link.absUrl("href"));
				} catch(Exception e){
//					System.out.println("Error getting url for vehicle : " + vehicle.getVin());
				}
				
				try{
					Element odomMeta = listing.select("dd[itemprop=mileageFromOdometer]").first();
					vehicle.setMileage(Double.valueOf(odomMeta.text()));
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
	
	private Set<Vehicle> getAlternateVehicles(Elements listings){
		Set<Vehicle> vehicles = new HashSet<Vehicle>();
		for(Element listing : listings){
			try{
				Vehicle vehicle = new Vehicle();
				Element vinElement = listing.select("a[data-vin]").first();
				vehicle.setVin(vinElement.attr("data-vin"));
//				System.out.println("vin in alternate vehicle : " + vehicle.getVin());
				try{
//					Element valueSpan = listing.select("li[if=prices.msrp] span[itemprop=price]").first();
//					vehicle.setMsrp(moneyString(valueSpan.text()));
				} catch(Exception e){
//					System.out.println("Error getting price of vehicle : " + vehicle.getVin());
				}
				try{
					Element priceElement = listing.select("a[data-saleprice]").first();
					vehicle.setOfferedPrice(doubleString(priceElement.attr("data-saleprice")));
				} catch(Exception e){
//					System.out.println("Error getting price of vehicle : " + vehicle.getVin());
				}
				try{
					Element link = listing.select("a[itemprop=url]").first();
					vehicle.setUrl(link.absUrl("href"));
				} catch(Exception e){
//					System.out.println("Error getting url for vehicle : " + vehicle.getVin());
				}
				
				try{
					Element odomElement = listing.select("li.Miles span:nth-of-type(2)").first();
					vehicle.setMileage(doubleString(odomElement.text()));
				} catch(Exception e){
//					System.out.println("Error getting mileage for vehicle : " + vehicle.getVin() + " : " + e.getMessage());
					
				}
				vehicles.add(vehicle);
			} catch(Exception e){
				continue;
			}
		}
		return vehicles;		
	}
	
}
