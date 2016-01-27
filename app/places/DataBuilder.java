package places;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import javax.persistence.EntityManager;

import net.sf.sprockets.google.Place;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.PlaceDetails;

import persistence.PlacesPage;
import persistence.ZipLocation;
import play.Logger;
import play.db.jpa.JPA;

public class DataBuilder {
	
	public static PlacesPage getPlacesDealer(PlaceDetails placeDetails){
		PlacesPage page = new PlacesPage();
		page.setFormattedAddress(placeDetails.formattedAddress);
		page.setFormattedPhoneNumber(placeDetails.formattedPhoneNumber);
		page.setGoogleUrl(placeDetails.url + "");
		page.setIconUrl(placeDetails.icon + "");
		page.setInternationalPhoneNumber(placeDetails.internationalPhoneNumber);
		page.setLatitude(placeDetails.geometry.location.lat);
		page.setLongitude(placeDetails.geometry.location.lat);
		page.setName(placeDetails.name);
		try{
			page.setOpenHours(placeDetails.openingHours + "");
			page.setPermanentlyClosed(placeDetails.openingHours.permanentlyClosed);
		}
		catch(NullPointerException e){
			page.setOpenHours(null);
		}
		page.setPlacesId(placeDetails.placeId);
		page.setPriceLevel(placeDetails.priceLevel + "");
		page.setRating(placeDetails.rating);
		if(placeDetails.types != null)
			page.setTypes(placeDetails.types + "");
		page.setUtcOffset(placeDetails.utcOffset);
		page.setVicinity(placeDetails.vicinity);
		page.setWebsite(placeDetails.website + "");
		AddressComponent country = getCountry(placeDetails.addressComponents);
		if(country != null){
			page.setCountry(country.longName);
			page.setShortCountry(country.shortName);
		}
		
		return page;
	}
	
	public static AddressComponent getCountry(AddressComponent[] components) {
		for(AddressComponent component : components){
			for(AddressComponentType type : component.types){
				if(type == AddressComponentType.COUNTRY){
					return component;
				}
			}
		}
		return null;
	}
	
	public static void readZipsFromFile() throws IOException {
		Reader in = new FileReader("dealersocket\\source/free-zipcode-database-Primary.csv");
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(in);
		
		EntityManager em = JPA.em();
		int total = 0;
		for(CSVRecord record : records) {
			ZipLocation zip = new ZipLocation();
			try { 
				zip.city = record.get("City");
				zip.decomissioned = Boolean.parseBoolean(record.get("Decommisioned"));
				zip.latitude = Double.parseDouble(record.get("Lat"));
				zip.longitude = Double.parseDouble(record.get("Long"));
				zip.state = record.get("State");
				zip.zip = record.get("Zipcode");
				zip.zipType = record.get("ZipCodeType");
				System.out.println("saving : " + zip.zip);
				em.persist(zip);
			}
			catch(Exception e) {
				//Do nothing.  Almost certainly an empty latitude or longitude.
				Logger.error("Error while reading record from CSV: " + e);
			}
			
		}
		
	}

}
