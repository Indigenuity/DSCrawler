package audit.sync;

import dao.GeneralDAO;
import datatransfer.reports.Report;
import datatransfer.reports.ReportRow;
import persistence.salesforce.DealershipType;
import persistence.salesforce.SalesforceAccount;
import persistence.salesforce.SalesforceAccountType;
import play.db.jpa.JPA;


//The methods of this class assume an entity manager bound to the calling thread
public class SalesforceSynchronizer extends KeyedSynchronizer<String, SalesforceAccount, ReportRow>{

	protected Report report;
	
	public SalesforceSynchronizer(Report report){
		this.report = report;
	}

	@Override
	public SalesforceAccount getLocal(String localKey) {
		return GeneralDAO.getFirst(SalesforceAccount.class, "salesforceId", localKey);
	}

	@Override
	public ReportRow getRemote(String remoteKey) {
		return report.getReportRow(remoteKey);
	}

	@Override
	public void insert(ReportRow remote) {
		SalesforceAccount account = new SalesforceAccount();
		update(new SalesforceAccount(), remote);
		JPA.em().persist(account);
	}
	
	@Override
	public void update(SalesforceAccount account, ReportRow reportRow){
		account.setName(reportRow.getCell("Account Name"));
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

	@Override
	public void outdate(SalesforceAccount account) {
		account.setOutdated(true);
	}
	
	

}
