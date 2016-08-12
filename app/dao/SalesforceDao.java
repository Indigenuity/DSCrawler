package dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.TypedQuery;

import persistence.salesforce.SalesforceAccount;
import play.db.jpa.JPA;



public class SalesforceDao {

	
	public static List<SalesforceAccount> getList(String valueName, Object value){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(valueName , value);
		return getList(parameters);
	}
	public static List<SalesforceAccount> getList(Map<String, Object> parameters){
		String query = "select sa from SalesforceAccount sa join sa.site s";
		String delimiter = " where s.";
		for(String key : parameters.keySet()) {
			query += delimiter + key + " = :" + key;
			delimiter = " and s.";
		}
		
		TypedQuery<SalesforceAccount> q = JPA.em().createQuery(query, SalesforceAccount.class);
		for(Entry<String, Object> entry : parameters.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}
		return q.getResultList();
	}
	
	//Searches based on the sites of SAlesforceAccounts
	public static Long getCount(String valueName, Object value){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(valueName , value);
		return getCount(parameters);
	}
	public static Long getCount(Map<String, Object> parameters){
		String query = "select count(sa) from SalesforceAccount sa join sa.site s";
		String delimiter = " where s.";
		for(String key : parameters.keySet()) {
			query += delimiter + key + " = :" + key;
			delimiter = " and s.";
		}
		
		TypedQuery<Long> q = JPA.em().createQuery(query, Long.class);
		for(Entry<String, Object> entry : parameters.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}
		return q.getSingleResult();
	}
}
