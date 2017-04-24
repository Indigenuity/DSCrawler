package salesforce;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import akka.actor.ActorRef;
import async.async.Asyncleton;
import async.functionalwork.ConsumerWorkOrder;
import async.functionalwork.JpaFunctionalBuilder;
import dao.GeneralDAO;
import dao.SiteOwnerLogic;
import dao.SitesDAO;
import datatransfer.reports.Report;
import datatransfer.reports.ReportRow;
import persistence.Site;
import salesforce.persistence.DealershipType;
import salesforce.persistence.SalesforceAccount;
import salesforce.persistence.SalesforceAccountType;
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
	
	public static void refreshRedirectPaths(List<Long> accountIds){
		Asyncleton.getInstance().runConsumerMaster(50, 
				JpaFunctionalBuilder.wrapConsumerInFind(SiteOwnerLogic::refreshRedirectPath, SalesforceAccount.class), 
				accountIds.stream(), 
				true);
	}

	public static void resetSite(SalesforceAccount account) {
		String salesforceWebsite = account.getSalesforceWebsite();
		Site site = SitesDAO.getOrNew(salesforceWebsite);
		account.setUnresolvedSite(site);
	}

	public static SalesforceAccount importReportRow(ReportRow reportRow){
		String salesforceId = reportRow.getCell("Salesforce Unique ID");
//		System.out.println("importing salesforceId : " + salesforceId);
		SalesforceAccount account = GeneralDAO.getFirst(SalesforceAccount.class, "salesforceId", salesforceId);
		if(account == null){
			System.out.println("Creating new account");
			account = new SalesforceAccount();
		} 
		updateFromReport(account, reportRow);
		return account;
	}
	
	public static void updateFromReport(SalesforceAccount account, ReportRow reportRow){
		updateFields(account, reportRow);
		assignBaseSite(account);
	}

	public static void updateFields(SalesforceAccount account, ReportRow reportRow){
		account.setName(reportRow.getCell("Account Name"));
		account.setLastUpdated(new Date());
		account.setSalesforceId(reportRow.getCell("Salesforce Unique ID"));
		account.setSalesforceWebsite(reportRow.getCell("Website"));
		account.setParentAccountSalesforceId(reportRow.getCell("Parent Account ID"));
		account.setParentAccountName(reportRow.getCell("Parent Account"));
		account.setAccountType(SalesforceAccountType.getBySalesforceValue(reportRow.getCell("Account Level")));
		account.setCustomerStatus(reportRow.getCell("Account Type"));
		account.setBrandAffiliation(reportRow.getCell("Brand Affiliation"));
		account.setCountry(reportRow.getCell("Dealership Country"));
		account.setCity(reportRow.getCell("Dealership City"));
		account.setZip(reportRow.getCell("Dealership Zip/Postal Code"));
		account.setState(reportRow.getCell("Dealership State/Province"));
		account.setStreet(reportRow.getCell("Dealership Street"));
		account.setPhone(reportRow.getCell("Phone"));
		account.setDealershipType(DealershipType.getBySalesforceValue(reportRow.getCell("Dealership Type")));
	}

	public static void assignBaseSite(SalesforceAccount account){
		if(!SiteLogic.homepageEquals(account.getUnresolvedSite(), account.getSalesforceWebsite())){
			Site unresolvedSite = SitesDAO.getOrNewThreadsafe(account.getSalesforceWebsite());
			account.setUnresolvedSite(unresolvedSite);
		}
	}
}
