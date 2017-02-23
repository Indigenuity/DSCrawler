package places;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

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
	
	public static void classifyRecordType(PlacesDealer dealer) {
		String name = dealer.getName().toLowerCase();
		String website = dealer.getSite().getHomepage().toLowerCase();
		if(name.contains("rv") || website.contains("rv") || name.contains("recreation") || website.contains("recreation")){
			dealer.setRecordType(RecordType.RV);
		} else if(name.contains("finance") || website.contains("finance") || name.contains("loans") || website.contains("loans")){
			dealer.setRecordType(RecordType.FINANCE);
		} else if(name.contains("repair") || website.contains("repair") || name.contains("autobody") || website.contains("autobody")){
			dealer.setRecordType(RecordType.REPAIR);
		} else if(name.contains("motorsports") || website.contains("motorsports")){
			dealer.setRecordType(RecordType.MOTORSPORTS);
		}
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
			String wtf = dealer.getResolvedSite().getHomepage();
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
			dealer.setCountry(place.getAddress().getCountry());
			dealer.setShortCountry(place.getAddress().getCountryAbbr());
			dealer.setStreet(place.getAddress().getStreetNumber() + " " + place.getAddress().getRoute());
			dealer.setCity(place.getAddress().getLocality());
			dealer.setProvince(place.getAddress().getAdminAreaL1());
			dealer.setPostal(place.getAddress().getPostalCode());
		}
//		System.out.println("formatted : " + place.getFormattedAddress()); 
//		System.out.println("address : " + place.getAddress());
//		System.out.println("admin : " + place.getAddress().getAdminAreaL1());
//		System.out.println("admin: " + place.getAddress().getAdminAreaL1Abbr());
//		System.out.println("address : " + place.getAddress().getAdminAreaL2());
//		System.out.println("address : " + place.getAddress().getAdminAreaL2Abbr());
//		System.out.println("address : " + place.getAddress().getAdminAreaL3());
//		System.out.println("address : " + place.getAddress().getAdminAreaL3Abbr());
//		System.out.println("address : " + place.getAddress().getAdminAreaL4());
//		System.out.println("address : " + place.getAddress().getAdminAreaL5());
//		System.out.println("country: " + place.getAddress().getCountry());
//		System.out.println("address : " + place.getAddress().getCountryAbbr());
//		System.out.println("locality : " + place.getAddress().getLocality());
//		System.out.println("neighborhood : " + place.getAddress().getNeighborhood());
//		System.out.println("postalcode : " + place.getAddress().getPostalCode());
//		System.out.println("postal town : " + place.getAddress().getPostalTown());
//		System.out.println("premise : " + place.getAddress().getPremise());
//		System.out.println("reoute : " + place.getAddress().getRoute());
//		System.out.println("street number : " + place.getAddress().getStreetNumber());
//		System.out.println("sublocality : " + place.getAddress().getSublocality());
//		System.out.println("address : " + place.getAddress().getSublocalityL1());
//		System.out.println("address : " + place.getAddress().getSublocalityL2());
		
		
//		System.out.println("after id : " + dealer.getPlacesId());
	}
}
