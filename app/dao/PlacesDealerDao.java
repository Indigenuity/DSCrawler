package dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import places.PlacesDealer;
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
	
	public static PlacesDealer findByPlacesId(String placesId) {
		String queryString = "from PlacesDealer pd where pd.placesId = :placesId";
		List<PlacesDealer> resultList = JPA.em().createQuery(queryString, PlacesDealer.class).setParameter("placesId", placesId).getResultList();
		if(resultList.size() > 0){
			return resultList.get(0);
		}
		return null;
	}
	
	public static List<PlacesDealer> siteless(){
		String queryString = "from PlacesDealer pd where pd.site is null";
		return JPA.em().createQuery(queryString, PlacesDealer.class).getResultList();
	}
	
	
	
	
	/*******Stats ***********/
	
	public static Long countUsZips(){
		String queryString = "select count(z) from ZipLocation z";
		return JPA.em().createQuery(queryString, Long.class).getSingleResult();
	}
	
	public static Long countCanadaPostals(){
		String queryString = "select count(cp) from CanadaPostal cp";
		return JPA.em().createQuery(queryString, Long.class).getSingleResult();
	}
	
	public static Long countPlacesDealers(){
		String queryString = "select count(pd) from PlacesDealer pd";
		return JPA.em().createQuery(queryString, Long.class).getSingleResult();
	}
	
	public static Long countOldUsZips(){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		Date monthAgo = cal.getTime();
		String queryString = "select count(z) from ZipLocation z where z.dateFetched is null or z.dateFetched < :monthAgo";
		return JPA.em().createQuery(queryString, Long.class).setParameter("monthAgo", monthAgo).getSingleResult();
	}
	
	public static Long countOldCanadaPostals(){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		Date monthAgo = cal.getTime();
		String queryString = "select count(cp) from CanadaPostal cp where cp.dateFetched is null or cp.dateFetched < :monthAgo";
		return JPA.em().createQuery(queryString, Long.class).setParameter("monthAgo", monthAgo).getSingleResult();
	}
	
	public static Long countOldPlacesDealers(){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		Date monthAgo = cal.getTime();
		String queryString = "select count(pd) from PlacesDealer pd where pd.detailFetchDate is null or pd.detailFetchDate < :monthAgo";
		return JPA.em().createQuery(queryString, Long.class).setParameter("monthAgo", monthAgo).getSingleResult();
	}
	
	
	
	
}
