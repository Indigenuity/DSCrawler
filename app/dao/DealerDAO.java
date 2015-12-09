package dao;

import java.util.List;

import javax.persistence.Query;

import persistence.Dealer;
import persistence.Site;
import play.db.jpa.JPA;

public class DealerDAO {
	
	public static List<Dealer> bySite(Site site) {
		String query = "from Dealer d where d.mainSite.siteId = " + site.getSiteId();
		return getList(query, false, 0,0);
	}
	
	public static List<Dealer> getList(String query, boolean isNative, int count, int offset) {
		Query q;
		if(isNative) {
			q = JPA.em().createNativeQuery(query, Dealer.class);
		}
		else {
			q = JPA.em().createQuery(query, Dealer.class);
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
