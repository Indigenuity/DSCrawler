package datadefinitions.inventory.implementations;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import datadefinitions.inventory.InventoryTool;
import sites.persistence.Vehicle;

public class DealerCom extends InventoryTool {
	
	// example https://www.kengarffvw.com/used-inventory/index.htm

	@Override
	protected void declareValues() {
		newPathString = "/new-inventory/index.htm";
		usedPathString = "/used-inventory/index.htm";
		paginationLinkString = ".*\\?start=([0-9]+)&";
		countRegexString = "vehicle-count[^0-9<(]+([0-9]+)";
		
		nextPageSelector = "a[rel=next]";
	}

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
	public Set<Vehicle> getVehicles(Document doc) {
		Set<Vehicle> vehicles = new HashSet<Vehicle>();
		Elements listings = doc.select("ul.inventoryList li");
		for(Element listing : listings){
			try{
				Vehicle vehicle = new Vehicle();
				Element vinElement = listing.select("dl.vin dd").first();
				vehicle.setVin(vinElement.text());
				try{
					Element valueSpan = listing.select("span.msrp span.value").first();
					if(valueSpan == null){
						valueSpan = listing.select("span.retailValue span.value").first();
					}
					if(valueSpan != null){
						vehicle.setMsrp(moneyString(valueSpan.text()));
					}
				} catch(Exception e){
					System.out.println("Error getting msrp of vehicle : " + vehicle.getVin());
					System.out.println("e : " + e.getMessage());
				}
				try{
					Element priceSpan = listing.select("span.final-price span.value, span.stackedConditionalFinal span.value").first();
					vehicle.setOfferedPrice(moneyString(priceSpan.text()));
				} catch(Exception e){
					System.out.println("Error getting offered price of vehicle : " + vehicle.getVin());
					System.out.println("e : " + e.getMessage());
				}
				try{
					Element link = listing.select("a.url").first();
					vehicle.setUrl(link.absUrl("href"));
//					System.out.println("link : " + link.absUrl("href"));
				} catch(Exception e){
					System.out.println("Error getting url for vehicle : " + vehicle.getVin());
				}
				
				try{
					Element odomElement = listing.select("dt:containsOwn(Mileage)").first();
					if(odomElement != null){
						odomElement = odomElement.nextElementSibling();
					}
					if(odomElement != null){
						vehicle.setMileage(mileageString(odomElement.text()));	
					}
					
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
