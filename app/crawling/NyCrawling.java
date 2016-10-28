package crawling;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import crawling.nydmv.County;
import crawling.nydmv.NYDealer;
import crawling.nydmv.NyDao;
import global.Global;
import play.db.jpa.JPA;

public class NyCrawling {
public static void nyDmvExperiment() {
		
		String seed = "https://process.dmv.ny.gov/FacilityLookup/vsiqEnterFacInfo.cfm"; 
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		
		if(Global.useProxy()){
			Proxy proxy = new Proxy();
			String proxyString = Global.getProxyUrl() + ":" + Global.getProxyPort(); 
			proxy.setFtpProxy(proxyString);
			proxy.setHttpProxy(proxyString);
			proxy.setSslProxy(proxyString);
			capabilities.setCapability(CapabilityType.PROXY, proxy);
		}
//		capabilities.setCapability("chrome.switches",  Arrays.asList("--disable-javascript"));
		WebDriver driver = new ChromeDriver(capabilities);
		
		while(processNextNyCounty(driver)){
			JPA.em().getTransaction().commit();
			JPA.em().getTransaction().begin();
		};
		
	}
	
	public static boolean processNextNyCounty( WebDriver driver) {
		String seed = "https://process.dmv.ny.gov/FacilityLookup/vsiqEnterFacInfo.cfm"; 
		driver.get(seed);
		System.out.println("selecting radio button");
		WebElement dealerRadioButton = driver.findElement(By.cssSelector("input[value='DEALER']"));
		System.out.println("clicking radio button");
		dealerRadioButton.click();
		System.out.println("selecting select element");
		Select select = new Select(driver.findElement(By.cssSelector("select[name='sCounty']")));
		
		System.out.println("selecting options");
		List<WebElement> options = select.getOptions();
		
		System.out.println("number of options : " + options.size());
		
		for(int i = 1; i <= options.size(); i++){
			WebElement option = options.get(i);
			County county = NyDao.countyGetOrNew(option.getText());
			county.setValue(option.getAttribute("value"));
			if(county.getDateCrawled() == null){
				System.out.println("found uncrawled county : " + county.getName());
				select.selectByIndex(i);
				System.out.println("selecting submit button");
				WebElement submitButton = driver.findElement(By.name("submit search"));
				System.out.println("Submitting button");
				submitButton.submit();
				awaitStale(submitButton);
				
				System.out.println("selecting retail in select element");
				WebElement dealerTypeSelectElement = driver.findElement(By.cssSelector("select[name='drpDownfilter']"));
				Select dealerTypeSelect = new Select(dealerTypeSelectElement);
				dealerTypeSelect.selectByValue("-DLN-,-DLU-,-DLQ-");
				awaitStale(dealerTypeSelectElement);
				
				WebElement nextButton = null;
				do{
					nextButton = null;
					System.out.println("selecting results table");
					driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
					WebElement table = (new WebDriverWait(driver, 10))
							.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div#Results table")));
					parseNyDealers(table);
					nextButton = getNyNextButton(table);
					if(nextButton != null) {
						followLink(nextButton);
					}
				}while(nextButton != null);
				
				county.setDateCrawled(Calendar.getInstance().getTime());
				return true;
			} else {
				System.out.println("found previously crawled county : " + county.getName());
			}
		}
		return false;
	}
	
	public static void followLink(WebElement link) {
		link.click();
		awaitStale(link);
	}
	
	public static void awaitStale(WebElement element) {
		while(!isStale(element)){};
	}
	
	public static boolean isStale(WebElement element) {
		try{
			element.getText();
			return false;
		} catch(StaleElementReferenceException e) {
			return true;
		}
	}
	
	public static void parseNyDealers(WebElement table) {
		System.out.println("selecting table rows");
		List<WebElement> tableRows = table.findElements(By.tagName("tr"));
		System.out.println("parsing table rows : " + tableRows.size());
		for(WebElement row : tableRows) {
			List<WebElement> cells= row.findElements(By.tagName("td"));
			if(cells.size() > 0 && StringUtils.equals(cells.get(0).getAttribute("class"), "displayRow")){
				String facilityNumber = cells.get(0).getText();
				String facilityName = cells.get(1).getText();
				String street = cells.get(2).getText();
				String city = cells.get(3).getText();
				String zip = cells.get(4).getText();
				String countyString = cells.get(5).getText();
				NYDealer dealer = NyDao.dealerGetOrNew(facilityNumber);
				dealer.setFacilityName(facilityName);
				dealer.setStreet(street);
				dealer.setCity(city);
				dealer.setZip(zip);
				dealer.setCounty(countyString);
				
//				System.out.println("facilityNumber " + facilityNumber);
//				System.out.println("facilityName " + facilityName);
//				System.out.println("street " + street);
//				System.out.println("city " + city);
//				System.out.println("zip " + zip);
//				System.out.println("countyString " + countyString);
				
				
			}
		}
	}
	
	public static WebElement getNyNextButton(WebElement table){
		System.out.println("getting next button");
		WebElement nextButton = null;
		
		List<WebElement> links = table.findElements(By.tagName("a"));
		for(WebElement link : links) {
			if(StringUtils.contains(link.getText(), "Next")){
				nextButton = link;
			}
		}
		return nextButton;
	}
	public static void processCounty(WebDriver driver, Select select, int index){
		System.out.println("selecting submit button");
		WebElement submitButton = driver.findElement(By.name("submit search"));
		System.out.println("Submitting button");
		submitButton.submit();
	}
}
