package analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.ElementCollection;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import datadefinitions.GeneralMatch;
import datadefinitions.Scheduler;
import datadefinitions.StringExtraction;
import datadefinitions.UrlExtraction;
import datadefinitions.WebProvider;
import analysis.MD.StringExtractionDefinition;
import analysis.MD.StringMatch;
import persistence.ExtractedString;
import persistence.ExtractedUrl;
import persistence.PageInformation;
import play.Logger;
import edu.uci.ics.crawler4j.parser.HtmlParseData;

public class PageAnalyzer {
	
	public final static int LARGE_FILE_THRESHOLD = 500000;
	
	
	public static void analyzeMatches(PageInformation pageInfo, File in) throws IOException {
		if(!in.exists()) {
			throw new IOException("File does not exist for page with path : " + in.getAbsolutePath());
		}
		if(isLargeFile(in)){
			pageInfo.setLargeFile(true);
			return ;
		}
		
		FileInputStream inputStream = new FileInputStream(in.getAbsolutePath());
		
        String text = IOUtils.toString(inputStream);
        inputStream.close();
		
//        System.out.println("wp: " + pageInfo.getPath());
        fillWp(text, pageInfo.getWebProviders());
//        System.out.println("sched: " + pageInfo.getPath());
		fillScheduler(text, pageInfo.getSchedulers());
//		System.out.println("gen: " + pageInfo.getPath());
		fillGeneralMatch(text, pageInfo.getGeneralMatches());
		
		
	}
	
	public static void analyzeStringExtractions(PageInformation pageInfo, File in) throws IOException {
		if(!in.exists()) {
			throw new IOException("File does not exist for page with path : " + in.getAbsolutePath());
		}
		if(isLargeFile(in)){
			pageInfo.setLargeFile(true);
			return ;
		}
		
		FileInputStream inputStream = new FileInputStream(in.getAbsolutePath());
		
        String text = IOUtils.toString(inputStream);
        inputStream.close();
        Document doc = Jsoup.parse(text);
        fillExtractedStrings(text, pageInfo.getExtractedStrings());
		fillExtractedUrls(doc, pageInfo.getExtractedUrls());
		 
		return ;
	}
	
	public static void analyzeStaff(PageInformation pageInfo, File in) throws IOException {
		if(!in.exists()) {
			throw new IOException("File does not exist for page with path : " + in.getAbsolutePath());
		}
		if(isLargeFile(in)){
			pageInfo.setLargeFile(true);
			return ;
		}
		
		FileInputStream inputStream = new FileInputStream(in.getAbsolutePath());
		
        String text = IOUtils.toString(inputStream);
        inputStream.close();
//        pageInfo.setAllStaff(StaffExtractor.extractStaff(text, pageInfo.getWebProviders()));
//		System.out.println("staff size : " + pageInfo.getAllStaff().size());
		return ;
	}
	
	public static boolean isLargeFile(File in) {
		if(in.length() > LARGE_FILE_THRESHOLD){
			return true;
		}
		return false;
	}
	
	private static void extractLinks(PageInformation pageInfo, Document doc) {
		Elements links = doc.select("a[href]");
		for(Element element : links) {
			if(element.attr("href") != null)
				pageInfo.addLink(element.attr("href"));
		}
	}
	
	private static void fillExtractedStrings(String text, List<ExtractedString> extractedStrings){
		for(StringExtraction enumElement : StringExtraction.values()){
			Matcher matcher = MD.EMAIL_ADDRESS.pattern.matcher(text);
			int count = 0;
	    	while (matcher.find()) {
//	    		System.out.println("found count : " + ++count);
	    		ExtractedString item = new ExtractedString(matcher.group(0), enumElement);
	    		if(!extractedStrings.contains(item)){
	    			extractedStrings.add(item);
	    		}
	    	}
		}
	}
	
	private static void fillExtractedUrls(Document doc,  List<ExtractedUrl> extractedUrls) {
		
		for(UrlExtraction enumElement : UrlExtraction.values()){
			Elements links = doc.select("a[href*=" +enumElement.getDefinition() + "]");
			for(Element element : links) {
				if(element.attr("href") != null){
					ExtractedUrl item = new ExtractedUrl(element.attr("href"), enumElement);
					if(!extractedUrls.contains(item)){
						extractedUrls.add(item);
		    		}
				}
			}
		}
	}
	
	private static void fillWp(String text, List<WebProvider> matches) {
		for(WebProvider wp : WebProvider.values()){
			if(text.contains(wp.getDefinition()) && !matches.contains(wp.getDefinition())){
				matches.add(wp);
			}
		}
	}
	
	private static void fillScheduler(String text, List<Scheduler> matches) {
		for(Scheduler sched : Scheduler.values()){
			if(text.contains(sched.getDefinition()) && !matches.contains(sched.getDefinition())){
				matches.add(sched);
			}
		}
	}
	
	private static void fillGeneralMatch(String text, List<GeneralMatch> matches) {
		for(GeneralMatch gm : GeneralMatch.values()){
			if(text.contains(gm.getDefinition()) && !matches.contains(gm.getDefinition())){
//				System.out.println("found general match : " + gm.getName() + ", " + gm.getId());
				matches.add(gm);
			}
		}
	}
	
//	private static void fillWpStats(String text, WebProviderStats wpStats) {
////		System.out.println("filling wpstats");
//		wpStats.DEALER_COM = text.contains(MD.DEALER_COM.definition);
//	    wpStats.FRESH_INPUT = text.contains(MD.FRESH_INPUT.definition);
//	    wpStats.JAZELAUTOS = text.contains(MD.JAZELAUTOS.definition);
//	    wpStats.FORD_DIRECT = text.contains(MD.FORD_DIRECT.definition);
//	    wpStats.CLICK_MOTIVE = text.contains(MD.CLICK_MOTIVE.definition);
//	    wpStats.VINSOLUTIONS = text.contains(MD.VINSOLUTIONS.definition);
//	    wpStats.COBALT = text.contains(MD.COBALT.definition);
//	    wpStats.COBALT_NITRA = text.contains(MD.COBALT_NITRA.definition);
//	    wpStats.COBALT_GROUP = text.contains(MD.COBALT_GROUP.definition);
//	    wpStats.DEALER_ON = text.contains(MD.DEALER_ON.definition);
//	    wpStats.DEALER_ON_SECONDARY = text.contains(MD.DEALER_ON_SECONDARY.definition);
//	    wpStats.AUTO_ONE = text.contains(MD.AUTO_ONE.definition);
//	    wpStats.DEALER_APEX = text.contains(MD.DEALER_APEX.definition);
//	    wpStats.DEALER_CAR_SEARCH = text.contains(MD.DEALER_CAR_SEARCH.definition);
//	    wpStats.DEALERFIRE = text.contains(MD.DEALERFIRE.definition);
//	    wpStats.DEALERINSPIRE = text.contains(MD.DEALERINSPIRE.definition);
//	    wpStats.DEALER_LAB = text.contains(MD.DEALER_LAB.definition);
//	    wpStats.SOKAL_MEDIA_GROUP = text.contains(MD.SOKAL_MEDIA_GROUP.definition);
//	    wpStats.MOTION_FUZE = text.contains(MD.MOTION_FUZE.definition);
//	    wpStats.DEALER_TRACK = text.contains(MD.DEALER_TRACK.definition);
//	    wpStats.DEALER_EPROCESS = text.contains(MD.DEALER_EPROCESS.definition);
//	    wpStats.DEALER_EPROCESS_PRIMARY = text.contains(MD.DEALER_EPROCESS_PRIMARY.definition);
//	    wpStats.DEALER_ZOOM = text.contains(MD.DEALER_ZOOM.definition);
//	    wpStats.DEALER_ZOOM_SECONDARY = text.contains(MD.DEALER_ZOOM_SECONDARY.definition);
//	    wpStats.DOMINION = text.contains(MD.DOMINION.definition);
//	    wpStats.DRIVE_WEBSITE = text.contains(MD.DRIVE_WEBSITE.definition);
//	    wpStats.FUSION_ZONE = text.contains(MD.FUSION_ZONE.definition);
//	    wpStats.INTERACTIVE_360 = text.contains(MD.INTERACTIVE_360.definition);
//	    wpStats.LIQUID_MOTORS = text.contains(MD.LIQUID_MOTORS.definition);
//	    wpStats.NAKED_LIME = text.contains(MD.NAKED_LIME.definition);
//	    wpStats.NAKED_LIME_SECONDARY = text.contains(MD.NAKED_LIME_SECONDARY.definition);
//	    wpStats.POTRATZ = text.contains(MD.POTRATZ.definition);
//	    wpStats.POTRATZ_SECONDARY = text.contains(MD.POTRATZ_SECONDARY.definition);
//	    wpStats.CARS_FOR_SALE = text.contains(MD.CARS_FOR_SALE.definition);
//	    wpStats.CARBASE = text.contains(MD.CARBASE.definition);
//	    wpStats.EBIZ_AUTOS = text.contains(MD.EBIZ_AUTOS.definition);
//	    wpStats.ELEAD_DIGITAL_CRM = text.contains(MD.ELEAD_DIGITAL_CRM.definition);
//	    wpStats.SMART_DEALER_SITES = text.contains(MD.SMART_DEALER_SITES.definition);
//	    wpStats.DIGIGO = text.contains(MD.DIGIGO.definition);
//	    wpStats.WAYNE_REAVES = text.contains(MD.WAYNE_REAVES.definition);
//	    wpStats.AREA_CARS = text.contains(MD.AREA_CARS.definition);
//	    wpStats.GARY_STOCK = text.contains(MD.GARY_STOCK.definition);
//	    wpStats.REYNOLDS = text.contains(MD.REYNOLDS.definition);
//	}
//	
//	private static void fillSchedulerStats(String text, SchedulerStats schedulerStats) {
////		System.out.println("filling scheduler stats");
//		schedulerStats.COBALT_SCHEDULER = text.contains(MD.COBALT_SCHEDULER.definition);
//	    schedulerStats.XTIME_SCHEDULER = text.contains(MD.XTIME_SCHEDULER.definition);
//	    schedulerStats.OTHER_XTIME = text.contains(MD.OTHER_XTIME.definition);
//	    schedulerStats.AUTO_APPOINTMENTS_SCHEDULER = text.contains(MD.AUTO_APPOINTMENTS_SCHEDULER.definition);
//	    schedulerStats.TIME_HIGHWAY_SCHEDULER = text.contains(MD.TIME_HIGHWAY_SCHEDULER.definition);
//	    schedulerStats.VIN_SOLUTION_SCHEDULER = text.contains(MD.VIN_SOLUTION_SCHEDULER.definition);
//	    schedulerStats.MY_VEHICLE_SITE_SCHEDULER = text.contains(MD.MY_VEHICLE_SITE_SCHEDULER.definition);
//	    schedulerStats.TOTAL_CUSTOMER_CONNECT_SCHEDULER = text.contains(MD.TOTAL_CUSTOMER_CONNECT_SCHEDULER.definition);
//	    schedulerStats.DEALER_CONNECTION_SCHEDULER = text.contains(MD.DEALER_CONNECTION_SCHEDULER.definition);
//	    schedulerStats.ADP_SCHEDULER = text.contains(MD.ADP_SCHEDULER.definition);
//	    schedulerStats.ADP_SCHEDULER_BACKUP = text.contains(MD.ADP_SCHEDULER_BACKUP.definition);
//	    schedulerStats.ADP_OLD_SCHEDULER = text.contains(MD.ADP_OLD_SCHEDULER.definition);
//	    schedulerStats.ADP_OLD_SCHEDULER_ALTERNATIVE = text.contains(MD.ADP_OLD_SCHEDULER_ALTERNATIVE.definition);
//	    schedulerStats.SHOPWATCH_SCHEDULER = text.contains(MD.SHOPWATCH_SCHEDULER.definition);
//	    schedulerStats.ACUITY_SCHEDULER = text.contains(MD.ACUITY_SCHEDULER.definition);
//	    schedulerStats.CIMA_SYSTEMS = text.contains(MD.CIMA_SYSTEMS.definition);
//	    schedulerStats.CIMA_SYSTEMS_SECONDARY = text.contains(MD.CIMA_SYSTEMS_SECONDARY.definition);
//	    schedulerStats.UDC_REVOLUTION = text.contains(MD.UDC_REVOLUTION.definition);
//	    schedulerStats.DEALER_SOCKET = text.contains(MD.DEALER_SOCKET.definition);
//	    schedulerStats.DEALER_FX_SCHEDULER = text.contains(MD.DEALER_FX_SCHEDULER.definition);
//	    schedulerStats.SERVICE_BOOK_PRO_SCHEDULER = text.contains(MD.SERVICE_BOOK_PRO_SCHEDULER.definition);
//	    schedulerStats.AD_WORKZ_SCHEDULER = text.contains(MD.AD_WORKZ_SCHEDULER.definition);
//	    schedulerStats.CAR_RESEARCH_SCHEDULER = text.contains(MD.CAR_RESEARCH_SCHEDULER.definition);
//	    schedulerStats.DRIVERSIDE_SCHEDULER = text.contains(MD.DRIVERSIDE_SCHEDULER.definition);
//	    schedulerStats.DEALERMINE_SCHEDULER = text.contains(MD.DEALERMINE_SCHEDULER.definition);
//	    schedulerStats.PBS_SYSTEMS_SCHEDULER = text.contains(MD.PBS_SYSTEMS_SCHEDULER.definition);
//	    schedulerStats.LEAD_RESULT_SCHEDULER = text.contains(MD.LEAD_RESULT_SCHEDULER.definition);
//	    schedulerStats.SCHEDULE_WEB_PRO_SCHEDULER = text.contains(MD.SCHEDULE_WEB_PRO_SCHEDULER.definition);
//	    schedulerStats.REYNOLDS_SCHEDULER = text.contains(MD.REYNOLDS_SCHEDULER.definition);
//	}
//	
//	private static void fillGeneralStats(String text, GeneralStats generalStats) {
////		System.out.println("filling general stats");
//		generalStats.DEALER_COM_POWERED_BY = text.contains(MD.DEALER_COM_POWERED_BY.definition);
//	    generalStats.USES_CLIENT_CONNEXION = text.contains(MD.USES_CLIENT_CONNEXION.definition);
//	    generalStats.VIN_LENS = text.contains(MD.VIN_LENS.definition);
//	    generalStats.SKYSA = text.contains(MD.SKYSA.definition);
//	    generalStats.DEALER_COM_VERSION_9 = text.contains(MD.DEALER_COM_VERSION_9.definition);
//	    generalStats.DEALER_COM_VERSION_8 = text.contains(MD.DEALER_COM_VERSION_8.definition);
//	    generalStats.DEMDEX = text.contains(MD.DEMDEX.definition);
//	    generalStats.CONTACTATONCE = text.contains(MD.CONTACTATONCE.definition);
//	    generalStats.DEALERVIDEOS = text.contains(MD.DEALERVIDEOS.definition);
//	    generalStats.OUTSELLCAMPAIGNSTORE = text.contains(MD.OUTSELLCAMPAIGNSTORE.definition);
//	    generalStats.CALL_MEASUREMENT = text.contains(MD.CALL_MEASUREMENT.definition);
//	    generalStats.SHOWROOM_LOGIC = text.contains(MD.SHOWROOM_LOGIC.definition);
//	    generalStats.COLLSERVE = text.contains(MD.COLLSERVE.definition);
//	    generalStats.SPEEDSHIFTMEDIA = text.contains(MD.SPEEDSHIFTMEDIA.definition);
//	    generalStats.AKAMAI = text.contains(MD.AKAMAI.definition);
//	    generalStats.CUTECHAT = text.contains(MD.CUTECHAT.definition);
//	    generalStats.CARCODE_SMS = text.contains(MD.CARCODE_SMS.definition);
//	    generalStats.YOAST = text.contains(MD.YOAST.definition);
//	    generalStats.LOT_LINX = text.contains(MD.LOT_LINX.definition);
//	    generalStats.INSPECTLET = text.contains(MD.INSPECTLET.definition);
//	    generalStats.MOTOFUZE = text.contains(MD.MOTOFUZE.definition);
//	    generalStats.CLICKY = text.contains(MD.CLICKY.definition);
//	    generalStats.GUBA_GOO_TRACKING = text.contains(MD.GUBA_GOO_TRACKING.definition);
//	    generalStats.ADD_THIS = text.contains(MD.ADD_THIS.definition);
//	    generalStats.E_CARLIST = text.contains(MD.E_CARLIST.definition);
//	    generalStats.WUFOO_FORMS = text.contains(MD.WUFOO_FORMS.definition);
//	    generalStats.DEALER_EPROCESS_CHAT = text.contains(MD.DEALER_EPROCESS_CHAT.definition);
//	    generalStats.APPNEXUS = text.contains(MD.APPNEXUS.definition);
//	    generalStats.MY_VEHICLE_SITE = text.contains(MD.MY_VEHICLE_SITE.definition);
//	    generalStats.PURE_CARS = text.contains(MD.PURE_CARS.definition);
//	    generalStats.FETCHBACK = text.contains(MD.FETCHBACK.definition);
//	    generalStats.FORCETRAC = text.contains(MD.FORCETRAC.definition);
//	    generalStats.CLOUDFLARE = text.contains(MD.CLOUDFLARE.definition);
//	    generalStats.SOCIAL_CRM_360 = text.contains(MD.SOCIAL_CRM_360.definition);
//	    generalStats.BLACKBOOK_INFORMATION = text.contains(MD.BLACKBOOK_INFORMATION.definition);
//	    generalStats.AD_ROLL = text.contains(MD.AD_ROLL.definition);
//	    generalStats.GHOSTERY = text.contains(MD.GHOSTERY.definition);
//	    generalStats.GHOSTERY_SECOND = text.contains(MD.GHOSTERY_SECOND.definition);
//	    generalStats.NAKED_LIME_IMAGES = text.contains(MD.NAKED_LIME_IMAGES.definition);
//	    generalStats.ACTIVE_ENGAGE = text.contains(MD.ACTIVE_ENGAGE.definition);
//	    generalStats.SHARP_SPRING = text.contains(MD.SHARP_SPRING.definition);
//	    generalStats.ADOBE_TAG_MANAGER = text.contains(MD.ADOBE_TAG_MANAGER.definition);
//	    generalStats.DEALER_CENTRIC = text.contains(MD.DEALER_CENTRIC.definition);
//	    generalStats.AUTO_TRADER_PLUGIN = text.contains(MD.AUTO_TRADER_PLUGIN.definition);
//	    generalStats.BOLD_CHAT = text.contains(MD.BOLD_CHAT.definition);
//	    generalStats.VOICESTAR = text.contains(MD.VOICESTAR.definition);
//	    generalStats.DEALERSHIP_INTEGRATED_DATA_SOLUTIONS = text.contains(MD.DEALERSHIP_INTEGRATED_DATA_SOLUTIONS.definition);
//	    
//	    generalStats.HAS_GOOGLE_PLUS = text.contains(MD.HAS_GOOGLE_PLUS.definition);
//	    generalStats.PLUS_ONE_BUTTON = text.contains(MD.PLUS_ONE_BUTTON.definition);
//	    generalStats.USES_GOOGLE_ANALYTICS = text.contains(MD.USES_GOOGLE_ANALYTICS.definition);
//	    generalStats.USES_GOOGLE_AD_SERVICES = text.contains(MD.USES_GOOGLE_AD_SERVICES.definition);
//	    generalStats.GOOGLE_TAG_MANAGER = text.contains(MD.GOOGLE_TAG_MANAGER.definition);
//	    generalStats.JQUERY = text.contains(MD.JQUERY.definition);
//	    generalStats.GOOGLE_MAPS = text.contains(MD.GOOGLE_MAPS.definition);
//	    generalStats.APPLE_APP = text.contains(MD.APPLE_APP.definition);
//	    generalStats.ANDROID_APP = text.contains(MD.ANDROID_APP.definition);
//	    generalStats.POSTS_INSTAGRAM_TO_SITE = text.contains(MD.POSTS_INSTAGRAM_TO_SITE.definition);
//	    generalStats.GOOGLE_TRANSLATE = text.contains(MD.GOOGLE_TRANSLATE.definition);
//	    generalStats.YOUTUBE_EMBEDDED = text.contains(MD.YOUTUBE_EMBEDDED.definition);
//	}
	
}
