package salesforce;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import akka.actor.ActorRef;
import async.async.Asyncleton;
import async.functionalwork.ConsumerWorkOrder;
import async.functionalwork.JpaFunctionalBuilder;
import dao.SiteOwnerLogic;
import dao.SitesDAO;
import persistence.Site;
import salesforce.persistence.SalesforceAccount;
import sites.SiteLogic;

public class SalesforceLogic {
	
	public static void resetSitesList(List<SalesforceAccount> accounts) {
		forwardSites(accounts.stream()
				.map((account)->account.getSalesforceAccountId())
				.collect(Collectors.toList()));
	}
	
	public static void resetSites(List<Long> accountIds){
		Asyncleton.getInstance().runConsumerMaster(5, 
				JpaFunctionalBuilder.wrapConsumerInFind(SalesforceLogic::resetSite, SalesforceAccount.class), 
				accountIds.stream(), 
				true);
	}
	
	public static void forwardSitesList(List<SalesforceAccount> accounts) {
		forwardSites(accounts.stream()
				.map((account)->account.getSalesforceAccountId())
				.collect(Collectors.toList()));
	}
	
	public static void forwardSites(List<Long> accountIds){
		Asyncleton.getInstance().runConsumerMaster(50, 
				JpaFunctionalBuilder.wrapConsumerInFind(SiteOwnerLogic::forwardSite, SalesforceAccount.class), 
				accountIds.stream(), 
				true);
	}

	public static void resetSite(SalesforceAccount account) {
		String salesforceWebsite = account.getSalesforceWebsite();
		Site site = SitesDAO.getOrNew(salesforceWebsite);
		account.setUnresolvedSite(site);
	}
}
