package dao;

import play.db.jpa.JPA;

public class GeneralDAO {

	
	public static Integer getSingleInt(String query, boolean isNative) {
		return Integer.parseInt(GeneralDAO.getSingleString(query, isNative));
	}

	public static String getSingleString(String query, boolean isNative) {
		return GeneralDAO.getSingleObject(query, isNative).toString();
	}

	public static Object getSingleObject(String query, boolean isNative) {
		if(isNative) {
			return JPA.em().createNativeQuery(query).getSingleResult();
		}
		return JPA.em().createQuery(query).getSingleResult();
	}
	
}
