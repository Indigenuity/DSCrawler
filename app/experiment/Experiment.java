package experiment;

import global.Global;
import net.sf.sprockets.google.Place;
import net.sf.sprockets.google.Places;
import net.sf.sprockets.google.Places.Params;
import net.sf.sprockets.google.Places.Response;
import newwork.TerminalWorker;
import newwork.urlcheck.UrlCheckWorkOrder;

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
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVPrinter;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.envers.query.AuditQueryCreator;
import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Functions;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
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
import crawling.CrawlSession;
import crawling.DealerCrawlController;
import crawling.GoogleCrawler;
import crawling.MobileCrawler;
import crawling.nydmv.County;
import crawling.nydmv.NYDealer;
import crawling.nydmv.NyControl;
import crawling.nydmv.NyDao;
import crawling.projects.BhphCrawl;
import dao.AnalysisDao;
import dao.GeneralDAO;
import dao.SiteCrawlDAO;
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
import datadefinitions.newdefinitions.LinkTextMatch;
import datadefinitions.newdefinitions.WPAttribution;
import datatransfer.Amalgamater;
import datatransfer.CSVGenerator;
import datatransfer.CSVImporter;
import datatransfer.Cleaner;
import datatransfer.reports.Report;
import datatransfer.reports.ReportFactory;
import datatransfer.reports.ReportRow;
import akka.actor.Props;
import analysis.AnalysisConfig;
import analysis.AnalysisControl;
import analysis.AnalysisSet;
import analysis.PageCrawlAnalysis;
import analysis.SiteCrawlAnalysis;
import analysis.SiteCrawlAnalyzer;
import analysis.TextAnalyzer;
import analysis.AnalysisConfig.AnalysisMode;
import async.async.GenericMaster;
import async.tools.InventoryTool;
import async.tools.Tool;
import async.tools.ToolGuide;
import async.tools.UrlResolveTool;
import async.work.WorkItem;
import async.work.WorkStatus;
import async.work.WorkType;
import audit.AuditDao;
import audit.Distance;
import audit.ListMatchResult;
import audit.ListMatcher;
import audit.sync.SalesforceDealerSyncSession;
import audit.sync.SalesforceGroupAccountSyncSession;
import audit.sync.SalesforceControl;
import audit.sync.SingleSyncSession;
import audit.sync.Sync;
import audit.sync.SyncSession;
import audit.sync.SyncType;
import persistence.CrawlSet;
import persistence.Dealer;
import persistence.ExtractedString;
import persistence.ExtractedUrl;
import persistence.FBPage;
import persistence.GoogleCrawl;
import persistence.GroupAccount;
import persistence.ImageTag;
import persistence.MobileCrawl;
import persistence.PageCrawl;
import persistence.SFEntry;
import persistence.Site;
import persistence.SiteCrawl;
import persistence.Temp;
import persistence.TestEntity;
import persistence.TestOtherEntity;
import persistence.UrlCheck;
import persistence.Site.SiteStatus;
import persistence.SiteCrawl.FileStatus;
import persistence.salesforce.SalesforceAccount;
import persistence.stateful.FetchJob;
import persistence.tasks.Task;
import persistence.tasks.TaskSet;
import places.CanadaPostal;
import places.DataBuilder;
import places.PlacesDealer;
import places.PlacesPage;
import places.PostalLocation;
import places.PostalSearchWorker;
import places.ZipLocation;
import play.db.DB;
import play.db.jpa.JPA;
import reporting.DashboardStats;
import scaffolding.Scaffolder;
import tyrex.services.UUID;
import urlcleanup.ListCheck;
import urlcleanup.ListCheckExecutor;
import utilities.DSFormatter;
import utilities.FB;
import utilities.Tim;
import utilities.UrlSniffer;

public class Experiment { 
	
	public static void runExperiment() {
//		BhphCrawl.runCrawl();
		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	}
	
	public static void nyLinkExperiment() throws IOException {
		NYDealer dealer = JPA.em().find(NYDealer.class, 732L);
		System.out.println("dealer : " + dealer.getFacilityName());
		System.out.println("dealer address : " + dealer.getStreet() + ", " + dealer.getCity());
		Response<List<Place>> resp = Places.textSearch(Params.create().query(dealer.getFacilityName() + " " + dealer.getStreet() + " " + dealer.getCity()));
		
		System.out.println("response : " + resp);
		
		List<Place> results = resp.getResult();
		for(Place place : results){
			System.out.println("place : " + place.getName());
			System.out.println("link : " + place.getUrl());
			System.out.println("address : " + place.getFormattedAddress());
			System.out.println("types : " + place.getTypes());
			System.out.println("location : " + place.getLatitude() + " " + place.getLongitude());
			System.out.println("places id : " + place.getPlaceId().getId());
			System.out.println();
			
//			Response<List<Place>> locSearch= Places.nearbySearch(
//					Params.create().latitude(place.getLatitude()).longitude(place.getLongitude()));
//			
//			for(Place locPlace : locSearch.getResult()) {
//				System.out.println("locPlace : " + locPlace.getName());
//				System.out.println("place : " + locPlace.getName());
//				System.out.println("link : " + locPlace.getUrl());
//				System.out.println("address : " + locPlace.getFormattedAddress());
//				System.out.println("types : " + locPlace.getTypes());
//				System.out.println("location : " + locPlace.getLatitude() + " " + locPlace.getLongitude());
//			}
		}
		
//		NyControl.runLinks();
	}
	
	
	
	public static void standardizeAddresses(){
		System.out.println("fetching");
		String queryString = "from NYDealer sa";
		List<NYDealer> sfAccounts = JPA.em().createQuery(queryString, NYDealer.class).getResultList();
		System.out.println("size : " + sfAccounts.size());
		int count = 0;
		for(NYDealer account : sfAccounts) {
			account.setStandardStreet(DSFormatter.standardizeStreetAddress(account.getStreet()));
			if(count++ % 500 == 0){
				System.out.println("count : " + count);
			}
		}
	}
	
	public static void runStringExperiment() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException, InterruptedException {
		
		String ab = "815 MIDDLE CNTRY RD";
		String full = "815 MIDDLE COUNTRY RD";
		String other = "This is some random string";
		
		String diff = StringUtils.difference(ab, full);
		
		System.out.println("diff: " + diff);
		System.out.println("jaro: " + StringUtils.getJaroWinklerDistance(ab, full));
		System.out.println("fuzzy  : " + StringUtils.getFuzzyDistance(ab,  full, Locale.US));
		System.out.println("levenshtein : " + StringUtils.getLevenshteinDistance(ab, full));
		
		System.out.println("diff: " + StringUtils.difference(ab, other));
		System.out.println("jaro: " + StringUtils.getJaroWinklerDistance(ab, other));
		System.out.println("fuzzy  : " + StringUtils.getFuzzyDistance(ab,  other, Locale.US));
		System.out.println("levenshtein : " + StringUtils.getLevenshteinDistance(ab, other));
	}
	
	public static void canadaExperiment() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException, InterruptedException {
		
		AnalysisSet canadaSet =JPA.em().find(AnalysisSet.class, 1L);
//		
		AnalysisConfig config = new AnalysisConfig();
		config.setAnalysisMode(AnalysisMode.PAGED);
		config.setDoTestMatches(true);
		config.setDoWpAttributionMatches(true);
		
		JPA.em().persist(config);
		
		canadaSet.setConfig(config);
//		
//		System.out.println("cancadaset : " + canadaSet.getCrawlAnalysisMap().keySet().size());
		
//		String queryString = "from SalesforceAccount sa where sa.country = 'CANADA'";
//		List<SalesforceAccount> accounts = JPA.em().createQuery(queryString, SalesforceAccount.class).getResultList();
//		
//		int count= 0;
//		for(SalesforceAccount account : accounts) {
//			Site site = account.getSite();
//			SiteCrawl siteCrawl = SiteCrawlDAO.getRecentPrimary(site);
//			if(siteCrawl != null){
////				System.out.println("putting site : " + site.getSiteId());
//				canadaSet.getCrawlAnalysisMap().put(siteCrawl.getSiteCrawlId(), 0L);		//Because JPA is retarded and can't save nulls in maps
//			}
//			System.out.println("count : " + count++);
//		}
//		System.out.println("cancadaset : " + canadaSet.getCrawlAnalysisMap().keySet().size());
//		canadaSet = JPA.em().merge(canadaSet);
		
		
		
//		PageCrawlAnalysis analysis = JPA.em().find(PageCrawlAnalysis.class, 65168L);
//		
//		System.out.println("pageCrawl's general matches : " + analysis.getGeneralMatches().size());
//		
//		for(GeneralMatch match : analysis.getGeneralMatches()){
//			System.out.println("match : " + match);
//		}
	}
	
	
	
	public static void crawlSessionExperimentExperiment() throws Exception {
		String queryString = "select sc from SiteCrawl sc join sc.site s where s.franchise = true and sc.fileStatus = :fileStatus";
		List<SiteCrawl> siteCrawls = JPA.em().createQuery(queryString, SiteCrawl.class)
				.setParameter("fileStatus", FileStatus.PRIMARY)
				.getResultList();
		
		System.out.println("siteCrawls : " + siteCrawls.size());
		
		CrawlSession crawlSession = new CrawlSession();
		
		for(SiteCrawl siteCrawl : siteCrawls) {
			crawlSession.addSeed(siteCrawl.getSeed());
			crawlSession.addSiteCrawl(siteCrawl);
		}
		
		crawlSession.setName("Franchise 9-7-2016");
		
		JPA.em().persist(crawlSession);
		
		
		
		
		
//		Site site = JPA.em().find(Site.class, 18152L);
//		
//		SiteCrawl siteCrawl = DealerCrawlController.crawlSite(site.getHomepage());
		
//		AnalysisControl.runAnalysisExperiment();
//		runCreditAppReport();
//		System.out.println("number matching : " + AnalysisDao.getCombinedCreditAppMatches());
//		System.out.println("number general matching : " + AnalysisDao.getCombinedGeneralCreditAppMatches());
//		System.out.println("number link matching : " + AnalysisDao.getCombinedCreditAppLinkMatches());
		
//		experimentTaskSet();
		
		
	}
	
	public static void runCreditAppReport() throws IOException {
		Report report = new Report();
		report.setName("Online Credit App Usage on Franchise Sites(Search by general text match)");
		
		System.out.println("creditAppMatches");
		for(GeneralMatch generalMatch : GeneralMatch.creditAppMatches){
			report.addReportRow(generalMatch.name(), makeCreditAppReportRow(generalMatch));
		}
		
		System.out.println("creditAppGeneralMatches");
		for(GeneralMatch generalMatch : GeneralMatch.creditAppGeneralMatches){
			report.addReportRow(generalMatch.name(), makeCreditAppReportRow(generalMatch));
		}
		
		Report report2 = new Report();
		report2.setName("Online Credit App Usage on Franchise Sites(Search by link text match)");
		
		System.out.println("linkTextMatches");
		for(LinkTextMatch match : LinkTextMatch.values()){
			report2.addReportRow(match.name(), makeCreditAppReportRow(match));
		}
		
		CSVGenerator.printReport(report);
		CSVGenerator.printReport(report2);
		
	}
	
	public static ReportRow makeCreditAppReportRow(GeneralMatch generalMatch) {
		ReportRow reportRow = new ReportRow();
		reportRow.putCell("Match Name", generalMatch.name());
		reportRow.putCell("Description", generalMatch.description);
		reportRow.putCell("Definition", generalMatch.definition);
		reportRow.putCell("Example", generalMatch.notes);
		Long matchingSites = AnalysisDao.getCountGeneralMatch(generalMatch);
		Long total = 19769L;
		Double marketShare = (matchingSites * 1.0) / total;
		reportRow.putCell("# Matching Sites", matchingSites + "");
		reportRow.putCell("Market Share", marketShare  + "");
		
		return reportRow;
	}
	
	public static ReportRow makeCreditAppReportRow(LinkTextMatch match) {
		ReportRow reportRow = new ReportRow();
		reportRow.putCell("Match Name", match.name());
		reportRow.putCell("Description", match.description);
		reportRow.putCell("Definition", match.definition);
		reportRow.putCell("Example", match.notes);
		Long matchingSites = AnalysisDao.getCountLinkTextMatch(match);
		Long total = 19769L;
		Double marketShare = (matchingSites * 1.0) / total;
		reportRow.putCell("# Matching Sites", matchingSites + "");
		reportRow.putCell("Market Share", marketShare  + "");
		
		return reportRow;
	}
	
	
	public static void runFileStatusExperiment() {
		String queryString = "from SiteCrawl sc";
		List<SiteCrawl> siteCrawls = JPA.em().createQuery(queryString, SiteCrawl.class).getResultList();
		
		System.out.println("siteCrawls : " + siteCrawls.size());
		
		int deleted = 0;
		int total = 0;
		for(SiteCrawl siteCrawl : siteCrawls) {
			String folderName = Global.getCrawlStorageFolder() + siteCrawl.getStorageFolder();
			File folder = new File(folderName);
			
			if(!folder.exists()){
				siteCrawl.setFileStatus(FileStatus.DELETED);
				deleted++;
			} else {
				siteCrawl.setFileStatus(FileStatus.PRIMARY);
			}
			if(total %500 == 0){
				System.out.println("total : " + total);
				System.out.println("folderName : " + folderName);
				System.out.println("filename : " + folder.getAbsolutePath());
				System.out.println("filestatus : " + siteCrawl.getFileStatus());
				System.out.println("SiteCrawlId : " + siteCrawl.getSiteCrawlId());
			}
			total++;
		}
		
		System.out.println("Total : " + total);
		System.out.println("deleted : " + deleted);
	}
	
	public static void siteCrawlAnalysisExperiment() throws Exception {
		SiteCrawl siteCrawl = JPA.em().find(SiteCrawl.class, 55426L);
		
//		SiteCrawlAnalysis analysis = SiteCrawlAnalyzer.analyzeSiteCrawl(siteCrawl, AnalysisMode.PAGED);
		
		
//		for(PageCrawlAnalysis pageAnalysis : analysis.getPageAnalyses()){
////			System.out.println("pageAnalysis : " + pageAnalysis.getGeneralMatches().size());
////			for(GeneralMatch match : pageAnalysis.getGeneralMatches()){
////				System.out.println(match);
////			}
//		}
//		
//		JPA.em().persist(analysis);
//		
//		System.out.println("pagecrawls : " + siteCrawl.getPageCrawls().size());
		
	}
	
	public static void runUrlExperiment() throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InterruptedException {
		
		experimentTaskSet();
//		useUrlChecks();
//		String queryString = "from Site s where domain is null";
//		List<Site> sites = JPA.em().createQuery(queryString, Site.class).getResultList();
//		for(Site site : sites){
//			site.setHomepage(site.getHomepage());
//		}
//		String queryString = "select sa from SalesforceAccount sa join sa.site s where s.siteStatus = :siteStatus and s.domain is null";
//		List<SalesforceAccount> accounts = JPA.em().createQuery(queryString, SalesforceAccount.class).setParameter("siteStatus", SiteStatus.APPROVED).getResultList();
//		System.out.println("account : " + accounts.size());
//		
//		for(SalesforceAccount account : accounts){
//			Site site = account.getSite();
//			System.out.println("account site : " + site.getHomepage());
//			site.setHomepage(site.getHomepage());
////			if(count++ > 100){
////				return;
////			}
//		}
	}
	
	public static <T, U> U doGenericThing(Function<T, U> instructions, T parameter){
		return instructions.apply(parameter);
	}
	
	public static void useUrlChecks() throws IOException {
		List<Site> sitesList = JPA.em().createQuery("from Site site", Site.class).getResultList();
		Map<String, Site> sites = sitesList.stream().collect(Collectors.toMap( (site) -> site.getHomepage(), (site) -> site));
		
		List<SalesforceAccount> accountsList = JPA.em().createQuery("from SalesforceAccount sa", SalesforceAccount.class).getResultList();
		Map<String, SalesforceAccount> accounts = accountsList.stream().collect(Collectors.toMap( (account) -> account.getSalesforceId(), (account) -> account));
		
		Report report = JPA.em().find(Report.class, 1L);
		int count = 0;
		for(ReportRow reportRow : report.getReportRows().values()){
			System.out.println("working on reportRow : " + ++count);
			Site beforeSite = sites.get(reportRow.getCell("Website"));
			Site afterSite;
			
			if(beforeSite == null){
//				System.out.println("no site yet" + count++);
				continue;
			}
			
			SalesforceAccount account = accounts.get(reportRow.getCell("Salesforce Unique ID"));
			String recommendation = reportRow.getCell("Recommendation");
			String urlCheckIdString = reportRow.getCell("urlCheckId");
			String manualSeed = reportRow.getCell("Manual Seed");
			boolean sameSite = false;
			
			if(urlCheckIdString != null){
				Long urlCheckId = Long.parseLong(urlCheckIdString);
				UrlCheck urlCheck = JPA.em().find(UrlCheck.class, urlCheckId);
				beforeSite.setUrlCheck(urlCheck);
			}
			
			if("Defunct".equals(recommendation)){
				beforeSite.setSiteStatus(SiteStatus.DEFUNCT);
//				System.out.println("defunct");
				continue;
			} else if ("Other Issue Requires Attention".equals(recommendation)){
				beforeSite.setSiteStatus(SiteStatus.OTHER_ISSUE);
//				System.out.println("other issue");
				continue;
			} else {
//				System.out.println("getting site from resolved seed");
				afterSite = sites.get(reportRow.getCell("Resolved Seed"));
				if(afterSite == null){
//					System.out.println("none found, creating site from resolved seed");
					afterSite = new Site(reportRow.getCell("Resolved Seed"));
				}
				
				if(afterSite == beforeSite){
					sameSite = true;
				}
				afterSite.setSiteStatus(SiteStatus.APPROVED);
				afterSite = JPA.em().merge(afterSite);
				
			}
			
			if(StringUtils.isEmpty(manualSeed)){
//				System.out.println("no manual seed found, creating redirect link");
				if(sameSite){
					afterSite.setForwardsTo(null);
					afterSite.setSiteStatus(SiteStatus.APPROVED);
				} else{
					beforeSite.setForwardsTo(afterSite);
					beforeSite.setSiteStatus(SiteStatus.REDIRECTS);	
				}
				
			}
			
			if("Approve Shared".equals(reportRow.getCell("Recommendation"))){
				System.out.println("setting site shared flag to true");
				afterSite.setSharedSite(true);
			}
			
			account.setSite(afterSite);
			
			
//			SalesforceAccount account = accounts.get(reportRow.getCell("Salesforce Unique ID"));
//			String website = reportRow.getCell("Website");
//			String resolvedSeed = reportRow.getCell("Resolved Seed");
//			String urlCheckIdString = reportRow.getCell("urlCheckId");
//			Site mainSite = account.getSite();
//			if(!StringUtils.equals(website, account.getSalesforceWebsite())){
//				System.out.println("website not equals : " + website + " : " + account.getSalesforceWebsite());
//			} else {
//				if(!StringUtils.isEmpty(website) && !StringUtils.equals(website, resolvedSeed)) {
//					
//				}
//			}
			
		}
	}
	
	public static void runSyncExperiment(){
		System.out.println("gettnig listcheck");
		ListCheck listCheck = JPA.em().find(ListCheck.class, 1L);
		System.out.println("getting report");
		Report report = listCheck.getReport();
		System.out.println("number of rows in report : " + report.getReportRows().size());
		
		Map<String, GroupAccount> groupAccounts = new HashMap<String, GroupAccount>();
		
		report.getReportRows().values().stream()
		.filter((reportRow) -> {
			return reportRow.getCell("Account Level") != null && reportRow.getCell("Account Level").equals("Group");
		}).forEach((reportRow) -> {
			
//			
			
//			if(dealer.getParentAccountSalesforceId() != null){
//				
//			}
			
		}); 
		
		JPA.em().getTransaction().commit();
		JPA.em().getTransaction().begin();
		
		
		report.getReportRows().values().stream()
		.forEach((reportRow) -> {
			Dealer dealer = GeneralDAO.getFirst(Dealer.class, "dealerName", reportRow.getCell("Account Name"));
			boolean isNew = false;
			if(dealer == null){
				dealer = new Dealer();
				isNew = true;
			}
			
			dealer.setDealerName(reportRow.getCell("Account Name"));
			dealer.setSalesforceId(reportRow.getCell("Salesforce Unique ID"));
			dealer.setSalesforceWebsite(reportRow.getCell("Website"));
			dealer.setParentAccountSalesforceId(reportRow.getCell("Parent Account ID"));
			dealer.setParentAccountName(reportRow.getCell("Parent Account"));
			dealer.setGroupAccount(groupAccounts.get(dealer.getParentAccountSalesforceId()));
			
		});
		
		
		
		List<Dealer> dealers = JPA.em().createQuery("from Dealer d where d.parentAccountSalesforceId is not null", Dealer.class).getResultList();
		
		dealers.stream()
			.forEach( (dealer) ->  {
				GroupAccount groupAccount = GeneralDAO.getFirst(GroupAccount.class, "salesforceId", dealer.getParentAccountSalesforceId());
				System.out.println("dealer : " + dealer.getDealerName());
				if(groupAccount != null) {
					dealer.setGroupAccount(groupAccount);
					
					System.out.println("group account : " + groupAccount.getName());
				} else {
					System.out.println("no group account");
				}
			});
		
	}
	
	public static void runEnversExperiment() throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		Sync sync = JPA.em().find(Sync.class, 9L);
		
		Integer revisionOfSync = AuditDao.getRevisionOfSync(sync);
		System.out.println("revision : " + revisionOfSync);
		
//		List<TestEntity> entities = AuditDao.getDeletedAtRevision(TestEntity.class, 15);
//		entities.stream().forEach( (entity -> {
//			System.out.println("entity : " + entity);
//		}));
//		TestEntity newEntity = new TestEntity();
//		JPA.em().persist(newEntity); 
//		Sync sync = new Sync(SyncType.TEST);
//		JPA.em().persist(sync);
//		SalesforceSync.syncGroups("C:\\Workspace\\DSStorage\\in\\websitelist\\report1455663101631.csv");
//		SalesforceSync.syncDealers("C:\\Workspace\\DSStorage\\in\\websitelist\\report1455663101631.csv");
//		TestOtherEntity newOtherEntity = new TestOtherEntity();
//		JPA.em().persist(newOtherEntity);
		
//		TestEntity testEntity = JPA.em().find(TestEntity.class, 14L);
//		BeanMap beanMap = new BeanMap(testEntity);
//		beanMap.values().stream().forEach((a) -> System.out.println("field : " + ((a == null)? a : a.getClass())));
//		System.out.println("fields : " + Scaffolder.getBasicFields(testEntity));
//		TestEntity secondTestEntity = JPA.em().find(TestEntity.class, 13L);
//		Object identifier = JPA.em().getDelegate().getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(testEntity);
//		System.out.println("Identifier : " + identifier);
//		TestOtherEntity otherEntity = JPA.em().find(TestOtherEntity.class, 1L);
//		
//		JPA.em().remove(testEntity);
//		testEntity.setOtherEntity(otherEntity);
//		otherEntity.setOtherString("Removed my parent");
//		testEntity.setMyString("changed string with sync a second time");
//		testEntity.setMyPrimitiveCharacter('b');
//		testEntity.setMyString("This string has changed");
		
//		AuditReader reader = AuditReaderFactory.get(JPA.em());
//		
//		AuditQuery query = reader
//				.createQuery()
//				.forRevisionsOfEntity(Sync.class, true, false).addProjection(AuditEntity.revisionNumber().max());
//		
//		Integer revisionNum = (Integer) query.getSingleResult();
//		System.out.println("Revision Number: " + revisionNum);
//		
//		query = reader
//				.createQuery()
//				.forEntitiesModifiedAtRevision(Sync.class, revisionNum).setMaxResults(1);
//		
//		Sync sync = (Sync) query.getSingleResult();
//		
//		System.out.println("Sync : " + sync);
		
//		TestEntity blah1 = AuditDao.getTestEntitySyncReport(28);
//		TestEntity blah2 = AuditDao.getTestEntitySyncReport(27);
//		
//		compareObjects(testEntity, secondTestEntity);
//		System.out.println("blah1 : " + blah1.getMyString());
//		System.out.println("blah2 : " + blah2.getMyString());
//		JPA.em().merge(blah);
		
		
		System.out.println("Ran Experiment");
	}
	
	public static void compareObjects(Object oldObject, Object newObject) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        BeanMap map = new BeanMap(oldObject);

        PropertyUtilsBean propUtils = new PropertyUtilsBean();
        
        for (Object propNameObject : map.keySet()) {
            String propertyName = (String) propNameObject;
            Object property1 = propUtils.getProperty(oldObject, propertyName);
            Object property2 = propUtils.getProperty(newObject, propertyName);
            if(property1 == null){
            	
            }
            if ((property1 == null && property2 == null ) || (property1 != null && property1.equals(property2))) {
                System.out.println("  " + propertyName + " is equal");
            } else {
                System.out.println("> " + propertyName + " is different (oldValue=\"" + property1 + "\", newValue=\"" + property2 + "\")");
            }
        }

    }
	
	public static void runOtherListCheckExperiment() throws Exception {
		
//		ActorRef experimentActor = Asyncleton.getInstance().getMainSystem().actorOf(Props.create(MyExperimentWorker.class));
//		
//		experimentActor.tell("http://www.kengarff.com", ActorRef.noSender());
//		
//		ActorRef secondActor = Asyncleton.getInstance().getMainSystem().actorOf(Props.create(MyActor.class));
//		
//		
//		firstActor.tell(1, secondActor);
		
//		runListCheckExperiment();
	}
	
	public static void runListCheckExperiment() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException, InterruptedException {
		System.out.println("gettnig listcheck");
		ListCheck listCheck = JPA.em().find(ListCheck.class, 1L);
		System.out.println("getting report");
		Report report = listCheck.getReport();
		System.out.println("number of rows in report : " + report.getReportRows().size());
		
		ListCheckExecutor.execute(listCheck);
		
//		ListCheckExecutor.markIndexChanges(listCheck);
		ListCheckExecutor.report(listCheck);
//		ListCheckExecutor.markBlanks(listCheck);
	}
	
	public static void twitterExperiment() {
	}
	
	public static void experimentTaskSet() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException, InterruptedException {
		
		TaskSet taskSet = new TaskSet();
		taskSet.setName("Crawling");
		
		String query = "select s from SalesforceAccount sf join sf.site s where sf.country ='Canada' and s.siteStatus = :siteStatus";
		List<Site> sites = JPA.em().createQuery(query, Site.class).setParameter("siteStatus", SiteStatus.APPROVED).getResultList();
		
//		List<String> dupDomains = SitesDAO.getDuplicateDomains(100000, 0);
//		System.out.println("dupDomains : " + dupDomains.size());
//		
//		for(String domain : dupDomains){
//			System.out.println("marking dupes on domain : " + domain);
//			List<Site> sites = GeneralDAO.getList(Site.class, "domain", domain);
//			System.out.println("found sites on dup domain : " + sites.size());
//			for(Site site : sites) {
//				site.setSiteStatus(SiteStatus.SUSPECTED_DUPLICATE);
//			}
//		}
		
		System.out.println("sites size : " + sites.size());
		for(Site site : sites){
			if(site.getHomepage().toLowerCase().contains("basant")){
				System.out.println("found basant");
			}
//			Task supertask = new Task();
//			supertask.setWorkStatus(WorkStatus.DO_WORK);
//			supertask.setWorkType(WorkType.SUPERTASK);
//			supertask.addContextItem("seed", site.getHomepage());
//			supertask.addContextItem("siteId", site.getSiteId() + "");
//			taskSet.addTask(supertask);
			
//			Task urlTask = new Task();
//			urlTask.setWorkType(WorkType.REDIRECT_RESOLVE);
//			urlTask.setWorkStatus(WorkStatus.DO_WORK);
//			supertask.addSubtask(urlTask);
//			
//			Task updateTask = new Task();
//			updateTask.setWorkType(WorkType.SITE_UPDATE);
//			updateTask.setWorkStatus(WorkStatus.DO_WORK);
//			updateTask.addPrerequisite(urlTask);
//			supertask.addSubtask(updateTask);
//			
//			Task crawlTask = new Task();
//			crawlTask.setWorkType(WorkType.SITE_CRAWL);
//			crawlTask.setWorkStatus(WorkStatus.DO_WORK);
//			supertask.addSubtask(crawlTask);
			
//			Task amalgTask = new Task();
//			amalgTask.setWorkStatus(WorkStatus.DO_WORK);
//			amalgTask.setWorkType(WorkType.AMALGAMATION);
//			amalgTask.addPrerequisite(crawlTask);
//			JPA.em().persist(amalgTask);
//			
//			
//			Task analysisTask = new Task();
//			analysisTask.setWorkType(WorkType.ANALYSIS);
//			analysisTask.setWorkStatus(WorkStatus.DO_WORK);
//			analysisTask.addPrerequisite(amalgTask);
//			supertask.addSubtask(analysisTask);
//			
//			site.setHomepage(site.getHomepage());
		}
		
		JPA.em().persist(taskSet);
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
		
		File folder = new File("C:\\Workspace\\DSStorage\\crawldata\\05-02-2016\\");
		
		System.out.println("is folder : " + folder.isDirectory());
		
		
		TaskSet taskSet = JPA.em().find(TaskSet.class, 9L);
		System.out.println("got task set : " + taskSet);
		Task doc = null;
		Task text = null;
		Task meta = null;
		Task inv = null;
		Task siteCrawlTask = null;
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
					siteCrawlTask = subtask;
				}else if(subtask.getWorkType() == WorkType.REDIRECT_RESOLVE){
					url = subtask;
				}else if(subtask.getWorkType() == WorkType.ANALYSIS){
					analysis = subtask;
				}
			}
			

			Task amalgTask = new Task();
			amalgTask.setWorkStatus(WorkStatus.DO_WORK);
			amalgTask.setWorkType(WorkType.AMALGAMATION);
			amalgTask.addPrerequisite(siteCrawlTask);
			JPA.em().persist(amalgTask);
			analysis.getPrerequisites().clear();
			analysis.addPrerequisite(amalgTask);
			supertask.addSubtask(amalgTask);
			
//			analysis.addPrerequisite(siteCrawl);
			
			
			
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

//		GeoApiContext context = Global.getPlacesContext();
//		PlaceDetailsRequest request = PlacesApi.placeDetails(context, "ChIJ_-fL8YBmA4wRlfmHQSkrqwM");
//		PlaceDetails details = request.await();
//		PlacesPage page = DataBuilder.getPlacesDealer(details);
//		JPA.em().persist(page);
//		System.out.println("details : " + details);
//		System.out.println("website : " + details.website);
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
					extractedStrings.addAll(TextAnalyzer.extractStrings(file));
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
					extractedStrings.addAll(TextAnalyzer.extractStrings(file));
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
					extractedStrings.addAll(TextAnalyzer.extractStrings(file));
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
