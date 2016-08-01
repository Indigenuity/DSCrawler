package urlcleanup;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import datatransfer.CSVGenerator;
import datatransfer.reports.Report;
import datatransfer.reports.ReportRow;
import persistence.UrlCheck;
import play.db.jpa.JPA;
import urlcleanup.ListCheckConfig.InputType;
import utilities.UrlSniffer;

public class ListCheckExecutor {

	public static void execute(ListCheck listCheck) {
		Report report = listCheck.getReport();
		Set<String> keys = new HashSet<String>();
		ListCheckConfig config = listCheck.getConfig();
		InputType inputType = config.getInputType();
		int count = 0;
		for(Entry<String, ReportRow> entry : report.getReportRows().entrySet()){
			ReportRow reportRow = entry.getValue();
			if(reportRow.getCell("Recommendation") == null || (reportRow.getCell("Recommendation").equals("Undecided") 
				&& reportRow.getCell("urlCheckId") == null)){
//			if(reportRow.getCell("Recommendation") == null){
				try{
					String seed = reportRow.getCell(inputType.seedColumnLabel);
					if(!StringUtils.isEmpty(reportRow.getCell("Manual Seed"))){
						seed = reportRow.getCell("Manual Seed");
					}
					UrlCheck urlCheck = UrlSniffer.checkUrl(seed);
					JPA.em().persist(urlCheck);
					reportRow.putCell("urlCheckId", urlCheck.getUrlCheckId() + "");
					reportRow.putCell("Standardized Seed", urlCheck.getStandardizedSeed());
					reportRow.putCell("Resolved Seed", urlCheck.getResolvedSeed());
					reportRow.putCell("Status Code", urlCheck.getStatusCode() + "");
					reportRow.putCell("Valid URL", urlCheck.isValid() + "");
					reportRow.putCell("Generic Redirect", urlCheck.isGenericChange() + "");
					reportRow.putCell("Valid Path", urlCheck.isPathApproved() + "");
					reportRow.putCell("Valid Query", urlCheck.isQueryApproved() + "");
					reportRow.putCell("Language Query", urlCheck.isLanguageQuery() + "");
					reportRow.putCell("Language Path", urlCheck.isLanguagePath() + "");
					reportRow.putCell("Domain", urlCheck.getResolvedHost());
					reportRow.putCell("Valid Domain", urlCheck.isDomainApproved() + "");
					reportRow.putCell("Shared Site", urlCheck.isSharedSite() + "");
					reportRow.putCell("All Approved", urlCheck.isAllApproved() + "");
					reportRow.putCell("Recommendation", getRecommendation(urlCheck));
				} catch(Exception e) {
					reportRow.putCell("Error", e.getClass().getSimpleName() + " exception : " + e.getMessage());
				} finally {
					JPA.em().getTransaction().commit();
					JPA.em().getTransaction().begin();
				}
				
//				if(count++ > 10){
//					return;
//				}
			}
			
			
		}
	}
	
	public static void markBlanks(ListCheck listCheck){
		Report report = listCheck.getReport();
		ListCheckConfig config = listCheck.getConfig();
		InputType inputType = config.getInputType();
		for(Entry<String, ReportRow> entry : report.getReportRows().entrySet()){
			ReportRow reportRow = entry.getValue();
			if("Undecided".equals(reportRow.getCell("Recommendation"))){
				String seed = reportRow.getCell(inputType.seedColumnLabel);
				if(StringUtils.isEmpty(reportRow.getCell("Website"))){
					System.out.println("equals : " + reportRow.getCell("Website"));
					reportRow.putCell("Recommendation", "No Website");
				}
			}
		}
	}
	
	public static void markIndexChanges(ListCheck listCheck){
		Report report = listCheck.getReport();
		ListCheckConfig config = listCheck.getConfig();
		InputType inputType = config.getInputType();
		for(Entry<String, ReportRow> entry : report.getReportRows().entrySet()){
			ReportRow reportRow = entry.getValue();
			if(reportRow.getCell("Manual Seed") != null) {
				reportRow.putCell("Recommendation", null);
			}
//			if("Undecided".equals(reportRow.getCell("Recommendation"))){
//				String seed = reportRow.getCell(inputType.seedColumnLabel);
//				String resolvedSeed = reportRow.getCell("Resolved Seed");
//				if(!StringUtils.isEmpty(seed) && !StringUtils.isEmpty(resolvedSeed)
//						&& !seed.equals(resolvedSeed) && seed.replace("index.htm", "").equals(resolvedSeed)){
//					System.out.println("equals : " + reportRow.getCell("Website"));
//					reportRow.putCell("Recommendation", null);
//					reportRow.putCell("Manual Seed", seed.replace("index.htm", ""));
//				}
//			}
		}
	}
	
	public static void report(ListCheck listCheck) throws IOException {
		CSVGenerator.printReport(listCheck.getReport());
	}
	
	public static void processReportRow(ReportRow reportRow, String seedColumnLabel) {
		
	}
	
	public static String getRecommendation(UrlCheck urlCheck) {
		if(urlCheck.isAllApproved()){
			if(urlCheck.isNoChange()){
				return "No Action";
			} else {
				return "Accept Generic Update";
			}
		} else {
			return "Undecided";
		}
	}
}
