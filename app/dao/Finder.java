package dao;

import java.util.List;

import persistence.CapEntry;
import persistence.Dealer;
import persistence.Site;
import play.db.jpa.JPA;
import utilities.DSFormatter;

public class Finder {
	
	public static List<Site> findSiteByDomain(String url){
		String domain = DSFormatter.getDomain(url);
		String query = "from Site s where s.domain = '" + domain +"'";
		List<Site> results = JPA.em().createQuery(query, Site.class).getResultList();
		
		return results;
	}

	public static List<Dealer> findDealerByName(String name) {
		name = name.replaceAll("'", "''");
		String query = "from Dealer d where d.dealerName like '" + name + "%'";
		List<Dealer> results = JPA.em().createQuery(query, Dealer.class).getResultList();
		return results;
	}
	
	
	public static List<CapEntry> findCapEntryByCapdb(String capdb) {
		String query = "from CapEntry ce where ce.lead_no = :capdb";
		List<CapEntry> results = JPA.em().createQuery(query, CapEntry.class).setParameter("capdb", capdb).getResultList();
		
		return results;
	}
	
	
	public static List<Dealer> findDealerByDomain(String url){
		String domain = DSFormatter.getDomain(url);
		String query = "from Dealer d join d.mainSite s where s.domain = '" + domain +"'";
		List<Dealer> results = JPA.em().createQuery(query, Dealer.class).getResultList();
		
		return results;
	}
	
	
	
}
