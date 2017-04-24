package datadefinitions.inventory;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import datadefinitions.newdefinitions.WebProvider;
import sites.persistence.Vehicle;

public class CdkGlobal extends InventoryTool{
	//example http://www.gregbell.com/VehicleSearchResults?pageContext=VehicleSearch&search=new&make=Chevrolet&offset=0&limit=20&bodyType=TRUCK
	
	private String alternateUsedPathString = "/VehicleSearchResults?search=used";
	@Override
	protected void declareValues() {
		newPathString = "/VehicleSearchResults?search=new";
		usedPathString = "/VehicleSearchResults?search=preowned";
		paginationLinkString = ".*/VehicleSearchResults\\?limit=[0-9]+&offset=([0-9]+)";
		countRegexString = "nv_search_count_container[^0-9<]+([0-9]+)";
		
		nextPageSelector = "a[data-action=next]";
	}
	
	
	
	@Override
	public URI getNextPageLink(Document doc, URI currentUri) {
		URI uri = super.getNextPageLink(doc, currentUri);
		try{
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



	@Override
	public boolean isUsedPath(URI uri) {
		boolean isUsed = super.isUsedPath(uri);
		if(isUsed){
			return true;
		}
		return StringUtils.equals(alternateUsedPathString, getPathAndQuery(uri));
	}
	
	
	
	@Override
	public boolean isUsedRoot(URI uri) {
		return pathAndQueryEquals(uri, usedPathString) || pathAndQueryEquals(uri, alternateUsedPathString);
	}



	@Override
	public Set<Vehicle> getVehicles(Document doc) {
		Set<Vehicle> vehicles = new HashSet<Vehicle>();
		Elements listings = doc.select("section[itemtype=http://schema.org/Car]");
		for(Element listing : listings){
			try{
				Vehicle vehicle = new Vehicle();
				Element vinDd = listing.select("dd[itemprop=vehicleIdentificationNumber]").first();
				vehicle.setVin(vinDd.text());
				try{
					Element valueSpan = listing.select("li[if=prices.msrp] span[itemprop=price]").first();
					vehicle.setMsrp(moneyString(valueSpan.text()));
				} catch(Exception e){
					System.out.println("Error getting price of vehicle : " + vehicle.getVin());
				}
				try{
					Element priceSpan = listing.select("li.total-sale-price span[itemprop=price]").first();
					vehicle.setOfferedPrice(moneyString(priceSpan.text()));
				} catch(Exception e){
					System.out.println("Error getting price of vehicle : " + vehicle.getVin());
				}
				try{
					Element link = listing.select("a[itemprop=url]").first();
					vehicle.setUrl(link.absUrl("href"));
				} catch(Exception e){
					System.out.println("Error getting url for vehicle : " + vehicle.getVin());
				}
				
				try{
					Element odomMeta = listing.select("dd[itemprop=mileageFromOdometer]").first();
					vehicle.setMileage(Double.valueOf(odomMeta.text()));
				} catch(Exception e){
					System.out.println("Error getting mileage for vehicle : " + vehicle.getVin());
				}
				vehicles.add(vehicle);
			} catch(Exception e){
				continue;
			}
		}
		return vehicles;
	}

}
