package places;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import javax.persistence.EntityManager;

import net.sf.sprockets.google.Place;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import persistence.PlacesDealer;
import persistence.ZipLocation;
import play.Logger;
import play.db.jpa.JPA;

public class DataBuilder {
	
	public static PlacesDealer getPlacesDealer(Place googlePlace){
		PlacesDealer dealer = new PlacesDealer();
		dealer.setFormattedAddress(googlePlace.getFormattedAddress());
		dealer.setFormattedPhoneNumber(googlePlace.getFormattedPhoneNumber());
		dealer.setGoogleUrl(googlePlace.getUrl());
		dealer.setIconUrl(googlePlace.getIcon());
		dealer.setInternationalPhoneNumber(googlePlace.getIntlPhoneNumber());
		dealer.setLatitude(googlePlace.getLatitude());
		dealer.setLongitude(googlePlace.getLongitude());
		dealer.setName(googlePlace.getName());
		if(googlePlace.getOpeningHours() != null)
			dealer.setOpenHours(googlePlace.getOpeningHours().toString());
		dealer.setPermanentlyClosed(googlePlace.isPermanentlyClosed());
		if(googlePlace.getPhotos() != null)
			dealer.setPhotos(googlePlace.getPhotos().toString());
		dealer.setPlacesId(googlePlace.getPlaceId().getId());
		dealer.setPriceLevel(googlePlace.getPriceLevel());
		dealer.setRating(googlePlace.getRating());
		if(googlePlace.getTypes() != null)
			dealer.setTypes(googlePlace.getTypes().toString());
		dealer.setUtcOffset(googlePlace.getUtcOffset());
		dealer.setVicinity(googlePlace.getVicinity());
		dealer.setWebsite(googlePlace.getWebsite());
		dealer.setCountry(googlePlace.getAddress().getCountry());
		return dealer;
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
