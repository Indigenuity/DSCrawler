package dao;

import java.util.List;

import javax.persistence.Query;

import places.PlacesPage;
import play.db.jpa.JPA;

public class PlacesPageDAO {
	
	public static List<PlacesPage> getByWebsite(String website){
		String query = "from PlacesPage pp where pp.website = :website";
		Query q = JPA.em().createQuery(query);
		q.setParameter("website", website);
		List<PlacesPage> places = q.getResultList();
		
		return places;
	}

}
