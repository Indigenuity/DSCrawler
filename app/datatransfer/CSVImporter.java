package datatransfer;

import global.Global;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.EntityManager;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import dao.Finder;
import persistence.CanadaPostal;
import persistence.CapEntry;
import persistence.Dealer;
import persistence.ExtractedString;
import persistence.SFEntry;
import persistence.Site;
import persistence.SiteCrawl;
import persistence.Dealer.Datasource;
import persistence.Temp;
import play.db.jpa.JPA;
import utilities.DSFormatter;
import utilities.UrlSniffer;

public class CSVImporter {
	
	public static void importTemp() throws IOException{
		Reader in = new FileReader("C:\\Workspace\\DSStorage\\source/AccountIdentifiers12-3-2015.csv");
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(in);
		
		Temp entry;
		Site site;
		EntityManager em = JPA.em();
		int total = 0;
		for(CSVRecord record : records) {
			
			entry = new Temp();
			entry.setSfId(record.get("Salesforce Unique ID"));
			entry.setGivenUrl(record.get("Website"));
			String intermediate = DSFormatter.toHttp(entry.getGivenUrl());
			entry.setIntermediateUrl(intermediate);
			try{
				String standard = DSFormatter.standardize(intermediate);
				entry.setStandardizedUrl(standard);				
			}
			catch(Exception e) {
				entry.setStandardizedUrl(null);
			}
			
			total++;
			
			System.out.println("Imported : " + total);
			em.persist(entry);
		}
		
		in.close();
	}
	
	public static void importSf() throws IOException{
		Reader in = new FileReader("C:\\Workspace\\DSStorage\\source/report1446049437609.csv");
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(in);
		
		SFEntry entry;
		Site site;
		EntityManager em = JPA.em();
		int total = 0;
		for(CSVRecord record : records) {
			
			entry = new SFEntry();
			entry.setAccountId(record.get("Salesforce Unique ID"));
//			entry.setAccountAuto(record.get("Account Auto Number"));
//			entry.setAccountType(record.get("Account Type"));
//			entry.setName(record.get("Account Name"));
//			entry.setState(record.get("Dealership State/Province"));
			entry.setGivenUrl(record.get("Website"));
			entry.setAccountLevel("Account Level");
			entry.setParentAccount("Parent Account");
			entry.setParentAccountId("Parent Account ID");
			entry.setImportDate(new Date((new java.util.Date().getTime())));
//			if(!DSFormatter.isEmpty(entry.getGivenUrl())){
//				site = new Site();
//				site.setHomepage(entry.getGivenUrl());
//				entry.setMainSite(site);
//			}
			total++;
			
			System.out.println("Imported : " + total);
			em.persist(entry);
		}
		
		in.close();
	}
	
	
	public static void importCapFranchiseCSV() throws Exception {
		Reader in = new FileReader("C:\\Workspace\\DSCrawler\\dealersocket\\source/CaPDB_Franchise_Src.csv");
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(in);
		
		Set<String> uniqueHosts = new TreeSet<String>();
		CapEntry row;
		EntityManager em = JPA.em();
		int total = 0;
		for(CSVRecord record : records) {
			row = new CapEntry();
			row.lead_no = record.get("lead_no");
			row.dealershipName = record.get("DEALERSHIP NAME");
			row.address = record.get("STREET ADDRESS");
			row.city = record.get("CITY");
			row.state = record.get("STATE");
			row.zip = record.get("ZIP");
			row.country = record.get("COUNTRY");
			row.phone = record.get("PHONE");
			row.website = record.get("WEBSITE");
			row.email = record.get("EMAIL");
			row.firstName = record.get("FIRST NAME");
			row.lastName = record.get("LAST NAME");
			row.role = record.get("Role");
			row.first1 = record.get("1. First");
			row.last1 = record.get("1. Last");
			row.role1 = record.get("1. Role");
			row.first2 = record.get("2. First");
			row.last2 = record.get("2. Last");
			row.role2 = record.get("2. Role");
			row.first3 = record.get("3. First");
			row.last3 = record.get("3. Last");
			row.role3 = record.get("3. Role");
			row.rating = record.get("RATING");
			row.leadStatus = record.get("LEADSTATUS");
			
			try{
				row.inventory = Integer.valueOf(record.get("INVENTORY"));
			}
			catch(Exception e) {
				row.inventory = -1;
			} 
			row.webProvider = record.get("WEBSITEPROVIDER");
			row.hasScheduler = record.get("HAS ONLINE SVC SCHEDULING TOOL");
			row.schedulerProvider = record.get("OnlineSvcScheduler");
			row.audi = Boolean.valueOf(record.get("AUDI"));
			row.bmw = Boolean.valueOf(record.get("BMW"));
			row.hyundai = Boolean.valueOf(record.get("HYUNDAI"));
			row.infiniti = Boolean.valueOf(record.get("INFINITI"));
			row.jaguar = Boolean.valueOf(record.get("JAGUAR"));
			row.jeep = Boolean.valueOf(record.get("JEEP"));
			row.landRover = Boolean.valueOf(record.get("LAND ROVER"));
			row.lexus = Boolean.valueOf(record.get("LEXUS"));
			row.lincoln = Boolean.valueOf(record.get("LINCOLN"));
			row.mercedes = Boolean.valueOf(record.get("MERCEDES"));
			row.mini = Boolean.valueOf(record.get("MINI"));
			row.porsche = Boolean.valueOf(record.get("PORSCHE"));
			row.toyota = Boolean.valueOf(record.get("TOYOTA"));
			row.vw =Boolean.valueOf( record.get("VW"));
			row.volvo =Boolean.valueOf( record.get("VOLVO"));
			row.acura = Boolean.valueOf(record.get("ACURA"));
			row.chevy = Boolean.valueOf(record.get("CHEVY"));
			row.chrysler = Boolean.valueOf(record.get("CHRYSLER"));
			row.dodge = Boolean.valueOf(record.get("DODGE"));
			row.fiat = Boolean.valueOf(record.get("FIAT"));
			row.ford = Boolean.valueOf(record.get("FORD"));
			row.gmc = Boolean.valueOf(record.get("GMC"));
			row.hummer = Boolean.valueOf(record.get("HUMMER"));
			row.kia = Boolean.valueOf(record.get("KIA"));
			row.mazda =Boolean.valueOf( record.get("MAZDA"));
			row.nissan = Boolean.valueOf(record.get("NISSAN"));
			row.scion = Boolean.valueOf(record.get("SCION"));
			row.smart = Boolean.valueOf(record.get("SMART"));
			row.subaru = Boolean.valueOf(record.get("SUBARU"));
			row.buick = Boolean.valueOf(record.get("BUICK"));
			row.cadillac = Boolean.valueOf(record.get("CADILLAC"));
			row.honda = Boolean.valueOf(record.get("HONDA"));
			row.mitsubishi = Boolean.valueOf(record.get("MITSUBISHI"));
			row.bentley = Boolean.valueOf(record.get("BENTLEY"));
			row.lotus = Boolean.valueOf(record.get("LOTUS"));
			row.maserati = Boolean.valueOf(record.get("MASERATI"));
			row.astonMartin = Boolean.valueOf(record.get("ASTON MARTIN"));
			row.ferrari = Boolean.valueOf(record.get("FERRARI"));
			row.mclaren = Boolean.valueOf(record.get("MCLAREN"));
			row.lamborghini = Boolean.valueOf(record.get("LAMBORGHINI"));
			row.rollsRoyce = Boolean.valueOf(record.get("ROLLSROYCE"));
			row.singlePoint = record.get("SINGLE/MULTI POINT");
			row.groupName = record.get("GROUP NAME");
			row.numSalesPeople = record.get("# OF SALESPEOPLE");
			row.hasBDC = record.get("HAS BDC DEPT");
			row.salesServiceBDC = record.get("DOES BDC HANDLE SALES AND SVC");
			row.carsSold = record.get("CARS SOLD PER MONTH");
			row.CRM = record.get( "CRM");
			row.salesServiceCRM = record.get("CRM FOR SALES AND SERVICE");
			row.serviceSoftware = record.get("SERVICE SOFTWARE");
			row.currentCRM = record.get("HOW LONG WITH CURRENT CRM");
			row.DMS = record.get("DMS");
			row.useDataMining = record.get("DO YOU USE DATAMINING TOOL");
			row.dataMiningToold = record.get("DATAMININGTOOL");
			row.currentCRMSatisfaction = record.get("HOW SATISFIED WITH CURRENT CRM");
			row.timeLeftCRM = record.get("TIME LEFT IN CURRENT CONTRACT");
			row.inMarketCRM = record.get("IN THE MARKET");
			row.areaCode = record.get("AreaCode");
			row.franchise = true;
			em.persist(row);
			System.out.println("Saving Cap Entry " + ++total + " " + row.dealershipName);
		}
		
		
	}
	
	public static void importCapIndependentCSV() throws Exception {
		Reader in = new FileReader("C:\\Workspace\\DSStorage\\source/CaPDB_Indep_062615.csv");
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(in);
		
		CapEntry row;
		EntityManager em = JPA.em();
		int total = 0;
		for(CSVRecord record : records) {
			row = new CapEntry();
			row.lead_no = record.get("Lead Number");
			row.niada = record.get("NIADA ID");
			row.rating = record.get("Rating");
			row.dealershipName = record.get("Dealership");
			row.address = record.get("Street");
			row.city = record.get("City");
			row.state = record.get("State");
			row.zip = record.get("Postal Code");
			row.country = record.get("Country");
			row.phone = record.get("Primary Phone");
			row.website = record.get("Website");
			row.email = record.get("Primary Email");
			row.firstName = record.get("First Name");
			row.lastName = record.get("Last Name");
			row.role = record.get("Role");
			row.first1 = record.get("1. First Name");
			row.last1 = record.get("1. Last Name");
			row.role1 = record.get("1. Role");
			row.first2 = record.get("2. First Name");
			row.last2 = record.get("2. Last Name");
			row.leadStatus = record.get("Lead Status");
			row.yearsInOperation = record.get( "How long has your business been in operation?");
			row.singleMulti = record.get("Single/Multi Point");
			row.groupName = record.get("Group Name");
			row.numLocations = record.get( "If Multiple Locations - How many?");
			row.numEmployees = record.get( "How many employees do you have? (incl. yourself)");
			row.characterizeInventory = record.get( "How would you characterize your inventory?");
			row.monthSales = record.get( "How many vehicles do you sell monthly?");
			row.retailAverage = record.get( "Average retail price per unit sold?");
			row.vehicleAge = record.get( "How old are most of the vehicles sold?");
			row.averageDown = record.get( "Average Down Payment?");
			row.howAdvertise = record.get( "How do you advertise?");
			row.adSpend = record.get( "Average Ad Spend per month?");
			row.reasonNotInterested = record.get("Reason Not Interested");
			
			row.noStaffPage = Boolean.valueOf(record.get("No Staff Page"));
			row.retail = Boolean.valueOf(record.get("Retail"));
			row.wholesale = Boolean.valueOf(record.get("Wholesale"));
			row.leasing = Boolean.valueOf(record.get("Leasing"));
			row.partsDept = Boolean.valueOf(record.get("Parts Dept"));
			row.rental = Boolean.valueOf(record.get("Rental"));
			row.serviceDept = Boolean.valueOf(record.get("Service Dept"));
			row.bhph= Boolean.valueOf(record.get( "BHPH (BuyHerePayHere)"));
			row.serviceInternal = record.get( "If has Service Dept - Retail or Internal only?");
			row.deliveryVehicles = Boolean.valueOf(record.get( "Delivery Vehicle Sales"));
			row.accessibilityVehicles = Boolean.valueOf(record.get( "Accessibility Vehicle Sales"));
			row.howFinance = record.get( "How do you finance/floorplan your inventory?");
			row.whichFinance = record.get( "Which financing options do you offer customers?");
			row.relatedFinance = record.get( "If BHPH - do you have a related finance company?");
			try{
				row.inventory = Integer.valueOf(record.get("How many vehicles in inventory on average?"));
			}
			catch(Exception e) {
				row.inventory = -1;
			} 
			row.webProvider = record.get("Website Provider");
			row.singlePoint = record.get("Single/Multi Point");
			row.groupName = record.get("Group Name");
			row.hasCRM = record.get("Has CRM?");
			row.CRM = record.get( "If yes - Which CRM?");
			row.DMS = record.get("DMS");
			
			row.franchise = false;

			System.out.println("Saving Cap Entry " + ++total + " " + row.dealershipName);
			em.persist(row);
		}
	}
	
	public static void importCanadaPostals() throws IOException {
		Reader in = new FileReader("C:\\Workspace\\DSStorage\\source/ca_postal_codes.csv");
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(in);
		
		CanadaPostal row;
		EntityManager em = JPA.em();
		int total = 0;
		for(CSVRecord record : records) {
			row = new CanadaPostal();
			
			row.latitude = Double.valueOf(record.get("Latitude"));
			row.longitude= Double.valueOf(record.get("Longitude"));
			row.code = record.get("Postal Code");
			row.name = record.get("Place Name");
			row.province = record.get("Province");
			JPA.em().persist(row);
		}
	}
	
	public static void specialProjectCSV2() throws IOException {
		Reader in = new FileReader("C:\\Workspace\\DSStorage\\source\\datasetfordilm.csv");
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(in);
		
		EntityManager em = JPA.em();
		int total = 0;
		List<String> urls = new ArrayList<String>();
		for(CSVRecord record : records) {
			Dealer dealer = new Dealer();
			dealer.setDealerName(record.get(0));
			dealer.setDatasource(Datasource.Special_Project);
			Site site = new Site();
			site.setHomepage(record.get(0));
			em.persist(dealer);
		}
	}
	
	//To be changed at will and ad hoc for special reports
	public static void specialProjectCSV() throws Exception {
		Reader in = new FileReader("C:\\Workspace\\DSStorage\\source\\Ford2.csv");
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(in);
		
		EntityManager em = JPA.em();
		int total = 0;
		Set<Dealer> unique = new HashSet<Dealer>();
		Set<Site> uniqueSites = new HashSet<Site>();
		
		List<String[]> CSVRows = new ArrayList<String[]>();
		String[] values;
		String emailString;
		boolean found = false;
		Map<String, String> toCreate = new HashMap<String, String>();
		for(CSVRecord record : records) {
			found = false;
			String url = record.get("website");
			String name = record.get("Dealership Name");
			String state = record.get("State");
			
			
			List<Dealer> dealers = Finder.findDealerByName(name);
			
			for(Dealer dealer : dealers) {
				if(unique.add(dealer)){
					found = true;
					Site site = dealer.getMainSite();
					if(site.getCrawls().size() > 0){
					SiteCrawl siteCrawl = site.getCrawls().get(0);
					Set<ExtractedString> strings = siteCrawl.getExtractedStrings();
					
					emailString = strings.toString();
					values = new String[] {dealer.getDealerName(),
							dealer.getCapdb(),
							state,
							site.getHomepage(),
							emailString
					};
					CSVRows.add(values);
					}
				}
			}
			
			if(dealers.size() < 1) {
				
//				System.out.println("Dealership : " + name + "(" + dealers.size() + ")");
				List<Site> sites = Finder.findSiteByDomain(url);
				
//				if(sites.size() > 0) {
//					System.out.println("found sites though : " + sites.size());
//				}
				
				for(Site site : sites) {
					found = true;
					SiteCrawl siteCrawl = site.getCrawls().get(0);
					Set<ExtractedString> strings = siteCrawl.getExtractedStrings();
					
					emailString = strings.toString();
					values = new String[] {name,
							"",
							state,
							site.getHomepage(),
							emailString
					};
					CSVRows.add(values);
				}
				if(!found && !url.equals("No Website")) {
					toCreate.put(name,  url);
					
					System.out.println("Nothing found for : " + name + "(" + url + ")");
				}
			}
		}
		for(String name : toCreate.keySet()){
			String url = toCreate.get(name);
			Dealer newDealer = new Dealer();
			Site newSite = new Site();
			String homepage = DSFormatter.regularize(url);
			System.out.println("getting redirect");
			homepage = UrlSniffer.getRedirectedUrl(homepage);
			newSite.setHomepage(url);
			newSite.setDomain(DSFormatter.getDomain(homepage));
			
			newDealer.setDatasource(Datasource.OEM);
			newDealer.setDealerName(name);
			newDealer.setMainSite(newSite);
//			JPA.em().persist(newDealer);
		}
		System.out.println("unique size : " + unique.size());
//		
//		List<String[]> CSVRows = new ArrayList<String[]>();
//		String[] values;
//		String emailString;
//		for(Dealer dealer : unique) {
//			Site site = dealer.getMainSite();
//			CapEntry capEntry = Finder.findCapEntryByCapdb(dealer.getCapdb()).get(0);
//			SiteCrawl siteCrawl = site.getCrawls().get(0);
//			Set<ExtractedString> strings = siteCrawl.getExtractedStrings();
//			
//			emailString = strings.toString();
//			
//			values = new String[] {dealer.getDealerName(),
//					dealer.getCapdb(),
//					capEntry.state.toUpperCase(),
//					site.getHomepage(),
//					emailString
//			};
//			CSVRows.add(values);
//		}
//		
		System.out.println("Writing to file ");
		
		FileWriter fileOut = new FileWriter(new File(Global.getStorageFolder() + "/csv/fordCustomList2.csv"));
		CSVPrinter printer = new CSVPrinter(fileOut, CSVFormat.EXCEL);
		printer.printRecords(CSVRows);
		printer.close();
		fileOut.close();
//		
	}
	
}
