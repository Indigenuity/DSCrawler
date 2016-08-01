package audit.sync;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.codec.binary.StringUtils;

import datatransfer.reports.ReportRow;
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
		String accountTypeString = reportRow.getCell("Account Level");
		if(StringUtils.equals(accountTypeString, "Group")){
			salesforceAccount.setAccountType(SalesforceAccountType.GROUP);
		}else {
			salesforceAccount.setAccountType(SalesforceAccountType.DEALER);
		}
//		groupAccount.setFranchise(reportRow.getCell("franchise"));
		
		return salesforceAccount;
	};
	
	public static final Function<SalesforceAccount, SalesforceAccount> DEFAULT_OUTDATER = Function.identity();
	
	
	protected Map<String, ReportRow> reportRows;
	protected Map<String, SalesforceAccount> localItems;
	
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
		persistenceContext.insert(sync);
	}
	
}
