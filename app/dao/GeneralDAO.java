package dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.TypedQuery;

import org.apache.poi.ss.formula.functions.T;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import persistence.UsState;
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
	
	public static <T> List<T> getFieldList(Class<T> clazz, Class<?> parentEntityClazz, String fieldName){
		Map<String, Object> parameters = new HashMap<String, Object>();
		return getFieldList(clazz, parentEntityClazz, fieldName, parameters);
	}
	
	public static <T> List<T> getFieldList(Class<T> fieldClazz, Class<?> parentEntityClazz, String fieldName, String valueName, Object value){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(valueName , value);
		return getFieldList(fieldClazz, parentEntityClazz, fieldName, parameters);
	}
	
	public static <T> List<T> getFieldList(Class<T> clazz, Class<?> parentEntityClazz, String fieldName,  Map<String, Object> parameters){
		String queryString = "select t." + fieldName + " from " + parentEntityClazz.getSimpleName() + " t";
		String delimiter = " where t.";
		for(String key : parameters.keySet()) {
			queryString += delimiter + key + " = :" + key;
			delimiter = " and t.";
		}
		TypedQuery<T> q = JPA.em().createQuery(queryString, clazz);
		for(Entry<String, Object> entry : parameters.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}
		return q.getResultList();
	}
	
	public static <T> List<T> getAll(Class<T> clazz){
		String query = "from " + clazz.getSimpleName() + " t";
		TypedQuery<T> q = JPA.em().createQuery(query, clazz);
		return q.getResultList();
	}
	
	public static Long countAll(Class<?> clazz){
		String query = "select count(t) from " + clazz.getSimpleName() + " t";
		TypedQuery<Long> q = JPA.em().createQuery(query, Long.class);
		return q.getSingleResult();
	}
	
	public static List<Long> getAllIds(Class<?> clazz){
		String idName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, clazz.getSimpleName()) + "Id";
		String query = "select t." + idName + " from " + clazz.getSimpleName() + " t";
		TypedQuery<Long> q = JPA.em().createQuery(query, Long.class);
		return q.getResultList();
	}
	
	public static <T> T getFirst(Class<T> clazz, String valueName, Object value){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(valueName , value);
		return getFirst(clazz, parameters);
	}
	public static <T> T getFirst(Class<T> clazz, Map<String, Object> parameters){
		String query = "from " + clazz.getSimpleName() + " t";
		String delimiter = " where t.";
		for(String key : parameters.keySet()) {
			query += delimiter + key + " = :" + key;
			delimiter = " and t.";
		}
		
		TypedQuery<T> q = JPA.em().createQuery(query, clazz);
		for(Entry<String, Object> entry : parameters.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}
		q.setMaxResults(1);
		List<T> results = q.getResultList();
		if(results.size() > 0){
			return results.get(0);
		}
		return null;
	}
	
	public static <T> T getFirstOrNew(Class<T> clazz, String valueName, Object value) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(valueName , value);
		return getFirstOrNew(clazz, parameters);
	}
	public static <T> T getFirstOrNew(Class<T> clazz, Map<String, Object> parameters) {
		T entity = getFirst(clazz, parameters);
		if(entity != null)
			return entity;
		try{
			return clazz.newInstance();
		} catch(Exception e) {
			throw new IllegalArgumentException("Can't instantiate class with illegal constructors : " + clazz.getSimpleName() + " : " + e.getMessage());
		}
	}
	
	public static <T> List<T> getList(Class<T> clazz, String valueName, Object value){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(valueName , value);
		return getListAnd(clazz, parameters);
	}
	
	public static <T> List<T> getList(Class<T> clazz, Map<String, Object> parameters, String operator){
		String query = "from " + clazz.getSimpleName() + " t";
		String delimiter = " where t.";
		for(String key : parameters.keySet()) {
			query += delimiter + key + " = :" + key;
			delimiter = " " + operator + " t.";
		}
		
		TypedQuery<T> q = JPA.em().createQuery(query, clazz);
		for(Entry<String, Object> entry : parameters.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}
		return q.getResultList();
	}
	public static <T> List<T> getListAnd(Class<T> clazz, Map<String, Object> parameters){
		return getList(clazz, parameters, "and");
	}
	public static <T> List<T> getListOr(Class<T> clazz, Map<String, Object> parameters){
		return getList(clazz, parameters, "or");
	}
	public static <T> List<T> getList(Class<T> clazz, Multimap<String, Object> parameters, String operator){
		String query = "from " + clazz.getSimpleName() + " t";
		String delimiter = " where t.";
		int suffix = 0;
		for(Entry<String, Object> entry : parameters.entries()) {
			query += delimiter + entry.getKey() + " = :" + entry.getKey() + String.valueOf(suffix);
			suffix++;
			delimiter = " " + operator + " t.";
		}
		System.out.println("query : " + query);
		suffix = 0;
		TypedQuery<T> q = JPA.em().createQuery(query, clazz);
		for(Entry<String, Object> entry : parameters.entries()) {
			q.setParameter(entry.getKey() + suffix, entry.getValue());
			suffix++;
		}
		return q.getResultList();
	}
	public static <T> List<T> getListAnd(Class<T> clazz, Multimap<String, Object> parameters){
		return getList(clazz, parameters, "and");
	}
	public static <T> List<T> getListOr(Class<T> clazz, Multimap<String, Object> parameters){
		return getList(clazz, parameters, "or");
	}
	public static <T> List<Long> getKeyList(Class<T> clazz, String keyName, String valueName, Object value) {
		Multimap<String, Object> parameters = ArrayListMultimap.create();
		parameters.put(valueName, value);
		return getKeyList(clazz, keyName, parameters, "and");
	}
	public static <T> List<Long> getKeyList(Class<T> clazz, String keyName, Multimap<String, Object> parameters, String operator){
		String query = "select t." + keyName + " from " + clazz.getSimpleName() + " t";
		String delimiter = " where t.";
		int suffix = 0;
		for(Entry<String, Object> entry : parameters.entries()) {
			query += delimiter + entry.getKey() + " = :" + entry.getKey() + String.valueOf(suffix);
			suffix++;
			delimiter = " " + operator + " t.";
		}
		System.out.println("query : " + query);
		suffix = 0;
		TypedQuery<Long> q = JPA.em().createQuery(query, Long.class);
		for(Entry<String, Object> entry : parameters.entries()) {
			q.setParameter(entry.getKey() + suffix, entry.getValue());
			suffix++;
		}
		return q.getResultList();
	}
	public static <T> List<Long> getKeyListAnd(Class<T> clazz, String keyName, Multimap<String, Object> parameters){
		return getKeyList(clazz, keyName, parameters, "and");
	}
	public static <T> List<Long> getKeyListOr(Class<T> clazz, String keyName, Multimap<String, Object> parameters){
		return getKeyList(clazz, keyName, parameters, "or");
	}
	
	
	public static Long getCount(Class<?> clazz, String valueName, Object value){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(valueName , value);
		return getCount(clazz, parameters);
	}
	public static <T> Long getCount(Class<T> clazz, Map<String, Object> parameters){
		String query = "select count(t) from " + clazz.getSimpleName() + " t";
		String delimiter = " where t.";
		for(String key : parameters.keySet()) {
			query += delimiter + key + " = :" + key;
			delimiter = " and t.";
		}
		TypedQuery<Long> q = JPA.em().createQuery(query, Long.class);
		for(Entry<String, Object> entry : parameters.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}
		return q.getSingleResult();
	}
	
	public static void refreshUsStates(){
		String query = "delete from UsState s";
		JPA.em().createQuery(query).executeUpdate();
		query = "insert into usState (stateName, stateCode, projectCode)"
				+ "values "
				+ "('Alabama', 'AL', 'none'),"
				+ "('Alaska', 'AK', 'none'),"
				+ "('Arizona', 'AZ', 'none'),"
				+ "('Arkansas', 'AR', 'none'),"
				+ "('California', 'CA', 'none'),"
				+ "('Colorado', 'CO', 'none'),"
				+ "('Connecticut', 'CT', 'none'),"
				+ "('Delaware', 'DE', 'none'),"
				+ "('District of Columbia', 'DC', 'none'),"
				+ "('Florida', 'FL', 'none'),"
				+ "('Georgia', 'GA', 'none'),"
				+ "('Hawaii', 'HI', 'none'),"
				+ "('Idaho', 'ID', 'none'),"
				+ "('Illinois', 'IL', 'none'),"
				+ "('Indiana', 'IN', 'none'),"
				+ "('Iowa', 'IA', 'none'),"
				+ "('Kansas', 'KS', 'none'),"
				+ "('Kentucky', 'KY', 'none'),"
				+ "('Louisiana', 'LA', 'none'),"
				+ "('Maine', 'ME', 'none'),"
				+ "('Maryland', 'MD', 'none'),"
				+ "('Massachusetts', 'MA', 'none'),"
				+ "('Michigan', 'MI', 'none'),"
				+ "('Minnesota', 'MN', 'none'),"
				+ "('Mississippi', 'MS', 'none'),"
				+ "('Missouri', 'MO', 'none'),"
				+ "('Montana', 'MT', 'none'),"
				+ "('Nebraska', 'NE', 'none'),"
				+ "('Nevada', 'NV', 'none'),"
				+ "('New Hampshire', 'NH', 'none'),"
				+ "('New Jersey', 'NJ', 'none'),"
				+ "('New Mexico', 'NM', 'none'),"
				+ "('New York', 'NY', 'none'),"
				+ "('North Carolina', 'NC', 'none'),"
				+ "('North Dakota', 'ND', 'none'),"
				+ "('Ohio', 'OH', 'none'),"
				+ "('Oklahoma', 'OK', 'none'),"
				+ "('Oregon', 'OR', 'none'),"
				+ "('Pennsylvania', 'PA', 'none'),"
				+ "('Rhode Island', 'RI', 'none'),"
				+ "('South Carolina', 'SC', 'none'),"
				+ "('South Dakota', 'SD', 'none'),"
				+ "('Tennessee', 'TN', 'none'),"
				+ "('Texas', 'TX', 'none'),"
				+ "('Utah', 'UT', 'none'),"
				+ "('Vermont', 'VT', 'none'),"
				+ "('Virginia', 'VA', 'none'),"
				+ "('Washington', 'WA', 'none'),"
				+ "('West Virginia', 'WV', 'none'),"
				+ "('Wisconsin', 'WI', 'none'),"
				+ "('Wyoming', 'WY', 'none');";
		JPA.em().createNativeQuery(query).executeUpdate();
	}
	
	public static void refreshCaProvinces(){
		String query = "delete from CaProvince c";
		JPA.em().createQuery(query).executeUpdate();
		query = "INSERT INTO `caProvince` (`provinceName`, `provinceCode`, `projectCode`)"
				+ " VALUES " 
				+ "('Alberta', 'AB', 'none'),"
    			+ "('British Columbia', 'BC', 'none'),"
    			+ "('Manitoba', 'MB', 'none'),"
    			+ "('New Brunswick', 'NB', 'none'),"
    			+ "('Newfoundland and Labrador', 'NL', 'none'),"
    			+ "('Northwest Territories', 'NT', 'none'),"
    			+ "('Nova Scotia', 'NS', 'none'),"
    			+ "('Nunavut', 'NU', 'none'),"
    			+ "('Ontario', 'ON', 'none'),"
    			+ "('Prince Edward Island', 'PE', 'none'),"
    			+ "('Quebec', 'QC', 'none'),"
    			+ "('Saskatchewan', 'SK', 'none'),"
    			+ "('Yukon', 'YT', 'none');";
		JPA.em().createNativeQuery(query).executeUpdate();
	}
}
