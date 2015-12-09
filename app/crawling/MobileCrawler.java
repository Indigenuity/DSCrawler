package crawling;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.gargoylesoftware.htmlunit.BrowserVersion;

import persistence.MobileCrawl;
import play.Logger;

public class MobileCrawler {
	
	public static final String DEFAULT_MOBILE_USER_AGENT_STRING = "Mozilla/5.0 (iPhone; CPU iPhone OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5376e Safari/8536.25";
	
	public static MobileCrawl testingMobileCrawl(String seed) {
		
		HtmlUnitDriver driver = new HtmlUnitDriver();
		
		driver.getBrowserVersion().setUserAgent(DEFAULT_MOBILE_USER_AGENT_STRING);
		
		
		MobileCrawl mobileCrawl = new MobileCrawl();
		mobileCrawl.setSeed(seed);
		driver.get(seed);
		try{
			
//			System.out.println("driver.getPageSource()" + driver.getPageSource());
			TimeUnit.SECONDS.sleep(3);  //Wait for redirects, etc. to finish.  This includes bad redirects after page load.
//			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); 
			WebElement bodyElement = driver.findElement(By.tagName("body"));
//			
			System.out.println("bod: " + bodyElement.getText());
//			mobileCrawl.setWidth(bodyElement.getSize().width);
//			mobileCrawl.setHeight(bodyElement.getSize().height);
//			
//			mobileCrawl.setWindowWidth(Integer.parseInt(((JavascriptExecutor) driver).executeScript("return document.documentElement.clientWidth").toString()));
//			mobileCrawl.setWindowHeight(Integer.parseInt(((JavascriptExecutor) driver).executeScript("return document.documentElement.clientHeight").toString()));
//			mobileCrawl.setResolvedSeed(driver.getCurrentUrl());
			System.out.println("current url : " + driver.getCurrentUrl());
			driver.quit();
			
			//Check status code because WebDriver doesn't provide this information
			
//			URL url = new URL(mobileCrawl.getResolvedSeed());
//			HttpURLConnection con = (HttpURLConnection)(url.openConnection());
//			con.setConnectTimeout(5 * 1000);
//			con.setRequestProperty("User-Agent", DEFAULT_MOBILE_USER_AGENT_STRING);
//			con.connect();
//			int responseCode = con.getResponseCode();
			
//			mobileCrawl.setResponseCode(responseCode);
		}
		catch(InterruptedException e) {
			Logger.error("Well ain't that a dandy interruption : " + e);
		}
		catch(Exception e) {
			driver.quit();
			System.out.println("Couldn't finish Selenium work : " + e);
			Logger.error("Couldn't finish Selenium work : " + e);
			throw e;//Now that we've closed the driver, we can throw the exception
		}
		return mobileCrawl;
	}
	
	public static MobileCrawl defaultMobileCrawl(String seed) throws Exception{
		Map<String, String> mobileEmulation = new HashMap<String, String>();
		mobileEmulation.put("deviceName",  "Apple iPhone 6");
		
		
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		
		String proxyString = "52.25.252.253:8888";
		
		
		Proxy proxy = new Proxy();
		proxy.setFtpProxy(proxyString);
		proxy.setHttpProxy(proxyString);
		proxy.setSslProxy(proxyString);
		capabilities.setCapability(CapabilityType.PROXY, proxy);
		capabilities.setCapability("chrome.switches",  Arrays.asList("--disable-javascript"));
		Map<String, Object> chromeOptions = new HashMap<String, Object>();
		chromeOptions.put("mobileEmulation",  mobileEmulation);
		capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
		WebDriver driver = new ChromeDriver(capabilities);
		
		MobileCrawl mobileCrawl = new MobileCrawl();
		mobileCrawl.setSeed(seed);
		driver.get(seed);
		try{
			TimeUnit.SECONDS.sleep(10);  //Wait for redirects, etc. to finish.  This includes bad redirects after page load.
//			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); 
			WebElement bodyElement = driver.findElement(By.tagName("body"));
			
			mobileCrawl.setWidth(bodyElement.getSize().width);
			mobileCrawl.setHeight(bodyElement.getSize().height);
			
			mobileCrawl.setWindowWidth(Integer.parseInt(((JavascriptExecutor) driver).executeScript("return document.documentElement.clientWidth").toString()));
			mobileCrawl.setWindowHeight(Integer.parseInt(((JavascriptExecutor) driver).executeScript("return document.documentElement.clientHeight").toString()));
			mobileCrawl.setResolvedSeed(driver.getCurrentUrl());
			driver.quit();
			
			//Check status code because WebDriver doesn't provide this information
			
//			URL url = new URL(mobileCrawl.getResolvedSeed());
//			HttpURLConnection con = (HttpURLConnection)(url.openConnection());
//			con.setConnectTimeout(5 * 1000);
//			con.setRequestProperty("User-Agent", DEFAULT_MOBILE_USER_AGENT_STRING);
//			con.connect();
//			int responseCode = con.getResponseCode();
			
//			mobileCrawl.setResponseCode(responseCode);
		}
		catch(InterruptedException e) {
			Logger.error("Well ain't that a dandy interruption : " + e);
		}
		catch(Exception e) {
			driver.quit();
			System.out.println("Couldn't finish Selenium work : " + e);
			Logger.error("Couldn't finish Selenium work : " + e);
			throw e;//Now that we've closed the driver, we can throw the exception
		}
		return mobileCrawl;
	}

}
