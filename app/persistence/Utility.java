package persistence;

import java.util.List;

import javax.persistence.TypedQuery;

import play.db.jpa.JPA;

public class Utility {
	
	public static String getPastRedirect(String givenUrl) {
		String query = "from Site s where ? member of s.redirectUrls";
		TypedQuery<Site> q = JPA.em().createQuery(query, Site.class);
		q.setParameter(1, givenUrl);
		List<Site> sites = q.getResultList();
		
		if(sites.size() > 0) {
			Site site = sites.get(0);	//Only care about one of them, since they all have same redirect
			return site.getHomepage();
		}
		
		return null;
	}
	
	public static String getPastApproval(String givenUrl) {
		String query = "from Site s where redirectResolveDate is not null and homepage = ?";
		TypedQuery<Site> q = JPA.em().createQuery(query, Site.class);
		q.setParameter(1, givenUrl);
		List<Site> sites = q.getResultList();
		if(sites.size() > 0) {
			Site site = sites.get(0);
			return site.getHomepage();
		}
		
		return null;
	}

}
