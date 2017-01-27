package crawling.projects;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import akka.actor.ActorRef;
import async.async.Asyncleton;
import async.async.GenericMaster;
import audit.ListMatchResult;
import audit.ListMatcher;
import crawling.discovery.entities.Endpoint;
import crawling.discovery.html.DocDerivationStrategy;
import crawling.discovery.html.Extractor;
import crawling.discovery.html.HttpConfig;
import crawling.discovery.html.HttpEndpoint;
import crawling.discovery.html.Scraper;
import crawling.discovery.html.Selector;
import crawling.discovery.planning.CrawlPlan;
import crawling.discovery.planning.DerivationStrategy;
import crawling.discovery.planning.PersistStrategy;
import crawling.discovery.planning.PrimaryResourcePlan;
import crawling.discovery.planning.ResourcePlan;
import datatransfer.CSVGenerator;
import datatransfer.reports.Report;
import datatransfer.reports.ReportFactory;
import global.Global;
import net.sf.sprockets.google.Place;
import places.PlacesMaster;
import places.TextSearchWorker;
import play.db.jpa.JPA;
import salesforce.persistence.SalesforceAccount;
import utilities.DSFormatter;

public class BhphCrawl {
	
	public static Set<String> visited = new HashSet<String>();
	
	public static void matchToPlaces() throws IOException {
		String queryString = "from BasicDealer bd where bd.foreignIdentifier is null";
		List<BasicDealer> dealers = JPA.em().createQuery(queryString, BasicDealer.class).getResultList();
		System.out.println("Basic Dealers : " + dealers.size());
		
		ActorRef master = Asyncleton.instance().getGenericMaster(10, TextSearchWorker.class);
		int count = 0;
		for(BasicDealer dealer : dealers) {
			master.tell(dealer, ActorRef.noSender());
			if(count++ %10 == 0){
				System.out.println("Count : " + count);
			}
		}
	}

	public static void matchToSalesforce(){
		String queryString = "from BasicDealer bd where bd.foreignIdentifier is null or bd.foreignType = 'Places'";
		List<BasicDealer> dealers = JPA.em().createQuery(queryString, BasicDealer.class).getResultList();
		System.out.println("Basic Dealers : " + dealers.size());
		queryString = "from SalesforceAccount sa";
		List<SalesforceAccount> sfAccounts = JPA.em().createQuery(queryString, SalesforceAccount.class).getResultList();
		System.out.println("sfAccounts: " + sfAccounts.size());
		BiFunction<BasicDealer, SalesforceAccount, Boolean> equalityFunction = (dealer, sfAccount) ->
		{
			if(dealer.getStdPhone() == null || sfAccount.getStdPhone() == null){
				return false;
			}
			return dealer.getStdPhone().equals(sfAccount.getStdPhone());
		};
		BiFunction<BasicDealer, SalesforceAccount, Integer> distanceFunction = (dealer, sfAccount) -> 0;
		
		System.out.println("saving matches");
		int count = 0;
//		ListMatchResult<BasicDealer,SalesforceAccount,Integer> listMatchResult = ListMatcher.compareLists(dealers, sfAccounts, equalityFunction, distanceFunction);
		for(BasicDealer dealer : dealers){
			for(SalesforceAccount sfAccount : sfAccounts) {
				if(dealer.getWebsite() != null && sfAccount.getSite() != null && dealer.getWebsite().equals(sfAccount.getSite().getHomepage())){
					dealer.setForeignIdentifier(sfAccount.getSalesforceAccountId());
					dealer.setForeignType("Salesforce");
				}
			}
			if(count++ %100 == 0){
				System.out.println("Count : " + count);
			}
		}
	}
	
	public static void standardizeAndDistinctify(){
		String queryString = "from BasicDealer bd";
		Set<String> uniquePhones = new HashSet<String>(); 
		List<BasicDealer> dealers = JPA.em().createQuery(queryString, BasicDealer.class).getResultList();
		int count = 0;
		for(BasicDealer dealer : dealers) {
			dealer.setStdPhone(DSFormatter.standardizePhone(dealer.getPhone()));
			if(dealer.getPhone() != null && !uniquePhones.add(dealer.getStdPhone())){
				JPA.em().remove(dealer);
				continue;
			}
			String dealerQuery = dealer.getName() + " " + dealer.getStreet() + ", " + dealer.getCity() + ", " + dealer.getState();
			dealer.setStdState(DSFormatter.standardizeState(dealer.getState()));
			dealer.setStdStreet(DSFormatter.standardizeStreetAddress(dealer.getStreet()));
			dealer.setCustom2(dealerQuery);
			dealer.setCustom3(PlacesMaster.getGoogleSearchUrl(dealerQuery));
			if(count++ %100 == 0){
				System.out.println("Count : " + count); 
			}
		}
		
//		queryString = "from SalesforceAccount sa";
//		List<SalesforceAccount> sfAccounts = JPA.em().createQuery(queryString, SalesforceAccount.class).getResultList();
//		for(SalesforceAccount sfAccount : sfAccounts) {
//			sfAccount.setStdState(DSFormatter.standardizeState(sfAccount.getState()));
//			sfAccount.setStdPhone(DSFormatter.standardizePhone(sfAccount.getPhone()));
//			sfAccount.setStdStreet(DSFormatter.standardizeStreetAddress(sfAccount.getStreet()));
//			if(count++ %100 == 0){
//				System.out.println("Count : " + count);
//			}
//		}
	}
	
	public static void justRunCrappy() throws IOException, InterruptedException {
		String bhphUrl = "http://buyherepayherevehicles.com/buy-here-pay-here-car-dealerships-directory/";
		URL url = new URL(bhphUrl);
		
		HttpConfig config = new HttpConfig();
		config.setProxyAddress(Global.getProxyUrl());
		config.setProxyPort(Global.getProxyPort());
		config.setUseProxy(true);
		config.setPolitenessDelay(1000);
		config.setUserAgent(Global.getDefaultUserAgentString());
		config.setConnectTimeout(10000);
		config.setReadTimeout(10000);
		
		HttpEndpoint endpoint = new HttpEndpoint(url);
		DocDerivationStrategy rootDerivationStrategy = new DocDerivationStrategy(config);
		Document doc = rootDerivationStrategy.apply(endpoint);
		
		System.out.println("doc : " + doc.title());
		
		Scraper<String> hrefScraper = Selector
				.byTag("body")
				.thenByAttributeValueMatching("href", "/buy-here-pay-here-car-dealerships-directory/[a-zA-Z]+")
				.each(Extractor.attr("href"));
		
		List<String> stateHrefs = hrefScraper.apply(doc);
		
		int count = 0;
		List<BasicDealer> basicDealers = new ArrayList<BasicDealer>();
		for(String stateHref : stateHrefs){
			count++;
			System.out.println("doing state " + count + " : " + stateHref);
			if(count >26){
				Thread.sleep(2000);
				basicDealers.addAll(runState(config, stateHref));
			} 
		}
		
		for(BasicDealer dealer : basicDealers) {
//			System.out.println("dealer : " + dealer.getName());
		}
		
		Set<BasicDealer> dealerSet = new HashSet<BasicDealer>(basicDealers);
		Report report = ReportFactory.fromGenericSet(dealerSet);
		report.setName("BHPH Dealers");
		CSVGenerator.printReport(report);
	}
	
	public static List<BasicDealer> runState(HttpConfig config, String stateHref) throws IOException {
		
		List<BasicDealer> basicDealers = new ArrayList<BasicDealer>();
		
		URL url = new URL(stateHref);
		HttpEndpoint endpoint = new HttpEndpoint(url);
		DocDerivationStrategy stateDerivationStrategy = new DocDerivationStrategy(config);
		Document doc = stateDerivationStrategy.apply(endpoint);
		
		
		System.out.println("stateDoc : " + doc.title());
		
		Scraper<BasicDealer> vcardScraper = Selector.byClass("vcard")
				.each((element) -> {
					
					BasicDealer dealer = new BasicDealer();
					dealer.setName(getText(element.getElementsByClass("organization-name").first()));
					dealer.setStreet(getText(element.getElementsByClass("street-address").first()));
					dealer.setCity(getText(element.getElementsByClass("locality").first()));
					dealer.setState(getText(element.getElementsByClass("region").first()));
					dealer.setPostal(getText(element.getElementsByClass("postal-code").first()));
					dealer.setPhone(getText(element.getElementsByClass("tel").first()));
//					System.out.println("found vcard dealer : " + dealer.getName());
					return dealer;
				});
		
		Scraper<BasicDealer> regexScraper = (element) -> {
			List<BasicDealer> regexDealers = new ArrayList<BasicDealer>();
			String dealerRegex = "([^–>]+)–([^–,]+)[,–]([^–,]+),([^0-9]+)([0-9]+)[^–]+–[^0-9]+([0-9]+-[0-9]+-[0-9]+)";
			Pattern dealerPattern = Pattern.compile(dealerRegex);
			Matcher matcher = dealerPattern.matcher(element.toString());
			while(matcher.find()){
//				System.out.println("found dealer : " + matcher.group(0));
				BasicDealer dealer = new BasicDealer();
				String name = matcher.group(1).trim();
				String street = matcher.group(2).trim();
				String city = matcher.group(3).trim();
				String state = matcher.group(4).trim();
				String postal = matcher.group(5).trim();
				String phone = matcher.group(6).trim();
				
				dealer.setName(name);
				dealer.setStreet(street);
				dealer.setCity(city);
				dealer.setState(state);
				dealer.setPostal(postal);
				dealer.setPhone(phone);
				regexDealers.add(dealer);
			}
			return regexDealers;
		};
		
		String cityHrefRegex = "/buy-here-pay-here-car-dealerships-directory/[a-zA-Z-]+/[a-zA-Z-]+\\b";
				
		Scraper<String> cityHrefScraper = Selector
				.byTag("body")
				.thenByAttributeValueMatching("href", cityHrefRegex)
				.each(Extractor.attr("href"));
		List<String> cityHrefs = cityHrefScraper.apply(doc);
		for(String cityHref : cityHrefs) {
			System.out.println("found city Href : " + cityHref);
			if(visited.add(cityHref)){
				try{
					basicDealers.addAll(runState(config, cityHref));
				} catch(MalformedURLException e) {
					System.out.println("Bad city Href : " + cityHref);
				}
			} else {
				System.out.println("not visiting repeat");
			}
			
//			throw new IllegalStateException("no more boogie");
		}
		
		basicDealers.addAll(vcardScraper.derive(doc));
		basicDealers.addAll(regexScraper.derive(doc));
		System.out.println("basic Dealers in state page (" + stateHref + ") : " + basicDealers.size());
		persistDealers(basicDealers);
		return basicDealers;
	}
	
	public static void persistDealers(List<BasicDealer> dealers) {
		for(BasicDealer dealer : dealers) {
			persistDealer(dealer);
		}
		JPA.em().getTransaction().commit();
		JPA.em().getTransaction().begin();
		JPA.em().clear();
	}
	
	public static void persistDealer(BasicDealer dealer) {
		dealer.setStdStreet(DSFormatter.standardizeStreetAddress(dealer.getStreet()));
		JPA.em().merge(dealer);
	}
	
	public static void runCrawlWell() throws IOException{
		
//		HttpConfig config = new HttpConfig();
//		config.setProxyAddress(Global.getProxyUrl());
//		config.setProxyPort(Global.getProxyPort());
//		config.setUseProxy(true);
//		config.setPolitenessDelay(1000);
//		config.setUserAgent(Global.getDefaultUserAgentString());
//		
//		String bhphUrl = "http://buyherepayherevehicles.com/buy-here-pay-here-car-dealerships-directory/arkansas/arkadelphia/";
//		URL url = new URL(bhphUrl);
//		HttpEndpoint endpoint = new HttpEndpoint(url);
//		
//		DocDerivationStrategy rootDerivationStrategy = new DocDerivationStrategy(config);
//		
//		PrimaryResourcePlan<Element> rootPlan = new PrimaryResourcePlan<Element>(rootDerivationStrategy, PersistStrategy.emptyStrategy(Element.class));
//		
		
//		ResourcePlan<Document, Endpoint> stateHrefPlan = new ResourcePlan<Element, Endpoint>(hrefScraper, PersistStrategy.emptyStrategy(Element.class))
//		
//		CrawlPlan crawlPlan = new CrawlPlan();
//		crawlPlan.addResourcePlan(rootPlan);
//		
//		
//		
//		
//		
//		
//		
		
		
		
//		DerivationStrategy<Element,List<BasicDealer>> each = vcardSelector.each(vcardDealerExtractor);
		
		Selector stateLinksSelector = Selector.byAttributeValueMatching("href", "/buy-here-pay-here-car-dealerships-directory/[a-zA-Z]+");
		
		Function<Element, List<String>> attrDerivationStrategy = stateLinksSelector.attr("href");
		
//		ResourceBlueprint<Element, List<String>> stateLinksBlueprint = 
		DerivationStrategy stateLinksStrategy = Selector
				.byAttributeValueMatching("href", "/buy-here-pay-here-car-dealerships-directory/[a-zA-Z]+")
				.attr("href");
		
//		List<String> hrefs = attrDerivationStrategy.apply(doc);
//		
//		System.out.println("hrefs : " + hrefs);
	}
	
	public static void parsePage(Document doc) {
		
		
	}
	
	public static <R> List<R> runScraper(Element element, Scraper<R> scraper){
		return scraper.apply(element);
	}
	
	public static String getText(Element element) {
		if(element == null) {
			return null;
		}
		return element.text();
	}
}
