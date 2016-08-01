package audit.sync;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import datatransfer.reports.ReportRow;
import persistence.GroupAccount;

//TODO delete this class down the road sometime
public class SalesforceGroupAccountSyncSession  {
	
	public static final Supplier<GroupAccount> DEFAULT_SUPPLIER = GroupAccount::new;
	public static final BiFunction<ReportRow, GroupAccount, GroupAccount> DEFAULT_UPDATER = (reportRow, groupAccount) -> {
		groupAccount.setName(reportRow.getCell("Account Name"));
		groupAccount.setSalesforceId(reportRow.getCell("Salesforce Unique ID"));
		groupAccount.setSalesforceWebsite(reportRow.getCell("Website"));
		groupAccount.setParentAccountSalesforceId(reportRow.getCell("Parent Account ID"));
		groupAccount.setParentAccountName(reportRow.getCell("Parent Account"));
//		groupAccount.setFranchise(reportRow.getCell("franchise"));
		
		return groupAccount;
	};
	public static final Function<GroupAccount, GroupAccount> DEFAULT_OUTDATER = Function.identity();
	
	

	public SalesforceGroupAccountSyncSession(Map<String, ReportRow> reportRows, Map<String, GroupAccount> groupAccounts){
//		super(reportRows, groupAccounts);
		init();
	}
	
	public SalesforceGroupAccountSyncSession(Map<String, ReportRow> reportRows, Map<String, GroupAccount> groupAccounts, PersistenceContext persistenceContext){
//		super(reportRows, groupAccounts, persistenceContext);
		init();
	}
	
	private void init(){
//		this.localClazz = GroupAccount.class;
//		this.supplier = DEFAULT_SUPPLIER;
//		this.updater = DEFAULT_UPDATER;
//		this.outdater = DEFAULT_OUTDATER;
	}
	
}
