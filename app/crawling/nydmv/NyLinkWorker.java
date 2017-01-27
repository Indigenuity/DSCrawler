package crawling.nydmv;

import java.io.IOException;
import java.util.List;

import akka.actor.UntypedActor;
import dao.GeneralDAO;
import dao.PlacesDealerDao;
import net.sf.sprockets.google.Place;
import net.sf.sprockets.google.Places;
import net.sf.sprockets.google.Places.Params;
import net.sf.sprockets.google.Places.Response;
import newwork.WorkResult;
import newwork.WorkStatus;
import play.db.jpa.JPA;
import salesforce.persistence.SalesforceAccount;

public class NyLinkWorker extends UntypedActor {

	@Override
	public void onReceive(Object message) throws Exception {
		NyLinkWorkOrder workOrder = (NyLinkWorkOrder) message;
		System.out.println("Performing Text Search on : " + message);
		
		JPA.withTransaction( () -> {
			NYDealer dealer = JPA.em().find(NYDealer.class, workOrder.nyDealerId);
			
			linkSalesforce(dealer);
			if(dealer.getLinkStatus() == null || dealer.getLinkStatus().equals("No Link")){
				linkPlaces(dealer);
			}
		});
		
		WorkResult workResult = new WorkResult(workOrder);
		workResult.setWorkStatus(WorkStatus.COMPLETE);
		getSender().tell(workResult, getSelf());
		
	}
	
	private static void linkPlaces(NYDealer nyDealer){
		try{
			Response<List<Place>> resp = Places.textSearch(Params.create().query(nyDealer.getFacilityName() + " " + nyDealer.getStreet() + " " + nyDealer.getCity()));
		
			List<Place> results = resp.getResult();
			
			if(results.size() == 1){
				Place place = results.get(0);
				nyDealer.setPlacesId(place.getPlaceId().getId());
				if(place.getTypes().contains("car_dealer")){
					nyDealer.setLinkStatus("Places Dealer String");
				} else {
					nyDealer.setLinkStatus("Places Non-Dealer String");
				}
			} else if (results.size() > 1){
				nyDealer.setLinkStatus("Multiple Places Results");
			} else {
//				nyDealer.setLinkStatus("No Link");
			}
		} catch(IOException e) {
			nyDealer.setLinkStatus("Error fetching Places");
		}
	}
	
	private static void linkSalesforce(NYDealer nyDealer) {
		List<SalesforceAccount> sfAccounts = GeneralDAO.getList(SalesforceAccount.class, "standardStreet", nyDealer.getStandardStreet());
		if(sfAccounts.size() == 1) {
			nyDealer.setLinkStatus("Single SalesforceAccount Match");
			nyDealer.setSfAccount(sfAccounts.get(0));
		} else if(sfAccounts.size() > 1){
			nyDealer.setLinkStatus("Multiple SalesforceAccount Matches");
		} else {
//			nyDealer.setLinkStatus("No Link");
		}
	}

}
