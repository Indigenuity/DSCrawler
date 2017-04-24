package dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.TypedQuery;

import datadefinitions.newdefinitions.WPAttribution;
import global.Global;
import persistence.Site;
import persistence.Site.SiteStatus;
import play.db.jpa.JPA;
import salesforce.persistence.SalesforceAccount;



public class SalesforceDao {
	
	public static SalesforceAccount getFirstFresh(String valueName, Object value){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(valueName , value);
		return getFirstFresh(parameters);
	}
	public static SalesforceAccount getFirstFresh(Map<String, Object> parameters){
		String query = "from SalesforceAccount t";
		String delimiter = " where t.";
		for(String key : parameters.keySet()) {
			query += delimiter + key + " = :" + key;
			delimiter = " and t.";
		}
		query += " and t.lastUpdated < :staleDate";
		
		TypedQuery<SalesforceAccount> q = JPA.em().createQuery(query, SalesforceAccount.class);
		for(Entry<String, Object> entry : parameters.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}
		q.setParameter("staleDate", Global.getStaleDate());
		q.setMaxResults(1);
		List<SalesforceAccount> results = q.getResultList();
		if(results.size() > 0){
			return results.get(0);
		}
		return null;
	}
	
	public static List<Long> getUsFranchiseSites(){
		String queryString = "select distinct(s.siteId) from SalesforceAccount sa join sa.site s where sa.country = 'United States' and sa.franchise = true and s.siteStatus = :siteStatus";
		List<Long> siteIds = JPA.em().createQuery(queryString, Long.class).setParameter("siteStatus", SiteStatus.APPROVED).getResultList();
		return siteIds;
	}
	
	public static List<Long> findByWpAttribution(WPAttribution wp){
		String queryString = "select sa.salesforceAccountId from SalesforceAccount sa where :wp member of sa.site.mostRecentCrawl.siteCrawlAnalysis.wpAttributions) ";
		List<Long> siteIds = JPA.em().createQuery(queryString, Long.class).setParameter("wp", wp).getResultList();
		return siteIds;
	}

	
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
	
	public static List<SalesforceAccount> findBySite(Site site) {
		String queryString = "from SalesforceAccount sa where sa.site = :site";
		return JPA.em().createQuery(queryString, SalesforceAccount.class).setParameter("site",  site).getResultList();
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
