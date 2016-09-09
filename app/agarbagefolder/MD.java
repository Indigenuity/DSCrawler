package agarbagefolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

//MD = Match Dictionary.  
public final class MD {

	public static class StringMatch {
		public final String description;
		public final String definition;
		
		public StringMatch(String description, String definition) {
			this.description = description;
			this.definition = definition;
		}
	}
	
	public static class StringExtractionDefinition {
		
		public final String description;
		public final Pattern pattern;
		
		public StringExtractionDefinition (String description, String regex) {
			this.description = description;
			this.pattern = Pattern.compile(regex);
		}
	}
	
	//Web Providers
	public final static StringMatch DEALER_COM = new StringMatch("DEALER_COM", "dealer.com");
    public final static StringMatch FRESH_INPUT = new StringMatch("FRESH_INPUT", "freshinput.com");
    public final static StringMatch JAZELAUTOS = new StringMatch("JAZELAUTOS", "jazelautos.com");
    public final static StringMatch FORD_DIRECT = new StringMatch("FORD_DIRECT", "forddirect");
    public final static StringMatch CLICK_MOTIVE = new StringMatch("CLICK_MOTIVE", "clickmotive.com");
    public final static StringMatch VINSOLUTIONS = new StringMatch("VINSOLUTIONS", "vinsolutions.com");
    public final static StringMatch COBALT = new StringMatch("COBALT", "cobalt.com");
    public final static StringMatch COBALT_NITRA = new StringMatch("COBALT_NITRA", "cobaltnitra.com");
    public final static StringMatch COBALT_GROUP = new StringMatch("COBALT_GROUP", "cobaltgroup.com");
    public final static StringMatch DEALER_ON = new StringMatch("DEALER_ON", "dealeron.com");
    public final static StringMatch DEALER_ON_SECONDARY = new StringMatch("DEALER_ON_SECONDARY", "dlron.us");	//Maybe just a CDN URL
    public final static StringMatch AUTO_ONE = new StringMatch("AUTO_ONE", "autoonemedia.com");
    public final static StringMatch DEALER_APEX = new StringMatch("DEALER_APEX", "dealerapex.com");
    public final static StringMatch DEALER_CAR_SEARCH = new StringMatch("DEALER_CAR_SEARCH", "dealercarsearch.com");
    public final static StringMatch DEALERFIRE = new StringMatch("DEALERFIRE", "dealerfire.com");
    public final static StringMatch DEALERINSPIRE = new StringMatch("DEALERINSPIRE", "dealerinspire.com");
    public final static StringMatch DEALER_LAB = new StringMatch("DEALER_LAB", "dealerlab.com");		//Maybe subsidiary of dealer eprocess
    public final static StringMatch SOKAL_MEDIA_GROUP = new StringMatch("SOKAL_MEDIA_GROUP", "sokalmediagroup.com");
    public final static StringMatch MOTION_FUZE = new StringMatch("MOTION_FUZE", "motionfuze.com");	//Maybe subsidiary of Sokal
    public final static StringMatch DEALER_TRACK = new StringMatch("DEALER_TRACK", "dealertrack.com");
    public final static StringMatch DEALER_EPROCESS = new StringMatch("DEALER_EPROCESS", "dealereprocess.com");
    public final static StringMatch DEALER_EPROCESS_PRIMARY = new StringMatch("DEALER_EPROCESS_PRIMARY", "www.dealereprocess.com");	//Providers like dealer lab use cdn.dealereprocess.com
    public final static StringMatch DEALER_ZOOM = new StringMatch("DEALER_ZOOM", "dealerzoom.com");
    public final static StringMatch DEALER_ZOOM_SECONDARY = new StringMatch("DEALER_ZOOM_SECONDARY", "dealerzoom.wufoo.com");
    public final static StringMatch DOMINION = new StringMatch("DOMINION", "drivedominion.com");
    public final static StringMatch DRIVE_WEBSITE = new StringMatch("DRIVE_WEBSITE", "drivewebsite.com");	//Likely part of Dominion
    public final static StringMatch FUSION_ZONE = new StringMatch("FUSION_ZONE", "fzautomotive.com");
    public final static StringMatch INTERACTIVE_360 = new StringMatch("INTERACTIVE_360", "interactive360.com");
    public final static StringMatch LIQUID_MOTORS = new StringMatch("LIQUID_MOTORS", "liquidmotors.com");
    public final static StringMatch NAKED_LIME = new StringMatch("NAKED_LIME", "nlmkt.com");
    public final static StringMatch NAKED_LIME_SECONDARY = new StringMatch("NAKED_LIME_SECONDARY", "nakedlime.com");
    public final static StringMatch POTRATZ = new StringMatch("POTRATZ", "potratzdev.com");
    public final static StringMatch POTRATZ_SECONDARY = new StringMatch("POTRATZ_SECONDARY", "exclusivelyautomotive.com");
    public final static StringMatch CARS_FOR_SALE = new StringMatch("CARS_FOR_SALE", "carsforsale.com");
    public final static StringMatch CARBASE = new StringMatch("CARBASE", "carbase.com");
    public final static StringMatch EBIZ_AUTOS = new StringMatch("EBIZ_AUTOS", "ebizautos.com");
    public final static StringMatch ELEAD_DIGITAL_CRM = new StringMatch("ELEAD_DIGITAL_CRM", "elead-crm.com");	//Uses Dealer On
    public final static StringMatch SMART_DEALER_SITES = new StringMatch("SMART_DEALER_SITES", "smartdealersites.com");
    public final static StringMatch DIGIGO = new StringMatch("DIGIGO", "digigo.com");
    public final static StringMatch WAYNE_REAVES = new StringMatch("WAYNE_REAVES", "waynereaves.com");
    public final static StringMatch AREA_CARS = new StringMatch("AREA_CARS", "areacars.com");
    public final static StringMatch GARY_STOCK = new StringMatch("GARY_STOCK", "gstockco.com");
    public final static StringMatch REYNOLDS = new StringMatch("REYNOLDS", "reyrey.com");
    public final static StringMatch DEALER_DIRECT = new StringMatch("DEALER_DIRECT", "Dealer Direct");
    
    public final static StringMatch GMPS_DEALER = new StringMatch("GMPS_DEALER", "gmpsdealer.com");
    public final static StringMatch PORSCHE_DEALER = new StringMatch("PORSCHE_DEALER", "porchedealer.com");
    
	
    //Schedulers
    public final static StringMatch COBALT_SCHEDULER = new StringMatch("COBALT_SCHEDULER", "service.xw.gm.com");
    public final static StringMatch XTIME_SCHEDULER = new StringMatch("XTIME_SCHEDULER", "consumer.xtime.com");
    public final static StringMatch OTHER_XTIME = new StringMatch("OTHER_XTIME", "xtime.com");
    public final static StringMatch AUTO_APPOINTMENTS_SCHEDULER = new StringMatch("AUTO_APPOINTMENTS_SCHEDULER", "autoappointments.com");	//Likely Dominion
    public final static StringMatch TIME_HIGHWAY_SCHEDULER = new StringMatch("TIME_HIGHWAY_SCHEDULER", "timehighway.com");
    public final static StringMatch VIN_SOLUTION_SCHEDULER = new StringMatch("VIN_SOLUTION_SCHEDULER", "SAMSPanel");	//Should get new grabber
    public final static StringMatch MY_VEHICLE_SITE_SCHEDULER = new StringMatch("MY_VEHICLE_SITE_SCHEDULER", "myvehiclesite.com/appt");	//AutoLoop?
    public final static StringMatch TOTAL_CUSTOMER_CONNECT_SCHEDULER = new StringMatch("TOTAL_CUSTOMER_CONNECT_SCHEDULER", "totalcustomerconnect.com");
    public final static StringMatch DEALER_CONNECTION_SCHEDULER = new StringMatch("DEALER_CONNECTION_SCHEDULER", "dealerconnection.com/service-appointment");	//A bit dubious
    public final static StringMatch ADP_SCHEDULER = new StringMatch("ADP_SCHEDULER", "adpserviceedge.com/appt");
    public final static StringMatch ADP_SCHEDULER_BACKUP = new StringMatch("ADP_SCHEDULER_BACKUP", "adpserviceedge.com");
    public final static StringMatch ADP_OLD_SCHEDULER = new StringMatch("ADP_OLD_SCHEDULER", "adponlineservice.com");
    public final static StringMatch ADP_OLD_SCHEDULER_ALTERNATIVE = new StringMatch("ADP_OLD_SCHEDULER_ALTERNATIVE", "dealerinventoryonline.com");
    public final static StringMatch SHOPWATCH_SCHEDULER = new StringMatch("SHOPWATCH_SCHEDULER", "sdilink.net");	//Service Dynamics
    public final static StringMatch ACUITY_SCHEDULER = new StringMatch("ACUITY_SCHEDULER", "acuityscheduling.com");
    public final static StringMatch CIMA_SYSTEMS = new StringMatch("CIMA_SYSTEMS", "cimasystems.biz");	//link from site to this url
    public final static StringMatch CIMA_SYSTEMS_SECONDARY = new StringMatch("CIMA_SYSTEMS_SECONDARY", "cimasystems.net");	//scheduler has link to here
    public final static StringMatch UDC_REVOLUTION = new StringMatch("UDC_REVOLUTION", "udcnet.com");
    public final static StringMatch DEALER_SOCKET = new StringMatch("DEALER_SOCKET", "my.dealersocket.com");
    public final static StringMatch DEALER_FX_SCHEDULER = new StringMatch("DEALER_FX_SCHEDULER", "dealer-fx.com");
    public final static StringMatch SERVICE_BOOK_PRO_SCHEDULER = new StringMatch("SERVICE_BOOK_PRO_SCHEDULER", "servicebookpro.com"); 	//Very similar to adpserviceedge
    public final static StringMatch AD_WORKZ_SCHEDULER = new StringMatch("AD_WORKZ_SCHEDULER", "adworkz.com");
    public final static StringMatch CAR_RESEARCH_SCHEDULER = new StringMatch("CAR_RESEARCH_SCHEDULER", "car-research.com");
    public final static StringMatch DRIVERSIDE_SCHEDULER = new StringMatch("DRIVERSIDE_SCHEDULER", "driverside.com");
    public final static StringMatch DEALERMINE_SCHEDULER = new StringMatch("DEALERMINE_SCHEDULER", "dealermine.net");
    public final static StringMatch PBS_SYSTEMS_SCHEDULER = new StringMatch("PBS_SYSTEMS_SCHEDULER", "pbssystems.com");
    public final static StringMatch LEAD_RESULT_SCHEDULER = new StringMatch("LEAD_RESULT_SCHEDULER", "leadresult.com");
    public final static StringMatch SCHEDULE_WEB_PRO_SCHEDULER = new StringMatch("SCHEDULE_WEB_PRO_SCHEDULER", "schedulemyservice.com");
    public final static StringMatch REYNOLDS_SCHEDULER = new StringMatch("REYNOLDS_SCHEDULER", "reyrey.net");
    
  //WP details or other tools
    public final static StringMatch DEALER_COM_POWERED_BY = new StringMatch("DEALER_COM_POWERED_BY", "powered by dealer.com");
    public final static StringMatch USES_CLIENT_CONNEXION = new StringMatch("USES_CLIENT_CONNEXION", "clientconnexion.com");
    public final static StringMatch VIN_LENS = new StringMatch("VIN_LENS", "vinlens.com");
    public final static StringMatch SKYSA = new StringMatch("SKYSA", "skysa.com");	//Utility bar used on forddirect
    public final static StringMatch DEALER_COM_VERSION_9 = new StringMatch("DEALER_COM_VERSION_9", "dealer.com/v9");
    public final static StringMatch DEALER_COM_VERSION_8 = new StringMatch("DEALER_COM_VERSION_8", "dealer.com/v8");
    public final static StringMatch DEMDEX = new StringMatch("DEMDEX", "demdex.com");	//Adobe Audience Manager -- Audience Profiles -- found in js, not source
    public final static StringMatch CONTACTATONCE = new StringMatch("CONTACTATONCE", "contactatonce.com");	//Chat system
    public final static StringMatch DEALERVIDEOS = new StringMatch("DEALERVIDEOS", "dealervideos.com");		
    public final static StringMatch OUTSELLCAMPAIGNSTORE = new StringMatch("OUTSELLCAMPAIGNSTORE", "outsellcampaignstore.com");
    public final static StringMatch CALL_MEASUREMENT = new StringMatch("CALL_MEASUREMENT", "callmeasurement.com");
    public final static StringMatch SHOWROOM_LOGIC = new StringMatch("SHOWROOM_LOGIC", "showroomlogic.com");
    public final static StringMatch COLLSERVE = new StringMatch("COLLSERVE", "collserve.com");
    public final static StringMatch SPEEDSHIFTMEDIA = new StringMatch("SPEEDSHIFTMEDIA", "speedshiftmedia.com");
    public final static StringMatch AKAMAI = new StringMatch("AKAMAI", "akamai.com");
    public final static StringMatch CUTECHAT = new StringMatch("CUTECHAT", "CuteSoft_Client/CuteChat");
    public final static StringMatch CARCODE_SMS = new StringMatch("CARCODE_SMS", "carcodesms.com");
    public final static StringMatch YOAST = new StringMatch("YOAST", "yoast");	//WordPress SEO plugin
    public final static StringMatch LOT_LINX = new StringMatch("LOT_LINX", "lotlinx.com");
    public final static StringMatch INSPECTLET = new StringMatch("INSPECTLET", "inspectlet.com"); 	//GA analysis tool
    public final static StringMatch MOTOFUZE = new StringMatch("MOTOFUZE", "fzlnk.com");	
    public final static StringMatch CLICKY = new StringMatch("CLICKY", "getclicky.com");	//Analytics tool
    public final static StringMatch GUBA_GOO_TRACKING = new StringMatch("GUBA_GOO_TRACKING", "gubagootracking.com");
    public final static StringMatch ADD_THIS = new StringMatch("ADD_THIS", "addthis.com");
    public final static StringMatch E_CARLIST = new StringMatch("E_CARLIST", "ecarlist.com");	//Bought by Dealertrack
    public final static StringMatch WUFOO_FORMS = new StringMatch("WUFOO_FORMS", "wufoo.com");
    public final static StringMatch DEALER_EPROCESS_CHAT = new StringMatch("DEALER_EPROCESS_CHAT", "dealereprocesschat.com");
    public final static StringMatch APPNEXUS = new StringMatch("APPNEXUS", "ib.adnxs.com");
    public final static StringMatch MY_VEHICLE_SITE = new StringMatch("MY_VEHICLE_SITE", "myvehiclesite.com");
    public final static StringMatch PURE_CARS = new StringMatch("PURE_CARS", "purecars.com");
    public final static StringMatch FETCHBACK = new StringMatch("FETCHBACK", "fetchback.com");
    public final static StringMatch FORCETRAC = new StringMatch("FORCETRAC", "forcetrac.com");
    public final static StringMatch CLOUDFLARE = new StringMatch("CLOUDFLARE", "cloudflare.com");
    public final static StringMatch SOCIAL_CRM_360 = new StringMatch("SOCIAL_CRM_360", "socialcrm360.com");
    public final static StringMatch BLACKBOOK_INFORMATION = new StringMatch("BLACKBOOK_INFORMATION", "blackbookinformation.com");
    public final static StringMatch AD_ROLL = new StringMatch("AD_ROLL", "adroll.com");
    public final static StringMatch GHOSTERY = new StringMatch("GHOSTERY", "betrad.com");
    public final static StringMatch GHOSTERY_SECOND = new StringMatch("GHOSTERY_SECOND", "evidon.com");
    public final static StringMatch NAKED_LIME_IMAGES = new StringMatch("NAKED_LIME_IMAGES", "webmakerx.net");
    public final static StringMatch ACTIVE_ENGAGE = new StringMatch("ACTIVE_ENGAGE", "activengage.com");
    public final static StringMatch SHARP_SPRING = new StringMatch("SHARP_SPRING", "sharpspring.com");
    public final static StringMatch ADOBE_TAG_MANAGER = new StringMatch("ADOBE_TAG_MANAGER", "adobedtm.com");
    public final static StringMatch DEALER_CENTRIC = new StringMatch("DEALER_CENTRIC", "dealercentric.com");
    public final static StringMatch AUTO_TRADER_PLUGIN = new StringMatch("AUTO_TRADER_PLUGIN", "tradein.autotrader.com/ATPages");
    public final static StringMatch BOLD_CHAT = new StringMatch("BOLD_CHAT", "boldchat.com");
    public final static StringMatch VOICESTAR = new StringMatch("VOICESTAR", "voicestar.com");
    public final static StringMatch DEALERSHIP_INTEGRATED_DATA_SOLUTIONS = new StringMatch("DEALERSHIP_INTEGRATED_DATA_SOLUTIONS", "dealershipids.com");
    
    //General details
    public final static StringMatch HAS_GOOGLE_PLUS = new StringMatch("HAS_GOOGLE_PLUS", "plus.google.com");
    public final static StringMatch PLUS_ONE_BUTTON = new StringMatch("PLUS_ONE_BUTTON", "plusone.js");
    public final static StringMatch USES_GOOGLE_ANALYTICS = new StringMatch("USES_GOOGLE_ANALYTICS", "google-analytics.com");
    public final static StringMatch USES_GOOGLE_AD_SERVICES = new StringMatch("USES_GOOGLE_AD_SERVICES", "googleadservices.com");
    public final static StringMatch GOOGLE_TAG_MANAGER = new StringMatch("GOOGLE_TAG_MANAGER", "googletagmanager.com");
    public final static StringMatch JQUERY = new StringMatch("JQUERY", "jquery");
    public final static StringMatch GOOGLE_MAPS = new StringMatch("GOOGLE_MAPS", "maps.google.com");
    public final static StringMatch APPLE_APP = new StringMatch("APPLE_APP", "itunes.apple.com/us/app");
    public final static StringMatch ANDROID_APP = new StringMatch("ANDROID_APP", "market.android.com");
    public final static StringMatch POSTS_INSTAGRAM_TO_SITE = new StringMatch("POSTS_INSTAGRAM_TO_SITE", "cdninstagram.com");
    public final static StringMatch GOOGLE_TRANSLATE = new StringMatch("GOOGLE_TRANSLATE", "translate.google.com");
    public final static StringMatch YOUTUBE_EMBEDDED = new StringMatch("YOUTUBE_EMBEDDED", "youtube.com/embed");
    
  //URL Extraction Matches
  	public static final StringMatch FACEBOOK = new StringMatch("FACEBOOK", "facebook.com");
  	public static final StringMatch GOOGLE_PLUS = new StringMatch("GOOGLE_PLUS", "plus.google.com");
  	public static final StringMatch TWITTER = new StringMatch("TWITTER", "twitter.com");
  	public static final StringMatch YOUTUBE = new StringMatch("YOUTUBE", "youtube.com");
  	public static final StringMatch FLICKER = new StringMatch("FLICKER", "flickr.com");
  	public static final StringMatch INSTAGRAM = new StringMatch("INSTAGRAM", "instagram.com");
  	public static final StringMatch YELP = new StringMatch("YELP", "yelp.com");
  	public static final StringMatch LINKED_IN = new StringMatch("LINKED_IN", "linkedin.com");
  	public static final StringMatch PINTEREST = new StringMatch("PINTEREST", "pinterest.com");
  	public static final StringMatch FOURSQUARE = new StringMatch("FOURSQUARE", "foursquare.com");
      
	//Regex extraction definitions
  	public static final StringExtractionDefinition EMAIL_ADDRESS = new StringExtractionDefinition("EMAIL_ADDRESS", "[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})");
	public static final StringExtractionDefinition GOOGLE_ANALYTICS_CODE = new StringExtractionDefinition("GOOGLE_ANALYTICS_CODE", "\\bUA-\\d{4,10}-\\d{1,4}\\b");
	
	
	
	
	/*
	 * 
	 * 
	 * 
	 * Gather lists of these properties for ease of use and categorization
	 * 
	 * 
	 * 
	 * 
	 */
	public final static List<StringMatch> WEB_PROVIDER_MATCH_DEFINITIONS;
	static {
        List<StringMatch> matchList = new ArrayList<StringMatch>();
        //WPs
        matchList.add(DEALER_COM);
        matchList.add(FRESH_INPUT);
        matchList.add(JAZELAUTOS);
        matchList.add(FORD_DIRECT);
        matchList.add(CLICK_MOTIVE);
        matchList.add(VINSOLUTIONS);
        matchList.add(COBALT);
        matchList.add(COBALT_NITRA);
        matchList.add(COBALT_GROUP);
        matchList.add(DEALER_ON);
        matchList.add(DEALER_ON_SECONDARY);
        matchList.add(AUTO_ONE);
        matchList.add(DEALER_APEX);
        matchList.add(DEALER_CAR_SEARCH);
        matchList.add(DEALERFIRE);
        matchList.add(DEALERINSPIRE);
        matchList.add(DEALER_LAB);
        matchList.add(SOKAL_MEDIA_GROUP);
        matchList.add(MOTION_FUZE);
        matchList.add(DEALER_TRACK);
        matchList.add(DEALER_EPROCESS);
        matchList.add(DEALER_EPROCESS_PRIMARY);
        matchList.add(DEALER_ZOOM);
        matchList.add(DEALER_ZOOM_SECONDARY);
        matchList.add(DOMINION);
        matchList.add(DRIVE_WEBSITE);
        matchList.add(FUSION_ZONE);
        matchList.add(INTERACTIVE_360);
        matchList.add(LIQUID_MOTORS);
        matchList.add(NAKED_LIME);
        matchList.add(NAKED_LIME_SECONDARY);
        matchList.add(POTRATZ);
        matchList.add(POTRATZ_SECONDARY);
        matchList.add(CARS_FOR_SALE);
        matchList.add(CARBASE);
        matchList.add(EBIZ_AUTOS);
        matchList.add(ELEAD_DIGITAL_CRM);
        matchList.add(SMART_DEALER_SITES);
        matchList.add(DIGIGO);
        matchList.add(WAYNE_REAVES);
        matchList.add(AREA_CARS);
        matchList.add(GARY_STOCK);
        matchList.add(REYNOLDS);
        
        WEB_PROVIDER_MATCH_DEFINITIONS = Collections.unmodifiableList(matchList);
    }
        
	public final static List<StringMatch> SCHEDULER_MATCH_DEFINITIONS;
	static {
		List<StringMatch> matchList = new ArrayList<StringMatch>();
        
      //Schedulers
        matchList.add(COBALT_SCHEDULER);
        matchList.add(XTIME_SCHEDULER);
        matchList.add(OTHER_XTIME);
        matchList.add(AUTO_APPOINTMENTS_SCHEDULER);
        matchList.add(TIME_HIGHWAY_SCHEDULER);
        matchList.add(VIN_SOLUTION_SCHEDULER);
        matchList.add(MY_VEHICLE_SITE_SCHEDULER);
        matchList.add(TOTAL_CUSTOMER_CONNECT_SCHEDULER);
        matchList.add(DEALER_CONNECTION_SCHEDULER);
        matchList.add(ADP_SCHEDULER);
        matchList.add(ADP_SCHEDULER_BACKUP);
        matchList.add(ADP_OLD_SCHEDULER);
        matchList.add(ADP_OLD_SCHEDULER_ALTERNATIVE);
        matchList.add(SHOPWATCH_SCHEDULER);
        matchList.add(ACUITY_SCHEDULER);
        matchList.add(CIMA_SYSTEMS);
        matchList.add(CIMA_SYSTEMS_SECONDARY);
        matchList.add(UDC_REVOLUTION);
        matchList.add(DEALER_SOCKET);
        matchList.add(DEALER_FX_SCHEDULER);
        matchList.add(SERVICE_BOOK_PRO_SCHEDULER);
        matchList.add(AD_WORKZ_SCHEDULER);
        matchList.add(CAR_RESEARCH_SCHEDULER);
        matchList.add(DRIVERSIDE_SCHEDULER);
        matchList.add(DEALERMINE_SCHEDULER);
        matchList.add(PBS_SYSTEMS_SCHEDULER);
        matchList.add(LEAD_RESULT_SCHEDULER);
        matchList.add(SCHEDULE_WEB_PRO_SCHEDULER);
        matchList.add(REYNOLDS_SCHEDULER);
        
        SCHEDULER_MATCH_DEFINITIONS = Collections.unmodifiableList(matchList);
	}
	
	public final static List<StringMatch> GENERAL_MATCH_DEFINITIONS;
	static {
		List<StringMatch> matchList = new ArrayList<StringMatch>();
        
      //WP details or other tools
		matchList.add(DEALER_COM_POWERED_BY);
        matchList.add(USES_CLIENT_CONNEXION);
        matchList.add(VIN_LENS);
        matchList.add(SKYSA);
        matchList.add(DEALER_COM_VERSION_9);
        matchList.add(DEALER_COM_VERSION_8);
        matchList.add(DEMDEX);
        matchList.add(CONTACTATONCE);
        matchList.add(DEALERVIDEOS);
        matchList.add(OUTSELLCAMPAIGNSTORE);
        matchList.add(CALL_MEASUREMENT);
        matchList.add(SHOWROOM_LOGIC);
        matchList.add(COLLSERVE);
        matchList.add(SPEEDSHIFTMEDIA);
        matchList.add(AKAMAI);
        matchList.add(CUTECHAT);
        matchList.add(CARCODE_SMS);
        matchList.add(YOAST);
        matchList.add(LOT_LINX);
        matchList.add(INSPECTLET);
        matchList.add(MOTOFUZE);
        matchList.add(CLICKY);
        matchList.add(GUBA_GOO_TRACKING);
        matchList.add(ADD_THIS);
        matchList.add(E_CARLIST);
        matchList.add(WUFOO_FORMS);
        matchList.add(DEALER_EPROCESS_CHAT);
        matchList.add(APPNEXUS);
        matchList.add(MY_VEHICLE_SITE);
        matchList.add(PURE_CARS);
        matchList.add(FETCHBACK);
        matchList.add(FORCETRAC);
        matchList.add(CLOUDFLARE);
        matchList.add(SOCIAL_CRM_360);
        matchList.add(BLACKBOOK_INFORMATION);
        matchList.add(AD_ROLL);
        matchList.add(GHOSTERY);
        matchList.add(GHOSTERY_SECOND);
        matchList.add(NAKED_LIME_IMAGES);
        matchList.add(ACTIVE_ENGAGE);
        matchList.add(SHARP_SPRING);
        matchList.add(ADOBE_TAG_MANAGER);
        matchList.add(DEALER_CENTRIC);
        matchList.add(AUTO_TRADER_PLUGIN);
        matchList.add(BOLD_CHAT);
        matchList.add(VOICESTAR);
        matchList.add(DEALERSHIP_INTEGRATED_DATA_SOLUTIONS);
        
        //General details
        matchList.add(HAS_GOOGLE_PLUS);
        matchList.add(PLUS_ONE_BUTTON);
        matchList.add(USES_GOOGLE_ANALYTICS);
        matchList.add(USES_GOOGLE_AD_SERVICES);
        matchList.add(GOOGLE_TAG_MANAGER);
        matchList.add(JQUERY);
        matchList.add(GOOGLE_MAPS);
        matchList.add(APPLE_APP);
        matchList.add(ANDROID_APP);
        matchList.add(POSTS_INSTAGRAM_TO_SITE);
        matchList.add(GOOGLE_TRANSLATE);
        matchList.add(YOUTUBE_EMBEDDED);
        
        GENERAL_MATCH_DEFINITIONS = Collections.unmodifiableList(matchList);
    }
	
	public final static List<StringMatch> ALL_MATCH_DEFINITIONS;
	static {
		List<StringMatch> matchList = new ArrayList<StringMatch>();
		matchList.addAll(WEB_PROVIDER_MATCH_DEFINITIONS);
		matchList.addAll(SCHEDULER_MATCH_DEFINITIONS);
		matchList.addAll(GENERAL_MATCH_DEFINITIONS);
		
		ALL_MATCH_DEFINITIONS = Collections.unmodifiableList(matchList);
	}
	
	public final static List<StringExtractionDefinition> STRING_EXTRACTION_DEFINITIONS;
	static {
        List<StringExtractionDefinition> matchList= new ArrayList<StringExtractionDefinition>();
        matchList.add(EMAIL_ADDRESS);
        matchList.add(GOOGLE_ANALYTICS_CODE);
        
        STRING_EXTRACTION_DEFINITIONS = Collections.unmodifiableList(matchList);
    }
	
	public final static List<StringMatch> URL_EXTRACTION_DEFINITIONS;
	static {
        List<StringMatch> matchList = new ArrayList<StringMatch>();
       
        matchList.add(FACEBOOK);
        matchList.add(GOOGLE_PLUS);
        matchList.add(TWITTER);
        matchList.add(YOUTUBE);
        matchList.add(FLICKER);
        matchList.add(INSTAGRAM);
        matchList.add(YELP);
        matchList.add(LINKED_IN);
        matchList.add(PINTEREST);
        matchList.add(FOURSQUARE);
        
        URL_EXTRACTION_DEFINITIONS = Collections.unmodifiableList(matchList);
    }
	
	
}
