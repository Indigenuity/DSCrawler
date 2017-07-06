package crawling.projects;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import dao.GeneralDAO;
import dao.SalesforceDao;
import places.PlacesDealer;
import play.db.jpa.JPA;
import salesforce.persistence.SalesforceAccount;
import sites.SiteLogic;

public class BasicDealerLogic {
	
	private static final Pattern ADDRESS_WITH_NO_STREET_WITH_COUNTRY = Pattern.compile("([^,]+), ([^0-9,]{2}) ([^,]+), ([a-zA-Z]+)");
	private static final Pattern ADDRESS_WITH_ADDRESSEE_WITH_COUNTRY = Pattern.compile("([^,]+),([^,]+),([^,]+), ([^0-9,]{2}) ([^,]+), ([a-zA-Z]+)");
	private static final Pattern ADDRESS_WITH_COUNTRY = Pattern.compile("([^,]+), ([^,]+), ([^0-9,]{2}) ([^,]+), ([a-zA-Z]+)");
	
	private static final Pattern ADDRESS_WITH_NO_STREET = Pattern.compile("([^,]+), ([^0-9,]{2}) ([^,]+)");
	private static final Pattern ADDRESS_WITH_ADDRESSEE = Pattern.compile("([^,]+),([^,]+),([^,]+), ([^0-9,]{2}) ([^,]+)");
	private static final Pattern ADDRESS = Pattern.compile("([^,]+), ([^,]+), ([^0-9,]{2}) ([^,]+)");

	public static boolean parseAddress(BasicDealer dealer) {
		String address = dealer.getCustom1();
		if(address == null){
			return false;
		}
//		System.out.println("address : " + address);
		address = address.replaceAll(",[\\n\\r]", ", ");
		address = address.replaceAll("\\s,", ",");
		address = address.replaceAll("[\\n\\r]", ", ");
		address = address.replaceAll("[\\s]+", " ");
		Matcher matcher = ADDRESS.matcher(address); 
		if(matcher.matches()){
			dealer.setStreet(matcher.group(1));
			dealer.setCity(matcher.group(2));
			dealer.setState(matcher.group(3));
			dealer.setPostal(matcher.group(4));
			dealer.setCountry("United States");
			return true;
		}
		matcher = ADDRESS_WITH_ADDRESSEE.matcher(address);
		if(matcher.matches()){
			dealer.setStreet(matcher.group(1) + " " + matcher.group(2));
			dealer.setCity(matcher.group(3));
			dealer.setState(matcher.group(4));
			dealer.setPostal(matcher.group(5));
			dealer.setCountry("United States");
			return true;
		}
		matcher = ADDRESS_WITH_NO_STREET.matcher(address);
		if(matcher.matches()){
			dealer.setStreet(null);
			dealer.setCity(matcher.group(1));
			dealer.setState(matcher.group(2));
			dealer.setPostal(matcher.group(3));
			dealer.setCountry("United States"); 
			return true;
		}
//		System.out.println("no address match : " + address);
		return false;
	}
	
	public static void salesforceMatch(BasicDealer dealer){
		Set<SalesforceAccount> accounts = new HashSet<SalesforceAccount>();
//		dealer.getPossibleMatches().clear();
		if(!StringUtils.isEmpty(dealer.getStdStreet())){
			accounts.addAll(GeneralDAO.getList(SalesforceAccount.class, "stdStreet", dealer.getStdStreet()));
		}
		if(!StringUtils.isEmpty(dealer.getStdPhone())){
			accounts.addAll(GeneralDAO.getList(SalesforceAccount.class, "stdPhone", dealer.getStdPhone()));
		}
		
		if(!SiteLogic.isBlankSite(dealer.getResolvedSite())){
			accounts.addAll(SalesforceDao.findBySite(dealer.getResolvedSite()));
		}
		dealer.getPossibleMatches().clear();
		JPA.em().getTransaction().commit();
		JPA.em().getTransaction().begin();
		dealer.setPossibleMatches(accounts);
	}
}
