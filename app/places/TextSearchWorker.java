package places;

import akka.actor.UntypedActor;
import crawling.projects.BasicDealer;
import net.sf.sprockets.google.Place;
import net.sf.sprockets.google.Places;
import net.sf.sprockets.google.Places.Params;
import net.sf.sprockets.google.Places.Response;
import play.Logger;
import play.db.jpa.JPA;

public class TextSearchWorker extends UntypedActor {

	@Override
	public void onReceive(Object message) throws Exception {
		BasicDealer dealer = (BasicDealer) message;
		System.out.println("Text Searching for dealer : " + dealer.getBasicDealerId());
		try{
			Place place = PlacesMaster.getDealerSearchMatch(dealer.getName(), dealer.getStreet(), dealer.getCity(), dealer.getState());
			if(place == null){
				return;
			}
			String placesId = place.getPlaceId().getId();
			
			
			
			JPA.withTransaction(() ->{
				PlacesDealer placesDealer = PlacesLogic.updateOrNew(placesId);
				dealer.setWebsite(placesDealer.getWebsiteString());
				dealer.setForeignIdentifier(placesDealer.getPlacesDealerId());
				dealer.setForeignIdentifierString(placesDealer.getPlacesId());
				dealer.setForeignType("Places");
				dealer.setCustom1(placesDealer.getGoogleUrl());
				JPA.em().merge(dealer);
			});
		}
		catch(Exception e){
			Logger.error( e.getClass().getSimpleName() + " in TextSearchWorker (dealerid " + dealer.getBasicDealerId() + ") : " + e);
			System.out.println( e.getClass().getSimpleName() + " in TextSearchWorker (dealerid " + dealer.getBasicDealerId() + ") : " + e);
		}
		
	}

}
