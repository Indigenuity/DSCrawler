package places;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import akka.actor.UntypedActor;
import dao.PlacesDealerDao;
import net.sf.sprockets.google.Place;
import net.sf.sprockets.google.Places;
import net.sf.sprockets.google.Places.Params;
import net.sf.sprockets.google.Places.Response;
import play.Logger;
import play.db.jpa.JPA;

public class PostalSearchWorker extends UntypedActor {

	@Override
	public void onReceive(Object message) throws Exception {
		PostalLocation location = (PostalLocation) message;
		System.out.println("Searching postal location : " + location);
		try{
			Response<List<Place>> resp = Places.nearbySearch(Params.create()
					.latitude(location.getLatitude())
					.longitude(location.getLongitude())
					.type("car_dealer"));
			String nextPageToken = null;
			List<Place> results = new ArrayList<Place>();
			int pageNum = 0;
			do {
				pageNum++;
				
				List<Place> searchPlaces = resp.getResult();
				System.out.println("Results on page " + pageNum + " : " + searchPlaces.size() + "(postal code " + location.getPostalCode() + ")");
				nextPageToken = resp.getNextPageToken();
				resp = Places.nearbySearch(Params.create().pageToken(nextPageToken));
				
				results.addAll(searchPlaces);
			}
			while(nextPageToken != null);
			
			JPA.withTransaction( () -> {
				results.stream().forEach((placesDealer) -> { 
					PlacesDealerDao.insertIgnore(placesDealer.getPlaceId().getId());
				});
				location.setDateFetched(Calendar.getInstance().getTime());
				JPA.em().merge(location);
			});
		} catch (Exception e) {
			System.out.println("Error while fetching for postal code : " + location.getPostalCode());
			Logger.error("Error while fetching for postal code : " + location.getPostalCode());
		}
		
	}

}
