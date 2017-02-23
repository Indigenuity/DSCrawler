package places;

import akka.actor.UntypedActor;
import net.sf.sprockets.google.Place;
import net.sf.sprockets.google.Places;
import net.sf.sprockets.google.Places.Params;
import net.sf.sprockets.google.Places.Response;
import play.Logger;
import play.db.jpa.JPA;

public class DetailsWorker extends UntypedActor {

	@Override
	public void onReceive(Object message) throws Exception {
		Long placesDealerId = (Long) message;
		System.out.println("Fetching details for PlacesDealer : " + placesDealerId);
		try{
			JPA.withTransaction(() ->{
				PlacesDealer dealer = JPA.em().find(PlacesDealer.class, placesDealerId);
				Response<Place> detailsResponse = Places.details(Params.create().placeId(dealer.getPlacesId()));
				PlacesLogic.fillPlacesDealer(dealer, detailsResponse);
				JPA.em().merge(dealer);
			});
		}
		catch(Exception e){
			Logger.error( e.getClass().getSimpleName() + " in DetailsWorker (placesdealerid " + placesDealerId + ") : " + e);
			System.out.println( e.getClass().getSimpleName() + " in DetailsWorker (placesdealerid " + placesDealerId + ") : " + e);
		}
		
	}

}
