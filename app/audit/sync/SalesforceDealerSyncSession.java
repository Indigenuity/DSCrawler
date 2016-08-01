package audit.sync;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import datatransfer.reports.ReportRow;
import persistence.Dealer;

//TODO delete this class sometime down the road.
public class SalesforceDealerSyncSession  {
	
	public static final Supplier<Dealer> DEFAULT_SUPPLIER = Dealer::new;
	public static final BiFunction<ReportRow, Dealer, Dealer> DEFAULT_UPDATER = (reportRow, dealer) -> {
		dealer.setDealerName(reportRow.getCell("Account Name"));
		dealer.setSalesforceId(reportRow.getCell("Salesforce Unique ID"));
		dealer.setSalesforceWebsite(reportRow.getCell("Website"));
		dealer.setParentAccountSalesforceId(reportRow.getCell("Parent Account ID"));
		dealer.setParentAccountName(reportRow.getCell("Parent Account"));
//		dealer.setFranchise(reportRow.getCell("franchise"));
		
		return dealer;
	};
	public static final Function<Dealer, Dealer> DEFAULT_OUTDATER = Function.identity();
	

	public SalesforceDealerSyncSession(Map<String, ReportRow> reportRows, Map<String, Dealer> dealers){
//		super(reportRows, dealers);
		init();
	}
	
	public SalesforceDealerSyncSession(Map<String, ReportRow> reportRows, Map<String, Dealer> dealers, PersistenceContext persistenceContext){
//		super(reportRows, dealers, persistenceContext);
		init();
	}
	
	private void init(){
//		this.localClazz = Dealer.class;
//		this.supplier = DEFAULT_SUPPLIER;
//		this.updater = DEFAULT_UPDATER;
//		this.outdater = DEFAULT_OUTDATER;
	}
}
