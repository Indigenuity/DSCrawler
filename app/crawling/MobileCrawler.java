package crawling;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;

import global.Global;
import persistence.MobileCrawl;
import play.Logger;

public class MobileCrawler {
	
	public static final String DEFAULT_MOBILE_USER_AGENT_STRING = "Mozilla/5.0 (iPhone; CPU iPhone OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5376e Safari/8536.25";
	
	public static MobileCrawl testingMobileCrawl(String seed) {
		WebClient[] webClient = new WebClient[1];
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		
		if(Global.useProxy()){
			Proxy proxy = new Proxy();
			String proxyString = Global.getProxyUrl() + ":" + Global.getProxyPort(); 
			proxy.setFtpProxy(proxyString);
			proxy.setHttpProxy(proxyString);
			proxy.setSslProxy(proxyString);
			capabilities.setCapability(CapabilityType.PROXY, proxy);
		}
		
		HtmlUnitDriver driver = new HtmlUnitDriver(capabilities)
		{
	        {
	            webClient[0] = this.getWebClient();
	        }
	    };
	    driver.setJavascriptEnabled(true);
//	    webClient.throw
		
		driver.getBrowserVersion().setUserAgent(DEFAULT_MOBILE_USER_AGENT_STRING);
		
		MobileCrawl mobileCrawl = new MobileCrawl();
		mobileCrawl.setSeed(seed);
		System.out.println("Performing mobile crawl : " + seed);
		
		driver.get(seed);
		try{
			
//			System.out.println("driver.getPageSource()" + driver.getPageSource());
//			TimeUnit.SECONDS.sleep(5);  //Wait for redirects, etc. to finish.  This includes bad redirects after page load.
//			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); 
			WebElement bodyElement = driver.findElement(By.cssSelector("body"));
			System.out.println("body height : " + bodyElement.getSize().getHeight());
			System.out.println("body width : " + bodyElement.getSize().getWidth());
			System.out.println("css body width: " + bodyElement.getCssValue("width"));
			System.out.println("css body height: " + bodyElement.getCssValue("height"));
			System.out.println("scroll width : " + bodyElement.getCssValue("scrollWidth"));
			System.out.println("window height : " + driver.manage().window().getSize().getHeight());
			System.out.println("window width : " + driver.manage().window().getSize().getWidth());
			String javascript = "return document.getElementsByTagName('html')[0].scrollWidth";
			System.out.println("scrollwidth : " + ((JavascriptExecutor) driver).executeScript(javascript));
			javascript = "return document.getElementsByTagName('html')[0].offsetWidth";
			System.out.println("offsetwidth : " + ((JavascriptExecutor) driver).executeScript(javascript));
			javascript = "return document.getElementsByTagName('html')[0].clientWidth";
			System.out.println("clientwidth : " + ((JavascriptExecutor) driver).executeScript(javascript));
			javascript = "return document.getElementsByTagName('html')[0].offsetLeft";
			System.out.println("offsetLeft : " + ((JavascriptExecutor) driver).executeScript(javascript));
			javascript = "return document.getElementsByTagName('html')[0].offsetParent";
			System.out.println("offsetParent : " + ((JavascriptExecutor) driver).executeScript(javascript));
			javascript = "return document.getElementsByTagName('html')[0].clientLeft";
			System.out.println("clientLeft : " + ((JavascriptExecutor) driver).executeScript(javascript));
			javascript = "return document.getElementsByTagName('html')[0].scrollLeft";
			System.out.println("scrollLeft : " + ((JavascriptExecutor) driver).executeScript(javascript));
			javascript = "return document.getElementsByTagName('html')[0].scrollLeftMax";
			System.out.println("scrollLeftMax : " + ((JavascriptExecutor) driver).executeScript(javascript));
			javascript = "return document.getElementsByTagName('html')[0].scrollHeight";
			System.out.println("scrollHeight : " + ((JavascriptExecutor) driver).executeScript(javascript));
			
			
			System.out.println(" ");
			driver.manage().window().setSize(new Dimension(480, 320));
			
			bodyElement = driver.findElement(By.cssSelector("body"));
			System.out.println("body height : " + bodyElement.getSize().getHeight());
			System.out.println("body width : " + bodyElement.getSize().getWidth());
			System.out.println("css body width: " + bodyElement.getCssValue("width"));
			System.out.println("css body height: " + bodyElement.getCssValue("height"));
			System.out.println("scroll width : " + bodyElement.getCssValue("scrollWidth"));
			System.out.println("window height : " + driver.manage().window().getSize().getHeight());
			System.out.println("window width : " + driver.manage().window().getSize().getWidth());
			
			javascript = "return document.getElementsByTagName('html')[0].scrollWidth";
			System.out.println("scrollwidth : " + ((JavascriptExecutor) driver).executeScript(javascript));
			javascript = "return document.getElementsByTagName('html')[0].offsetWidth";
			System.out.println("offsetwidth : " + ((JavascriptExecutor) driver).executeScript(javascript));
			javascript = "return document.getElementsByTagName('html')[0].clientWidth";
			System.out.println("clientwidth : " + ((JavascriptExecutor) driver).executeScript(javascript));
			javascript = "return document.getElementsByTagName('html')[0].offsetLeft";
			System.out.println("offsetLeft : " + ((JavascriptExecutor) driver).executeScript(javascript));
			javascript = "return document.getElementsByTagName('html')[0].offsetParent";
			System.out.println("offsetParent : " + ((JavascriptExecutor) driver).executeScript(javascript));
			javascript = "return document.getElementsByTagName('html')[0].clientLeft";
			System.out.println("clientLeft : " + ((JavascriptExecutor) driver).executeScript(javascript));
			javascript = "return document.getElementsByTagName('html')[0].scrollLeft";
			System.out.println("scrollLeft : " + ((JavascriptExecutor) driver).executeScript(javascript));
			javascript = "return document.getElementsByTagName('html')[0].scrollLeftMax";
			System.out.println("scrollLeftMax : " + ((JavascriptExecutor) driver).executeScript(javascript));
			javascript = "return document.getElementsByTagName('html')[0].scrollHeight";
			System.out.println("scrollHeight : " + ((JavascriptExecutor) driver).executeScript(javascript));
			
			System.out.println(" ");
			driver.manage().window().setSize(new Dimension(1480, 3120));
			
			bodyElement = driver.findElement(By.cssSelector("body"));
			System.out.println("body height : " + bodyElement.getSize().getHeight());
			System.out.println("body width : " + bodyElement.getSize().getWidth());
			System.out.println("css body width: " + bodyElement.getCssValue("width"));
			System.out.println("css body height: " + bodyElement.getCssValue("height"));
			System.out.println("scroll width : " + bodyElement.getCssValue("scrollWidth"));
			System.out.println("window height : " + driver.manage().window().getSize().getHeight());
			System.out.println("window width : " + driver.manage().window().getSize().getWidth());
			
			javascript = "return document.getElementsByTagName('html')[0].scrollWidth";
			System.out.println("scrollwidth : " + ((JavascriptExecutor) driver).executeScript(javascript));
			javascript = "return document.getElementsByTagName('html')[0].offsetWidth";
			System.out.println("offsetwidth : " + ((JavascriptExecutor) driver).executeScript(javascript));
			javascript = "return document.getElementsByTagName('html')[0].clientWidth";
			System.out.println("clientwidth : " + ((JavascriptExecutor) driver).executeScript(javascript));
			javascript = "return document.getElementsByTagName('html')[0].offsetLeft";
			System.out.println("offsetLeft : " + ((JavascriptExecutor) driver).executeScript(javascript));
			javascript = "return document.getElementsByTagName('html')[0].offsetParent";
			System.out.println("offsetParent : " + ((JavascriptExecutor) driver).executeScript(javascript));
			javascript = "return document.getElementsByTagName('html')[0].clientLeft";
			System.out.println("clientLeft : " + ((JavascriptExecutor) driver).executeScript(javascript));
			javascript = "return document.getElementsByTagName('html')[0].scrollLeft";
			System.out.println("scrollLeft : " + ((JavascriptExecutor) driver).executeScript(javascript));
			javascript = "return document.getElementsByTagName('html')[0].scrollLeftMax";
			System.out.println("scrollLeftMax : " + ((JavascriptExecutor) driver).executeScript(javascript));
			javascript = "return document.getElementsByTagName('html')[0].scrollHeight";
			System.out.println("scrollHeight : " + ((JavascriptExecutor) driver).executeScript(javascript));
			
			
			
			
			
			
			
			
			
//			System.out.println("bod: " + bodyElement.getText());
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
		catch(Exception e) {
			driver.quit();
			System.out.println("Couldn't finish Selenium work : " + e);
			Logger.error("Couldn't finish Selenium work : " + e);
			throw e;//Now that we've closed the driver, we can throw the exception
		}
		return mobileCrawl;
	}
	
	public static MobileCrawl defaultMobileCrawl(String seed) throws Exception{
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
		
		MobileCrawl mobileCrawl = new MobileCrawl();
		mobileCrawl.setSeed(seed);
		System.out.println("Performing faux mobile crawl : " + seed);
		
		/*********************** Perform crawl with small windowed normal browser ************************************/
		driver.manage().window().setSize(new Dimension(480, 320));
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.get(seed);
		try{
			
			WebElement bodyElement = driver.findElement(By.cssSelector("body"));
			int bodyWidth = bodyElement.getSize().getWidth();
			int bodyHeight = bodyElement.getSize().getHeight();
			String javascript = "return document.getElementsByTagName('html')[0].scrollWidth";
			int scrollWidth = Integer.parseInt(((JavascriptExecutor) driver).executeScript(javascript) + "");
			javascript = "return document.getElementsByTagName('html')[0].scrollHeight";
			int scrollHeight= Integer.parseInt(((JavascriptExecutor) driver).executeScript(javascript) + "");
			String content = driver.getPageSource();
			
			
			mobileCrawl.setFauxWidth(bodyWidth);
			mobileCrawl.setFauxHeight(bodyHeight);
			mobileCrawl.setFauxScrollWidth(scrollWidth);
			mobileCrawl.setFauxScrollHeight(scrollHeight);
			mobileCrawl.setFauxWindowWidth(Integer.parseInt(((JavascriptExecutor) driver).executeScript("return document.documentElement.clientWidth").toString()));
			mobileCrawl.setFauxWindowHeight(Integer.parseInt(((JavascriptExecutor) driver).executeScript("return document.documentElement.clientHeight").toString()));
			mobileCrawl.setFauxResolvedSeed(driver.getCurrentUrl());
			System.out.println("Checking for errors");
			mobileCrawl.setFauxDetected400(content.matches("(?s).*\\b400\\b.*"));
			mobileCrawl.setFauxDetected401(content.matches("(?s).*\\b401\\b.*"));
			mobileCrawl.setFauxDetected402(content.matches("(?s).*\\b402\\b.*"));
			mobileCrawl.setFauxDetected403(content.matches("(?s).*\\b403\\b.*"));
			mobileCrawl.setFauxDetected404(content.matches("(?s).*\\b404\\b.*"));
			mobileCrawl.setFauxDetected500(content.matches("(?s).*\\b500\\b.*"));
			mobileCrawl.setFauxDetected501(content.matches("(?s).*\\b501\\b.*"));
			mobileCrawl.setFauxDetected502(content.matches("(?s).*\\b502\\b.*"));
			mobileCrawl.setFauxDetected503(content.matches("(?s).*\\b503\\b.*"));
			
			driver.quit();
			
			System.out.println("starting actual mobile crawl");
			/*********************** Perform actual mobile crawl with mobile browser ************************************/	
			Map<String, String> mobileEmulation = new HashMap<String, String>();
			mobileEmulation.put("deviceName",  "Apple iPhone 6");
			Map<String, Object> chromeOptions = new HashMap<String, Object>();
			chromeOptions.put("mobileEmulation",  mobileEmulation);
			capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
			driver = new ChromeDriver(capabilities);
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			driver.get(seed);
			
			bodyElement = driver.findElement(By.cssSelector("body"));
			bodyWidth = bodyElement.getSize().getWidth();
			bodyHeight = bodyElement.getSize().getHeight();
			javascript = "return document.getElementsByTagName('html')[0].scrollWidth";
			scrollWidth = Integer.parseInt(((JavascriptExecutor) driver).executeScript(javascript) + "");
			javascript = "return document.getElementsByTagName('html')[0].scrollHeight";
			scrollHeight= Integer.parseInt(((JavascriptExecutor) driver).executeScript(javascript) + "");
			content = driver.getPageSource();
			
			
			mobileCrawl.setWidth(bodyWidth);
			mobileCrawl.setHeight(bodyHeight);
			mobileCrawl.setScrollWidth(scrollWidth);
			mobileCrawl.setScrollHeight(scrollHeight);
			mobileCrawl.setWindowWidth(Integer.parseInt(((JavascriptExecutor) driver).executeScript("return document.documentElement.clientWidth").toString()));
			mobileCrawl.setWindowHeight(Integer.parseInt(((JavascriptExecutor) driver).executeScript("return document.documentElement.clientHeight").toString()));
			mobileCrawl.setResolvedSeed(driver.getCurrentUrl());
			mobileCrawl.setCrawlDate(Calendar.getInstance().getTime());
			System.out.println("Checking for errors");
			mobileCrawl.setDetected400(content.matches("(?s).*\\b400\\b.*"));
			mobileCrawl.setDetected401(content.matches("(?s).*\\b401\\b.*"));
			mobileCrawl.setDetected402(content.matches("(?s).*\\b402\\b.*"));
			mobileCrawl.setDetected403(content.matches("(?s).*\\b403\\b.*"));
			mobileCrawl.setDetected404(content.matches("(?s).*\\b404\\b.*"));
			mobileCrawl.setDetected500(content.matches("(?s).*\\b500\\b.*"));
			mobileCrawl.setDetected501(content.matches("(?s).*\\b501\\b.*"));
			mobileCrawl.setDetected502(content.matches("(?s).*\\b502\\b.*"));
			mobileCrawl.setDetected503(content.matches("(?s).*\\b503\\b.*"));
			
			driver.quit();
//			TimeUnit.SECONDS.sleep(30);
			
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
