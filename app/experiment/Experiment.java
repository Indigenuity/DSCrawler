package experiment;

import global.Global;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.instrument.Instrumentation;
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
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVPrinter;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.maps.GeoApiContext;
import com.google.maps.PlaceDetailsRequest;
import com.google.maps.PlacesApi;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.PlaceDetails;

import agarbagefolder.InfoFetch;
import agarbagefolder.SiteWork;
import agarbagefolder.WorkSet;
import agarbagefolder.urlresolve.UrlResolveWorkOrder;
import crawling.DealerCrawlController;
import crawling.GoogleCrawler;
import crawling.MobileCrawler;
import dao.SitesDAO;
import dao.StatsDAO;
import dao.TaskDAO;
import dao.TaskSetDAO;
import datadefinitions.GeneralMatch;
import datadefinitions.Scheduler;
import datadefinitions.StringExtraction;
import datadefinitions.UrlExtraction;
import datadefinitions.WebProvider;
import datadefinitions.newdefinitions.InventoryType;
import datadefinitions.newdefinitions.WPAttribution;
import datatransfer.Amalgamater;
import datatransfer.CSVGenerator;
import datatransfer.CSVImporter;
import datatransfer.Cleaner;
import akka.actor.ActorRef;
import analysis.SiteCrawlAnalyzer;
import async.async.Asyncleton;
import async.tools.InventoryTool;
import async.tools.Tool;
import async.tools.ToolGuide;
import async.tools.UrlResolveTool;
import async.work.WorkItem;
import async.work.WorkStatus;
import async.work.WorkType;
import persistence.CanadaPostal;
import persistence.CrawlSet;
import persistence.Dealer;
import persistence.ExtractedString;
import persistence.ExtractedUrl;
import persistence.FBPage;
import persistence.GoogleCrawl;
import persistence.ImageTag;
import persistence.MobileCrawl;
import persistence.PageCrawl;
import persistence.PlacesDealer;
import persistence.PlacesPage;
import persistence.SFEntry;
import persistence.Site;
import persistence.SiteCrawl;
import persistence.Temp;
import persistence.UrlCheck;
import persistence.stateful.FetchJob;
import persistence.tasks.Task;
import persistence.tasks.TaskSet;
import places.DataBuilder;
import play.db.DB;
import play.db.jpa.JPA;
import reporting.DashboardStats;
import scaffolding.Scaffolder;
import tyrex.services.UUID;
import utilities.DSFormatter;
import utilities.FB;
import utilities.Tim;
import utilities.UrlSniffer;

public class Experiment {
	
	public static void runExperiment() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException, InterruptedException {
		
		TaskSet taskSet = JPA.em().find(TaskSet.class, 1L);
		CSVGenerator.generateSiteImportReport(taskSet);
		
//		String with = "www.495chryslerjeepdodge.net";
//		String wik = "http://www.495chryslerjeepdodge.net";
//		System.out.println("generic : " + UrlSniffer.isGenericRedirect(wik, with));
//		String without = with.replaceAll("/en/$", "/");
//		System.out.println("without : " + without);
//		String domain = "http://www.honda.com";
//		System.out.println("approved domain : " + DSFormatter.isApprovedDomain(domain));
//		fetchingBenchmark();
//		modifyTaskSet();
//		CSVGenerator.generateInventoryCountReport();
//		PageCrawl pageCrawl = JPA.em().find(PageCrawl.class, 8798L);
//		System.out.println("pagecrawl inventory : " + pageCrawl.getInventoryNumber());
	}
	
	public static void fetchingBenchmark(){
		Tim.start();
		int count = 5000;
		int offset = 0;
		
//		List<PageCrawl> pageCrawls = JPA.em().createQuery("from PageCrawl pc", PageCrawl.class).setMaxResults(count).setFirstResult(offset)
//				.setHint("pageCrawlFull", JPA.em().getEntityGraph("pageCrawlFull"))
//				.getResultList();
		CriteriaBuilder builder = JPA.em().getCriteriaBuilder();
		CriteriaQuery<SiteCrawl> q = builder.createQuery(SiteCrawl.class);
		Root<SiteCrawl> root = q.from(SiteCrawl.class);
		ParameterExpression<Long> id = builder.parameter(Long.class);
//		root.fetch("pageCrawls");
//		root.fetch("allLinks", JoinType.LEFT);
//		root.fetch("uniqueCrawledPageUrls", JoinType.LEFT);
//		root.fetch("crawledUrls", JoinType.LEFT);
//		root.fetch("failedUrls", JoinType.LEFT);
//		root.fetch("webProviders", JoinType.LEFT);
//		root.fetch("schedulers", JoinType.LEFT);
//		root.fetch("generalMatches", JoinType.LEFT);
//		root.fetch("extractedStrings", JoinType.LEFT);
//		root.fetch("extractedUrls", JoinType.LEFT);
//		root.fetch("allStaff", JoinType.LEFT);
//		root.fetch("fbPages", JoinType.LEFT);
//		root.fetch("inventoryNumbers", JoinType.LEFT);
//		root.fetch("brandMatchAverages", JoinType.LEFT);
		
		q.select(root).where(builder.equal(root.get("siteCrawlId"), id))
		
		;
		
		TypedQuery<SiteCrawl> typed = JPA.em().createQuery(q)
//				.setHint("javax.persistence.loadgraph", JPA.em().getEntityGraph("siteCrawlFull"))
				.setParameter(id, 5000L);
		
//		SiteCrawl siteCrawl = typed.getSingleResult();
		List<SiteCrawl> siteCrawls = JPA.em().createQuery("from SiteCrawl sc", SiteCrawl.class).setMaxResults(count).getResultList();
		Tim.intermediate();
		for(SiteCrawl siteCrawl : siteCrawls){
			siteCrawl.getSite().getHomepage();
//			siteCrawl.initAll();
		}
//		System.out.println("siteCrawl : " + siteCrawls.get(0).getUniqueCrawledPageUrls().toArray()[0]);
//		System.out.println("pagecrawl : " + ((PageCrawl)siteCrawl.getPageCrawls().toArray()[5]).getImageTags().size());
//		Tim.intermediate();
//		List<PageCrawl> pageCrawls = JPA.em().createQuery("from PageCrawl pc", PageCrawl.class)
////				.setHint("javax.persistence.loadgraph", JPA.em().getEntityGraph("pageCrawlFull"))
//				.setMaxResults(count).getResultList();
		Tim.end();
	}
	
	public static void invTask() throws Exception {
		runAnalysis();
		Task task = new Task();
		task.setWorkType(WorkType.INVENTORY_COUNT);
		task.addContextItem("siteCrawlId", 76 + "");
		InventoryTool.doathing(task);
		System.out.println("task : " + task.getWorkStatus());
		System.out.println("task : " + task.getNote());
	}
	
	public static void runAnalysis() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {
		SiteCrawl siteCrawl = JPA.em().find(SiteCrawl.class, 2L);
		siteCrawl.initAll();
		SiteCrawlAnalyzer.docAnalysis(siteCrawl);
//		InventoryTool.doExperiment(siteCrawl);
//		System.out.println("sitecrawl invNumbers : " + siteCrawl.getInventoryNumbers().size()); 
	}
	
	public static void modifyTaskSet() {
		TaskSet taskSet = JPA.em().find(TaskSet.class, 1L);
		System.out.println("got task set : " + taskSet);
		Task doc = null;
		Task text = null;
		Task meta = null;
		Task inv = null;
		Task siteCrawl = null;
		Task url = null;
		Task analysis = null;
		int count = 1;
		for(Task supertask : taskSet.getTasks()){
			for(Task subtask : supertask.getSubtasks()){
				if(subtask.getWorkType() == WorkType.DOC_ANALYSIS){
					doc = subtask;
				}else if(subtask.getWorkType() == WorkType.INVENTORY_COUNT){
					inv = subtask;
				}else if(subtask.getWorkType() == WorkType.TEXT_ANALYSIS){
					text = subtask;
				}else if(subtask.getWorkType() == WorkType.META_ANALYSIS){
					meta = subtask;
				}else if(subtask.getWorkType() == WorkType.SITE_CRAWL){
					siteCrawl = subtask;
				}else if(subtask.getWorkType() == WorkType.REDIRECT_RESOLVE){
					url = subtask;
				}else if(subtask.getWorkType() == WorkType.ANALYSIS){
					analysis = subtask;
				}
			}
			
			analysis.addPrerequisite(siteCrawl);
			
			
			
//			UrlCheck urlCheck = JPA.em().find(UrlCheck.class, Long.parseLong(url.getContextItem("urlCheckId")));
//			
//			if(!urlCheck.isAllApproved()){
//				url.setNote("URL not approved");
//				url.setWorkStatus(WorkStatus.NEEDS_REVIEW);
//				supertask.setWorkStatus(WorkStatus.DO_WORK);
//			}
			
			count++;
			if(count %500 == 0){
				System.out.println("count : " + count);
				JPA.em().getTransaction().commit();
				JPA.em().getTransaction().begin();
			}
		}
	}
	
	
	
	public static void createTaskSet() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {
		
		TaskSet taskSet = new TaskSet();
		taskSet.setName("WP Experiment Task Set");
		JPA.em().persist(taskSet);
		
		System.out.println("retrieving sitecrawls");
		List<SiteCrawl> siteCrawls = JPA.em().createQuery("from SiteCrawl sc where sc.amalgamationDone = true", SiteCrawl.class).getResultList();
		System.out.println("siteCrawls : " + siteCrawls.size());
		for(SiteCrawl siteCrawl : siteCrawls){
//			siteCrawl.initAll();
			if(siteCrawl.getPageCrawls().size() > 10){
				Task supertask = new Task();
				supertask.setWorkType(WorkType.SUPERTASK);
				JPA.em().persist(supertask);
				taskSet.addTask(supertask);
				
				Task textAnalysisTask = new Task();
				textAnalysisTask.setWorkType(WorkType.TEXT_ANALYSIS);
				textAnalysisTask.addContextItem("siteCrawlId", siteCrawl.getSiteCrawlId() + "");
				JPA.em().persist(textAnalysisTask);
				supertask.addSubtask(textAnalysisTask);
				
//				Task docAnalysisTask = new Task();
//				docAnalysisTask.setWorkType(WorkType.DOC_ANALYSIS);
//				docAnalysisTask.addContextItem("siteCrawlId", siteCrawl.getSiteCrawlId() + "");
//				JPA.em().persist(docAnalysisTask);
//				supertask.addSubtask(docAnalysisTask);
//				
//				Task metaAnalysisTask = new Task();
//				metaAnalysisTask.setWorkType(WorkType.META_ANALYSIS);
//				metaAnalysisTask.addPrerequisite(docAnalysisTask);
//				metaAnalysisTask.addPrerequisite(textAnalysisTask);
//				JPA.em().persist(metaAnalysisTask);
//				supertask.addSubtask(metaAnalysisTask);
				
				
//				Task siteImportTask = new Task();
//				siteImportTask.setWorkType(WorkType.SITE_IMPORT);
//				siteImportTask.addPrerequisite(urlCheckTask);				
			}
		}
	}
	
	
	public static void testPlaces() throws Exception {

		GeoApiContext context = Global.getPlacesContext();
		PlaceDetailsRequest request = PlacesApi.placeDetails(context, "ChIJ_-fL8YBmA4wRlfmHQSkrqwM");
		PlaceDetails details = request.await();
		PlacesPage page = DataBuilder.getPlacesDealer(details);
		JPA.em().persist(page);
		System.out.println("details : " + details);
		System.out.println("website : " + details.website);
	}
	
	public static void checkMobile() {
		String query = "select mc from CrawlSet cs join cs.mobileCrawls mc where cs.crawlSetId = 7";
		System.out.println("Getting mobile crawls ");
		List<MobileCrawl> crawls = JPA.em().createQuery(query, MobileCrawl.class).getResultList();
		System.out.println("crawls size : " + crawls.size());
		
		int responsive = 0;
		int adaptive = 0;
		int scrollResponsive = 0;
		int scrollAdaptive = 0;
		int almostResponsive = 0;
		int almostAdaptive = 0;
		for(MobileCrawl crawl : crawls) {
			if(crawl.getFauxWindowWidth() >= crawl.getFauxWidth()){
				responsive++;
			}
			if(crawl.getWindowWidth() >= crawl.getWidth()){
				adaptive++;
			}
			if(crawl.getFauxWindowWidth() >= crawl.getFauxScrollWidth()){
				scrollResponsive++;
			}
			else if(crawl.getFauxScrollWidth() < 724 ){
				almostResponsive++;
			}
			if(crawl.getWindowWidth() >= crawl.getScrollWidth()){
				scrollAdaptive++;
			}
			else if(crawl.getScrollWidth() < 724){
				almostAdaptive++;
			}
			
		}
		System.out.println("responsive : " + responsive);
		System.out.println("adaptive : " + adaptive);
		System.out.println("scrollResponsive : " + scrollResponsive);
		System.out.println("scrollAdaptive : " + scrollAdaptive);
		System.out.println("almostAdaptive : " + almostAdaptive);
		System.out.println("slmost responsieve: " + almostResponsive);
	}
	
	public static void gcrawl() throws Exception {
		GoogleCrawler.googleCrawl("toyota houston tx");
	}
	
	public static void mobileExperiment() throws Exception {
		String seed = "http://www.sewelltoyota.com/";
//		seed = "http://www.jaguarhoustoncentral.com/";
//		seed = "http://allsolutionsnetwork.com/10/1000/";
//		seed = "http://facebook.com";
//		seed = "http://xkcd.com";
//		seed = "http://www.pipkinsmotors.com/";
//		seed = "http://www.lidtkemotors.com/";
		seed = "http://lawrencehalllincoln.net/";
//		MobileCrawl mobileCrawl = MobileCrawler.testingMobileCrawl(seed);
		MobileCrawl mobileCrawl = MobileCrawler.defaultMobileCrawl(seed);
		
		System.out.println("width : " + mobileCrawl.getWidth());
		System.out.println("window width : " + mobileCrawl.getWindowWidth());
		System.out.println("scroll width : " + mobileCrawl.getScrollWidth());
		System.out.println("seed : " + mobileCrawl.getSeed());
		System.out.println("resolved seed : " + mobileCrawl.getResolvedSeed());
		System.out.println("crawl date : " + mobileCrawl.getCrawlDate());
		System.out.println("400 : " + mobileCrawl.isDetected400());
		System.out.println("401 : " + mobileCrawl.isDetected401());
		System.out.println("402 : " + mobileCrawl.isDetected402());
		System.out.println("403 : " + mobileCrawl.isDetected403());
		System.out.println("404 : " + mobileCrawl.isDetected404());
		System.out.println("500 : " + mobileCrawl.isDetected500());
		System.out.println("501 : " + mobileCrawl.isDetected501());
		System.out.println("502 : " + mobileCrawl.isDetected502());
		System.out.println("503 : " + mobileCrawl.isDetected503());
		System.out.println("Faux ********************************************");
		System.out.println("width : " + mobileCrawl.getFauxWidth());
		System.out.println("window width : " + mobileCrawl.getFauxWindowWidth());
		System.out.println("scroll width : " + mobileCrawl.getFauxScrollWidth());
		System.out.println("resolved seed : " + mobileCrawl.getFauxResolvedSeed());
		System.out.println("400 : " + mobileCrawl.isFauxDetected400());
		System.out.println("401 : " + mobileCrawl.isFauxDetected401());
		System.out.println("402 : " + mobileCrawl.isFauxDetected402());
		System.out.println("403 : " + mobileCrawl.isFauxDetected403());
		System.out.println("404 : " + mobileCrawl.isFauxDetected404());
		System.out.println("500 : " + mobileCrawl.isFauxDetected500());
		System.out.println("501 : " + mobileCrawl.isFauxDetected501());
		System.out.println("502 : " + mobileCrawl.isFauxDetected502());
		System.out.println("503 : " + mobileCrawl.isFauxDetected503());
		
		
		
		System.out.println();
	}
	
	public static void setSiteCities() {
		List<Site> sites = JPA.em().createQuery("from Site s", Site.class).setMaxResults(5000).setFirstResult(50000).getResultList();
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
	
	public static void moveToSecondary() {
		File storageRoot = new File(Global.getCrawlStorageFolder());
		
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
	
//	public static void makeLookupTables() throws SQLException {
//		Connection connection = DB.getConnection();
//		
//		String query = "delete from webprovider where webproviderid >= 0";
//		Statement deleteStatement = connection.createStatement();
//		deleteStatement.executeUpdate(query);
//		
//		query = "delete from stringextraction where stringextractionid >= 0";
//		deleteStatement = connection.createStatement();
//		deleteStatement.executeUpdate(query);
//		
//		query = "delete from urlextraction where urlextractionid >= 0";
//		deleteStatement = connection.createStatement();
//		deleteStatement.executeUpdate(query);
//		
//		query = "delete from generalmatch where generalmatchid >= 0";
//		deleteStatement = connection.createStatement();
//		deleteStatement.executeUpdate(query);
//		
//		query = "delete from scheduler where schedulerid >= 0";
//		deleteStatement = connection.createStatement();
//		deleteStatement.executeUpdate(query);
//		 
//		query = "insert into webprovider (webproviderid, description, definition, notes, name) values (?, ?, ?, ?, ?)";
//		PreparedStatement statement = connection.prepareStatement(query);
//		Set<Integer> testing = new HashSet<Integer>();
//		
//		
//		for (WebProvider enumElement : WebProvider.values()){
////			System.out.println("inserting with id : " + enumElement.getId());
//			statement.setInt(1, enumElement.getId());
//			statement.setString(2, enumElement.getDescription());
//			statement.setString(3, enumElement.getDefinition());
//			statement.setString(4, enumElement.getNotes());
//			statement.setString(5, enumElement.name());
//			if(!testing.add(enumElement.getId())){
//				System.out.println("dup : " + enumElement.getId());
//			}
//			statement.executeUpdate();
//		}
//		
//		
//		testing.clear();
//		query = "insert into scheduler (schedulerid, description, definition, notes, name) values (?, ?, ?, ?, ?)";
//		statement = connection.prepareStatement(query);
//		for (Scheduler enumElement : Scheduler.values()){
////			System.out.println("inserting with id : " + enumElement.getId());
//			statement.setInt(1, enumElement.getId());
//			statement.setString(2, enumElement.getDescription());
//			statement.setString(3, enumElement.getDefinition());
//			statement.setString(4, enumElement.getNotes());
//			statement.setString(5, enumElement.name());
//			statement.executeUpdate();
//			if(!testing.add(enumElement.getId())){
//				System.out.println("dup : " + enumElement.getId());
//			}
//		}
//		
//		testing.clear();
//		query = "insert into generalmatch (generalmatchid, description, definition, notes, name) values (?, ?, ?, ?, ?)";
//		statement = connection.prepareStatement(query);
//		for (GeneralMatch enumElement : GeneralMatch.values()){
////			System.out.println("inserting with id : " + enumElement.getId());
//			statement.setInt(1, enumElement.getId());
//			statement.setString(2, enumElement.getDescription());
//			statement.setString(3, enumElement.getDefinition());
//			statement.setString(4, enumElement.getNotes());
//			statement.setString(5, enumElement.name());
//			statement.executeUpdate();
//			if(!testing.add(enumElement.getId())){
//				System.out.println("dup : " + enumElement.getId());
//			}
//		}
//		
//		testing.clear();
//		query = "insert into urlextraction (urlextractionid, description, definition, notes, name) values (?, ?, ?, ?, ?)";
//		statement = connection.prepareStatement(query);
//		for (UrlExtraction enumElement : UrlExtraction.values()){
////			System.out.println("inserting with id : " + enumElement.getId());
//			statement.setInt(1, enumElement.getId());
//			statement.setString(2, enumElement.getDescription());
//			statement.setString(3, enumElement.getDefinition());
//			statement.setString(4, enumElement.getNotes());
//			statement.setString(5, enumElement.name());
//			statement.executeUpdate();
//			if(!testing.add(enumElement.getId())){
//				System.out.println("dup : " + enumElement.getId());
//			}
//		}
//		
//		testing.clear();
//		query = "insert into stringextraction (stringextractionid, description, definition, notes, name) values (?, ?, ?, ?, ?)";
//		statement = connection.prepareStatement(query);
//		for (StringExtraction enumElement : StringExtraction.values()){
////			System.out.println("inserting with id : " + enumElement.getId());
//			statement.setInt(1, enumElement.getId());
//			statement.setString(2, enumElement.getDescription());
//			statement.setString(3, enumElement.getDefinition());
//			statement.setString(4, enumElement.getNotes());
//			statement.setString(5, enumElement.name());
//			statement.executeUpdate();
//			if(!testing.add(enumElement.getId())){
//				System.out.println("dup : " + enumElement.getId());
//			}
//		}
//		
//	}
	
	public static void fillDomains() {
		String query = "from Site s where domain = ''";
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
//				site.setDomain(host);
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
	
}
