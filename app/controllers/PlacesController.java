package controllers;

import java.io.IOException;
import java.util.List;

import places.DataBuilder;
import places.PlacesDealer;
import places.Retriever;
import play.Logger;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class PlacesController extends Controller { 
	
	final static Logger.ALogger dsLogger = Logger.of("ds");

	@Transactional
    public static Result refreshZipcodeDatabase() throws IOException {
		DataBuilder.refreshZipcodeDatabase();
        return ok("US Zipcodes Refreshed"); 
    }
	
	@Transactional
	public static Result placesDashboard() {
		return ok(views.html.places.placesDashboard.render());
	}
	
	@Transactional
	public static Result importCanadaList() throws IOException {
		Retriever.retrieveCanada();
		return ok("Retrieved Canada Places");
	}
	
	@Transactional
	public static Result importUsList() throws IOException {
		Retriever.retrieveUs();
		return ok("Retrieved US Places");
	}
	
	@Transactional
	public static Result fillBlankDetails() throws IOException {
		PlacesDealer wtf = JPA.em().find(PlacesDealer.class, 8938L);
		System.out.println("name ; " + wtf.getName());
		List<PlacesDealer> places = JPA.em().createQuery("from PlacesDealer pd where pd.name is null", PlacesDealer.class).getResultList();
		System.out.println("places : " + places.size());
		int count = 0;
		for(PlacesDealer place : places) {
//			System.out.println("Name : " + place.getName() + "(" + place.getPlacesId() + ")");
			
			Retriever.retrieveDetails(place.getPlacesId());
			count++;
			if(count % 50 == 0){
				System.out.println("count : " + count);
				JPA.em().getTransaction().commit();
				JPA.em().getTransaction().begin();
			}
		}
		return ok();
	}
}
