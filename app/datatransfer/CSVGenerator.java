package datatransfer;

import global.Global;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import datatransfer.reports.Report;
import datatransfer.reports.ReportRow;
import places.PlacesPage;
import play.db.jpa.JPA;

public class CSVGenerator {
	
	public static void printReport(Report report) throws IOException {
		System.out.println("printing report : " + report.getName());
		report.acquireColumnNamesFromRows();
		List<String[]> rows = new ArrayList<String[]>();
		String[] headers = report.getColumnLabels().toArray(new String[report.getColumnLabels().size()]);
		rows.add(headers);
		for(ReportRow reportRow : report.getReportRows().values()){
			List<String> cells = new ArrayList<String>();
			for(String header : headers){
				cells.add(reportRow.getCell(header));
			}
			String[] row = cells.toArray(new String[cells.size()]);
			rows.add(row);
		}
		
		String targetFilename = Global.getReportsStorageFolder() + "/" + report.getName();
		if(report.isAppendDate()){
			targetFilename += System.currentTimeMillis();  
		}
		targetFilename += ".csv";
		File target = new File(targetFilename);
		FileWriter fileOut = new FileWriter(target);
		CSVPrinter printer = new CSVPrinter(fileOut, CSVFormat.EXCEL);
		printer.printRecords(rows);
		printer.close();
		fileOut.close();
	}
	
	public static void writeReport(CSVReport report) throws IOException{
		System.out.println("Writing to file ");
		
		String targetFilename = Global.getReportsStorageFolder() + "/" + report.getName();
		if(report.isAppendDate()){
			targetFilename += " " + System.currentTimeMillis();  
		}
		targetFilename += ".csv";
		File target = new File(targetFilename);
		FileWriter fileOut = new FileWriter(target);
		CSVPrinter printer = new CSVPrinter(fileOut, CSVFormat.EXCEL);
		printer.printRecords(report.getRows());
		printer.close();
		fileOut.close();
	}
	
	

	public static void generateInventoryCountReport() throws IOException {
//		List<String[]> CSVRows = new ArrayList<String[]>();
//		List<String> values = new ArrayList<String>();
//		values.add("Salesforce Unique ID");
//		values.add("Account Name");
//		values.add("Task status");
//		values.add("Current SalesForce Primary Website URL:");
//		values.add("Resolved URL");
//		values.add("Web Provider");
//		values.add("Inventory Type");
//		values.add("Web Provider Attributions");
//		values.add("New Inventory URL");
////		values.add("New Inventory Filename");
////		values.add("PageCrawlId");
//		values.add("New Inventory Count");
//		values.add("Used Inventory URL");
////		values.add("Used Inventory Filename");
//		values.add("Used Inventory Count");
//		values.add("Largest Inventory Count");
//		values.add("Brand Affiliation Averages");
//		CSVRows.add((String[])values.toArray(new String[values.size()]));
//		
//		TaskSet taskSet = JPA.em().find(TaskSet.class, 1L);
//		int count = 0;
//		for(Task supertask : taskSet.getTasks()){
//			if(supertask.getWorkStatus() != WorkStatus.WORK_COMPLETED){
//				continue;
//			}
//			String sfIdString = supertask.getContextItem("sfEntryId");
//			String siteCrawlIdString = supertask.getContextItem("siteCrawlId");
//			
//			SFEntry sf = JPA.em().find(SFEntry.class, Long.parseLong(sfIdString));
//			SiteCrawl siteCrawl = null;
//			if(siteCrawlIdString != null && !siteCrawlIdString.equals("")){
//				siteCrawl = JPA.em().find(SiteCrawl.class, Long.parseLong(siteCrawlIdString));
//			}
//			
//			values = new ArrayList<String>();
//			values.add(sf.getAccountId());
//			values.add(sf.getName());
//			values.add(supertask.getWorkStatus() + "");
//			values.add(sf.getWebsite());
//			if(siteCrawl != null){
//				values.add(siteCrawl.getSeed());
//				values.add(siteCrawl.getWebProvider() + "");
//				values.add(siteCrawl.getInventoryType() + "");
//				String wp = "";
//				String delim = "";
//				for(WPAttribution item : siteCrawl.getWpAttributions()){
//					wp += delim + item.name();
//					delim = ", ";
//				}
//				values.add(wp);
//				if(siteCrawl.getNewInventoryPage() != null){
//					values.add(siteCrawl.getNewInventoryPage().getUrl());
////					values.add(siteCrawl.getStorageFolder() + "/" + siteCrawl.getNewInventoryPage().getFilename());
////					values.add(siteCrawl.getNewInventoryPage().getPageCrawlId() + "");
//					if(siteCrawl.getNewInventoryPage().getInventoryNumber() != null){
//						values.add(siteCrawl.getNewInventoryPage().getInventoryNumber().getCount() + "");
//					}
//					else{
//						values.add("Could not fetch");
//					}
//				}
//				else{
//					values.add("");
//					values.add("");
//				}
//				
//				
//				if(siteCrawl.getUsedInventoryPage() != null){
//					values.add(siteCrawl.getUsedInventoryPage().getUrl());
////					values.add(siteCrawl.getStorageFolder() + "/" + siteCrawl.getUsedInventoryPage().getFilename());
//					if(siteCrawl.getUsedInventoryPage().getInventoryNumber() != null){
//						values.add(siteCrawl.getUsedInventoryPage().getInventoryNumber().getCount() + "");
//					}
//					else{
//						values.add("Could not fetch");
//					}
//				}
//				else{
//					values.add("");
//					values.add("");
//				}
//				
//				values.add(siteCrawl.getMaxInventoryCount() + "");
//				delim = "";
//				String brand = "";
//				for(Entry<OEM, Double> entry : siteCrawl.getBrandMatchAverages().entrySet()){
//					if(entry.getKey() == OEM.GM ? entry.getValue() > 20 : entry.getValue() >= 10){
//						brand += delim;
//						delim = "; ";
//						brand += entry.getKey() + " : " +entry.getValue();
//					}
//				}
//				values.add(brand);
//			}
//			else {
//				values.add("");
//				values.add("");
//				values.add("");
//				values.add("");
//				values.add("");
//				values.add("");
//				values.add("");
//				values.add("");
//			}
//			
//			CSVRows.add((String[])values.toArray(new String[values.size()]));
//			if(++count % 500 == 0) {
//				System.out.println("count : " + count);
//			}
//		}
//		System.out.println("Writing to file ");
//		
//		String targetFilename = Global.getReportsStorageFolder() + "/invreport" + System.currentTimeMillis() + ".csv";  
//		File target = new File(targetFilename);
//		FileWriter fileOut = new FileWriter(target);
//		CSVPrinter printer = new CSVPrinter(fileOut, CSVFormat.EXCEL);
//		printer.printRecords(CSVRows);
//		printer.close();
//		fileOut.close();
		
	}
	
	public static void generatePlacesCSV() throws IOException {
		System.out.println("executing query");
		String query = "from PlacesDealer pd";
		List<PlacesPage> places = JPA.em().createQuery(query, PlacesPage.class).getResultList();
		System.out.println("Finished with query");
		
		List<String[]> CSVRows = new ArrayList<String[]>();
		String[] values = new String[] {"Name",
				"Website",
				"Address",
				"Phone",
				"Google URL",
				"Latitude",
				"Longitude",
				"Open Hours",
				"Permanently Closed?",
				"Price Level",
				"Rating",
				"Business Type",
				"Country"
		};
		CSVRows.add(values);
		
		int count = 0;
		for(PlacesPage place : places) {
			count++;
			if(count % 100 == 0){
				System.out.println("printed site " + count);
			}
			
			values = new String[] {place.getName(),
					place.getWebsite(),
					place.getFormattedAddress(),
					place.getFormattedPhoneNumber(),
					place.getGoogleUrl(),
					String.valueOf(place.getLatitude()),
					String.valueOf(place.getLongitude()),
					place.getOpenHours(),
					String.valueOf(place.isPermanentlyClosed()),
					String.valueOf(place.getPriceLevel()),
					String.valueOf(place.getRating()),
					place.getTypes(),
					place.getCountry(),
			};
			CSVRows.add(values);
		}
		String targetFilename = Global.getReportsStorageFolder() + "/" +new SimpleDateFormat("MM-dd-yyyy").format(new java.util.Date()) + "-GooglePlaces.csv";  
		File target = new File(targetFilename);
		FileWriter fileOut = new FileWriter(target);
		CSVPrinter printer = new CSVPrinter(fileOut, CSVFormat.EXCEL);
		printer.printRecords(CSVRows);
		printer.close();
		fileOut.close();
	}
}
