package dao;

import persistence.SFEntry;
import play.db.jpa.JPA;

public class SFEntryDAO {
	
	
	public static SFEntry bySite(Long siteId) {
		
		String query = "from SFEntry sf where sf.mainSite.siteId = :siteId";
		return JPA.em().createQuery(query, SFEntry.class).setParameter("siteId", siteId).getSingleResult();
	}
}
