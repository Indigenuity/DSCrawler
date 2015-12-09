package datatransfer;

import java.util.ArrayList;
import java.util.List;

import persistence.CapEntry;
import persistence.Dealer;
import persistence.PlacesDealer;
import persistence.Dealer.Datasource;
import persistence.SFEntry;
import persistence.Site;
import play.db.jpa.JPA;
import utilities.DSFormatter;

public class SourceSwapper {

	static int count = 0;
	static int total = 0;
	public static Dealer toNative(CapEntry capEntry) {
		
		Dealer dealer = new Dealer();
		dealer.setCapdb(capEntry.lead_no);
		dealer.setDatasource(Datasource.CapDB);
		dealer.setDealerName(capEntry.dealershipName);
		dealer.setFranchise(capEntry.franchise);
		dealer.setNiada(capEntry.niada);
		
		if(!DSFormatter.isEmpty(capEntry.website)){
			Site site = new Site();
			count++;
			site.setHomepage(capEntry.website);
			dealer.setMainSite(site);
		}
		
		return dealer;
	}
	
	public static List<Dealer> toNative(List<CapEntry> capEntries) {
		List<Dealer> dealers = new ArrayList<Dealer>();
		for(CapEntry capEntry : capEntries) {
			total++;
			dealers.add(toNative(capEntry));
		}
		System.out.println("count : " + count);
		System.out.println("total : " + total);
		return dealers;
	}
	
	public static void saveAllCapToNative() {
		String query = "from CapEntry ce where not exists (select d.capdb from Dealer d where d.capdb = ce.lead_no)";
		System.out.println("executing query");
		List<CapEntry> entries = JPA.em().createQuery(query, CapEntry.class).getResultList();
		System.out.println("size : " + entries.size());
		List<Dealer> dealers = toNative(entries);
		for(Dealer dealer : dealers) {
			JPA.em().persist(dealer);
		}
	}
	
	public static void placesToNative(){
		String query = "from PlacesDealer pd where pd.website is not null and not exists (from Site s where s.domain = pd.domain)";
    	List<PlacesDealer> places = JPA.em().createQuery(query, PlacesDealer.class).getResultList();
    	System.out.println("places size : " + places.size());
    	int count = 0;
    	for(PlacesDealer place : places) {
    		System.out.println("count : " + ++count);
    		Dealer dealer = new Dealer();
    		dealer.setPlacesId(place.getPlacesId());
    		dealer.setDatasource(Datasource.GooglePlacesAPI);
    		dealer.setDealerName(place.getName());
    		Site site = new Site();
    		site.setHomepage(place.getWebsite());
    		dealer.setMainSite(site);
    		JPA.em().persist(dealer);
    	}
	}
	
	public static void entryToDealer() {
		String query = "from SFEntry sf";
		List<SFEntry> entries = JPA.em().createQuery(query, SFEntry.class).getResultList();
		System.out.println("entries : " + entries.size());
		int count = 0;
		
		for(SFEntry entry : entries) {
			Dealer dealer = new Dealer();
			dealer.setDealerName(entry.getName());
			dealer.setSfId(entry.getAccountId());
			dealer.setDatasource(Datasource.SalesForce);
			dealer.setFranchise(true);
			dealer.setMainSite(entry.getMainSite());
			JPA.em().persist(dealer);
			if(count++ % 100 == 0) {
				System.out.println("count : " + count);
				JPA.em().getTransaction().commit();
				JPA.em().getTransaction().begin();
			}
		}
		
	}
}
