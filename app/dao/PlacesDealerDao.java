package dao;

import java.util.List;

import javax.persistence.Query;

import play.db.jpa.JPA;

public class PlacesDealerDao {

	public static void insertIgnore(List<String> placesIds){
		placesIds.stream().forEach(PlacesDealerDao::insertIgnore);
	}
	
	public static void insertIgnore(String placesId) {
		String queryString = "insert ignore into placesdealer (placesId) values (:placesId)";
		Query query = JPA.em().createNativeQuery(queryString);
		query.setParameter("placesId", placesId);
		query.executeUpdate();
	}
}
