package crawling.projects;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import audit.Standardizer;
import dao.GeneralDAO;
import dao.SalesforceDao;
import datadefinitions.StringExtraction;
import datatransfer.CSVGenerator;
import datatransfer.reports.Report;
import datatransfer.reports.ReportFactory;
import global.Global;
import persistence.Site;
import persistence.UsState;
import play.db.jpa.JPA;
import pods.PodZip;
import salesforce.persistence.SalesforceAccount;

public class MotorradScraper {
	private WebDriver driver;
	private String seed = "http://www.bmwmotorcycles.com/us/en/individual/dlo/dlo-mcsla.html?country=US";
	private static final Pattern DEALER_ID = Pattern.compile("([0-9]+)");
	
	private Set<String> dealerIds = new HashSet<String>();
	
	public MotorradScraper() {
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		
		if(Global.useProxy()){
			Proxy proxy = new Proxy();
			String proxyString = Global.getProxyUrl() + ":" + Global.getProxyPort();  
			proxy.setFtpProxy(proxyString);
			proxy.setSslProxy(proxyString);
			capabilities.setCapability(CapabilityType.PROXY, proxy);
		}
//		capabilities.setCapability("chrome.switches",  Arrays.asList("--disable-javascript"));
		this.driver = new ChromeDriver(capabilities);
		
	}
	
	public void runCrawl() throws InterruptedException{ 
		System.out.println("Getting motorrad dealers : " + seed);
		driver.get(seed);
		System.out.println("Got seed : " + seed);
		driver.switchTo().frame("fad-iframe");
		System.out.println("Selected iframe");
		WebElement input = driver.findElement(By.cssSelector("#c2b_locator_town"));
		System.out.println("input : " + input);
		
		
//		search("alabama, usa");
//		
		for(UsState state : GeneralDAO.getAll(UsState.class)){
			runState(state);
		}
	}
	
	
	
	public void runState(UsState state) throws InterruptedException{
		System.out.println("Starting on state : " + state.getStateName());
		if(state.getProjectCode() != null && state.getProjectCode().equals("finished")){
			System.out.println("State already bmwed.");
			return;
		}
//		if(state.getStateName().equals("Alaska")){
//			state.setProjectCode("bmwed");
//			return;
//		}
		
		WebElement input = driver.findElement(By.cssSelector("#c2b_locator_town"));
		
		String searchTerm = state.getStateName();
		input.click();
		for(int i = 0; i < 20; i++){
			input.sendKeys(Keys.BACK_SPACE);
		}
		input.sendKeys(searchTerm);
		input.sendKeys(Keys.RETURN);
		Thread.sleep(4000);
		for(WebElement listing : driver.findElements(By.className("result"))){
			processListing(listing);
		}
		state.setProjectCode("finished");
		JPA.em().getTransaction().commit();
		JPA.em().getTransaction().begin();
	}
	
	private void processListing(WebElement listing) {
		BasicDealer dealer = new BasicDealer();
		
//		dealer.setIdentifier(getDealerId(listing.findElement(By.cssSelector("h4 a")).getAttribute("onclick")));
//		
//		if(!dealerIds.add(dealer.getIdentifier())){
//			return;
//		}
		
		dealer.setName(listing.findElement(By.cssSelector(".dealer-name")).getText());
		dealer.setCustom1(listing.findElement(By.className("address")).getText());
		
		WebElement website = findOptional(listing, By.cssSelector(".link-homepage a"));
		if(website != null){
			dealer.setWebsite(website.getAttribute("href"));
		}
		
		WebElement phone = findOptional(listing, By.className("tel"));
		if(phone != null){
			dealer.setPhone(phone.getText());
		}
		
		dealer.setProjectIdentifier("finished");
		
		System.out.println("dealer : " + dealer.getName());
		System.out.println("address : " + dealer.getCustom1());
		System.out.println("phone: " + dealer.getPhone());
		System.out.println("website : " + dealer.getWebsite());
		System.out.println("offerings : " + dealer.getCustom2());
		System.out.println("dealer id: " + dealer.getIdentifier());
		JPA.em().persist(dealer);
	}
	
	private String getDealerId(String onclick) {
		System.out.println("searching href : " + onclick);
		Matcher matcher = DEALER_ID.matcher(onclick);
		if(matcher.find()){
			return matcher.group(1);
		} else {
			System.out.println("no find ");
		}
		return null;
	}
	
	private boolean followPagination() throws InterruptedException{
//		return false;
		WebElement nextLink = getNextPagination();
		if(nextLink != null){
			nextLink.click();
			Thread.sleep(4000);
			return true;
		}
		return false;
	}
	
	private WebElement getNextPagination(){
		List<WebElement> paginationLinks = driver.findElements(By.cssSelector(".sn_pagination_list a"));
		if(paginationLinks.size() < 1){
			return null;
		}
		boolean foundActive = false;
		for(WebElement link : paginationLinks){
			String classString = link.getAttribute("class"); 
			if(!foundActive && !StringUtils.isEmpty(classString) && classString.equals("active")){
				foundActive = true;
			} else if(foundActive){
				return link;
			}
		}
		return null;
	}
	
	public void search(String searchTerm) throws InterruptedException{
		WebElement searchReset = findOptional(By.className("sn_search_reset"));
		if(searchReset.isDisplayed()){
			searchReset.click();
			Thread.sleep(500);
		}
		
		WebElement searchBox = driver.findElement(By.id("search"));
		searchBox.click();
		searchBox.sendKeys(searchTerm);
		searchBox.sendKeys(Keys.RETURN);
		Thread.sleep(4000);
		followSuggested();
	}
	
	private void followSuggested() throws InterruptedException {
		WebElement suggested = findOptional(By.cssSelector(".didYouMeanList a"));
		if(suggested != null){
			suggested.click();
			Thread.sleep(4000);
		}
	}
	
	public WebElement findOptional(WebElement context, By by){
		List<WebElement> elements = context.findElements(by);
		if(elements.size() < 1){
			return null;
		}
		return elements.get(0);
	}
	
	public WebElement findOptional(By by){
		List<WebElement> elements = driver.findElements(by);
		if(elements.size() < 1){
			return null;
		}
		return elements.get(0);
	}
	
	public void awaitStale(WebElement element) {
		while(!isStale(element)){};
	}
	
	public boolean isStale(WebElement element) {
		try{
			element.getText();
			return false;
		} catch(StaleElementReferenceException e) {
			return true;
		}
	}
	
	
	public static void standardizeDealers(){
		List<BasicDealer> dealers = GeneralDAO.getList(BasicDealer.class, "projectIdentifier", "Harley");
		System.out.println("dealers : " + dealers.size());
		for(BasicDealer dealer : dealers){
			String[] addressParts = dealer.getCustom1().split("\n");
			if(!addressParts[1].contains(", USA") && !addressParts[1].contains(", CAN")){
				dealer.setCustom6("not domestic");
			}
			dealer.setStreet(addressParts[0]);
			Matcher matcher = StringExtraction.US_ADDRESS.getPattern().matcher(addressParts[1]);
			if(matcher.find()){
				dealer.setCity(matcher.group(1));
				dealer.setState(matcher.group(2));
				dealer.setCountry(matcher.group(3));
				dealer.setPostal(matcher.group(4));
			}
			Standardizer.standardize(dealer);
		}
	}
	
	public static void classifyDealers() {
		List<BasicDealer> dealers = GeneralDAO.getList(BasicDealer.class, "projectIdentifier", "Harley");
		System.out.println("dealers : " + dealers.size());
		List<PodZip> pzs = GeneralDAO.getAll(PodZip.class);
		Map<String, PodZip> podMapping = pzs.stream().collect(Collectors.toMap((pz) -> pz.getPostalCode(), Function.identity()));
		System.out.println("pzs : " + pzs.size());
		int count = 0;
		for(BasicDealer dealer : dealers){
			PodZip pz = podMapping.get(dealer.getStdPostal());
			if(pz == null){
				dealer.setCustom4("No pod mapping");
				dealer.setCustom5("No pod mapping");
			} else{
				dealer.setCustom4(pz.getIndyPod());
				dealer.setCustom5(pz.getFranchisePod());
			} 
//			dealer.setCustom6(salesforceMatch(dealer));
			System.out.println("count : " + count++);
		}
	}
	
	public static String salesforceMatch(BasicDealer dealer){
		if(StringUtils.equals("not domestic", dealer.getCustom6())){
			return "not domestic";
		}
		if(!dealer.getCustom2().contains("H-D® Motorcycles")){
			return "Not a dealer";
		}
		SalesforceAccount account = GeneralDAO.getFirst(SalesforceAccount.class, "stdStreet", dealer.getStdStreet());
		if(account != null){
			return "Address Match : " + account.getSalesforceId();
		}
		account = GeneralDAO.getFirst(SalesforceAccount.class, "stdPhone", dealer.getStdPhone());
		if(account != null){
			return "Phone Match : " + account.getSalesforceId();
		}
		Site site = GeneralDAO.getFirst(Site.class, "homepage", dealer.getWebsite());
		if(site != null){
			List<SalesforceAccount> accounts = SalesforceDao.findBySite(site);
			if(accounts.size() > 0){
				return "Website Match : " + accounts.get(0).getSalesforceId();
			}	
		}
		return "Not in salesforce";
	}
	
	public static void generateReport() throws IOException {
		System.out.println("Generating Report for Harley Dealers");
		List<BasicDealer> dealers = GeneralDAO.getList(BasicDealer.class, "projectIdentifier", "Harley");
		Report report = ReportFactory.fromEntityCollection(dealers);
		report.setName("Harley Davidson Dealers");
		CSVGenerator.printReport(report);
	}
}
