package audit.sync;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;


import datatransfer.reports.ReportRow;
import persistence.salesforce.DealershipType;
import persistence.salesforce.SalesforceAccount;
import persistence.salesforce.SalesforceAccountType;
import play.db.jpa.JPA;

public class SalesforceSyncSession extends SingleSyncSession<String, ReportRow, SalesforceAccount>{
	
	public static final Supplier<SalesforceAccount> DEFAULT_SUPPLIER = SalesforceAccount::new;
	public static final BiFunction<ReportRow, SalesforceAccount, SalesforceAccount> DEFAULT_UPDATER = (reportRow, salesforceAccount) -> {
		salesforceAccount.setName(reportRow.getCell("Account Name"));
		salesforceAccount.setSalesforceId(reportRow.getCell("Salesforce Unique ID"));
		salesforceAccount.setSalesforceWebsite(reportRow.getCell("Website"));
		salesforceAccount.setParentAccountSalesforceId(reportRow.getCell("Parent Account ID"));
		salesforceAccount.setParentAccountName(reportRow.getCell("Parent Account"));
		salesforceAccount.setAccountType(SalesforceAccountType.getBySalesforceValue(reportRow.getCell("Account Level")));
		salesforceAccount.setCustomerStatus(reportRow.getCell("Account Type"));
		salesforceAccount.setBrandAffiliation(reportRow.getCell("Brand Affiliation"));
		salesforceAccount.setCountry(reportRow.getCell("Dealership Country"));
		salesforceAccount.setCity(reportRow.getCell("Dealership City"));
		salesforceAccount.setZip(reportRow.getCell("Dealership Zip/Postal Code"));
		salesforceAccount.setState(reportRow.getCell("Dealership State/Province"));
		salesforceAccount.setStreet(reportRow.getCell("Dealership Street"));
		salesforceAccount.setPhone(reportRow.getCell("Phone"));
		salesforceAccount.setDealershipType(DealershipType.getBySalesforceValue(reportRow.getCell("Dealership Type")));
		
		return salesforceAccount;
	};
	
	public static final Function<SalesforceAccount, SalesforceAccount> DEFAULT_OUTDATER = Function.identity();
	
	
	protected Map<String, ReportRow> reportRows;
	protected Map<String, SalesforceAccount> localItems;
	
	protected Sync sync;
	
	public SalesforceSyncSession(Map<String, ReportRow> reportRows, Map<String, SalesforceAccount> localItems){
		init(reportRows, localItems);
		this.persistenceContext = new JpaPersistenceContext(JPA.em());
	}
	
	public SalesforceSyncSession(Map<String, ReportRow> reportRows, Map<String, SalesforceAccount> localItems, PersistenceContext persistenceContext){
		init(reportRows, localItems);
		this.persistenceContext = persistenceContext;
	}
	
	private void init(Map<String, ReportRow> reportRows, Map<String, SalesforceAccount> localItems){
		this.keyClazz = String.class;
		this.remoteClazz = ReportRow.class;
		this.syncSessionKeys = new SyncSessionKeys<String>(reportRows.keySet(), localItems.keySet());
		this.remoteFetcher = reportRows::get;
		this.localFetcher = localItems::get;
		this.localClazz = SalesforceAccount.class;
		this.supplier = DEFAULT_SUPPLIER;
		this.updater = DEFAULT_UPDATER;
		this.outdater = DEFAULT_OUTDATER;
	}
	
	@Override
	protected void preCommit(){
		Sync sync = new Sync(SyncType.ACCOUNT_IMPORT);
		System.out.println("in override");
		this.sync = persistenceContext.insert(sync);
	}

	public Sync getSync() {
		return sync;
	}

	public void setSync(Sync sync) {
		this.sync = sync;
	}
	
	
	
}
