package urlcleanup;

import java.io.IOException;

import datatransfer.CSVImporter;
import datatransfer.reports.Report;
import urlcleanup.ListCheckConfig.InputType;

public class ListCheckFactory {
	
	public static ListCheck createListCheck(ListCheckConfig config) throws IOException {
		
		if(config.getInputType() == InputType.SALESFORCE_REPORT){
			return createFromSalesforceReport(config);
		}
		else if(config.getInputType() == InputType.DATABASE){
			return createFromDatabase(config);
		}
		return null;
	}
	
	public static ListCheck createFromSalesforceReport(ListCheckConfig config) throws IOException {
		
		ListCheck listCheck = new ListCheck();
		listCheck.setConfig(config);
		
		Report report = CSVImporter.importReportWithKey(config.getInputFilename(), InputType.SALESFORCE_REPORT.keyColumnLabel);
		report.setName("Website List Check");
		report.addColumnLabel("urlCheckId");
		report.addColumnLabel("Manual Seed");
		report.addColumnLabel("Standardized Seed");
		report.addColumnLabel("Resolved Seed");
		report.addColumnLabel("Status Code");
		report.addColumnLabel("Valid URL");
		report.addColumnLabel("Generic Redirect");
		report.addColumnLabel("Valid Path");
		report.addColumnLabel("Valid Query");
		report.addColumnLabel("Language Query");
		report.addColumnLabel("Language Path");
		report.addColumnLabel("Domain");
		report.addColumnLabel("Valid Domain");
		report.addColumnLabel("Shared Site");
		report.addColumnLabel("Recommendation");
		report.addColumnLabel("Error");
		
		listCheck.setReport(report);
		
		return listCheck;
		
	}
	
	public static ListCheck createFromDatabase(ListCheckConfig config) {
		return null;
	}

}
