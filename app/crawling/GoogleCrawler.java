package crawling;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import crawling.parsing.GoogleParser;
import global.Global;
import persistence.GoogleCrawl;
import persistence.MobileCrawl;
import play.Logger;

public class GoogleCrawler {
	
	private static final String GOOGLE_URL = "http://www.google.com";
	private static final String DELETE = Keys.chord(Keys.CONTROL + "a") + Keys.DELETE;
	
	private static WebDriver driver = null;
	
	private static final Integer MUTEX = 1;
	
	private static WebDriver getDriver() {
		System.out.println("getting driver");
		synchronized(MUTEX) {
			if(driver == null) {
				init();
			}
			return driver;
		}
	}
	
	private static void init() {
		System.out.println("Initting driver");
		synchronized(MUTEX){
			DesiredCapabilities capabilities = DesiredCapabilities.chrome();
			if(Global.useProxy()){
				Proxy proxy = new Proxy();
				String proxyString = Global.getProxyUrl() + ":" + Global.getProxyPort(); 
				proxy.setFtpProxy(proxyString);
				proxy.setHttpProxy(proxyString);
				proxy.setSslProxy(proxyString);
				capabilities.setCapability(CapabilityType.PROXY, proxy);
			}
			driver = new ChromeDriver(capabilities);
			driver.get(GOOGLE_URL);
		}
	}
	
	private static void onError(Exception e) {
		Logger.error("Received error in Google Crawler.  Restarting. " + e);
		stop();
	}
	
	private static void restart() {
		synchronized(MUTEX) {
			if(driver != null) {
				try{driver.quit();}
				catch(Exception e){
					Logger.error("Caught error while restarting : " + e);
					//TODO figure out what to do on error
				}
			}
			driver = null;
			init();
		}
	}
	
	private static void stop() {
		synchronized(MUTEX) {
			if(driver != null) {
				driver.quit();
			}
			driver = null;
		}
	}

	public static GoogleCrawl googleCrawl(String query) throws Exception{
		try{
			WebElement inputField = getDriver().findElement(By.id("lst-ib"));
			inputField.click();
			
			inputField.sendKeys(DELETE);
			inputField.sendKeys(query);
			inputField.sendKeys(Keys.ENTER);
			System.out.println("page source : " + driver.getPageSource());
			System.out.println("tostring : " + driver);
			System.out.println("window : " + driver.manage().window());
			System.out.println("javascript : " + ((JavascriptExecutor) driver).executeScript("return document.documentElement.outerHTML"));
			
//			driver.get("https://www.google.com/search?q=wikipedia");
			TimeUnit.SECONDS.sleep(120);  //Wait for redirects, etc. to finish.  This includes bad redirects after page load.
			return GoogleParser.fromRaw(null);
		}
		catch(Exception e) {
			System.out.println("Error while google crawling (" + query + ": " + e);
			Logger.error("Error while google crawling (" + query + ": " + e);
			onError(e);
		}
		return null;
	}
		
}
