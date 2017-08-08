package global;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.google.maps.GeoApiContext;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import crawling.discovery.html.HttpConfig;
import play.Application;
import play.GlobalSettings;
import play.Logger;


public class Global extends GlobalSettings { 

	private static final String STORAGE_FOLDER = "C:/Workspace/DSStorage";
	private static final String SECONDARY_STORAGE_FOLDER = "E:/DSStorage";
	private final static String CRAWL_STORAGE_FOLDER = Global.STORAGE_FOLDER + "/crawldata";
	private final static String COMBINED_STORAGE_FOLDER = Global.STORAGE_FOLDER + "/combined";
	private final static String REPORTS_STORAGE_FOLDER = Global.STORAGE_FOLDER + "/reports";
	private final static String SECONDARY_CRAWL_STORAGE_FOLDER = Global.SECONDARY_STORAGE_FOLDER + "/crawldata";
	private final static String SECONDARY_COMBINED_STORAGE_FOLDER = Global.SECONDARY_STORAGE_FOLDER + "/combined";
	private final static String SECONDARY_REPORTS_STORAGE_FOLDER = Global.SECONDARY_STORAGE_FOLDER + "/reports";
	private final static String INPUT_FOLDER = "C:/Workspace/DSStorage/in";
	private final static String WEBSITE_LIST_INPUT_FOLDER= "C:/Workspace/DSStorage/in/websitelist";
	private final static int LARGE_FILE_THRESHOLD = 500 * 1000;	//~ .5MB
	
	private static final String DEFAULT_MOBILE_USER_AGENT_STRING = "Mozilla/5.0 (iPhone; CPU iPhone OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5376e Safari/8536.25";
	private final static String DEFAULT_USER_AGENT_STRING = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.96 Safari/537.36";
	
	private final static String OLD_LOGS_FOLDER = "logs/old";
	
	
	private ActorSystem crawlingSystem; 
	private ActorRef crawlingListener;
	private ActorRef crawlingMaster;
	
	private ActorSystem snifferSystem;
	private ActorRef snifferListener;
	private ActorRef snifferMaster;
	
	private static boolean useProxy = true;
	private static String proxyUrl = "52.38.91.30";
	private static int proxyPort = 80;

//	private static String proxyUrl = "97.77.104.22";
//	private static int proxyPort = 3128;
	
	private static final Date STALE_DATE;
	static {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, - 2);
		STALE_DATE = calendar.getTime();
	}
	
	private static final Date SALESFORCE_LAST_UPDATED;
	static {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2017, 7, 5);
		SALESFORCE_LAST_UPDATED = calendar.getTime();
	}
	
	private static GeoApiContext placesContext = new GeoApiContext().setApiKey("AIzaSyD3GEnaHTMZPNdfd1kdWtu61rxkaBEghsw");
	
	public HttpConfig getHttpConfig(){
		HttpConfig config = new HttpConfig();
		config.setUseProxy(useProxy());
		config.setProxyAddress(getProxyUrl());
		config.setProxyPort(getProxyPort());
		return config;
	}
	
	public void onStart(Application app) {
		System.out.println("in startup");
		System.setProperty("webdriver.chrome.driver", "./chromedriver.exe");
//		Connection connection = DB.getConnection(); 
//		try {
////			System.out.println("Refreshing schema");
////			SiteInformationDAO.refreshSchema(connection);
////			SiteSummaryDAO.refreshSchema(connection);
////			PageInformationDAO.refreshSchema(connection);
//			connection.close();
//		} catch (SQLException e) {
//			Logger.error("Failed to refresh schema");
//			System.out.println("Failed to refresh schema");
//			System.out.println(e);
//		}
		
	}
	
	public void onStop(Application app) {
        Logger.info("Application shutdown...");
        Logger.info(app.toString());
        
    }
	
	
	
	public ActorSystem getSnifferSystem() {
		return snifferSystem;
	}

	public ActorRef getSnifferListener() {
		return snifferListener;
	}

	public ActorRef getSnifferMaster() {
		return snifferMaster;
	}

	public ActorSystem getCrawlingSystem() {
		return crawlingSystem;
	}

	public ActorRef getCrawlingListener() {
		return crawlingListener;
	}

	public ActorRef getCrawlingMaster() {
		return crawlingMaster;
	}
	
	public static boolean useProxy() {
		return useProxy;
	}

	public static void setUseProxy(boolean useProxy) {
		Global.useProxy = useProxy;
	}

	public static String getProxyUrl() {
		return proxyUrl;
	}

	public static void setProxyUrl(String proxyUrl) {
		Global.proxyUrl = proxyUrl;
	}

	public static int getProxyPort() {
		return proxyPort;
	}

	public static void setProxyPort(int proxyPort) {
		Global.proxyPort = proxyPort;
	}

	public static GeoApiContext getPlacesContext() {
		return placesContext;
	}
	

	public static String getStorageFolder() {
		return STORAGE_FOLDER;
	}

	public static String getSecondaryStorageFolder() {
		return SECONDARY_STORAGE_FOLDER;
	}

	public static String getCrawlStorageFolder() {
		return CRAWL_STORAGE_FOLDER;
	}
	
	public static String getTodaysCrawlStorageFolder() {
		return CRAWL_STORAGE_FOLDER + "/" + new SimpleDateFormat("MM-dd-yyyy").format(new java.util.Date());
	}

	public static String getCombinedStorageFolder() {
		return COMBINED_STORAGE_FOLDER;
	}

	public static String getReportsStorageFolder() {
		return REPORTS_STORAGE_FOLDER;
	}

	public static String getSecondaryCrawlStorageFolder() {
		return SECONDARY_CRAWL_STORAGE_FOLDER;
	}

	public static String getSecondaryCombinedStorageFolder() {
		return SECONDARY_COMBINED_STORAGE_FOLDER;
	}

	public static String getSecondaryReportsStorageFolder() {
		return SECONDARY_REPORTS_STORAGE_FOLDER;
	}

	public static int getLargeFileThreshold() {
		return LARGE_FILE_THRESHOLD;
	}

	public static String getDefaultMobileUserAgentString() {
		return DEFAULT_MOBILE_USER_AGENT_STRING;
	}

	public static String getDefaultUserAgentString() {
		return DEFAULT_USER_AGENT_STRING;
	}

	public static String getOldLogsFolder() {
		return OLD_LOGS_FOLDER;
	}

	public static Date getStaleDate() {
		return STALE_DATE;
	}
	public static String getInputFolder() {
		return INPUT_FOLDER;
	}

	public static String getWebsiteListInputFolder() {
		return WEBSITE_LIST_INPUT_FOLDER;
	}
	
	public static Date getSalesforceLastUpdated() {
		return SALESFORCE_LAST_UPDATED;
	}




	public enum HomepageAction {
		NO_ACTION,						//Do Nothing
		IGNORE,							//Do Nothing but erase the suggestedHomepage 
		MARK_FOR_CLOSING, 				//Site isn't there anymore
		MARK_FOR_REVIEW, 				//Need to look into it more
		ACCEPT, 						//Minor change, like prepending www. 
		ACCEPT_AND_MARK_CHANGE, 		//Major change, like http://www.davidraganford.com/ -> http://www.perrygaford.com/ 
		NEW_SITE 						//Likely also needs a new Dealer
	}

}
