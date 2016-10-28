package places;

import akka.actor.UntypedActor;
import net.sf.sprockets.google.Place;
import net.sf.sprockets.google.Places;
import net.sf.sprockets.google.Places.Params;
import net.sf.sprockets.google.Places.Response;
import play.db.jpa.JPA;

public class PlacesDetailsWorker extends UntypedActor {

	@Override
	public void onReceive(Object message) throws Exception {
		JPA.withTransaction(() -> {
			long placesDealerId = (long) message;
			PlacesDealer dealer = JPA.em().find(PlacesDealer.class, placesDealerId);
			Response<Place> detailsResponse = Places.details(Params.create().placeId(dealer.getPlacesId()));
			DataBuilder.fillPlacesDealer(dealer, detailsResponse);
		});
		
	}

	
}
