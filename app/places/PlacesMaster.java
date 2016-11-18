package places;

import java.io.IOException;
import java.util.List;

import dao.PlacesDealerDao;
import net.sf.sprockets.google.Place;
import net.sf.sprockets.google.Places;
import net.sf.sprockets.google.Places.Params;
import net.sf.sprockets.google.Places.Response;
import play.db.jpa.JPA;
import utilities.DSFormatter;

public class PlacesMaster {

	public static Place getDealerSearchMatch(String name, String street, String city, String state) throws IOException{
		String queryString = name + " " + street + ", " + city + ", " + state;
		return getDealerSearchMatch(queryString);
	}
	
	public static Place getAnySearchMatch(String name, String street, String city, String state) throws IOException{
		Response<List<Place>> resp = Places.textSearch(Params.create().query(name + " " + street + ", " + city + ", " + state));
		List<Place> places = resp.getResult();
		if(places.size() > 0){
			return places.get(0);
		}
		return null;
	}
	
	public static Place getDealerSearchMatch(String queryString) throws IOException {
		System.out.println("Querying : " + queryString);
		Response<List<Place>> resp = Places.textSearch(Params.create().query(queryString).type("car_dealer"));
		List<Place> places = resp.getResult();
		if(places.size() > 0){
			return places.get(0);
		}
		return null;
	}
	
	
	
	public static PlacesDealer updateOrNew(String placesId) throws IOException{
		PlacesDealer dealer = PlacesDealerDao.findByPlacesId(placesId);
		if(dealer == null){
			dealer = new PlacesDealer();
			dealer.setPlacesId(placesId);
		}
		Response<Place> detailsResponse = Places.details(Params.create().placeId(dealer.getPlacesId()));
		DataBuilder.fillPlacesDealer(dealer, detailsResponse);
		return JPA.em().merge(dealer);
	}
	
	public static String getGoogleSearchUrl(String query){
		query = DSFormatter.encode(query);
		return "https://www.google.com/webhp?sourceid=chrome-instant&ion=1&espv=2&ie=UTF-8#q=" + query;
	}
	
}
