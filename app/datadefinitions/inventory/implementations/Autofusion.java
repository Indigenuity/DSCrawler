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
	
	

//		paginationLinkString = ".*/VehicleSearchResults\\?limit=[0-9]+&offset=([0-9]+)";
//		nextPageSelector = "a[data-action=next]";
	
	protected static final String NEW_PATH = "/search/New+t";
	protected static final String USED_PATH = "/search/Used+t";
	
	
	protected static final Pattern COUNT_PATTERN = Pattern.compile("Viewing matches [0-9]+ - [0-9]+ of ([0-9]+)");
	protected static final String NEXT_PAGE_SELECTOR = "a[data-action=next]";
	
	public final static Pattern PAGE_NUM_PATTERN = Pattern.compile("page=([0-9]+)");
	
	@Override
	public boolean isNewPath(URI uri){
		return pathAndQueryContains(uri, NEW_PATH);
	}
	@Override
	public boolean isNewRoot(URI uri){
		return pathAndQueryEquals(uri, NEW_PATH);
	}
	@Override
	public boolean isUsedPath(URI uri){
		return pathAndQueryContains(uri, USED_PATH); 
	}
	@Override
	public boolean isUsedRoot(URI uri){
		return pathAndQueryEquals(uri, USED_PATH);
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
	public int getCount(Document doc) {
		return findNumberMatch(doc.toString(), COUNT_PATTERN, 1);
	}
	
	//Checks for presence of next button, then generates next page link.  Links on site are all javascript-generated
	@Override
	public URI getNextPageLink(Document doc, URI currentUri) {
		Elements nextButton = doc.select("a[aria-label=next]");
		if(nextButton == null){
			return null;
		}
		String uriString = currentUri.toString();
		Matcher matcher = PAGE_NUM_PATTERN.matcher(uriString);
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
	
	@Override
	public Set<URI> getPaginationLinks(Document doc, URI currentUri) {
		throw new UnsupportedOperationException();
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
					vehicle.setMsrp(doubleString(valueSpan.text()));
				} catch(Exception e){
//					System.out.println("Error getting value for vehicle : " + vehicle.getVin());
				}
				try{
					Element offeredSpan = listing.select("td.af-final-price-value").first();
					vehicle.setOfferedPrice(doubleString(offeredSpan.text()));
				} catch(Exception e){
//					System.out.println("Error getting price for vehicle : " + vehicle.getVin());
				}
				try{
					Element link = listing.select("a.af-clickable-div-a").first();
					vehicle.setUrl(link.absUrl("href"));
				} catch(Exception e){
//					System.out.println("Error getting url for vehicle : " + vehicle.getVin());
				}
				
				try{
					Element odomMeta = listing.select("meta[itemprop=mileageFromOdometer]").first();
					vehicle.setMileage(Double.valueOf(odomMeta.attr("content")));
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
