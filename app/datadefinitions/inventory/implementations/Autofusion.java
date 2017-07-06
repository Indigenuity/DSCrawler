package datadefinitions.inventory.implementations;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import datadefinitions.inventory.InventoryTool;
import sites.persistence.Vehicle;

public class Autofusion extends InventoryTool {
	
	// example https://www.sterlingmccalltoyota.com/search/New+Toyota+tm?page=229
	
	public final static Pattern PAGE_NUM_REGEX = Pattern.compile("page=([0-9]+)");

	@Override
	protected void declareValues() {
		newPathString = "/search/New+t";
		usedPathString = "/search/Used+t";
//		paginationLinkString = ".*/VehicleSearchResults\\?limit=[0-9]+&offset=([0-9]+)";
		countRegexString = "Viewing matches [0-9]+ - [0-9]+ of ([0-9]+)";
//		nextPageSelector = "a[data-action=next]";
		
	}

	@Override
	public boolean isNewPath(URI uri) {
		// TODO Auto-generated method stub
		return super.isNewPath(uri);
	}

	@Override
	public boolean isUsedPath(URI uri) {
		// TODO Auto-generated method stub
		return super.isUsedPath(uri);
	}

	@Override
	public Set<Vehicle> getVehicles(Document doc) {
		Set<Vehicle> vehicles = new HashSet<Vehicle>();
		Elements listings = doc.select("div.af-vehicle-result");
		for(Element listing : listings){
			try{
				Vehicle vehicle = new Vehicle();
				Element vinDd = listing.select("meta[itemprop=vehicleIdentificationNumber]").first();
				vehicle.setVin(vinDd.attr("content"));
				try{
					Element valueSpan = listing.select("td.af-price-value").first();
					vehicle.setMsrp(moneyString(valueSpan.text()));
				} catch(Exception e){
					System.out.println("Error getting value for vehicle : " + vehicle.getVin());
				}
				try{
					Element offeredSpan = listing.select("td.af-final-price-value").first();
					vehicle.setOfferedPrice(moneyString(offeredSpan.text()));
				} catch(Exception e){
					System.out.println("Error getting price for vehicle : " + vehicle.getVin());
				}
				try{
					Element link = listing.select("a.af-clickable-div-a").first();
					vehicle.setUrl(link.absUrl("href"));
				} catch(Exception e){
					System.out.println("Error getting url for vehicle : " + vehicle.getVin());
				}
				
				try{
					Element odomMeta = listing.select("meta[itemprop=mileageFromOdometer]").first();
					vehicle.setMileage(Double.valueOf(odomMeta.attr("content")));
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

	//Checks for presence of next button, then generates next page link.  Links on site are all javascript-generated
	@Override
	public URI getNextPageLink(Document doc, URI currentUri) {
		Elements nextButton = doc.select("a[aria-label=next]");
		if(nextButton == null){
			return null;
		}
		String uriString = currentUri.toString();
		Matcher matcher = PAGE_NUM_REGEX.matcher(uriString);
		if(matcher.find()){
			int pageNum = Integer.valueOf(matcher.group(1));
			pageNum++;
			uriString = matcher.replaceAll("page=" + pageNum);
			try {
				return new URI(uriString);
			} catch (URISyntaxException e) {
				return null;
			}
		} else {
			return null;
		}
	}
	
	
	

}
