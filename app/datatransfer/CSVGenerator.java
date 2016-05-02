package datatransfer;

import global.Global;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;

import async.work.WorkStatus;
import async.work.WorkType;
import dao.SitesDAO;
import datadefinitions.GeneralMatch;
import datadefinitions.OEM;
import datadefinitions.Scheduler;
import datadefinitions.WebProvider;
import datadefinitions.newdefinitions.WPAttribution;
import persistence.Dealer;
import persistence.ExtractedString;
import persistence.FBPage;
import persistence.PlacesPage;
import persistence.SFEntry;
import persistence.Site;
import persistence.SiteCrawl;
import persistence.Staff;
import persistence.UrlCheck;
import persistence.tasks.Task;
import persistence.tasks.TaskSet;
import play.db.jpa.JPA;

public class CSVGenerator {
	
	public static void generateSiteImportReport(TaskSet taskSet) throws IOException {
		CSVReport report = new CSVReport("Site Import Report");
		List<String> values = new ArrayList<String>();
		values.add("Salesforce Unique ID");
		values.add("Account Name");
		values.add("Current SalesForce Primary Website URL:");
		values.add("Resolved URL");
		values.add("Status Code");
		values.add("Accepted");
		values.add("No change");
		values.add("Changed but accepted");
		//values.add("Manually Changed");
		values.add("Manually Approved");
		values.add("Shared Site");
		values.add("Needs Attention");
		values.add("Shares homepage with");
		values.add("Shares domain with");
		report.setHeaderValues(values);
		
		int count = 0;
		for(Task task : taskSet.getTasks()){
			values = new ArrayList<String>();
			
			if(task.getWorkType() != WorkType.SUPERTASK){
				continue;
			}
			Task urlTask = task.getSubtask(WorkType.REDIRECT_RESOLVE);
			Task siteImportTask = task.getSubtask(WorkType.SITE_IMPORT);
			if(urlTask == null || siteImportTask == null){
				continue;
			}
			String sfEntryIdString = task.getContextItem("sfEntryId");
			String urlCheckIdString = urlTask.getContextItem("urlCheckId");
			String siteIdString = siteImportTask.getContextItem("siteId");
			if(StringUtils.isEmpty(urlCheckIdString) || StringUtils.isEmpty(sfEntryIdString)){
				continue;
			}
			SFEntry sf =JPA.em().find(SFEntry.class, Long.parseLong(sfEntryIdString));
			UrlCheck urlCheck = JPA.em().find(UrlCheck.class, Long.parseLong(urlCheckIdString));
			Site site = null;
			if(siteIdString != null){
				site = JPA.em().find(Site.class, Long.parseLong(siteIdString));
			}
			
			values.add(sf.getAccountId());
			values.add(sf.getName());
			values.add(sf.getWebsite());
			values.add(urlCheck.getResolvedSeed());
			values.add(urlCheck.getStatusCode() + "");
			values.add(urlCheck.isAccepted() + "");
			values.add(urlCheck.isNoChange() + "");
			values.add((!urlCheck.isNoChange() && urlCheck.isAccepted()) + "");
			values.add(urlCheck.isManuallyApproved() + "");
			values.add(urlCheck.isSharedSite() + "");
			if(site == null){
				values.add("");
				values.add("");
			} else {
//				List<Site> dupHomepages = SitesDAO.getList("homepage", site.getHomepage(), 20, 0);
//				for(Site dup : dupHomepages){
//					SFEntry = 
//				}
			}
			report.addRow(values);
			if(++count % 500 == 0) {
				System.out.println("count : " + count);
			}
		}
		writeReport(report);
	}
	
	public static void writeReport(CSVReport report) throws IOException{
		System.out.println("Writing to file ");
		
		String targetFilename = Global.getReportsStorageFolder() + "/" + report.getName();
		if(report.isAppendDate()){
			targetFilename += System.currentTimeMillis();  
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
		List<String[]> CSVRows = new ArrayList<String[]>();
		List<String> values = new ArrayList<String>();
		values.add("Salesforce Unique ID");
		values.add("Account Name");
		values.add("Task status");
		values.add("Current SalesForce Primary Website URL:");
		values.add("Resolved URL");
		values.add("Web Provider");
		values.add("Inventory Type");
		values.add("Web Provider Attributions");
		values.add("New Inventory URL");
//		values.add("New Inventory Filename");
//		values.add("PageCrawlId");
		values.add("New Inventory Count");
		values.add("Used Inventory URL");
//		values.add("Used Inventory Filename");
		values.add("Used Inventory Count");
		values.add("Largest Inventory Count");
		values.add("Brand Affiliation Averages");
		CSVRows.add((String[])values.toArray(new String[values.size()]));
		
		TaskSet taskSet = JPA.em().find(TaskSet.class, 1L);
		int count = 0;
		for(Task supertask : taskSet.getTasks()){
			if(supertask.getWorkStatus() != WorkStatus.WORK_COMPLETED){
				continue;
			}
			String sfIdString = supertask.getContextItem("sfEntryId");
			String siteCrawlIdString = supertask.getContextItem("siteCrawlId");
			
			SFEntry sf = JPA.em().find(SFEntry.class, Long.parseLong(sfIdString));
			SiteCrawl siteCrawl = null;
			if(siteCrawlIdString != null && !siteCrawlIdString.equals("")){
				siteCrawl = JPA.em().find(SiteCrawl.class, Long.parseLong(siteCrawlIdString));
			}
			
			values = new ArrayList<String>();
			values.add(sf.getAccountId());
			values.add(sf.getName());
			values.add(supertask.getWorkStatus() + "");
			values.add(sf.getWebsite());
			if(siteCrawl != null){
				values.add(siteCrawl.getSeed());
				values.add(siteCrawl.getWebProvider() + "");
				values.add(siteCrawl.getInventoryType() + "");
				String wp = "";
				String delim = "";
				for(WPAttribution item : siteCrawl.getWpAttributions()){
					wp += delim + item.name();
					delim = ", ";
				}
				values.add(wp);
				if(siteCrawl.getNewInventoryPage() != null){
					values.add(siteCrawl.getNewInventoryPage().getUrl());
//					values.add(siteCrawl.getStorageFolder() + "/" + siteCrawl.getNewInventoryPage().getFilename());
//					values.add(siteCrawl.getNewInventoryPage().getPageCrawlId() + "");
					if(siteCrawl.getNewInventoryPage().getInventoryNumber() != null){
						values.add(siteCrawl.getNewInventoryPage().getInventoryNumber().getCount() + "");
					}
					else{
						values.add("Could not fetch");
					}
				}
				else{
					values.add("");
					values.add("");
				}
				
				
				if(siteCrawl.getUsedInventoryPage() != null){
					values.add(siteCrawl.getUsedInventoryPage().getUrl());
//					values.add(siteCrawl.getStorageFolder() + "/" + siteCrawl.getUsedInventoryPage().getFilename());
					if(siteCrawl.getUsedInventoryPage().getInventoryNumber() != null){
						values.add(siteCrawl.getUsedInventoryPage().getInventoryNumber().getCount() + "");
					}
					else{
						values.add("Could not fetch");
					}
				}
				else{
					values.add("");
					values.add("");
				}
				
				values.add(siteCrawl.getMaxInventoryCount() + "");
				delim = "";
				String brand = "";
				for(Entry<OEM, Double> entry : siteCrawl.getBrandMatchAverages().entrySet()){
					if(entry.getKey() == OEM.GM ? entry.getValue() > 20 : entry.getValue() >= 10){
						brand += delim;
						delim = "; ";
						brand += entry.getKey() + " : " +entry.getValue();
					}
				}
				values.add(brand);
			}
			else {
				values.add("");
				values.add("");
				values.add("");
				values.add("");
				values.add("");
				values.add("");
				values.add("");
				values.add("");
			}
			
			CSVRows.add((String[])values.toArray(new String[values.size()]));
			if(++count % 500 == 0) {
				System.out.println("count : " + count);
			}
		}
		System.out.println("Writing to file ");
		
		String targetFilename = Global.getReportsStorageFolder() + "/invreport" + System.currentTimeMillis() + ".csv";  
		File target = new File(targetFilename);
		FileWriter fileOut = new FileWriter(target);
		CSVPrinter printer = new CSVPrinter(fileOut, CSVFormat.EXCEL);
		printer.printRecords(CSVRows);
		printer.close();
		fileOut.close();
		
	}
	
	public static void generateNonOemPlacesDealers() throws IOException {
		
		String query = "from Dealer d where d.datasource = 'GooglePlacesAPI' and d.oemDealer = false "
				+ "and d.sharesDomain = false and d.sharesAddress = false "
				+ "and d.address not like '%Puerto Rico%' and d.address not like '% PR %' "
				+ "and d.address not like '%British Virgin Islands%' "
				+ "and d.address not like '%, Mexico%' "
				+ "and d.address not like '%, Canada%' ";
		List<Dealer> dealers = JPA.em().createQuery(query, Dealer.class).getResultList();
		System.out.println("size : "  + dealers.size());
		createClean(dealers, "Non OEM Places Dealers");
	}
	
	public static void generatePlacesDealers() throws IOException {
		
		String query = "from Dealer d where d.datasource = 'GooglePlacesAPI'";
		createClean(JPA.em().createQuery(query, Dealer.class).getResultList(), "Places Dealers");
	}
	
	public static void generateCapdbDealers() throws IOException {
		String query = "from Dealer d where d.datasource = 'CapDB'";
		createClean(JPA.em().createQuery(query, Dealer.class).getResultList(), "CapDB Dealers");
	}
	
	public static void generateAllStaff() throws IOException {
		String query = "from Dealer d where d.mainSite is not null";
		List<Dealer> dealers = JPA.em().createQuery(query, Dealer.class).getResultList();
		generateStaff(dealers, "AllStaff");
	}
	
	public static void generateStaff(List<Dealer> dealers, String fileSuffix) throws IOException {
		List<String[]> CSVRows = new ArrayList<String[]>();
		String[] values = new String[] {"CapDB ID",
				"Google Places ID",
				"Crawl ID",
				"Site",
				"Name",
				"Title",
				"Email",
				"Phone",
				"Full Name (and more)",
				"Cell",
				"Other"
				
		};
		CSVRows.add(values);
		
		int count = 0;
		for(Dealer dealer : dealers) {
			count++;
			if(count % 200 == 0)
				System.out.println("printed site " + count);
			
			Site site = dealer.getMainSite();
			if(site != null && !site.getCrawls().isEmpty()){
				
				SiteCrawl siteCrawl = site.getLatestCrawl();
				siteCrawl.lazyInit();
				for(Staff staff : siteCrawl.getAllStaff()) {
					values = new String[] {dealer.getCapdb(),
							dealer.getPlacesId(),
							String.valueOf(siteCrawl.getSiteCrawlId()),
							site.getHomepage(),
							staff.getName(),
							staff.getTitle(),
							staff.getEmail(),
							staff.getPhone(),
							staff.getFn(),
							staff.getCell(),
							staff.getOther()
					};
					CSVRows.add(values);
				}
			}
		}
		
		System.out.println("Writing to file ");
		
		String targetFilename = Global.getReportsStorageFolder() + "/" +new SimpleDateFormat("MM-dd-yyyy").format(new java.util.Date()) + fileSuffix + ".csv";  
		File target = new File(targetFilename);
		FileWriter fileOut = new FileWriter(target);
		CSVPrinter printer = new CSVPrinter(fileOut, CSVFormat.EXCEL);
		printer.printRecords(CSVRows);
		printer.close();
		fileOut.close();
	}
	
	public static void createClean(List<Dealer> dealers, String fileSuffix) throws IOException {
		System.out.println("Creating report for " + dealers.size() + " dealers");
		List<String[]> CSVRows = new ArrayList<String[]>();
		String[] values = new String[] {"CapDB ID",
				"Google Places ID",
				"Crawl ID",
				"Dealer Name",
				"Homepage",
				"Domain", 
				"Address",
				"Phone",
				"Needs to be reviewed",
				"Should be removed",
				"Shares Address",
				"Shares Domain",
				"Number of pages in site",
				"Most Probable WP",
				"Number of Staff Found",
				"Number of social media URLs retrieved",
				"Web Provider matches",
				"Scheduling Tool matches",
				"Other General matches",
				"Emails on website",
				"Emails on Facebook"
				
		};
		CSVRows.add(values);
		
		int count = 0;
		for(Dealer dealer : dealers) {
//			System.out.println("dealer : " + dealer.getDealerName());
			count++;
			if(count % 200 == 0)
				System.out.println("printed site " + count);
			
			Site site = dealer.getMainSite();
			if(site == null){
				System.out.println("empty row");
				values = new String[] {dealer.getCapdb(),
						dealer.getPlacesId(),
						"",
						dealer.getDealerName(),
						"No URL",
						"",
						dealer.getAddress(),
						dealer.getPhone()};
			}
			else if(site.getCrawls().isEmpty()){
				
				values = new String[] {dealer.getCapdb(),
						dealer.getPlacesId(),
						"",
						dealer.getDealerName(),
						site.getHomepage(),
						site.getDomain(),
						dealer.getAddress(),
						dealer.getPhone(),
						"Error while crawling, no stats"};
			}
			else {
				SiteCrawl siteCrawl = site.getLatestCrawl();
				siteCrawl.lazyInit();
				StringBuilder sb = new StringBuilder();
				for(WebProvider item : siteCrawl.getWebProviders()) {
					sb.append(item.getDescription());
					sb.append(", ");
				}
				String wps = sb.toString();
				
				sb = new StringBuilder();
				for(Scheduler item : siteCrawl.getSchedulers()) {
					sb.append(item.getDescription());
					sb.append(", ");
				}
				String scheds = sb.toString();
				
				sb = new StringBuilder();
				for(GeneralMatch item : siteCrawl.getGeneralMatches()) {
					sb.append(item.getDescription());
					sb.append(", ");
				}
				String gens = sb.toString();
				
				sb = new StringBuilder();
				for(ExtractedString item : siteCrawl.getExtractedStrings()){
					sb.append(item.getValue());
					sb.append(", ");
				}
				String emailString = sb.toString();
				
				Set<String> fbEmailSet = new HashSet<String>();
				for(FBPage page : siteCrawl.getFbPages()){
					fbEmailSet.addAll(page.getEmails());
				}
				String fbEmails = fbEmailSet.toString();
				
				values = new String[]{dealer.getCapdb(),
						dealer.getPlacesId(),
						String.valueOf(siteCrawl.getSiteCrawlId()),
						dealer.getDealerName(),
						site.getHomepage(),
						site.getDomain(),
						dealer.getAddress(),
						dealer.getPhone(),
//						String.valueOf(site.isHomepageNeedsReview() | site.isReviewLater()),
						String.valueOf(site.isMaybeDefunct() | site.isDefunct()),
						String.valueOf(dealer.isSharesAddress()),
						String.valueOf(dealer.isSharesDomain()),
						String.valueOf(siteCrawl.getNumRetrievedFiles()),
						String.valueOf(siteCrawl.getInferredWebProvider()),
						String.valueOf(siteCrawl.getAllStaff().size()),
						String.valueOf(siteCrawl.getExtractedUrls().size()),
						wps,
						scheds,
						gens,
						emailString,
						fbEmails
				};
				
			}//end else
			CSVRows.add(values);
		}//end for(dealer)
		System.out.println("Writing to file ");
		
		String targetFilename = Global.getReportsStorageFolder() + "/" +new SimpleDateFormat("MM-dd-yyyy").format(new java.util.Date()) + fileSuffix + ".csv";  
		File target = new File(targetFilename);
		FileWriter fileOut = new FileWriter(target);
		CSVPrinter printer = new CSVPrinter(fileOut, CSVFormat.EXCEL);
		printer.printRecords(CSVRows);
		printer.close();
		fileOut.close();
	}
	
	public static void generateFranchiseCrawlCSV(List<SiteCrawl> crawls) throws ClassNotFoundException, IOException {
		
		List<String[]> CSVRows = new ArrayList<String[]>();
		String[] values = new String[] {"Crawl ID",
				"URL",
				"Number of pages in site",
				"Is Maybe a Duplicate",
				"Most Probable WP",
				"Number of Staff Found",
				"Number of social media URLs retrieved",
				"Web Provider matches",
				"Scheduling Tool matches",
				"Other General matches",
				"Emails"
				
		};
		CSVRows.add(values);
		
		int count = 0;
		for(SiteCrawl siteCrawl : crawls){
			count++;
			if(count % 200 == 0)
				System.out.println("printed site " + count);
			siteCrawl.lazyInit();
			StringBuilder sb = new StringBuilder();
			for(WebProvider item : siteCrawl.getWebProviders()) {
				sb.append(item.getDescription());
				sb.append(", ");
			}
			String wps = sb.toString();
			
			sb = new StringBuilder();
			for(Scheduler item : siteCrawl.getSchedulers()) {
				sb.append(item.getDescription());
				sb.append(", ");
			}
			String scheds = sb.toString();
			
			sb = new StringBuilder();
			for(GeneralMatch item : siteCrawl.getGeneralMatches()) {
				sb.append(item.getDescription());
				sb.append(", ");
			}
			String gens = sb.toString();
			
			sb = new StringBuilder();
			for(ExtractedString item : siteCrawl.getExtractedStrings()){
				sb.append(item.getValue());
				sb.append(", ");
			}
			String emailString = sb.toString();
			
			String inferredWp;
			if(siteCrawl.getInferredWebProvider() == null)
				inferredWp = WebProvider.NONE.getDescription();
			else
				inferredWp = siteCrawl.getInferredWebProvider().getDescription();
			
			values = new String[] {String.valueOf(siteCrawl.getSiteCrawlId()),
					siteCrawl.getSeed(),
					String.valueOf(siteCrawl.getNumRetrievedFiles()),
					String.valueOf(siteCrawl.isMaybeDuplicate()),
					inferredWp,
					String.valueOf(siteCrawl.getAllStaff().size()),
					String.valueOf(siteCrawl.getExtractedUrls().size()),
					wps,
					scheds,
					gens,
					emailString
			};
			CSVRows.add(values);
		}
		
		
		System.out.println("Writing to file ");
		
		String targetFilename = Global.getReportsStorageFolder() + "/" +new SimpleDateFormat("MM-dd-yyyy").format(new java.util.Date()) + "-SiteCrawls.csv";  
		File target = new File(targetFilename);
		FileWriter fileOut = new FileWriter(target);
		CSVPrinter printer = new CSVPrinter(fileOut, CSVFormat.EXCEL);
		printer.printRecords(CSVRows);
		printer.close();
		fileOut.close();
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
