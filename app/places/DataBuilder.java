package places;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import net.sf.sprockets.google.Place;
import net.sf.sprockets.google.Places;
import net.sf.sprockets.google.Places.Params;
import net.sf.sprockets.google.Places.Response;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.google.common.base.Functions;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.PlaceDetails;

import dao.GeneralDAO;
import dao.PlacesDealerDao;
import global.Global;
import play.Logger;
import play.db.jpa.JPA;

public class DataBuilder {
	
	public static void importPlaces(List<Place> places)  {
		String queryString = "select pd.placesId from PlacesDealer pd";
		List<String> ids = JPA.em().createQuery(queryString, String.class).getResultList();
		
		System.out.println("ids : "+ ids.size());
		
		for(Place place : places) {
			PlacesDealerDao.insertIgnore(place.getPlaceId().getId());
//			if(!ids.contains(place.getPlaceId().getId())){
//				
//				PlacesDealer dealer = new PlacesDealer();
//				dealer.setPlacesId(place.getPlaceId().getId());
//				JPA.em().persist(dealer);
//			}
		}
	}
	
	public static PlacesDealer importPlace(Place place)  {
		PlacesDealer dealer  = GeneralDAO.getFirst(PlacesDealer.class, "placesId", place.getPlaceId().getId());
		
		if(dealer == null) {
			dealer = new PlacesDealer();
			dealer.setPlacesId(place.getPlaceId().getId());
			dealer = JPA.em().merge(dealer);
		}
		
//		dealer.setFormattedAddress(place.getFormattedAddress());
//		dealer.setCountry(place.getAddress().getCountry());
//		dealer.setFormattedPhoneNumber(place.getFormattedPhoneNumber());
//		dealer.setLongitude(place.getLongitude());
//		dealer.setLatitude(place.getLatitude());
//		dealer.setInternationalPhoneNumber(place.getIntlPhoneNumber());
//		dealer.setName(place.getName());
//		dealer.setPermanentlyClosed(place.get);
		
		return dealer;
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
	
	public static void refreshZipcodeDatabase() throws IOException {
		refreshUsZips();
		refreshCanadaPostalCodes();
	}
	
	public static void refreshCanadaPostalCodes() throws IOException {
		System.out.println("refreshing Canada Postal Codes");
		Reader in = new FileReader(Global.getInputFolder() + "/zips/ca_postal_codes.csv");
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(in);
		
		List<CanadaPostal> postalList = JPA.em().createQuery("from CanadaPostal cp", CanadaPostal.class).getResultList();
		Map<String, CanadaPostal> postals = postalList.stream().collect(Collectors.toMap( (postalCode) -> postalCode.code, (postalCode) -> {return postalCode;}));
		System.out.println("postalCode already in database : " + postalList.size());
		
		EntityManager em = JPA.em();
		int total = 0;
		for(CSVRecord record : records) {
			try {
				CanadaPostal postalCode = postals.get(record.get("Postal Code"));
				if(postalCode == null) {
					postalCode = new CanadaPostal();
				}
				
				postalCode.code = record.get("Postal Code");
				postalCode.name = record.get("Place Name");
				postalCode.province = record.get("Province");
				postalCode.latitude = Double.parseDouble(record.get("Latitude"));
				postalCode.longitude = Double.parseDouble(record.get("Longitude"));
				postalCode = em.merge(postalCode);
				
				if(total++ %500 == 0){
					System.out.println("Refreshed " + total + " postalCode");
				}
			}
			catch(Exception e) {
				//Do nothing.  Almost certainly an empty latitude or longitude.
				Logger.error("Error while reading record from CSV: " + e);
			}
		}
	}
	
	
	public static void refreshUsZips() throws IOException{
		System.out.println("refreshing US Zipcodes");
		Reader in = new FileReader(Global.getInputFolder() + "/zips/free-zipcode-database-Primary.csv");
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(in);
		
		List<ZipLocation> zipList = JPA.em().createQuery("from ZipLocation zl", ZipLocation.class).getResultList();
		Map<String, ZipLocation> zips = zipList.stream().collect(Collectors.toMap( (zip) -> zip.zip, (zip) -> {return zip;}));
		System.out.println("Zips already in database : " + zipList.size());
		
		EntityManager em = JPA.em();
		int total = 0;
		for(CSVRecord record : records) {
			try {
				ZipLocation zip = zips.get(record.get("Zipcode"));
				if(zip == null) {
					zip = new ZipLocation();
				}
				zip.city = record.get("City");
				zip.decomissioned = Boolean.parseBoolean(record.get("Decommisioned"));
				zip.latitude = Double.parseDouble(record.get("Lat"));
				zip.longitude = Double.parseDouble(record.get("Long"));
				zip.state = record.get("State");
				zip.zip = record.get("Zipcode");
				zip.zipType = record.get("ZipCodeType");
//				System.out.println("saving : " + zip.zip);
				zip = em.merge(zip);
				if(total++ %500 == 0){
					System.out.println("Refreshed " + total + " zip codes");
				}
			}
			catch(Exception e) {
				//Do nothing.  Almost certainly an empty latitude or longitude.
				Logger.error("Error while reading record from CSV: " + e);
			}
		}
	}

}
