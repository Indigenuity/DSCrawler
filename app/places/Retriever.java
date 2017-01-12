package places;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import play.Logger;
import play.db.jpa.JPA;
import net.sf.sprockets.google.Place;
import net.sf.sprockets.google.Places;
import net.sf.sprockets.google.Places.Params;
import net.sf.sprockets.google.Places.Response;

import com.google.gson.Gson;

import akka.actor.ActorRef;
import async.async.Asyncleton;

public class Retriever {

	
	public static void startingOut() throws IOException {
		 
		EntityManager em = JPA.em();
//		Response<List<Place>> bob = Places.nearbySearch(new Params().pageToken("CqQDmAEAALDdzSWERZKtm_z8zZycn5xJ-44uUboAHGUGF-6SJshxXWd22zwJoeexckYQUAQ6vYVq2spvxJErc9wAeDcQ8_rKnFXF3wPHp1Oj8ghKP2N9Q5_UzsTCiZ4h80LE2B8PcADaqLp3EyvdAcuQT6DnKacPWJ7Z6A5rtdoTMh8f6GWt0yV2Zdkl-jm93rEFl3_tIRvFGuUNL4B-fNFRg9sqSxVear8d5wlDdWP24K7T8pkmrg0mfbHQAxukNPzRQvv_sP1UvYFM6RS-2wIe96sjv3yVNraKGnHxknxoS5JwNdtrjNqBGPXj8sLyO7TvxWbeMQnwFBVEd4JjwG6ZXhZVaNeu_fvCR84m7WVd9FFDSXVyhMcZDrmdb41OeMQTaHrdl5hHPttq9D2RiK04jCOF7bhRGVpXQV31XkSuuRXfSUWeIUMVd-4ChImgrZ3zeESZ7yfcHDbCPObqQtqhKeVHWgHl-WuWHT3mtjzQmt6f6BhkzFqkHd2SnOWSGOWysBoGySt_FLpT7QDoDuMdl2r0-Jy_PR-b9kQwlJqmDw8NatdeEhA8-8R1VsmsxAAL7owK7S5uGhTDeGprpTOBCD6Hf48nlhJCLn0MHA"));
//		System.out.println("size : " + bob.getResult().size());
//		System.out.println("page : " + bob.getNextPageToken());
		//('AL', 'AP', 'AR', 'AZ', 'CA', 'CO', 'CT', 'DE', 'FL', 'GA', 'HI', 'ID', 'IL', 'IN', 'IA')
		int maxResults = 10000;
		int offset = 38300;
		String queryString = "from ZipLocation z";
		List<ZipLocation> zips = em.createQuery(queryString, ZipLocation.class).setMaxResults(maxResults).setFirstResult(offset).getResultList();
		System.out.println("size : " + zips.size());
		int count = 0;
		for(ZipLocation zip : zips) {
//			if(count < 10){
				try{
					System.out.println("Retrieving information for zip : " + zip.zip + "(" + ++count + " of " + zips.size() + ")");
					retrieveForLocation(zip.latitude, zip.longitude);
				}
				catch(Exception e) {
					Logger.error("Error while retrieving information for zip : " + zip.zip);
				}
				em.getTransaction().commit();	//Would be very large transaction without this
				em.getTransaction().begin();
//			}
		}
		
	}
	
	public static void retrieveCanada() throws IOException {
		int maxResults = 5000;
		int offset = 1200;
		String queryString = "from CanadaPostal cp";
		List<CanadaPostal> postals = JPA.em().createQuery(queryString, CanadaPostal.class).setMaxResults(maxResults).setFirstResult(offset).getResultList();
		System.out.println("size : " + postals.size());
		int count = 0;
		for(CanadaPostal postal : postals) {
				System.out.println("Retrieving information for postal : " + postal.code + "(" + ++count + " of " + postals.size() + ")");
				List<Place> places = retrieveForLocation(postal.latitude, postal.longitude);
				DataBuilder.importPlaces(places);
				
				JPA.em().getTransaction().commit();	//Would be very large transaction without this
				JPA.em().getTransaction().begin();
		}
	}
	
	public static void retrieveUs() throws IOException {
//		int maxResults = 50000;
//		int offset = 2153 + 2294  + 575 + 780 + 1739 + 30000;
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		Date monthAgo = cal.getTime();
		
		String queryString = "from ZipLocation zl where zl.dateFetched is null or zl.dateFetched < :monthAgo";
		List<ZipLocation> zips = JPA.em().createQuery(queryString, ZipLocation.class).setParameter("monthAgo", monthAgo).getResultList();
		System.out.println("size : " + zips.size());
		ActorRef master = Asyncleton.getInstance().getGenericMaster(50, PostalSearchWorker.class);
		
		int count = 0;
		for(ZipLocation zip : zips) {
			
			master.tell(zip, ActorRef.noSender());
//			try{
//				System.out.println("Retrieving information for zip : " + zip.zip + "(" + ++count + " of " + zips.size() + ")");
//				List<Place> places = retrieveForLocation(zip.latitude, zip.longitude);
//				DataBuilder.importPlaces(places);
//				
//				JPA.em().getTransaction().commit();	//Would be very large transaction without this
//				JPA.em().getTransaction().begin();
//			} catch(Exception e){
//				System.out.println("Error retrieving for zip : " + zip.zip);
//				Logger.error("Error retrieving for zip : " + zip.zip);
//			}
		}
	}
	
	public static void retrieveDetails() {
		String queryString = "from PlacesDealer pd where pd.website is null or pd.website = ''";
		System.out.println("Retrieving PlacesDealers to retrieve details");
		List<PlacesDealer> dealers = JPA.em().createQuery(queryString, PlacesDealer.class).setMaxResults(1).getResultList();
		System.out.println("Dealers : " + dealers.size());
		ActorRef master = Asyncleton.getInstance().getGenericMaster(50, DetailsWorker.class);
		for(PlacesDealer dealer : dealers) {
			master.tell(dealer.getPlacesDealerId(), ActorRef.noSender());
		}
		
	}
	
	public static List<Place> retrieveForLocation(double latitude, double longitude) throws IOException{
		Response<List<Place>> resp = Places.nearbySearch(Params.create().latitude(latitude).longitude(longitude).type("car_dealer"));
		String nextPageToken = null;
		List<Place> results = new ArrayList<Place>();
		int pageNum = 0;
		do {
			pageNum++;
			
			List<Place> searchPlaces = resp.getResult();
			System.out.println("Results on page " + pageNum + " : " + searchPlaces.size());
			nextPageToken = resp.getNextPageToken();
			resp = Places.nearbySearch(Params.create().pageToken(nextPageToken));
			
			results.addAll(searchPlaces);
		}
		while(nextPageToken != null);
		
		return results;
	}
	

	//Check our database to see if the details have already been stored for this PlacesId
	public static boolean isDetailsRecorded(String placesId){
//		String queryString = "SELECT COUNT(pd.id) FROM PlacesDealer pd where placesId = '" + placesId + "'";
//		Query q = JPA.em().createQuery(queryString);
//		long count = (long)q.getSingleResult();
//		
//		return count > 0;
		return false;
	}
	
	public static void retrieveDetails(String placesId) throws IOException {
		Response<Place> detailsResponse = Places.details(Params.create().placeId(placesId));
		PlacesDealer dealer = DataBuilder.getPlacesDealer(detailsResponse);
		JPA.em().merge(dealer);
	}
	
	public static void retrieveDetails(PlacesDealer dealer) throws IOException {
		Response<Place> detailsResponse = Places.details(Params.create().placeId(dealer.getPlacesId()));
		DataBuilder.fillPlacesDealer(dealer, detailsResponse);
	}
}
