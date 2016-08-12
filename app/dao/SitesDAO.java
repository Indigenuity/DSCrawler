package dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;

import persistence.MobileCrawl;
import persistence.Site;
import persistence.SiteCrawl;
import persistence.UrlCheck;
import persistence.Site.SiteStatus;
import play.db.jpa.JPA;

public class SitesDAO {
	
	private static final long MONTH_IN_MS = 1000 * 60 * 60 * 24 * 31;
	
	
	private static final Date STALE_DATE;
	static {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, - 1);
		STALE_DATE = calendar.getTime();
	}
	
	public static Site markError(Site site) {
		site.setSiteStatus(SiteStatus.OTHER_ISSUE);
		return site;
	}
	
	public static Site markDefunct(Site site) {
		site.setSiteStatus(SiteStatus.DEFUNCT);
		return site;
	}
	
	public static Site approve(Site site) {
		site.setSiteStatus(SiteStatus.APPROVED);
		return site;
	}
	
	public static Site review(Site site) {
		site.setSiteStatus(SiteStatus.NEEDS_REVIEW);
		return site;
	}
	
	public static Site acceptRedirect(Site site, String newHomepage) {
		return applyRedirect(site, newHomepage, true);
	}
	
	public static Site reviewRedirect(Site site, String newHomepage) {
		return applyRedirect(site, newHomepage, false);
	}
	
	public static Site applyRedirect(Site site, String newHomepage, boolean approved){
		Site newSite = new Site(newHomepage);
		if(approved){
			newSite.setSiteStatus(SiteStatus.APPROVED);
		} else {
			newSite.setSiteStatus(SiteStatus.UNVALIDATED);
		}
		JPA.em().persist(newSite);
		site.setSiteStatus(SiteStatus.REDIRECTS);
		site.setForwardsTo(newSite);
		return newSite;
	}
	
	public static Site getOrNew(String homepage) {
		Site site = getFirst("homepage", homepage);
		if(site == null){
			return new Site(homepage);
		}
		return site;
	}
	
	public static Site updateFromUrlCheck(UrlCheck urlCheck){
		String queryString = "from Site s where s.homepage = :seed";
		List<Site> resultList = JPA.em().createQuery(queryString, Site.class).setParameter("seed", urlCheck.getSeed()).getResultList();
		if(resultList.size() < 1){
			return null;
		} else if(resultList.size() > 1) {
			throw new IllegalStateException("Found more than one Site with homepage : " + urlCheck.getSeed());
		}
		Site site = resultList.get(0);
		site.setHomepage(urlCheck.getResolvedSeed());
		
		return site;
	}
	
	
	public static List<String> getDuplicateHomepages(int count, int offset) {
		String query = "select homepage from Site group by homepage having count(*) > 1";
		List<String> dups = JPA.em().createNativeQuery(query).setMaxResults(count).setFirstResult(offset).getResultList();
		return dups;
	}
	public static List<String> getDuplicateDomains(int count, int offset) {
		String query = "select domain from Site where franchise = true and standaloneSite = true group by domain having count(*) > 1";
		List<String> dups = JPA.em().createNativeQuery(query).setMaxResults(count).setFirstResult(offset).getResultList();
		return dups;
	}
	
	public static List<Site> getSitesWithRedirectUrl(String compare, int count, int offset) {
		if(StringUtils.isEmpty(compare)){
			return new ArrayList<Site>();
		}
		String query = "select sr.Site_siteId from site_redirectUrls sr " +
				"join temp t on sr.redirectUrls = '" + compare + "'";
//		System.out.println("query : " + query);
		List<Object> siteIds = JPA.em().createNativeQuery(query).setMaxResults(count).setFirstResult(offset).getResultList();
		
		Set<Site> sites = new HashSet<Site>();
		for(Object id : siteIds) {
			long longId = Long.parseLong(id.toString());
			Site site = JPA.em().find(Site.class, longId);
			sites.add(site);
		}
		List<Site> returned = new ArrayList<Site>(sites);
		return returned;
	}
	
	
	public static List<Site> getSitesWithSimilarRedirectUrl(String compare, int count, int offset) {
		if(StringUtils.isEmpty(compare)){
			return new ArrayList<Site>();
		}
		String query = "select sr.Site_siteId from site_redirectUrls sr " +
				"join temp t on sr.redirectUrls like '%" + compare + "%'";
//		System.out.println("query : " + query);
		
		List<Object> siteIds = JPA.em().createNativeQuery(query).setMaxResults(count).setFirstResult(offset).getResultList();
		
		Set<Site> sites = new HashSet<Site>();
		for(Object id : siteIds) {
			long longId = Long.parseLong(id.toString());
			Site site = JPA.em().find(Site.class, longId);
			sites.add(site);
		}
		List<Site> returned = new ArrayList<Site>(sites);
		return returned;
	}
	
	
	public static Integer getCount(boolean franchise) {
		String query = "select count(*) from Site s where s.franchise = " + franchise;
		return GeneralDAO.getSingleInt(query, false);
	}
	public static List<Site> getAll(boolean franchise, int count, int offset) {
		String query = "from Site s where s.franchise = " + franchise;
		return SitesDAO.getList(query, false, count, offset);
	}
	
	
	
	public static Long getOldHomepagesCount(long crawlSetId){ 
		System.out.println("stale date : " + STALE_DATE);
		String query = "select count(s) from CrawlSet cs join cs.sites s" +  
				" where (s.redirectResolveDate not between :staleDate and current_date or s.redirectResolveDate = null) and cs.crawlSetId = :crawlSetId";
		TypedQuery<Long> q = JPA.em().createQuery(query, Long.class);
		q.setParameter("crawlSetId", crawlSetId);
		q.setParameter("staleDate", STALE_DATE, TemporalType.DATE);
		
		return q.getSingleResult();
	}
	public static List<Site> getOldHomepages(long crawlSetId, int count, int offset) {
		Date monthAgo = new Date(System.currentTimeMillis() - MONTH_IN_MS);
		String query = "select s from CrawlSet cs join cs.sites s" +  
				" where (s.redirectResolveDate > :monthAgo) or s.redirectResolveDate = null) and cs.crawlSetId = :crawlSetId";
		TypedQuery<Site> q = JPA.em().createQuery(query, Site.class);
		q.setParameter("crawlSetId", crawlSetId);
		q.setParameter("monthAgo", monthAgo, TemporalType.DATE);
		
		return q.setFirstResult(offset).setMaxResults(count).getResultList();
	}
	
	
	
	public static Long getOldCrawlsCount(long crawlSetId) {
		Date now = new Date(System.currentTimeMillis());
		Date monthAgo = new Date(now.getTime() -  MONTH_IN_MS);
		String query = "select count(ccc) from CrawlSet cs join cs.completedCrawls ccc where crawlSetId = :crawlSetId and ccc.crawlDate > :monthAgo";
		TypedQuery<Long> q = JPA.em().createQuery(query, Long.class);
		q.setParameter("crawlSetId", crawlSetId);
		q.setParameter("monthAgo", monthAgo, TemporalType.DATE);
		
		return q.getSingleResult();
	}
	//TODO
//	public static List<Site> getOldCrawls(long crawlSetId, int count, int offset) {
//		String query = "select * from Site where siteId in ( " +
//				" SELECT s.siteId FROM ds.site s " +
//				" join site_siteCrawl ssc on ssc.site_siteId = s.siteId " +
//				" join siteCrawl sc on ssc.crawls_sitecrawlId = sc.sitecrawlid " +
//				" where s.franchise = " + franchise +
//				" group by s.siteId having max(sc.crawlDate) < DATE_SUB(NOW(), INTERVAL 2 MONTH)) ";
//		return SitesDAO.getList(query, true, count, offset);
//	}
//	
	
	
	public static Integer getNoCrawlsCount(boolean franchise) {
		String query = "select count(*) from Site s where s.crawls is empty and franchise = " + franchise;
		return GeneralDAO.getSingleInt(query, false);
	}
	
	public static List<Site> getNoCrawls(boolean franchise, int count, int offset) {
		String query = "from Site s where s.crawls is empty and franchise = " + franchise;
		return SitesDAO.getList(query, false, count, offset);
	}
	

	
	public static long getCount(String valueName, Object value){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(valueName , value);
		return getCount(parameters);
	}
	public static long getCount(Map<String, Object> parameters) {
		String query = "select count(s) from Site s where s.siteId > 0";
		for(String key : parameters.keySet()) {
			query += " and s." + key + " = :" + key;
		}
		
		TypedQuery<Long> q = JPA.em().createQuery(query, Long.class);
		for(Entry<String, Object> entry : parameters.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}
		return q.getSingleResult();
	}
	public static List<Site> getList(String valueName, Object value, int count, int offset){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(valueName , value);
		return getList(parameters, count, offset);
	}
	public static List<Site> getList(Map<String, Object> parameters, int count, int offset){
		String query = "from Site s where s.siteId > 0";
		for(String key : parameters.keySet()) {
			query += " and s." + key + " = :" + key;
		}
		
		TypedQuery<Site> q = JPA.em().createQuery(query, Site.class);
		for(Entry<String, Object> entry : parameters.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}
		q.setFirstResult(offset);
		q.setMaxResults(count);
		return q.getResultList();
	}
	
	public static Site getFirst(String valueName, Object value){
		return getFirst(valueName, value, 0);
	}
	
	public static Site getFirst(String valueName, Object value, int offset){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(valueName , value);
		return getFirst(parameters, offset);
	}
	public static Site getFirst(Map<String, Object> parameters, int offset){
		String query = "from Site s where s.siteId > 0";
		for(String key : parameters.keySet()) {
			query += " and s." + key + " = :" + key;
		}
		
		TypedQuery<Site> q = JPA.em().createQuery(query, Site.class);
		for(Entry<String, Object> entry : parameters.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}
		q.setFirstResult(offset);
		q.setMaxResults(1);
		List<Site> results = q.getResultList();
		if(results.size() > 0){
			return results.get(0);
		}
		return null;
	}
	
	
	public static long getCrawlSetCount(long crawlSetId, String valueName, Object value){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(valueName , value);
		return getCrawlSetCount(crawlSetId, parameters);
	}
	public static long getCrawlSetCount(long crawlSetId, Map<String, Object> parameters) {
		String query = "select count(s) from CrawlSet cs join cs.sites s where cs.crawlSetId = :crawlSetId ";
		for(String key : parameters.keySet()) {
			query += " and s." + key + " = :" + key;
		}
		
		TypedQuery<Long> q = JPA.em().createQuery(query, Long.class);
		q.setParameter("crawlSetId", crawlSetId);
		for(Entry<String, Object> entry : parameters.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}
		return q.getSingleResult();
	}
	public static List<Site> getCrawlSetList(long crawlSetId, String valueName, Object value, int count, int offset){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(valueName , value);
		return getCrawlSetList(crawlSetId, parameters, count, offset);
	}
	public  static List<Site> getCrawlSetList(long crawlSetId, Map<String, Object> parameters, int count, int offset){
		String query = "select s from CrawlSet cs join cs.sites s where cs.crawlSetId = :crawlSetId ";
		for(String key : parameters.keySet()) {
			query += " and s." + key + " = :" + key;
		}
		
		TypedQuery<Site> q = JPA.em().createQuery(query, Site.class);
		q.setParameter("crawlSetId", crawlSetId);
		for(Entry<String, Object> entry : parameters.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}
		q.setFirstResult(offset);
		q.setMaxResults(count);
		return q.getResultList();
	}

	
	
	public static List<Site> getList(String query, boolean isNative, int count, int offset) {
		Query q;
		if(isNative) {
			q = JPA.em().createNativeQuery(query, Site.class);
		}
		else {
			q = JPA.em().createQuery(query, Site.class);
		}
		
		if(count > 0){
			q.setMaxResults(count);
		}
		if(offset > 0){
			q.setFirstResult(offset);
		}
		
		return q.getResultList();
	}
}
