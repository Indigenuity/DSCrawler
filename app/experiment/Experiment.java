package experiment;

import global.Global;
import net.sf.sprockets.google.Place;
import net.sf.sprockets.google.Places;
import net.sf.sprockets.google.Places.Params;
import net.sf.sprockets.google.Places.Response;
import newwork.StartWork;

import java.beans.IntrospectionException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.PropertyUtilsBean;


import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.gargoylesoftware.htmlunit.WebClient;
import com.google.common.io.Files;
import com.google.common.util.concurrent.RateLimiter;

import crawling.CrawlSession;
import crawling.DealerCrawlController;
import crawling.HarleyCrawlingson;
import crawling.HttpFetcher;
import crawling.MobileCrawler;
import crawling.SiteCrawlOrder;
import crawling.anansi.UriFetch;
import crawling.anansi.SiteCrawlConfig;
import crawling.anansi.SiteCrawlWorkOrder;
import crawling.anansi.SiteCrawler;
import crawling.discovery.async.TempCrawlingWorker;
import crawling.discovery.entities.SourcePool;
import crawling.discovery.execution.CrawlContext;
import crawling.discovery.execution.Crawler;
import crawling.discovery.execution.DiscoveryContext;
import crawling.discovery.execution.ResourceContext;
import crawling.discovery.execution.ResourceWorkResult;
import crawling.discovery.execution.SeedWorkOrder;
import crawling.discovery.html.DocDerivationStrategy;
import crawling.discovery.html.HttpConfig;
import crawling.discovery.html.HttpEndpoint;
import crawling.discovery.html.HttpResponseFile;
import crawling.discovery.html.HttpToFilePlan;
import crawling.discovery.html.HttpToFileTool;
import crawling.discovery.html.InternalLinkDiscoveryTool;
import crawling.discovery.local.RegularToInventoryDiscoveryTool;
import crawling.discovery.local.PageCrawlDiscoveryPlan;
import crawling.discovery.local.PageCrawlPlan;
import crawling.discovery.local.PageCrawlTool;
import crawling.discovery.local.SiteCrawlPlan;
import crawling.discovery.local.SiteCrawlTool;
import crawling.discovery.planning.CrawlPlan;
import crawling.discovery.planning.DiscoveryPlan;
import crawling.discovery.planning.ResourcePlan;
import crawling.nydmv.NYDealer;
import crawling.projects.BasicDealer;
import dao.AnalysisDao;
import dao.GeneralDAO;
import dao.SalesforceDao;
import dao.SitesDAO;
import datadefinitions.GeneralMatch;
import datadefinitions.inventory.InvType;
import datadefinitions.inventory.InventoryTool;
import datadefinitions.newdefinitions.LinkTextMatch;
import datatransfer.Amalgamater;
import datatransfer.CSVGenerator;
import datatransfer.CSVImporter;
import datatransfer.Cleaner;
import datatransfer.SiteCrawlImporter;
import datatransfer.reports.Report;
import datatransfer.reports.ReportRow;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.pattern.Patterns;
import analysis.AnalysisConfig;
import analysis.AnalysisSet;
import analysis.SiteCrawlAnalysis;
import analysis.SiteCrawlAnalyzer;
import analysis.TextAnalyzer;
import analysis.AnalysisConfig.AnalysisMode;
import async.async.Asyncleton;
import async.functionalwork.FunctionWorkOrder;
import async.functionalwork.FunctionalWorker;
import async.functionalwork.JpaFunctionalBuilder;
import audit.AuditDao;
import audit.sync.Sync;
import persistence.ExtractedString;
import persistence.MobileCrawl;
import persistence.PageCrawl;
import persistence.Site;
import persistence.SiteCrawl;
import persistence.UrlCheck;
import places.PlacesDealer;
import places.PlacesLogic;
import persistence.Site.SiteStatus;
import persistence.SiteCrawl.FileStatus;
import play.Logger;
import play.db.jpa.JPA;
import pods.PodZip;
import pods.PodsLoader;
import salesforce.persistence.DealershipType;
import salesforce.persistence.SalesforceAccount;
import scala.concurrent.Future;
import sites.SiteLogic;
import sites.UrlChecker;
import sites.crawling.SiteCrawlLogic;
import urlcleanup.ListCheck;
import urlcleanup.ListCheckExecutor;
import utilities.DSFormatter;
import utilities.Tim;

public class Experiment { 
	
	public static void runExperiment() throws Exception {
//		SiteCrawl siteCrawl= JPA.em().find(SiteCrawl.class, 17102L);
//		SiteCrawlPlan siteCrawlPlan = new SiteCrawlPlan(siteCrawl);
//		System.out.println("uncrawled : " + siteCrawl.getUnCrawledUrls().size());
//		System.out.println("failed: " + siteCrawl.getFailedUrls().size());
//		siteCrawlPlan.setMaxPages(SiteCrawlPlan.DEFAULT_MAX_PAGES_TO_FETCH);
		
//		Site site = SitesDAO.getOrNewThreadsafe("https://www.thisisnotavalidsitedontmakeanaccountwiththisurl.com/");
//		SiteCrawlPlan siteCrawlPlan = new SiteCrawlPlan(site);
//		siteCrawlPlan.setMaxPages(1);
		
//		Asyncleton.getInstance().getCrawlMaster().tell(siteCrawlPlan, ActorRef.noSender());
//		crawlingStuff();
//		analyzingStuff();
		
		
//		SiteCrawlLogic.ensureFreshInventorySiteCrawls(1, 0);
		
		PodsLoader.loadFromCsv();
	}
	
	public static void analyzingStuff() throws Exception {
		String queryString = "select sc from SiteCrawl sc join sc.pageCrawls pc where pc.invType = :invType";
		List<SiteCrawl> siteCrawls = JPA.em().createQuery(queryString, SiteCrawl.class).setParameter("invType", InvType.DEALER_COM)
				.setMaxResults(1)
				.setFirstResult(1)
				.getResultList();
		System.out.println("siteCrawls :" + siteCrawls.size());
		for(SiteCrawl siteCrawl : siteCrawls){
			SiteCrawlAnalysis analysis = AnalysisDao.getOrNew(siteCrawl);
			analysis.getConfig().setDoVehicles(true);
			SiteCrawlAnalyzer.runSiteCrawlAnalysis(analysis);
		}
	}
	
	public static void crawlingStuff() throws Exception {
		String queryString = "select s from SalesforceAccount sa join sa.site s where sa.dealershipType = :dealershipType";
		List<Site> sites = JPA.em().createQuery(queryString, Site.class)
				.setFirstResult(111)
				.setMaxResults(200)
				.setParameter("dealershipType", DealershipType.FRANCHISE)
				.getResultList();
		System.out.println("siteIds : " + sites.size());
		
		for(Site site : sites) {
			SiteCrawlPlan siteCrawlPlan = new SiteCrawlPlan(site);
			Asyncleton.getInstance().getCrawlMaster().tell(siteCrawlPlan, ActorRef.noSender());
		}
		
//		ActorRef master = Asyncleton.getInstance().runConsumerMaster(50, 
//				JpaFunctionalBuilder.wrapConsumerInFind((site) -> SiteLogic.refreshRe, Site.class), inputs, needsJpa);.getMonotypeMaster(50, FunctionalWorker.class);
		
//		Site site = JPA.em().find(Site.class, 45517L);
//		site.setHomepage(site.getHomepage());
		
//		System.out.println("url check : " + site.getUrlCheck().getCheckDate());
//		Site redirected = SiteLogic.refreshRedirectPath(site, true);
//		
//		System.out.println("site : " + site.getHomepage());
//		System.out.println("redirected : " + redirected.getHomepage());
		
		singlePlace();
	}
	
	public static void singlePlace() throws IOException {
		PlacesLogic.updateOrNew("ChIJ4f___7_7bFMROocwBIzFXe8");
	}
	
	public static void testApache() throws IOException, URISyntaxException{
		HttpConfig config = new HttpConfig();
		config.setUserAgent(Global.getDefaultUserAgentString());
		config.setUseProxy(true);
		config.setProxyAddress(Global.getProxyUrl());
		config.setProxyPort(Global.getProxyPort());
		config.setFollowRedirects(false);
		
		URI uri = new URI("http://httpstat.us/500");
		try(CloseableHttpClient httpClient = config.buildHttpClient()){
			HttpGet request = new HttpGet(uri);
			try(CloseableHttpResponse response = httpClient.execute(request)){
				System.out.println("status : " + response.getStatusLine().getStatusCode());
				System.out.println("entity : " + response.getEntity());
				System.out.println("entitytoString : " + EntityUtils.toString(response.getEntity()));
//				System.out.println("location : " + response.getFirstHeader("Location").getValue());
//				URI redirectedUri = new URI(response.getFirstHeader("Location").getValue());
//				System.out.println(redirectedUri);
//				System.out.println("is absolute : " + redirectedUri.isAbsolute());
//				redirectedUri = uri.resolve(redirectedUri);
//				System.out.println(redirectedUri);
			}
		} catch(Exception e) {
			System.out.println("exception class : " + e.getClass().getSimpleName());
		}
		
	}
	
	public static void crawlTesting() throws Exception {
//		String queryString = "select pd.placesDealerId from PlacesDealer pd join pd.site s where pd.salesforceMatchString is null and s";
		ActorRef crawlMaster = Asyncleton.getInstance().getCrawlMaster();
		String queryString = "select s.siteId from SalesforceAccount sa join sa.site s where sa.dealershipType = :dealershipType";
		List<Long> keyList = JPA.em().createQuery(queryString, Long.class).setParameter("dealershipType", DealershipType.FRANCHISE).setMaxResults(500).setFirstResult(2).getResultList();
		System.out.println("found : " + keyList.size());
		
		for(Long key : keyList) {
			SiteCrawlOrder workOrder = new SiteCrawlOrder(key);
			crawlMaster.tell(workOrder, ActorRef.noSender());
		}
		
//		Site site = SitesDAO.getOrNewThreadsafe("http://www.gregbell.com/VehicleSearchResults?search=used");
//		SiteCrawlOrder workOrder = new SiteCrawlOrder(site.getSiteId());
//		crawlMaster.tell(workOrder, ActorRef.noSender());
		
//		URI uri = new URI("http://www.gregbell.com/");
//		crawlUri(uri);
	}
	

	
	public static void crawlUri(Site site) {
		SiteCrawlPlan crawlPlan = new SiteCrawlPlan(site);
		Asyncleton.getInstance().getCrawlMaster().tell(crawlPlan, ActorRef.noSender());
	}
	
	public static void checkInvType() throws URISyntaxException{
		URI uri = new URI("http://www.donmcgilltoyota.com/");
//		Document doc = Jsoup.parse(uri.toURL(), 5000);
//		for(InvType invType : InvType.getInvTypes()){
//			System.out.println("next page : " + invType.getNextPageLink(doc, uri));
//			if(invType.isNewPath(uri)){
//				System.out.println("Found match");
//			}
//		}
	}
	
	public static void processObject(Object bob) {
		System.out.println("bob : " + bob);
	}
	
	public static void harleyCrawling() throws Exception {
//		PodsLoader.loadFromCsv();
//		List<PodZip> pzs = GeneralDAO.getAll(PodZip.class);
//		System.out.println("pzs: " + pzs.size());
//		int count = 0;
//		for(PodZip pz : pzs){
//			if(count++ % 1000 == 0){
//				System.out.println("count : " + count);
//			}
//			pz.setPostalCode(DSFormatter.standardizeZip(pz.getPostalCode()));
//		}
		
		HarleyCrawlingson.standardizeDealers();
		HarleyCrawlingson.classifyDealers();
		HarleyCrawlingson.generateReport();
//		
		
	}
	
	public static void testAsyncSystem() {
		Asyncleton.getInstance().runConsumerMaster(5, 
				(input) -> {
					System.out.println("Start : " + input);
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("End : " + input);
				}, IntStream.range(1, 35).mapToObj(String::valueOf), false);
	}
	
	public static void testAnalysis() throws Exception {
		
		
//		SiteCrawl siteCrawl = JPA.em().find(SiteCrawl.class, 88148L);
//		System.out.println("siteCrawl : " + siteCrawl);
//		SiteCrawlAnalysis analysis = AnalysisDao.getOrNew(siteCrawl);
//		AnalysisConfig config = new AnalysisConfig();
//		config.setDoTitleTagScoring(true);
//		config.setDoUrlScoring(true);
//		config.setDoH1Score(true);
//		config.setDoMetaDescriptionScore(true);
//		config.setDoBrandMatches(true);
//		config.setDoMetaBrandMatches(true);
//		config.setDoAltImageTagScore(true);
//		analysis.setConfig(config);
//		SiteCrawlAnalyzer.runSiteCrawlAnalysis(analysis);
//		System.out.println("url contains city : " + analysis.getNumUrlContainsCity());
		
//		System.out.println("cities : " + SitesDAO.findCities(18511L));
		Asyncleton.getInstance();
	}
	
	public static void setMostRecentCrawls() {
		System.out.println("Fetching sites");
		String queryString = "from Site s";
		List<Site> sites = JPA.em().createQuery(queryString, Site.class).getResultList();
		System.out.println("sites : " + sites.size());
		int count = 0;
		for(Site site : sites){
			for(SiteCrawl siteCrawl : site.getCrawls()){
//				if(site.getMostRecentCrawl() == null || siteCrawl.getCrawlDate().compareTo(site.getMostRecentCrawl().getCrawlDate()) > 0){
//					site.setMostRecentCrawl(siteCrawl);
//				}
			}
			if(count++ %100 == 0){
				System.out.println("count : " + count);
			}
		}
	}
	
	public static void autoCanadaExperiment() throws IOException {
		
		String queryString = "from BasicDealer bd where bd.projectIdentifier = 'auto_canada'";
		List<BasicDealer> dealers = JPA.em().createQuery(queryString, BasicDealer.class).setMaxResults(1).setFirstResult(5).getResultList();
		System.out.println("dealers : " + dealers);
		
		ActorRef master = Asyncleton.getInstance().getMonotypeMaster(15, TempCrawlingWorker.class);
		for(BasicDealer dealer : dealers){
			System.out.println("dealer : " + dealer.getName());
			Site site = SitesDAO.getOrNew(dealer.getWebsite());
			master.tell(site, ActorRef.noSender());
		}
		
		
//		Report report = CSVImporter.importReport(Global.getInputFolder() + "/auto_canada.csv");
//		System.out.println("report : " + report);
//		for(ReportRow reportRow : report.getReportRows().values()){
//			System.out.println("reportrow : " + reportRow.getCell("Dealer Name"));
//			BasicDealer dealer = new BasicDealer();
//			dealer.setName(reportRow.getCell("Dealer Name"));
//			dealer.setWebsite(reportRow.getCell("Actual site"));
//			dealer.setProjectIdentifier("auto_canada");
//			JPA.em().persist(dealer);
//		}
		
	}
	
	public static void runbhphExperiment() throws Exception {
//		BhphCrawl.standardizeAndDistinctify();
		
		HttpConfig config = new HttpConfig();
		config.setProxyAddress(Global.getProxyUrl());
		config.setProxyPort(Global.getProxyPort());
		config.setUseProxy(false);
		config.setRequestsPerSecond(1);
		config.setUserAgent(Global.getDefaultUserAgentString());
		System.out.println("–");
//		URL url = new URL("http://buyherepayherevehicles.com/buy-here-pay-here-car-dealerships-directory/illinois/aurora/");
		URL url = new URL("https://www.kengarffvw.com/");
		HttpEndpoint endpoint = new HttpEndpoint(url);
		
		
		DocDerivationStrategy stateDerivationStrategy = new DocDerivationStrategy(config);
		Document doc = stateDerivationStrategy.apply(endpoint);
		System.out.println("doc : " + doc);
		
		
////		System.out.println("clean : " + Jsoup.clean(doc.toString(), Whitelist.simpleText()));
//		
//		Scraper<BasicDealer> regexScraper = (element) -> {
//			System.out.println("searching for regex dealers");
//			List<BasicDealer> regexDealers = new ArrayList<BasicDealer>();
//			String dealerRegex = "([^–>]+)–([^–,]+)[,–]([^–,]+),([^0-9]+)([0-9]+)[^–]+–[^0-9]+([0-9]+-[0-9]+-[0-9]+)";
//			Pattern dealerPattern = Pattern.compile(dealerRegex);
//			Matcher matcher = dealerPattern.matcher(element.toString());
//			while(matcher.find()){
//				System.out.println("found dealer : " + matcher.group(0));
////				BasicDealer dealer = new BasicDealer();
////				String name = matcher.group(1).trim();
////				String street = matcher.group(2).trim();
////				String city = matcher.group(3).trim();
////				String state = matcher.group(4).trim();
////				String postal = matcher.group(5).trim();
////				String phone = matcher.group(6).trim();
////				
////				dealer.setName(name);
////				dealer.setStreet(street);
////				dealer.setCity(city);
////				dealer.setState(state);
////				dealer.setPostal(postal);
////				dealer.setPhone(phone);
////				regexDealers.add(dealer);
//			}
//			System.out.println("regex dealers : " + regexDealers.size());
//			return regexDealers;
//		};
//		
//		regexScraper.derive(doc);
		
//		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
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
//		SiteCrawl siteCrawl = JPA.em().find(SiteCrawl.class, 55426L);
		
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
	
	public static void fetchingBenchmark(){
		Tim.start();
		int count = 5000;
//		int offset = 0;
		
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
		
//		TypedQuery<SiteCrawl> typed = JPA.em().createQuery(q)
//				.setHint("javax.persistence.loadgraph", JPA.em().getEntityGraph("siteCrawlFull"))
//				.setParameter(id, 5000L);
		
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
	
	
	public static void runAnalysis() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {
		SiteCrawl siteCrawl = JPA.em().find(SiteCrawl.class, 2L);
		siteCrawl.initAll();
		SiteCrawlAnalyzer.docAnalysis(siteCrawl);
//		InventoryTool.doExperiment(siteCrawl);
//		System.out.println("sitecrawl invNumbers : " + siteCrawl.getInventoryNumbers().size()); 
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
	
	public static void parseDealerAddresses() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IntrospectionException, NoSuchMethodException {
//		List<Dealer> dealers = JPA.em().createQuery("from Dealer d where d.address is not null", Dealer.class).getResultList();
//		
//		Matcher matcher;
//		System.out.println("City matcher : " + StringExtraction.CITY.getPattern());
//		int count = 0;
//		int nomatch = 0;
//		for(Dealer dealer : dealers) {
//			matcher = StringExtraction.CITY.getPattern().matcher(dealer.getAddress());
//			if(matcher.find()) {
//				String city = matcher.group(1);
//				String state = matcher.group(2);
////				System.out.println("city : " + city);
////				System.out.println("state : " + state);
//				dealer.setCity(city);
//				dealer.setState(state);
//			}
//			else {
//				nomatch++;
////				System.out.println("no match : " + dealer.getAddress());
//			}
//			if(count++ % 500 == 0) {
//				System.out.println("count : " + count);
//				JPA.em().getTransaction().commit();
//				JPA.em().getTransaction().begin();
//			}
//		}
//		System.out.println("nomatch : " + nomatch);
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
	
	public static void binaryStuff(){
		int oddPositions = 		0b0101010;
		int evenPositions = 	0b1010101;
		int max = 				0b1111111;
		System.out.println("Trying on bits : " + Integer.bitCount(max) + " (" + max + " in decimal)");
		int level2 = 0;
		int level3 = 0;
		int level4 = 0;
		int level5 = 0;
		int level6 = 0;
		int unknown = 0;
		for(int i = 1; i < max; i++){
			boolean divisibleBy3 = i % 3 == 0;
			
			int odd = Integer.bitCount(i & oddPositions);
			int even = Integer.bitCount(i & evenPositions);
			int difference = odd - even;
			
//			even = even >> 1;
//			System.out.println("i : " + i + " " + divisibleBy3);
//			System.out.println("difference : " + (odd - even));
			
			if(divisibleBy3 && difference != 0){
				System.out.println("i : " + i + " : " + Integer.toBinaryString(i));
				if(Math.abs(difference) != 3){
					if(Math.abs(difference) != 6){
						if(Math.abs(difference) != 9){
							if(Math.abs(difference) != 12){
								unknown = difference;	
							}else{
								level5++;
							}
						}else {
							level4++;
						}
					}else {
						level3++;
					}
				}else{
					level2++;
				}
			}
//			System.out.println("even : " + Integer.toBinaryString(even));
//			System.out.println("odd : " + Integer.toBinaryString(odd));
//			System.out.println("xnor : " + ~(odd ^ even));
//			System.out.println("i binary: " + Integer.toBinaryString(i) + " : " + Integer.toBinaryString(i & 0b01010101));
		}
		System.out.println("level2 : " + level2);
		System.out.println("level3 : " + level3);
		System.out.println("level4 : " + level4);
		System.out.println("level5 : " + level5);
		System.out.println("unknown : " + unknown);
	}
	
}
