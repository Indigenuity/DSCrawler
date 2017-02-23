package audit;

import async.async.Asyncleton;
import async.functionalwork.JpaFunctionalBuilder;
import crawling.projects.BasicDealer;
import dao.GeneralDAO;
import places.PlacesDealer;
import salesforce.persistence.SalesforceAccount;
import utilities.DSFormatter;

public class Standardizer {

	public static void standardize(BasicDealer dealer){
		dealer.setStdPhone(DSFormatter.standardizePhone(dealer.getPhone()));
		dealer.setStdState(DSFormatter.standardizeState(dealer.getState()));
		dealer.setStdStreet(DSFormatter.standardizeStreetAddress(dealer.getStreet()));
		dealer.setStdCountry(DSFormatter.standardizeCountry(dealer.getCountry()));
		if(dealer.getStdCountry().equals("United States")){
			dealer.setStdPostal(DSFormatter.standardizeZip(dealer.getPostal()));
		} else if(dealer.getStdCountry().equals("Canada")){
			dealer.setStdPostal(DSFormatter.standardizeCanadaPostal(dealer.getPostal()));
		}
	}
	
	public static void standardize(SalesforceAccount account){
		account.setStdPhone(DSFormatter.standardizePhone(account.getPhone()));
		account.setStdState(DSFormatter.standardizeState(account.getState()));
		account.setStdStreet(DSFormatter.standardizeStreetAddress(account.getStreet()));
	}
	
	public static void standardize(PlacesDealer dealer){
		dealer.setStdPhone(DSFormatter.standardizePhone(dealer.getFormattedPhoneNumber()));
		dealer.setStdProvince(DSFormatter.standardizeState(dealer.getProvince()));
		dealer.setStdStreet(DSFormatter.standardizeStreetAddress(dealer.getStreet()));
		dealer.setStdCountry(DSFormatter.standardizeCountry(dealer.getCountry()));
		if(dealer.getStdCountry().equals("United States")){
			dealer.setStdPostal(DSFormatter.standardizeZip(dealer.getPostal()));
		} else if(dealer.getStdCountry().equals("Canada")){
			dealer.setStdPostal(DSFormatter.standardizeCanadaPostal(dealer.getPostal()));
		}
	}
	
	public static void standardizeBasicDealers() {
		for(BasicDealer dealer :GeneralDAO.getAll(BasicDealer.class)){
			standardize(dealer);
		}
	}
	
	public static void standardizeSalesforceAccounts() {
		Asyncleton.getInstance().runConsumerMaster(50, 
				JpaFunctionalBuilder.wrapConsumerInFind(Standardizer::standardize, SalesforceAccount.class),
				GeneralDAO.getAllIds(SalesforceAccount.class).stream(), 
				true);
	}
	
	public static void standardizePlacesDealers(){
		Asyncleton.getInstance().runConsumerMaster(50, 
				JpaFunctionalBuilder.wrapConsumerInFind(Standardizer::standardize, PlacesDealer.class),
				GeneralDAO.getAllIds(PlacesDealer.class).stream(), 
				true);
	}
}
