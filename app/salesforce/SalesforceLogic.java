package salesforce;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import akka.actor.ActorRef;
import async.async.Asyncleton;
import async.functionalwork.ConsumerWorkOrder;
import async.functionalwork.JpaFunctionalBuilder;
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
		ActorRef workMaster = Asyncleton.getInstance().getFunctionalMaster(5, true);
		Consumer<Long> resetter = JpaFunctionalBuilder.wrapConsumerInFind(SalesforceLogic::resetSite, SalesforceAccount.class);
		for(Long accountId : accountIds){
			ConsumerWorkOrder<Long> workOrder = new ConsumerWorkOrder<Long>(resetter, accountId);
			workMaster.tell(workOrder, ActorRef.noSender());
		}
	}
	
	public static void forwardSitesList(List<SalesforceAccount> accounts) {
		forwardSites(accounts.stream()
				.map((account)->account.getSalesforceAccountId())
				.collect(Collectors.toList()));
	}
	
	public static void forwardSites(List<Long> accountIds){
		ActorRef workMaster = Asyncleton.getInstance().getFunctionalMaster(5, true);
		Consumer<Long> resetter = JpaFunctionalBuilder.wrapConsumerInFind(SalesforceLogic::forwardSite, SalesforceAccount.class);
		for(Long accountId : accountIds){
			ConsumerWorkOrder<Long> workOrder = new ConsumerWorkOrder<Long>(resetter, accountId);
			workMaster.tell(workOrder, ActorRef.noSender());
		}
	}

	public static void resetSite(SalesforceAccount account) {
		String salesforceWebsite = account.getSalesforceWebsite();
		Site site = SitesDAO.getOrNew(salesforceWebsite);
		account.setSite(site);
	}
	
	public static void forwardSite(SalesforceAccount account) {
		Site redirectEndpoint = SitesDAO.getRedirectEndpoint(account.getSite(), true);
		account.setSite(redirectEndpoint);
	}
	
}
