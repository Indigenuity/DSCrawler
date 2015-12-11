package experiment;

import global.Global;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.EntityType;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVPrinter;


import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import crawling.DealerCrawlController;
import dao.SitesDAO;
import datadefinitions.GeneralMatch;
import datadefinitions.Scheduler;
import datadefinitions.StringExtraction;
import datadefinitions.UrlExtraction;
import datadefinitions.WebProvider;
import datatransfer.Amalgamater;
import datatransfer.CSVGenerator;
import datatransfer.CSVImporter;
import datatransfer.Cleaner;
import akka.actor.ActorRef;
import analysis.SiteCrawlAnalyzer;
import async.Asyncleton;
import async.work.SiteWork;
import async.work.WorkItem;
import async.work.WorkSet;
import async.work.WorkType;
import persistence.CrawlSet;
import persistence.Dealer;
import persistence.ExtractedString;
import persistence.ExtractedUrl;
import persistence.FBPage;
import persistence.PageCrawl;
import persistence.Site;
import persistence.SiteCrawl;
import play.db.DB;
import play.db.jpa.JPA;
import scaffolding.Scaffolder;
import utilities.DSFormatter;
import utilities.FB;

public class Experiment {
	
	public static void runExperiment() {
		SiteCrawl siteCrawl  = JPA.em().find(SiteCrawl.class, 97878L);
		
		for(WebProvider wp : siteCrawl.getWebProviders()){
			System.out.println("wp :  " + wp);
		}
		System.out.println("inferred : " + siteCrawl.getInferredWebProvider());
	}
	public static void sfExport() throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		CSVGenerator.generateSpecialProjectReport();
	}
	
	public static void setSiteCities() {
		List<Site> sites = JPA.em().createQuery("from Site s").setMaxResults(5000).setFirstResult(50000).getResultList();
		System.out.println("sites size : " + sites.size());
		int count = 0;
		for(Site site : sites) {
			List<Dealer> dealers = JPA.em().createQuery("from Dealer d where d.mainSite.siteId = " + site.getSiteId(), Dealer.class).getResultList();
			Set<String> cities = new HashSet<String>();
//			System.out.println("dealers : " + dealers.size());
			for(Dealer dealer : dealers) {
				if(!StringUtils.isEmpty(dealer.getCity())){
					cities.add(dealer.getCity());					
				}
				
			}
//			System.out.println("cities : " + cities);
			site.setCities(cities);
			if(count++ % 499 == 0) {
				System.out.println("count : " + count);
				JPA.em().getTransaction().commit();
				JPA.em().getTransaction().begin();
			}
		}
	}
	
	public static void parseDealerAddresses() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IntrospectionException, NoSuchMethodException {
		Dealer dealertemp = JPA.em().find(Dealer.class, 48L);
		List<Dealer> dealers = JPA.em().createQuery("from Dealer d where d.address is not null", Dealer.class).getResultList();
		
		Matcher matcher;
		System.out.println("City matcher : " + StringExtraction.CITY.getPattern());
		int count = 0;
		int nomatch = 0;
		for(Dealer dealer : dealers) {
			matcher = StringExtraction.CITY.getPattern().matcher(dealer.getAddress());
			if(matcher.find()) {
				String city = matcher.group(1);
				String state = matcher.group(2);
//				System.out.println("city : " + city);
//				System.out.println("state : " + state);
				dealer.setCity(city);
				dealer.setState(state);
			}
			else {
				nomatch++;
//				System.out.println("no match : " + dealer.getAddress());
			}
			if(count++ % 500 == 0) {
				System.out.println("count : " + count);
				JPA.em().getTransaction().commit();
				JPA.em().getTransaction().begin();
			}
		}
		System.out.println("nomatch : " + nomatch);
	}
	
	
	
	public static void runRedirectExperiment() {
		String query = "select s from CrawlSet cs join cs.sites s where s.homepageNeedsReview = true ";
		List<Site> sites = JPA.em().createQuery(query, Site.class).getResultList();
//		Site temp = JPA.em().find(Site.class, 78732L);
//		List<Site> sites = new ArrayList<Site>();
//		sites.add(temp);
		System.out.println("sites size : " + sites.size());
		for(Site site : sites) {
			WorkSet workSet = new WorkSet();
			workSet.setSiteId(site.getSiteId());
			WorkItem workItem = new WorkItem(WorkType.REDIRECT_RESOLVE);
			workSet.addWorkItem(workItem);
			JPA.em().detach(site);
			Asyncleton.instance().getMainMaster().tell(workSet, ActorRef.noSender());
		}
	}
	public static void dedupDomains() {
		List<String> dups = SitesDAO.getDuplicateDomains(500, 0);
		for(String domain : dups) {
			List<Site> sites = SitesDAO.getList("domain", domain, 0,0);
			if(sites.size() != 2) {
				continue; 
			}
			String firstHomepage = sites.get(0).getHomepage();
			String secondHomepage = sites.get(1).getHomepage();
			String firstSlash = firstHomepage + "/";
			String secondSlash = secondHomepage + "/";
			if(firstHomepage.equals(secondSlash)){
				Cleaner.combineOnDomain(sites.get(0));
				System.out.println("favoring first");
			}
			else if(secondHomepage.equals(firstSlash)){
				Cleaner.combineOnDomain(sites.get(1));
				System.out.println("favoring second");
			}
			else{
				System.out.println("favoring none");
			}
			
		}
	}
	
	public static void runasdfExperiment() {
		
		String query = "from Site s where exists (select * from Site s2 where franchise = true and invalidUrl = false";
		List<Site> sites = JPA.em().createQuery(query, Site.class).getResultList();
	}
	
	public static void runProxyExperiment() throws Exception {

		String seed = "http://myrkkia.com/";
//		String seed = "http://kengarffvw.com";
//		String seed = "https://bayden.com/echo.aspx";
//		String seed = "http://facebook.com";
//		MobileCrawl mobileCrawl= MobileCrawler.defaultMobileCrawl(seed);
//		
//		System.out.println("resolved seed : " + mobileCrawl.getResolvedSeed());
		Site.isBoolean("homepageNeedsReview");
	}
	
	public static void assignFranchise() {
		
		String query = "from Dealer d where d.mainSite != null";
		List<Dealer> dealers = JPA.em().createQuery(query, Dealer.class).getResultList();
		System.out.println("dealer count : " + dealers.size());
		int count = 0;
		for(Dealer dealer : dealers) {
			if(count++ % 1000 == 0){
				System.out.println("Working on dealer : " + count);
			}
			dealer.getMainSite().setFranchise(dealer.isFranchise());
		}
	}
	
	public static void assignFb() {
		String query = "from SiteCrawl sc where sc.extractedUrls is not empty";
		System.out.println("executing query");
		int max = 5000;
		int offset = 36000;
		List<SiteCrawl> crawls = JPA.em().createQuery(query, SiteCrawl.class).setMaxResults(max).setFirstResult(offset).getResultList();
		System.out.println("size : " + crawls.size());
		
		query = "from FBPage where givenUrl = ?";
		javax.persistence.Query q = JPA.em().createQuery(query);
		
		int count = 0;
		for(SiteCrawl siteCrawl : crawls) {
			System.out.println("num urls: " + siteCrawl.getExtractedUrls().size());
			for(ExtractedUrl url : siteCrawl.getExtractedUrls()) { 
				if(url.getUrlType() == UrlExtraction.FACEBOOK && FB.getIdentifier(url.getValue()) != FB.NOT_A_PAGE){
					q.setParameter(1, url.getValue());
					List<FBPage> pages = q.getResultList();
					if(pages.size() > 0) {
						siteCrawl.addFbPage(pages.get(0));
						System.out.println("adding fbpage on givenurl : " + url.getValue());
					}
				}
			}
			
			count++;
			if(count %100 == 0) {
				System.out.println("committing transaction: " + count);
				JPA.em().getTransaction().commit();
				JPA.em().getTransaction().begin();
			}
			JPA.em().detach(siteCrawl);
		}
		
		
	}
	
	
	public static void runExperiment96806() {
		System.out.println("starting");
		String query = "from SiteCrawl sc where sc.siteCrawlId >= 96806";
		List<SiteCrawl> siteCrawls = JPA.em().createQuery(query, SiteCrawl.class).getResultList();
		System.out.println("got the stuff");
	}
	
	public static void doClear() {
		System.out.println("doing the clear");
//		JPA.em().flush();
//		JPA.em().clear();
		System.gc();
	}
	
	
	public static void move100() {
		CrawlSet crawlSet = JPA.em().find(CrawlSet.class, 2L);
		String query = "select sc from CrawlSet cs join cs.completedCrawls sc where sc.filesMoved = true and cs.crawlSetId = 2";
		List<SiteCrawl> siteCrawls = JPA.em().createQuery(query, SiteCrawl.class).getResultList();
		int count = 0;
		for(SiteCrawl siteCrawl : siteCrawls) {
			
			if(!siteCrawl.isFilesMoved()){
				System.out.println("not moved : " + siteCrawl.getSiteCrawlId());
			}
//					
			if(siteCrawl.isFilesMoved() && count < 5000){
				System.out.println("file location: " + siteCrawl.getStorageFolder());
				File origin = new File(Global.SECONDARY_CRAWL_STORAGE_FOLDER + siteCrawl.getStorageFolder());
				File destination = new File(Global.CRAWL_STORAGE_FOLDER + siteCrawl.getStorageFolder());
				count++;
				try{
					FileUtils.moveDirectory(origin, destination);
				}
				catch(Exception e) {
					System.out.println(e);
				}
				siteCrawl.setFilesMoved(false);
			}
		}
	}
	
	
	public static void runExpasderiment() { 
		
		CrawlSet crawlSet = JPA.em().find(CrawlSet.class, 2L);
		String query = "select d from Dealer d join d.mainSite s where d.datasource = 'salesforce' "
				+ "and s.homepageNeedsReview = false and s.reviewLater = false and s.maybeDefunct = false "
				+ "and s.defunct = false group by domain having count(*) < 2";
		List<Dealer> dealers= JPA.em().createQuery(query, Dealer.class).getResultList();
		
		System.out.println("dealers : " + dealers.size());
		Set<Site> sites = new HashSet<Site>();
		for(Dealer dealer : dealers) {
			sites.add(dealer.getMainSite());
		}
		
		query = "from SiteCrawl sc where sc.crawlDate >= '2015-09-23' and seed != 'http://www.jjkane.com/'";
		
		List<SiteCrawl> siteCrawls = JPA.em().createQuery(query, SiteCrawl.class).getResultList();
		
		System.out.println("siteCrawls : " + siteCrawls.size());
		int count = 0;
		for(SiteCrawl siteCrawl : siteCrawls) {
			if(sites.contains(siteCrawl.getSite())) {
				sites.remove(siteCrawl.getSite());
				System.out.println("removing site");
				count++;
			}
		}
		System.out.println("already crawled count : " + count);
		
		crawlSet.setCompletedCrawls(siteCrawls);
		crawlSet.setUncrawled(sites);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -2);
		java.util.Date yesterday = cal.getTime();
		crawlSet.setStartDate(new Date(yesterday.getTime()));
		
		crawlSet.setName("DealerFire Research Crawl");
		JPA.em().persist(crawlSet);
		
	}
	
	public static void crawlNonDupes() {
		String query = "select d from Dealer d join d.mainSite s where d.datasource = 'salesforce' "
				+ "and s.homepageNeedsReview = false and s.reviewLater = false and s.maybeDefunct = false "
				+ "and s.defunct = false group by domain having count(*) < 2";
		List<Dealer> dealers= JPA.em().createQuery(query, Dealer.class).setMaxResults(1000).getResultList();
		
		Set<Site> sites = new HashSet<Site>();
		for(Dealer dealer : dealers) {
			sites.add(dealer.getMainSite());
		}
		
		for(Site site : sites){ 
			JPA.em().detach(site);
			SiteWork work = new SiteWork();
			work.setSite(site);
			work.setCrawlWork(SiteWork.DO_WORK);
//			work.setDocAnalysisWork(SiteWork.DO_WORK);
//			work.setAmalgamationWork(SiteWork.DO_WORK);
//			work.setTextAnalysisWork(SiteWork.DO_WORK);
			System.out.println("site id " + site.getSiteId());
			Asyncleton.instance().getMainMaster().tell(work, ActorRef.noSender());
		}
		
		System.out.println("sites : " + sites.size());
		
	}
	
	public static void runExpeasdfriment() throws Exception {
		Site site = JPA.em().find(Site.class, 38552L);
		
		SiteCrawl siteCrawl = DealerCrawlController.crawlSite(site.getHomepage());
//		SiteCrawl siteCrawl = new SiteCrawl("http://www.jjkane.com/");
//		PageCrawl pageCrawl = new PageCrawl();
//		pageCrawl.setUrl("http://www.jjkane.com/");
//		siteCrawl.addPageCrawl(pageCrawl);
//		JPA.em().persist(pageCrawl);
		JPA.em().persist(siteCrawl);
//		site.addCrawl(siteCrawl);
//		
//		JPA.em().merge(site);
		
	}
	
	public static void getUncopied() throws IOException, SQLException {
		Connection connection = DB.getConnection();
		String query = "select * from capentry where lead_no not in (select lead_no from capentry ce "
				+ "join dealer d on d.capdb = ce.lead_no)";
		Statement statement = connection.createStatement();
		
		ResultSet rs = statement.executeQuery(query);
		
		List<Dealer> dealers = new ArrayList<Dealer>();
		int count = 0;
		while(rs.next()) {
			System.out.println("count : " + ++count);
			Dealer dealer = new Dealer();
			dealer.setCapdb(rs.getString("lead_no"));
			dealer.setDealerName(rs.getString("dealershipName"));
			dealer.setFranchise(true);
			dealer.setDatasource(Dealer.Datasource.CapDB);
			
			String website = rs.getString("website");
			if(!DSFormatter.isEmpty(website)){
				Site site = new Site();
				site.setHomepage(website);
				dealer.setMainSite(site);
			}
			dealers.add(dealer);
		}
		
		rs.close();
		statement.close();
		connection.close();
		
		for(Dealer dealer : dealers) {
			System.out.println("dealer : " + dealer.getDealerName());
			JPA.em().persist(dealer);
		}
	}
	
	
	public static void moveToSecondary() {
		File storageRoot = new File(Global.CRAWL_STORAGE_FOLDER);
		
		for(File dateFolder : storageRoot.listFiles()) {
			for(File siteFolder : dateFolder.listFiles()){
				for(File file : siteFolder.listFiles()){
					System.out.println("filename length : " + file.getName().length());
					String shortname = file.getParentFile().getAbsolutePath() + String.valueOf(file.getName().hashCode());
					file.renameTo(new File(shortname));
				}
			}
		}
	}
	
	public static void makeLookupTables() throws SQLException {
		Connection connection = DB.getConnection();
		
		String query = "delete from webprovider where webproviderid >= 0";
		Statement deleteStatement = connection.createStatement();
		deleteStatement.executeUpdate(query);
		
		query = "delete from stringextraction where stringextractionid >= 0";
		deleteStatement = connection.createStatement();
		deleteStatement.executeUpdate(query);
		
		query = "delete from urlextraction where urlextractionid >= 0";
		deleteStatement = connection.createStatement();
		deleteStatement.executeUpdate(query);
		
		query = "delete from generalmatch where generalmatchid >= 0";
		deleteStatement = connection.createStatement();
		deleteStatement.executeUpdate(query);
		
		query = "delete from scheduler where schedulerid >= 0";
		deleteStatement = connection.createStatement();
		deleteStatement.executeUpdate(query);
		 
		query = "insert into webprovider (webproviderid, description, definition, notes, name) values (?, ?, ?, ?, ?)";
		PreparedStatement statement = connection.prepareStatement(query);
		Set<Integer> testing = new HashSet<Integer>();
		
		
		for (WebProvider enumElement : WebProvider.values()){
//			System.out.println("inserting with id : " + enumElement.getId());
			statement.setInt(1, enumElement.getId());
			statement.setString(2, enumElement.getDescription());
			statement.setString(3, enumElement.getDefinition());
			statement.setString(4, enumElement.getNotes());
			statement.setString(5, enumElement.name());
			if(!testing.add(enumElement.getId())){
				System.out.println("dup : " + enumElement.getId());
			}
			statement.executeUpdate();
		}
		
		
		testing.clear();
		query = "insert into scheduler (schedulerid, description, definition, notes, name) values (?, ?, ?, ?, ?)";
		statement = connection.prepareStatement(query);
		for (Scheduler enumElement : Scheduler.values()){
//			System.out.println("inserting with id : " + enumElement.getId());
			statement.setInt(1, enumElement.getId());
			statement.setString(2, enumElement.getDescription());
			statement.setString(3, enumElement.getDefinition());
			statement.setString(4, enumElement.getNotes());
			statement.setString(5, enumElement.name());
			statement.executeUpdate();
			if(!testing.add(enumElement.getId())){
				System.out.println("dup : " + enumElement.getId());
			}
		}
		
		testing.clear();
		query = "insert into generalmatch (generalmatchid, description, definition, notes, name) values (?, ?, ?, ?, ?)";
		statement = connection.prepareStatement(query);
		for (GeneralMatch enumElement : GeneralMatch.values()){
//			System.out.println("inserting with id : " + enumElement.getId());
			statement.setInt(1, enumElement.getId());
			statement.setString(2, enumElement.getDescription());
			statement.setString(3, enumElement.getDefinition());
			statement.setString(4, enumElement.getNotes());
			statement.setString(5, enumElement.name());
			statement.executeUpdate();
			if(!testing.add(enumElement.getId())){
				System.out.println("dup : " + enumElement.getId());
			}
		}
		
		testing.clear();
		query = "insert into urlextraction (urlextractionid, description, definition, notes, name) values (?, ?, ?, ?, ?)";
		statement = connection.prepareStatement(query);
		for (UrlExtraction enumElement : UrlExtraction.values()){
//			System.out.println("inserting with id : " + enumElement.getId());
			statement.setInt(1, enumElement.getId());
			statement.setString(2, enumElement.getDescription());
			statement.setString(3, enumElement.getDefinition());
			statement.setString(4, enumElement.getNotes());
			statement.setString(5, enumElement.name());
			statement.executeUpdate();
			if(!testing.add(enumElement.getId())){
				System.out.println("dup : " + enumElement.getId());
			}
		}
		
		testing.clear();
		query = "insert into stringextraction (stringextractionid, description, definition, notes, name) values (?, ?, ?, ?, ?)";
		statement = connection.prepareStatement(query);
		for (StringExtraction enumElement : StringExtraction.values()){
//			System.out.println("inserting with id : " + enumElement.getId());
			statement.setInt(1, enumElement.getId());
			statement.setString(2, enumElement.getDescription());
			statement.setString(3, enumElement.getDefinition());
			statement.setString(4, enumElement.getNotes());
			statement.setString(5, enumElement.name());
			statement.executeUpdate();
			if(!testing.add(enumElement.getId())){
				System.out.println("dup : " + enumElement.getId());
			}
		}
		
	}
	
	public static void fillDomains() {
		String query = "from Site s where domain = ''";
		int numToProcess = 500;
		int offset = 55;
		System.out.println("running query " );
		List<Site> sites = JPA.em().createQuery(query, Site.class).getResultList();
		System.out.println("sites size : " + sites.size());
		int size = sites.size();
		int count = 0;
		for(Site site : sites) {
			count++;
			System.out.println("homepage : " + site.getHomepage());
			System.out.println(count + " of " + size);
			try {
				URL url = new URL(site.getHomepage());
				String host = url.getHost().replace("www.", "");
//				System.out.println("domain : " + site.getDomain());
				System.out.println("host : "  + host);
				site.setDomain(host);
			} catch (MalformedURLException e) {
				System.out.println("Malformed url");
			}
		}
	}
	
	public static void stringExtractBenchmarks() throws IOException {
//		SiteCrawl siteCrawl = JPA.em().find(SiteCrawl.class, 5L);
		
//		File storageFolder = new File(Global.CRAWL_STORAGE_FOLDER + siteCrawl.getStorageFolder());
		File storageFolder = new File("C:\\Workspace\\DSStorage\\crawldata\\06-05-2015\\dealereprocess");

//    	System.out.println("Amalgamating");
//    	File combined = Amalgamater.amalgamateFiles(storageFolder);
//    	
//    	System.out.println("splitting");
//    	Amalgamater.splitFile(combined);
    	
		int iterations = 1;
		
		Set<ExtractedString> extractedStrings = new HashSet<ExtractedString>();
		
		System.out.println("testing normal extraction");
		long normalStart = System.currentTimeMillis();
		int normalNum = 0;
		for(int i = 0; i < iterations; i++){
			extractedStrings.clear();
			System.out.println("iteration : " + i);
			for(File file : storageFolder.listFiles()) {
				if(file.isFile() && !Amalgamater.isAmalgamation(file)){
					normalNum++;
					System.out.println("analyzing file : " + normalNum);
					extractedStrings.addAll(SiteCrawlAnalyzer.extractStrings(file));
				}
			}
			normalNum = extractedStrings.size();
		}
		long normalEnd = System.currentTimeMillis();
		long normalTime = normalEnd - normalStart;
		
		System.out.println("testing reading line by line");
		long lineStart = System.currentTimeMillis();
		int lineNum = 0;
		for(int i = 0; i < iterations; i++){
			extractedStrings.clear();
			System.out.println("iteration : " + i);
			for(File file : storageFolder.listFiles()) {
				if(file.isFile() && !Amalgamater.isAmalgamation(file)){
					lineNum++;
//					extractedStrings.addAll(CrawlAnalyzer.extractLineStrings(file));
				}
			}
			lineNum = extractedStrings.size();
		}
		long lineEnd = System.currentTimeMillis();
		long lineTime = lineEnd - lineStart;
		
		System.out.println("testing reading split amalgamation");
		long splitStart = System.currentTimeMillis();
		int splitNum = 0;
		for(int i = 0; i < iterations; i++){
			extractedStrings.clear();
			System.out.println("iteration : " + i);
			for(File file : storageFolder.listFiles()) {
//				System.out.println("filename : " + file.getDescription());
//				System.out.println("isamalgamater : " + Amalgamater.isAmalgamation(file));
//				System.out.println("contains : " + file.getDescription().contains("split"));
				if(file.isFile() && Amalgamater.isAmalgamation(file) && file.getName().contains("split.")){
					splitNum++;
					System.out.println("analyzing file : " + splitNum);
					extractedStrings.addAll(SiteCrawlAnalyzer.extractStrings(file));
				}
			}
			splitNum = extractedStrings.size();
		}
		long splitEnd = System.currentTimeMillis();
		long splitTime = splitEnd - splitStart;
		
		System.out.println("testing reading large amalgamation");
		long largeStart = System.currentTimeMillis();
		int largeNum = 0;
		for(int i = 0; i < iterations; i++){
			extractedStrings.clear();
			System.out.println("iteration : " + i);
			for(File file : storageFolder.listFiles()) {
				if(file.isFile() && Amalgamater.isAmalgamation(file) && !file.getName().contains("split.")){
					largeNum++;
					extractedStrings.addAll(SiteCrawlAnalyzer.extractStrings(file));
				}
			}
			largeNum = extractedStrings.size();
		}
		long largeEnd = System.currentTimeMillis();
		long largeTime = largeEnd - largeStart;
		
		System.out.println("testing reading large line by line");
		long lineLargeStart = System.currentTimeMillis();
		int lineLargeNum = 0;
		for(int i = 0; i < iterations; i++){
			extractedStrings.clear();
			System.out.println("iteration : " + i);
			for(File file : storageFolder.listFiles()) {
				if(file.isFile() && Amalgamater.isAmalgamation(file) && !file.getName().contains("split.")){
					lineLargeNum++;
//					extractedStrings.addAll(CrawlAnalyzer.extractLineStrings(file));
				}
			}
			lineLargeNum = extractedStrings.size();
		}
		long lineLargeEnd = System.currentTimeMillis();
		long lineLargeTime = lineLargeEnd - lineLargeStart;
		
		
		
		System.out.println("normal : (" + normalNum + ")" + normalTime);
		System.out.println("line by line : (" + lineNum + ")" + lineTime);
		System.out.println("split : (" + splitNum + ")" + splitTime);
		System.out.println("large : (" + largeNum + ")" + largeTime);
		System.out.println("line large : (" + lineLargeNum + ")" + lineLargeTime);
	}
	
	public static void clearDups() {
		
		String query = "from Site s where size(s.crawls) > 1";
		List<Site> sites = JPA.em().createQuery(query, Site.class).getResultList();
		System.out.println("size : " + sites.size());
		for(Site site : sites) {
			List<SiteCrawl> siteCrawls = site.getCrawls();
			System.out.println("site : " + site.getHomepage());
			JPA.em().remove(siteCrawls.get(0));
			siteCrawls.remove(0);
		}
	}
	
	
	
}
