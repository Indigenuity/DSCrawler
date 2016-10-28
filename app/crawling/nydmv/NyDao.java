package crawling.nydmv;

import java.util.List;

import play.db.jpa.JPA;

public class NyDao {

	
	public static County countyGetOrNew(String name) {
		String queryString = "from County c where c.name = :name";
		List<County> counties = JPA.em().createQuery(queryString, County.class).setParameter("name", name).getResultList();
		
		if(counties.size() < 1){
			County county = new County(name);
			JPA.em().persist(county);
			return county;
		}
		return counties.get(0);
	}
	
	public static NYDealer dealerGetOrNew(String facilityNumber) {
		String queryString = "from NYDealer c where c.facilityNumber = :facilityNumber";
		List<NYDealer> dealers = JPA.em().createQuery(queryString, NYDealer.class).setParameter("facilityNumber", facilityNumber).getResultList();
		
		if(dealers.size() < 1){
			NYDealer dealer = new NYDealer();
			dealer.setFacilityNumber(facilityNumber);
			JPA.em().persist(dealer);
			return dealer;
		}
		return dealers.get(0);
	}
}
