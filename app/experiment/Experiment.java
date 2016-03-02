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
import javax.persistence.metamodel.EntityType;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVPrinter;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
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

import com.google.maps.GeoApiContext;
import com.google.maps.PlaceDetailsRequest;
import com.google.maps.PlacesApi;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.PlaceDetails;

import agarbagefolder.urlresolve.UrlResolveWorkOrder;
import crawling.DealerCrawlController;
import crawling.GoogleCrawler;
import crawling.MobileCrawler;
import dao.SitesDAO;
import dao.StatsDAO;
import dao.TaskDAO;
import datadefinitions.GeneralMatch;
import datadefinitions.Scheduler;
import datadefinitions.StringExtraction;
import datadefinitions.UrlExtraction;
import datadefinitions.WebProvider;
import datadefinitions.newdefinitions.WPAttribution;
import datatransfer.Amalgamater;
import datatransfer.CSVGenerator;
import datatransfer.CSVImporter;
import datatransfer.Cleaner;
import akka.actor.ActorRef;
import analysis.SiteCrawlAnalyzer;
import async.Asyncleton;
import async.tools.Tool;
import async.tools.ToolGuide;
import async.tools.UrlResolveTool;
import async.work.SiteWork;
import async.work.WorkItem;
import async.work.WorkSet;
import async.work.WorkStatus;
import async.work.WorkType;
import async.work.infofetch.InfoFetch;
import persistence.CrawlSet;
import persistence.Dealer;
import persistence.ExtractedString;
import persistence.ExtractedUrl;
import persistence.FBPage;
import persistence.GoogleCrawl;
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
import utilities.DSFormatter;
import utilities.FB;
import utilities.UrlSniffer;

public class Experiment {
	
	public static void runExperiment() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {
		createTaskSet();
	}
	
	public static void modifyTaskSet() {
		TaskSet taskSet = JPA.em().find(TaskSet.class, 3L);
		
		for(Task supertask : taskSet.getTasks()){
			Task textAnalysisTask = new Task();
			textAnalysisTask.setWorkType(WorkType.TEXT_ANALYSIS);
			textAnalysisTask.addContextItem("siteCrawlId", supertask.getSubtasks().get(0).getContextItem("siteCrawlId"));
			JPA.em().persist(textAnalysisTask);
			supertask.addSubtask(textAnalysisTask);
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
	
	
	public static void testing(Tool tool) {
		tool.getAbilities();
	}
	
	public static void assignTemps() {
		String query = "from Temp t where t.projectId = 1 and t.infoFetchId is null and t.givenUrl != ''";
		List<Temp> temps = JPA.em().createQuery(query).getResultList();
		System.out.println("temp : " + temps.size());
		query = "from InfoFetch info where info.fetchJob.fetchJobId = 6 ";
		List<InfoFetch> fetches = JPA.em().createQuery(query).getResultList();
		System.out.println("fetches : " + fetches.size());
		
		int count = 0;
		for(InfoFetch fetch : fetches) {
			fetch.initObjects();
		}
		System.out.println("fetches initialized");
		for(Temp temp : temps) {
			for(InfoFetch fetch : fetches) {
				UrlCheck urlCheck = fetch.getUrlCheckObject();
				Site site = fetch.getSiteObject();
				
				if(urlCheck != null && StringUtils.equals(urlCheck.getSeed(), temp.getIntermediateUrl())){
					System.out.println("found match in urlcheck seed: " + ++count);
					temp.setInfoFetchId(fetch.getInfoFetchId());
					continue;
				}
				
				if(site != null){
					if(StringUtils.equals(site.getHomepage(), temp.getIntermediateUrl())){
						System.out.println("found match in site homepage: " + ++count);
						temp.setInfoFetchId(fetch.getInfoFetchId());
						continue;
					}
					for(String url : site.getRedirectUrls()){
						if(StringUtils.equals(url, temp.getIntermediateUrl())){
							System.out.println("found match in redirect urls: " + ++count);
							temp.setInfoFetchId(fetch.getInfoFetchId());
							continue;
						}
					}
				}
			}
//			fetch.initObjects();
//			if(fetch.getUrlCheckObject() != null){
//				q.setParameter("url", fetch.getUrlCheckObject().getSeed());
//				List<Temp> temps = q.getResultList();
//				if(temps.size() ==1){
//					fetch.setSfId(temps.get(0).getTempId());
//					System.out.println("found intermediate : " + ++count);
//				}
//				else if(temps.size() > 1){
//					System.out.println("argh");
//					continue;
//				}
//			}
//			if(fetch.getSfId() == null){
//				Site site = fetch.getSiteObject();
//				Set<String> redirectUrls = site.getRedirectUrls();
//				for(String url : redirectUrls){
//					q.setParameter("url", url);
//					List<Temp> temps = q.getResultList();
//					if(temps.size() ==1){
//						fetch.setSfId(temps.get(0).getTempId());
//						System.out.println("found intermediate in redirect urls: " + ++count);
//						continue;
//					}
//					else if(temps.size() > 1){
//						System.out.println("argh2");
//						continue;
//					}
//				}
//			}
			
			if(count %100 ==0) {
				JPA.em().getTransaction().commit();
				JPA.em().getTransaction().begin();
			}
			
		}
	}
	
	public static void utf8Experiment() throws IOException {
		String definition = "Site conçu et hébergé par"; 
		String weird = " Site conÃ§u et hÃ©bergÃ© par";
		String full = "Site conÃ§u et hÃ©bergÃ© par <a href=\"http://www.evolio.ca/fr/\" target=\"_blank\">EVOLIO";
		String otherDef = "Site con";
		String filename = "C:\\Workspace\\DSStorage\\crawldata\\02-11-2016\\http%253a%252f%252fwww.grandportagenissan.com%252ffr%252f/%2ffr%2fneuf%2faltima%2f";
		String amalgamated = "C:\\Workspace\\DSStorage\\combined\\02-11-2016\\http%253a%252f%252fwww.grandportagenissan.com%252ffr%252f/amalgamated.txt";
		FileInputStream inputStream = new FileInputStream((new File(amalgamated).getAbsolutePath()));
        String text = IOUtils.toString(inputStream, "UTF-8");
        inputStream.close();
//		System.out.println("text : " + text);
		System.out.println("contains : " + text.contains(definition));
		System.out.println("contains weird: " + text.contains(weird));
		System.out.println("contains full: " + text.contains(full));
		System.out.println("contains other : " + text.contains(otherDef));
	}
	
	public static void evolio() throws IOException {
		String query = "select s from SiteCrawl sc join sc.site s where s.temp = true and ('EVOLIO' member of sc.wpAttributions "
				+ "or 'EVOLIO_FR' member of sc.wpAttributions or 'AUTO_123_FR' member of sc.wpAttributions or 'AUTO_123' member of sc.wpAttributions)";
		List<Site> results = JPA.em().createQuery(query).getResultList();

		System.out.println("results : " + results.size());
		int count = 0;
		for(Site site: results) {
//			System.out.println("got result  (" + siteCrawl.getSiteCrawlId() + ") : " + siteCrawl.getSeed() + " with pages : " + siteCrawl.getPageCrawls().size());
//			System.out.println("latest crawl : " + siteCrawl.getSite().getLatestCrawl().getSiteCrawlId());
//			SiteCrawlAnalyzer.textAnalysis(siteCrawl);
//			System.out.println("site : " + site.getSiteId());
		}
	}
	
	public static void testWpAttributions() throws Exception {
//		String query = "from SiteCrawl sc order by "
		SiteCrawl siteCrawl = JPA.em().find(SiteCrawl.class, 118701L);
		System.out.println("doing amalgamation");
		File storageFolder = new File(Global.getCrawlStorageFolder() + "/" + siteCrawl.getStorageFolder());
		File destination = new File(Global.getCombinedStorageFolder() + "/" + siteCrawl.getStorageFolder());
		Amalgamater.amalgamateFiles(storageFolder, destination);
		
		System.out.println("doing text analysis");
		SiteCrawlAnalyzer.textAnalysis(siteCrawl);
		System.out.println("doing doc analysis");
		SiteCrawlAnalyzer.docAnalysis(siteCrawl);
		
		System.out.println("printing wpattributions:");
		for(WPAttribution wp : siteCrawl.getWpAttributions()){
			System.out.println("wp : " + wp.name());
		}
	}
	
	public static void testInfoFetch() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {
		UrlResolveWorkOrder workOrder = new UrlResolveWorkOrder("http://www.conquerclub.com/");
		InfoFetch infoFetch = JPA.em().find(InfoFetch.class, 45022L);
		
//		System.out.println("urlcheck before : " + infoFetch.getUrlCheckId());
//		infoFetch.setUrlCheckId(0L);
		infoFetch.getUrlCheck().workStatus = WorkStatus.DO_WORK;
		infoFetch.getSiteUpdate().workStatus = WorkStatus.DO_WORK;
		JPA.em().merge(infoFetch);
		JPA.em().getTransaction().commit();
		JPA.em().getTransaction().begin();
		JPA.em().refresh(infoFetch);
//		System.out.println("urlcheck after : " + infoFetch.getUrlCheckId());
		Asyncleton.getInstance().getMaster(infoFetch.getWorkType()).tell(infoFetch, Asyncleton.getInstance().getMainListener());
	}
	
	
	public static void createSiteUpdateFetchJobs() {
		String query = "from Site s where s.franchise = true";
		
		List<Site> sites = JPA.em().createQuery(query).getResultList();
		System.out.println("sites : " + sites.size());
		FetchJob job = new FetchJob();
		job.setName("Checking non-Franchise Sites");
		JPA.em().persist(job);
		for(Site site : sites) {
			InfoFetch fetch = new InfoFetch();
			fetch.setSiteId(site.getSiteId());
			fetch.setSeed(site.getHomepage());
			fetch.getUrlCheck().workStatus = WorkStatus.DO_WORK;
			fetch.getSiteUpdate().workStatus = WorkStatus.DO_WORK;
			fetch.getSiteCrawl().workStatus = WorkStatus.DO_WORK;
			fetch.setFetchJob(job);
			JPA.em().persist(fetch);
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
	
	public static void mergingLists() {
		List<Temp> sfs = JPA.em().createQuery("from Temp t where t.nextStep is not null").getResultList();
		System.out.println("sfs : " + sfs.size());
		int count = 0;
		for(Temp temp : sfs) {
			if("Follow up on urlcheck".equals(temp.getNextStep())){
				Site match = SitesDAO.getFirst("homepage",  temp.getSuggestedUrl(), 0);
				if(match != null){
					temp.setSite(match);
					temp.setSiteSource("Direct Match after url check");
				}				
			}
//			String domain = "";
//			if(temp.getStandardizedUrl() != null){
//				domain = DSFormatter.getDomain(temp.getStandardizedUrl());
//			}
//			temp.setDomain(domain);
//			if(temp.getDomain().equals("ERROR")){
//				temp.setNextStep("Fix URL");
//				continue;
//			}
//			Site match = SitesDAO.getFirst("homepage",  temp.getStandardizedUrl(), 0);
//			if(match != null){
//				temp.setSite(match);
//				temp.setSiteSource("Direct Match");
//			}
//			
//			
//			List<Site> sites = SitesDAO.getSitesWithRedirectUrl(temp.getStandardizedUrl(), 20, 0);
//			if(sites.size() == 1){
//				temp.setSite(sites.get(0));
//				System.out.println("Found by redirect urls");
//				temp.setSiteSource("Redirect Url");
//				continue;
//			}
//			else if (sites.size() > 1){
//				String siteIds = "";
//				String sep = "";
//				for(Site site : sites){
//					siteIds = siteIds + sep;
//					siteIds = siteIds + site.getSiteId();
//					sep = ", ";
//				}
//				temp.setNextStep("Decide which site is best : " + siteIds);
//			}
//			String query = "from UrlCheck uc where seed = '" + temp.getStandardizedUrl() + "'";
//			List<UrlCheck> checks = JPA.em().createQuery(query).getResultList();
//			if(checks.size() > 0){
//				System.out.println("found checks : " + checks.size());
//				UrlCheck check = checks.get(0);
//				temp.setSuggestedUrl(check.getResolvedSeed());
//				temp.setSuggestedSource("UrlCheck from FetchJob");
//				temp.setNextStep("Follow up on urlcheck");
//				if(check.getErrorMessage() != null){
//					temp.setProblem("error from url check : " + check.getErrorMessage());
//				}
//				else if(check.getStatusCode() != 200){
//					temp.setProblem("Status code from UrlCheck : " + check.getStatusCode());
//				}
//				continue;
//			}
			
			
			
			
			
//			sites = SitesDAO.getSitesWithSimilarRedirectUrl(temp.getStandardizedUrl(), 20, 0);
//			if(sites.size() == 1){
//				for(String redirectUrl : sites.get(0).getRedirectUrls()){
//					if(UrlSniffer.isGenericRedirect(redirectUrl, temp.getStandardizedUrl())){
//						temp.setSite(sites.get(0));
//						temp.setSiteSource("Similar redirect was generic");
//						continue;
//					}
//				}
//				if(temp.getSite() != null){
//					continue;
//				}
//			}
//			if(sites.size() >1){
//				String siteIds = "";
//				String sep = "";
//				for(Site site : sites){
//					siteIds = siteIds + sep;
//					siteIds = siteIds + site.getSiteId();
//					sep = ", ";
//				}
//				temp.setNextStep("Decide which similar redirect is best : " + siteIds);
//			}
			
//			if(UrlSniffer.isGenericRedirect(temp.getStandardizedUrl(), temp.getIntermediateUrl())){
//				temp.setSuggestedUrl(temp.getIntermediateUrl());
//				temp.setSuggestedSource("Domain similarity");
//			}
			if(++count % 100 == 0) {
				System.out.println("count : " + count);
				JPA.em().getTransaction().commit();
				JPA.em().getTransaction().begin();
			}
		}
	}
	
	public static void generateQualityReport() throws IOException {
		CSVGenerator.generateSourceQualityReport();
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
		GoogleCrawl gCrawl = GoogleCrawler.googleCrawl("toyota houston tx");
	}
	
	public static void resetMobile() {
		CrawlSet crawlSet = JPA.em().find(CrawlSet.class, 7L);
		
		List<MobileCrawl> taken = new ArrayList<MobileCrawl>();
		int normal = 0;
		int faux = 0;
		for(MobileCrawl mobileCrawl : crawlSet.getMobileCrawls()){
			if(mobileCrawl.getWindowWidth() > 375){
				crawlSet.getNeedMobile().add(mobileCrawl.getSite());
				System.out.println("Incorrect window height : " + normal++);
				taken.add(mobileCrawl);
			}
			else if(mobileCrawl.getFauxWindowWidth() > 447){
				crawlSet.getNeedMobile().add(mobileCrawl.getSite());
				System.out.println("Incorrect faux window height : " + faux++);
				taken.add(mobileCrawl);
			}
		}
		
		crawlSet.getMobileCrawls().removeAll(taken);
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
				File origin = new File(Global.getSecondaryCrawlStorageFolder() + siteCrawl.getStorageFolder());
				File destination = new File(Global.getCrawlStorageFolder() + siteCrawl.getStorageFolder());
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
