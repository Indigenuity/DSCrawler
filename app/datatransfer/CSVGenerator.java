package datatransfer;

import global.Global;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;

import datadefinitions.GeneralMatch;
import datadefinitions.Scheduler;
import datadefinitions.WebProvider;
import persistence.Dealer;
import persistence.ExtractedString;
import persistence.FBPage;
import persistence.PlacesDealer;
import persistence.Site;
import persistence.SiteCrawl;
import persistence.SiteInformationOld;
import persistence.SiteSummary;
import persistence.Staff;
import persistence.Temp;
import play.db.jpa.JPA;
import scaffolding.Scaffolder;

public class CSVGenerator {
	
	public static void generateSpecialProjectReport() throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		String query = "select s from CrawlSet cs join cs.completedCrawls s where cs.crawlSetId = 7";
		List<SiteCrawl> siteCrawls = JPA.em().createQuery(query).getResultList();
		List<Temp> sfs = JPA.em().createQuery("from Temp t").getResultList();
		
		Map<String, Object> fields = Scaffolder.getBasicFields(siteCrawls.get(0));
		List<String[]> CSVRows = new ArrayList<String[]>();
		List<String> values = new ArrayList<String>();
		values.add("Salesforce Unique ID");
		values.add("Account Name");
		values.add("Primary Website URL:");
		values.add("Inferred Website Provider");
		values.add("Alt. Image Tag Score");
		
		values.add("Meta Description Content Score");
		values.add("URL Location Qualifier Score");
		values.add("Title Tag Content Score");
		
		values.add("Meta Description Length Score");
		values.add("Title Tag Length Score");
		
		values.add("Unique H1 Score");
		values.add("Unique Meta Description Score");
		values.add("Unique Title Tag Score");
		values.add("Unique URL Score");
//		values.add("Primary Website Provider");
		
		
		
		
		
		CSVRows.add((String[])values.toArray(new String[values.size()]));
		int count = 0;
		query = "from Temp t where t.standardizedUrl = :standardizedUrl";
		TypedQuery<Temp> q = JPA.em().createQuery(query, Temp.class);
		for(SiteCrawl siteCrawl : siteCrawls){
			fields = Scaffolder.getBasicFields(siteCrawl);
			values = new ArrayList<String>();
			Temp match = null;
			for(Temp sf: sfs) {
				if(StringUtils.equals(siteCrawl.getSeed(), sf.getStandardizedUrl())){
					match = sf;
					break;
				}
			}
			if(match == null || siteCrawl.getNumRetrievedFiles() < 10) {
				continue;
			}
			values.add(match.getSfId());
			values.add(match.getName());
			values.add(match.getStandardizedUrl());
			if(siteCrawl.getInferredWebProvider() != null){
				values.add(siteCrawl.getInferredWebProvider().getDescription());
			}
			else{
				values.add("");
			}
			values.add("" + siteCrawl.getAltImageScore());
			values.add("" + siteCrawl.getContentMetaDescriptionScore());
			values.add("" + siteCrawl.getContentUrlScore());
			values.add("" + siteCrawl.getContentTitleScore());
			values.add("" + siteCrawl.getLengthMetaDescriptionScore());
			values.add("" + siteCrawl.getLengthTitleScore());
			values.add("" + siteCrawl.getUniqueH1Score());
			values.add("" + siteCrawl.getUniqueMetaDescriptionScore());
			values.add("" + siteCrawl.getUniqueTitleScore());
			values.add("" + siteCrawl.getUniqueUrlScore());
//			if(siteCrawl.getInferredWebProvider() != null){
//				values.add(siteCrawl.getInferredWebProvider().name());				
//			}
//			else {
//				values.add("Couldn't infer WP");
//			}
			
			
//			q.setParameter("standardizedUrl", siteCrawl.getSeed());
//			List<Temp> temps =  q.getResultList();
//			if(temps.size() > 0 ){
//				values.add(temps.get(0).getSfId());
//			}
//			else {
//				values.add("No SalesForce");
//			}
			
			CSVRows.add((String[])values.toArray(new String[values.size()]));
			if(++count % 500 == 0) {
				System.out.println("count : " + count);
			}
		}
		
		System.out.println("Writing to file ");
		
		String targetFilename = Global.REPORTS_STORAGE_FOLDER + "/testing.csv";  
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
		List<Dealer> dealers = JPA.em().createQuery(query).getResultList();
		System.out.println("size : "  + dealers.size());
		createClean(dealers, "Non OEM Places Dealers");
	}
	
	public static void generatePlacesDealers() throws IOException {
		
		String query = "from Dealer d where d.datasource = 'GooglePlacesAPI'";
		List<Dealer> dealers = JPA.em().createQuery(query).getResultList();
		createClean(JPA.em().createQuery(query).getResultList(), "Places Dealers");
	}
	
	public static void generateCapdbDealers() throws IOException {
		String query = "from Dealer d where d.datasource = 'CapDB'";
		createClean(JPA.em().createQuery(query).getResultList(), "CapDB Dealers");
	}
	
	public static void generateSpecialStaff() throws IOException {
		String query = "from Dealer d where d.mainSite is not null and d.datasource = 'Special_Project'";
		List<Dealer> dealers = JPA.em().createQuery(query).getResultList();
		generateStaff(dealers, "RequestedDealersStaff");
		int fordDirect = WebProvider.FORD_DIRECT.getId();
		query = "from Dealer d where d.datasource != 'Special_Project' and "
				+ "d.datasource != 'GooglePlacesAPI' and exists "
				+ "(from Site s where s = d.mainSite and exists "
				+ "(from SiteCrawl sc where sc member of s.crawls and " 
				+ fordDirect + " member of sc.webProviders))";
		dealers = JPA.em().createQuery(query).getResultList();
		generateStaff(dealers, "Other Ford Direct Dealers Staff");
	}
	
	public static void generateAllStaff() throws IOException {
		String query = "from Dealer d where d.mainSite is not null";
		List<Dealer> dealers = JPA.em().createQuery(query).getResultList();
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
		
		String targetFilename = Global.REPORTS_STORAGE_FOLDER + "/" +new SimpleDateFormat("MM-dd-yyyy").format(new java.util.Date()) + fileSuffix + ".csv";  
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
				
				String inferredWp;
				if(siteCrawl.getInferredWebProvider() == null)
					inferredWp = WebProvider.NONE.getDescription();
				else
					inferredWp = siteCrawl.getInferredWebProvider().getDescription();
				
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
						String.valueOf(site.isHomepageNeedsReview() | site.isReviewLater()),
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
		
		String targetFilename = Global.REPORTS_STORAGE_FOLDER + "/" +new SimpleDateFormat("MM-dd-yyyy").format(new java.util.Date()) + fileSuffix + ".csv";  
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
		
		String targetFilename = Global.REPORTS_STORAGE_FOLDER + "/" +new SimpleDateFormat("MM-dd-yyyy").format(new java.util.Date()) + "-SiteCrawls.csv";  
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
		List<PlacesDealer> places = JPA.em().createQuery(query, PlacesDealer.class).getResultList();
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
		for(PlacesDealer place : places) {
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
		String targetFilename = Global.REPORTS_STORAGE_FOLDER + "/" +new SimpleDateFormat("MM-dd-yyyy").format(new java.util.Date()) + "-GooglePlaces.csv";  
		File target = new File(targetFilename);
		FileWriter fileOut = new FileWriter(target);
		CSVPrinter printer = new CSVPrinter(fileOut, CSVFormat.EXCEL);
		printer.printRecords(CSVRows);
		printer.close();
		fileOut.close();
	}
}
