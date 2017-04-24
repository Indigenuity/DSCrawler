package places;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import analysis.TextAnalyzer;
import crawling.projects.BasicDealer;
import dao.GeneralDAO;
import dao.PlacesDealerDao;
import dao.SalesforceDao;
import dao.SitesDAO;
import datadefinitions.RecordType;
import net.sf.sprockets.google.Place;
import net.sf.sprockets.google.Places;
import net.sf.sprockets.google.Places.Params;
import net.sf.sprockets.google.Places.Response;
import persistence.Site;
import play.db.jpa.JPA;
import salesforce.persistence.SalesforceAccount;

public class PlacesLogic {
	
	private static final Pattern ADDRESS_WITH_NO_STREET = Pattern.compile("([^,]+), ([^0-9,]{2}) ([^,]+), ([a-zA-Z]+)");
	private static final Pattern ADDRESS_WITH_ADDRESSEE = Pattern.compile("([^,]+),([^,]+),([^,]+), ([^0-9,]{2}) ([^,]+), ([a-zA-Z]+)");
	private static final Pattern ADDRESS = Pattern.compile("([^,]+),([^,]+), ([^0-9,]{2}) ([^,]+), ([a-zA-Z]+)");
	
	public static void classifyRecordType(PlacesDealer dealer) {
		String name = dealer.getName().toLowerCase();
		String website = dealer.getSite().getHomepage().toLowerCase();
		if(name.contains(" rv") || name.contains("rv ") || name.contains("recreation") || website.contains("recreation") ||
				name.contains("camper") || website.contains("camper")){
			dealer.setRecordType(RecordType.RV);
		} else if(name.contains("finance") || website.contains("finance") || name.contains("loans") || website.contains("loans")){
			dealer.setRecordType(RecordType.FINANCE);
		} else if(name.contains("repair") || website.contains("repair") || name.contains("autobody") || website.contains("autobody") ||
				name.contains("service") || website.contains("service") || name.contains("auto parts") || website.contains("autoparts") ||
				name.contains("collision") || website.contains("collision")){
			dealer.setRecordType(RecordType.REPAIR);
		} else if(name.contains("powersports") || name.contains("power sports") || website.contains("powersports")){
			dealer.setRecordType(RecordType.POWERSPORTS);
		} else if(name.contains("motorsports") || website.contains("motorsports")){
			dealer.setRecordType(RecordType.MOTORSPORTS);
		} else if(name.contains("auction") || website.contains("auction")){
			dealer.setRecordType(RecordType.AUCTION);
		} else if(name.contains("marine") || website.contains("marine")){
			dealer.setRecordType(RecordType.MARINE);
		} else if(TextAnalyzer.hasOemOccurrence(name) || TextAnalyzer.hasOemOccurrence(website)){
			dealer.setRecordType(RecordType.NEW);
		} else {
			dealer.setRecordType(RecordType.UNCLASSIFIED);
		}
//		System.out.println("Dealer: " + dealer.getName() + " " + dealer.getWebsite() + " : " + dealer.getRecordType());
	}
	
	public static void salesforceMatch(PlacesDealer dealer){
//			return "Not a dealer";
		
		String matchStatus = null;
		SalesforceAccount account = GeneralDAO.getFirst(SalesforceAccount.class, "stdStreet", dealer.getStdStreet());
		if(account != null){
			matchStatus += "Address Match : " + account.getSalesforceId() + "\n";
		}
		account = GeneralDAO.getFirst(SalesforceAccount.class, "stdPhone", dealer.getStdPhone());
		if(account != null){
			matchStatus += "Phone Match : " + account.getSalesforceId() + "\n";
		}
		if(dealer.getResolvedSite() != null){
			List<SalesforceAccount> accounts = SalesforceDao.findBySite(dealer.getResolvedSite());
//			String wtf = dealer.getResolvedSite().getHomepage();
//			if(wtf.contains("bonnyville")){
//				System.out.println("resolved site : " + );
//			}
			if(accounts.size() > 0){
				matchStatus += "Website Match : " + accounts.get(0).getSalesforceId();
			}	
		}
		
		dealer.setSalesforceMatchString(matchStatus);
	}
	
	public static void fillDetails(PlacesDealer dealer) {
		try{
			Response<Place> detailsResponse = Places.details(Params.create().placeId(dealer.getPlacesId()));
			fillPlacesDealer(dealer, detailsResponse);
		}catch(IOException e){
			throw new UncheckedIOException(e);
		}
	}
	
	public static PlacesDealer getPlacesDealer(Response<Place> detailsResponse){
		PlacesDealer dealer = new PlacesDealer();
		fillPlacesDealer(dealer, detailsResponse);
		return dealer;
	}
	
	public static PlacesDealer updateOrNew(String placesId) throws IOException{
		PlacesDealer dealer = PlacesDealerDao.findByPlacesId(placesId);
		if(dealer == null){
			dealer = new PlacesDealer();
			dealer.setPlacesId(placesId);
		}
		Response<Place> detailsResponse = Places.details(Params.create().placeId(dealer.getPlacesId()));
		fillPlacesDealer(dealer, detailsResponse);
		return JPA.em().merge(dealer);
	}
	
	public static void fillPlacesDealer(PlacesDealer dealer, Response<Place> detailsResponse) {
		dealer.setPlacesStatus(detailsResponse.getStatus());
		dealer.setDetailFetchDate(Calendar.getInstance().getTime());
		if(!detailsResponse.getStatus().equals(Response.STATUS_OK)){
			return;
		}
		
		Place place = detailsResponse.getResult();
		
		String placesId = place.getPlaceId().getId();
		if(!placesId.equals(dealer.getPlacesId())){
			PlacesDealer existingDealer = PlacesDealerDao.findByPlacesId(placesId);
			if(existingDealer != null) {
				dealer.setForwardsTo(existingDealer);
				return;
			}
		}
		
		dealer.setFormattedAddress(place.getFormattedAddress());
		dealer.setFormattedPhoneNumber(place.getFormattedPhoneNumber());
		dealer.setGoogleUrl(place.getUrl()+ "");
		dealer.setIconUrl(place.getIcon() + "");
		dealer.setInternationalPhoneNumber(place.getIntlPhoneNumber());
		dealer.setLatitude(place.getLatitude());
		dealer.setLongitude(place.getLongitude());
		dealer.setName(place.getName());
		try{
			dealer.setOpenHours(place.getOpeningHours() + "");
		}
		catch(NullPointerException e){
			dealer.setOpenHours(null);
		}
		dealer.setPermanentlyClosed(place.isPermanentlyClosed());
		dealer.setPlacesId(place.getPlaceId().getId());
		dealer.setPriceLevel(place.getPriceLevel() + "");
		dealer.setRating(place.getRating());
		if(place.getTypes() != null)
			dealer.setTypes(place.getTypes()+ "");
		dealer.setUtcOffset(place.getUtcOffset());
		dealer.setVicinity(place.getVicinity());
		dealer.setWebsite(place.getWebsite());
		
		if(place.getAddress() != null){
//			System.out.println("address : " + place.getAddress());
//			System.out.println("address : " + place.getAddress().);
			dealer.setCountry(place.getAddress().getCountry());
			dealer.setShortCountry(place.getAddress().getCountryAbbr());
			dealer.setStreet(place.getAddress().getStreetNumber() + " " + place.getAddress().getRoute());
			dealer.setCity(place.getAddress().getLocality());
			dealer.setProvince(place.getAddress().getAdminAreaL1());
			dealer.setPostal(place.getAddress().getPostalCode());
		}
//		System.out.println("formatted : " + place.getFormattedAddress()); 
//		System.out.println("address : " + place.getAddress());
//		System.out.println("adminl1 : " + place.getAddress().getAdminAreaL1());
//		System.out.println("adminl1a: " + place.getAddress().getAdminAreaL1Abbr());
//		System.out.println("adminl2 : " + place.getAddress().getAdminAreaL2());
//		System.out.println("adminl2a : " + place.getAddress().getAdminAreaL2Abbr());
//		System.out.println("adminl3 : " + place.getAddress().getAdminAreaL3());
//		System.out.println("adminl3a : " + place.getAddress().getAdminAreaL3Abbr());
//		System.out.println("adminl4 : " + place.getAddress().getAdminAreaL4());
//		System.out.println("adminl5 : " + place.getAddress().getAdminAreaL5());
//		System.out.println("country: " + place.getAddress().getCountry());
//		System.out.println("country abbr : " + place.getAddress().getCountryAbbr());
//		System.out.println("locality : " + place.getAddress().getLocality());
//		System.out.println("neighborhood : " + place.getAddress().getNeighborhood());
//		System.out.println("postalcode : " + place.getAddress().getPostalCode());
//		System.out.println("postal town : " + place.getAddress().getPostalTown());
//		System.out.println("premise : " + place.getAddress().getPremise());
//		System.out.println("reoute : " + place.getAddress().getRoute());
//		System.out.println("street number : " + place.getAddress().getStreetNumber());
//		System.out.println("sublocality : " + place.getAddress().getSublocality());
//		System.out.println("sublocalityl1 : " + place.getAddress().getSublocalityL1());
//		System.out.println("sublocalityl2: " + place.getAddress().getSublocalityL2());
		
		
//		System.out.println("after id : " + dealer.getPlacesId());
	}
	
	public static boolean fixAddress(PlacesDealer dealer) {
		if(dealer.getFormattedAddress() == null){
			return false;
		}
		Matcher matcher = ADDRESS.matcher(dealer.getFormattedAddress());
		if(matcher.matches()){
			
			dealer.setStreet(matcher.group(1));
			dealer.setCity(matcher.group(2));
			dealer.setProvince(matcher.group(3));
			dealer.setPostal(matcher.group(4));
			return true;
		}
		matcher = ADDRESS_WITH_ADDRESSEE.matcher(dealer.getFormattedAddress());
		if(matcher.matches()){
			
			dealer.setStreet(matcher.group(1));
			dealer.setCity(matcher.group(2));
			dealer.setProvince(matcher.group(3));
			dealer.setPostal(matcher.group(4));
			return true;
		}
		matcher = ADDRESS_WITH_NO_STREET.matcher(dealer.getFormattedAddress());
		if(matcher.matches()){
			
			dealer.setStreet(matcher.group(1));
			dealer.setCity(matcher.group(2));
			dealer.setProvince(matcher.group(3));
			dealer.setPostal(matcher.group(4));
			return true;
		}
		return false;
	}
	
}
