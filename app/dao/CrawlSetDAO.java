package dao;

import java.util.List;

import javax.persistence.Query;

import persistence.CrawlSet;
import persistence.Site;
import play.db.jpa.JPA;

public class CrawlSetDAO {
	
	public static List<CrawlSet> bySite(Site site) {
		String query = "select cs from CrawlSet cs join cs.sites css on css.siteId =  " + site.getSiteId();
		return getList(query, false, 0,0);
	}
	
	public static List<CrawlSet> getList(String query, boolean isNative, int count, int offset) {
		Query q;
		
		if(isNative) {
			q = JPA.em().createNativeQuery(query, CrawlSet.class);
		}
		else {
			q = JPA.em().createQuery(query, CrawlSet.class);
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
