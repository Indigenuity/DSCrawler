package global;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import com.google.maps.GeoApiContext;

import dao.SiteInformationDAO;
import dao.SiteSummaryDAO;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import async.Asyncleton;
import async.crawling.CrawlingListener;
import async.crawling.CrawlingMaster;
import async.sniffer.SnifferListener;
import async.sniffer.SnifferMaster;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.db.DB;


public class Global extends GlobalSettings {

	public static final String STORAGE_FOLDER = "E:/DSStorage";
	public static final String SECONDARY_STORAGE_FOLDER = "E:/DSStorage";
	public final static String CRAWL_STORAGE_FOLDER = Global.STORAGE_FOLDER + "/crawldata";
	public final static String COMBINED_STORAGE_FOLDER = Global.STORAGE_FOLDER + "/combined";
	public final static String REPORTS_STORAGE_FOLDER = Global.STORAGE_FOLDER + "/reports";
	public final static String SECONDARY_CRAWL_STORAGE_FOLDER = Global.SECONDARY_STORAGE_FOLDER + "/crawldata";
	public final static String SECONDARY_COMBINED_STORAGE_FOLDER = Global.SECONDARY_STORAGE_FOLDER + "/combined";
	public final static String SECONDARY_REPORTS_STORAGE_FOLDER = Global.SECONDARY_STORAGE_FOLDER + "/reports";
	public final static int LARGE_FILE_THRESHOLD = 500 * 1000;	//~ .5MB
	
	public static final String DEFAULT_MOBILE_USER_AGENT_STRING = "Mozilla/5.0 (iPhone; CPU iPhone OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5376e Safari/8536.25";
	public final static String DEFAULT_USER_AGENT_STRING = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.90 Safari/537.36";
	
	public final static String OLD_LOGS_FOLDER = "logs/old";
	
	private ActorSystem crawlingSystem; 
	private ActorRef crawlingListener;
	private ActorRef crawlingMaster;
	
	private ActorSystem snifferSystem;
	private ActorRef snifferListener;
	private ActorRef snifferMaster;
	
	private static boolean useProxy = true;
	private static String proxyUrl = "54.69.89.6";
	private static int proxyPort = 8888;

	public static final Date STALE_DATE;
	static {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, - 1);
		STALE_DATE = calendar.getTime();
	}
	
	private static GeoApiContext placesContext = new GeoApiContext().setApiKey("AIzaSyD3GEnaHTMZPNdfd1kdWtu61rxkaBEghsw");
	
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
