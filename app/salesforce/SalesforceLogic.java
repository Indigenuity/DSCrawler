package salesforce;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.persistence.Column;

import org.apache.commons.lang3.StringUtils;

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
import play.Logger;
import play.db.jpa.JPA;
import salesforce.persistence.DealershipType;
import salesforce.persistence.SalesforceAccount;
import salesforce.persistence.SalesforceAccountType;
import salesforce.persistence.SalesforceCustomerType;
import sites.utilities.SiteLogic;
import utilities.DSFormatter;

public class SalesforceLogic {

	public static String generateLink(SalesforceAccount account){
		return "https://dealersocket.my.salesforce.com/" + account.getSalesforceId();
	}
	
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
//			System.out.println("Creating new account");
			account = new SalesforceAccount();
			JPA.em().persist(account);
		} 
		updateFromReport(account, reportRow);
		return account;
	}
	
	public static void updateFromReport(SalesforceAccount account, ReportRow reportRow){
		try{
			updateFields(account, reportRow);
		} catch(Exception e) {
			Logger.error("Exception while updating fields on row " + reportRow.getCell("rowNumber") + " : " + DSFormatter.toString(e));
			System.out.println("Exception while updating fields on row " + reportRow.getCell("rowNumber") + " : " + DSFormatter.toString(e));
			throw e;
		}
		SiteOwnerLogic.assignUnresolvedSiteThreadsafe(account);
	}

	public static void updateFields(SalesforceAccount account, ReportRow reportRow){
//		System.out.println("updating fields on account : " + account.getSalesforceAccountId());
		account.setLastUpdated(new Date());
		account.setCreated(toDate(reportRow.getCell("Created Date")));
		account.setLastModified(toDate(reportRow.getCell("Last Modified Date")));
		
		account.setName(reportRow.getCell("Account Name"));
		account.setAlias(reportRow.getCell("Alias/DBA"));
		account.setSalesforceId(reportRow.getCell("Salesforce Unique ID"));
		account.setParentAccountName(reportRow.getCell("Parent Account"));
		account.setParentAccountSalesforceId(reportRow.getCell("Parent Account ID"));
		account.setGroupOrganization(reportRow.getCell("Group Organization"));
		
		account.setBrandAffiliation(reportRow.getCell("Brand Affiliation"));
		account.setPrimaryArea(reportRow.getCell("Primary Area"));
		account.setCapDbRating(reportRow.getCell("CAPdb Rating"));
		
		account.setDealershipType(DealershipType.getBySalesforceValue(reportRow.getCell("Dealership Type")));
		account.setAccountType(SalesforceAccountType.getBySalesforceValue(reportRow.getCell("Account Level")));
		account.setCustomerType(SalesforceCustomerType.getBySalesforceValue(reportRow.getCell("Account Type")));
		
		account.setSalesforceWebsite(reportRow.getCell("Website (Primary) URL"));
		account.setPhone(reportRow.getCell("Phone"));
		account.setStreet(reportRow.getCell("Dealership Street"));
		account.setCity(reportRow.getCell("Dealership City"));
		account.setState(reportRow.getCell("Dealership State/Province"));
		account.setZip(reportRow.getCell("Dealership Zip/Postal Code"));
		account.setCountry(reportRow.getCell("Dealership Country"));
		
		account.setEclNum(reportRow.getCell("ECL #"));
		account.setAaxNum(reportRow.getCell("#AAX"));
		account.setInventoryId(reportRow.getCell("Inventory ID"));
		account.setInvGroupId(reportRow.getCell("Inv Group ID"));
		account.setDealerTrackId(reportRow.getCell("DealerTrack ID"));
		account.setAaxGroupId(reportRow.getCell("AAX GroupID"));
		account.setCrmSiteId(reportRow.getCell("Site ID"));
		account.setSocketId(reportRow.getCell("Socket ID"));
		account.setFusionId(reportRow.getCell("Fusion Id"));
		account.setPortalPayId(reportRow.getCell("Portal Pay ID"));
		account.setFexIntitutionalId(reportRow.getCell("FEX Institutional ID"));
		account.setLegacyFexId(reportRow.getCell("Legacy FEX ID"));
		account.setLegacyAutoStarId(reportRow.getCell("Legacy AutoStar ID"));
		
		account.setTotalMrr(mrrToFloat(reportRow.getCell("Total Asset MRR (converted)")));
		account.setTotalAtRiskMrr(mrrToFloat(reportRow.getCell("Total At-Risk MRR (converted)")));
		account.setCrmMrr(mrrToFloat(reportRow.getCell("CRM Total Asset MRR (converted)")));
		account.setCrmAtRiskMrr(mrrToFloat(reportRow.getCell("CRM At-Risk MRR (converted)")));
		account.setDeskingMrr(mrrToFloat(reportRow.getCell("Desking Total Asset MRR (converted)")));
		account.setDeskingAtRiskMrr(mrrToFloat(reportRow.getCell("Desking At-Risk MRR (converted)")));
		account.setInventoryMrr(mrrToFloat(reportRow.getCell("Inventory Total Asset MRR (converted)")));
		account.setInventoryAtRiskMrr(mrrToFloat(reportRow.getCell("Inventory At-Risk MRR (converted)")));
		account.setIdmsMrr(mrrToFloat(reportRow.getCell("iDMS Total Asset MRR (converted)")));
		account.setRevenueRadarMrr(mrrToFloat(reportRow.getCell("Revenue Radar Total Asset MRR (converted)")));
		account.setRevenueAtRiskRadarMrr(mrrToFloat(reportRow.getCell("Revenue Radar At-Risk MRR (converted)")));
		account.setWebsiteDgmMrr(mrrToFloat(reportRow.getCell("Website & Digital Marketing Total Asset MRR (converted)")));
		account.setWebsiteAtRiskDgmMrr(mrrToFloat(reportRow.getCell("Website & Digital Marketing At-Risk MRR (converted)")));
	}
	
	private static Date toDate(String dateString) {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy"); 
		try {
			return df.parse(dateString);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	private static Float mrrToFloat(String mrr){
		if(StringUtils.isEmpty(mrr)){
			return 0F;
		}
		mrr = mrr.replaceAll("[^0-9\\.]", "");
		return Float.parseFloat(mrr);
	}
}
