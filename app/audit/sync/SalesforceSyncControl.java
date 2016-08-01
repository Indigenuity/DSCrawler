package audit.sync;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.hibernate.Session;

import datatransfer.CSVGenerator;
import datatransfer.CSVImporter;
import datatransfer.reports.Report;
import datatransfer.reports.ReportFactory;
import datatransfer.reports.ReportRow;
import persistence.Dealer;
import persistence.GroupAccount;
import persistence.salesforce.SalesforceAccount;
import play.db.jpa.JPA;

public class SalesforceSyncControl {
	
	
	
	public static void sync(String filename, boolean generateReports) throws IOException{
		Report report = CSVImporter.importReportWithKey(filename, "Salesforce Unique ID");
		List<SalesforceAccount> localAccountsList = JPA.em().createQuery("from SalesforceAccount sa", SalesforceAccount.class).getResultList();
		Map<String, SalesforceAccount> localAccounts = localAccountsList.stream().collect(Collectors.toMap(SalesforceAccount::getSalesforceId, Function.identity()));
		 
		Map<String, ReportRow> remoteAccounts = report.getReportRows().entrySet().stream()
				.collect(Collectors.toMap( Entry::getKey, Entry::getValue));
		SyncSession syncSession = new SalesforceSyncSession(remoteAccounts, localAccounts);
		syncSession.runSync();
		
		if(generateReports){
			List<Report> reports = syncSession.getReports();
			for(Report printedReport : reports){
				CSVGenerator.printReport(printedReport);			
			}
			Report contrastReport = ReportFactory.contrastReports(reports.get(1), reports.get(2)).setName("Salesforce Accounts Contrast Report");
			CSVGenerator.printReport(contrastReport);
		}
		
	}

//	public static void syncGroups(String filename, boolean generateReports) throws IOException{ 
//		Report report = CSVImporter.importReportWithKey(filename, "Salesforce Unique ID");
//		
//		List<GroupAccount> groupAccountsList = JPA.em().createQuery("from GroupAccount a", GroupAccount.class).getResultList();
//		Map<String, GroupAccount> groupAccounts = groupAccountsList.stream().collect(Collectors.toMap(GroupAccount::getSalesforceId, Function.identity()));
//		
//		Map<String, ReportRow> remoteGroupAccounts = report.getReportRows().entrySet().stream()
//				.filter((entry) -> {
//					return entry.getValue().getCell("Account Level") != null && entry.getValue().getCell("Account Level").equals("Group");
//				}).collect(Collectors.toMap( Entry::getKey, Entry::getValue));
//		
//		SyncSession syncSession = new SalesforceGroupAccountSyncSession(remoteGroupAccounts, groupAccounts);
//		syncSession.runSync();
//		
//		if(generateReports){
//			List<Report> reports = syncSession.getReports();
//			for(Report printedReport : reports){
//				CSVGenerator.printReport(printedReport);			
//			}
//			Report contrastReport = ReportFactory.contrastReports(reports.get(1), reports.get(2)).setName("Group Accounts Contrast Report");
//			CSVGenerator.printReport(contrastReport);
//		}
//	}
	
//	public  static void syncDealers(String filename, boolean generateReports) throws IOException {
//Report report = CSVImporter.importReportWithKey(filename, "Salesforce Unique ID");
//		
//		List<Dealer> dealersList = JPA.em().createQuery("from Dealer a", Dealer.class).getResultList();
//		Map<String, Dealer> dealers = dealersList.stream().collect(Collectors.toMap(Dealer::getSalesforceId, Function.identity()));
//		
//		Map<String, ReportRow> remoteDealers = report.getReportRows().entrySet().stream()
//				.filter((entry) -> {
//					return entry.getValue().getCell("Account Level") != null && !entry.getValue().getCell("Account Level").equals("Group");
//				}).collect(Collectors.toMap( Entry::getKey, Entry::getValue));
//		
//		SyncSession syncSession = new SalesforceDealerSyncSession(remoteDealers, dealers);
//		syncSession.runSync();
//		
//		if(generateReports){
//			List<Report> reports = syncSession.getReports();
//			for(Report printedReport : reports){
//				CSVGenerator.printReport(printedReport);			
//			}
//			Report contrastReport = ReportFactory.contrastReports(reports.get(1), reports.get(2)).setName("Dealers Contrast Report");
//			CSVGenerator.printReport(contrastReport);
//		}
//	}
}
