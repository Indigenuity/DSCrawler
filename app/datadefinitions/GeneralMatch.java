package datadefinitions;

import java.util.HashSet;
import java.util.Set;


public enum GeneralMatch implements StringMatch{
	
	//************ Credit Applications	
	DEALER_COM_CREDIT_APP					("Dealer.com Credit application", 						"dealer.com/SecureFinancingGateway", "https://www.kengarffvw.com/financing/application.htm"),
	DEALER_COM_CREDIT_APP_2					("Dealer.com's 2nd credit application", 				"SecureFinanceDriverGateway", "https://www.bmwrochester.com/get-financing.htm"),
	BUYATOYOTA_COM_CREDIT_APP				("Credit application through buyatoyota.com", 			"buyatoyota.com/page/creditapplication", "http://cobbcountytoyota.com/Credit_Application"),
	DEALERTRACK_EBUSINESS					("Likely is just credit applications", 					"ebusiness.dealertrack.com/Suite", "https://www.liatoyotaofcolonie.com/get-pre-approved-in-seconds.htm"),
	ROUTE_ONE								("RouteOne does financing, desking, and contracting", 	"routeone.net", "https://www.hamertoyota.com/apply-for-credit.htm"),
	DEALER_CENTRIC_CREDIT_APP				("DealerCentric's credit application (eLendSolutions)",	"dealercentric.com/app-templates/paymentpromise/application", "http://www.vannyorktoyota.com/credit-application"),
	DEALER_ON_CREDIT_APP_LINK				("This is the usual link to DealerOn's credit app.", 	"preapproved.aspx", "http://www.westchestertoyota.com/finance.aspx"),
	DEALER_ON_CREDIT_APP					("This is the actual credit app for dealeron", 			"dealeron.com/preapproved", "http://www.westchestertoyota.com/finance.aspx"),
	DEALER_TRACK_CREDIT_APP					("DealerTrack's credit application", 					"dealertrack.com/consumerweb/app_loan", "http://www.stokesbrowntoyotahiltonhead.com/credit-application/"),
	NCCI_CREDIT								("NCCI Credit", "nccicredit", ""),
	EZAPP									("NCCDirect's Credit app", "ezapp"),
	FINANCE_CONNECT							("Finance Connect Credit App", "financeconnect.com"),
	DEALER_INSPIRE_CREDIT_APP				("DealerInspire's creddit app", "dealerinspire.com/application"),
	DEALER_700								("700Dealer.com is the website for 700 Credit Apps", "700dealer", ""),
	QUICK_QUALIFY							("700 Credit quick credit check", "quickqualify"),
	CREDIT_700								("700 Credit", "700credit"),
	SECURE_CLIENT_FORMS						("Secure Client Forms (not exclusively for credit apps", "secureclientforms.com"),
	DEALERTRACK_FINANCE						("Dealertrack financing app", "dealertrack.com/consumerweb", ""),
    MOTORWEBS_FINANCE						("MotorWebs financing app", "secure.motorwebs.com/default.aspx?", ""),
    CDK_FINANCE								("CDK's credit app for cobalt sites", "motorplacedealersecureforms.cdk.com"),
    DEALER_EPROCESS_CREDIT_APP				("DealerEProcess Credit App", "dealereprocess.com/virtualassistant", ""),
    DEALERTRACK_FINANCE_SECONDARY			("DealerTrack's other credit app", "ebusiness.dealertrack.com", ""),
    AUTO_REVO_CREDIT_APP					("Auto Revo Credit App pdf", "autorevo.com/images/creditapp.pdf", ""),
    QUICK_APPLICATION						("700 Credit quick credit tool", "quickapplication"),
    CBC_QUICK_APP							("CBC's Quick Credit App", "cbc/quick_app"),
    STRATHCOM_CREDIT_APP					("Strathcom's Credit App", "https://secure.strathcom.com/"),
	
    
	////************* Dubious Credit App Matches
	AUTOSMART								("CUDirect's AutoSmart", "autosmart"),
	NCC										("NCCDirect General Match", "nccdirect"),
	CBC										("General Match for CBC", "cbc"),
	CUDL									("General Match for CUDL", "cudl"),
	CUDIRECT								("General Match for CUDirect", "cudirect"),
	AUTO_PREMIER							("General Match for Auto Premier(sic)", "autopremier"),
	ECREDIT									("CBC's eCredit online", "ecredit"),
	EQUALIFY								("CBC's eQualify online", "equalify"),
	EVAULT									("CBC's compliance vault", "evault"),
	ELEND									("eLend, which affiliates with MANY programs, particularly CBC", "elend", "https://blog.cbcecredit.com/2015/03/get-prequalified-in-seconds-officials-from-elend-solutions-recently-unveiled-an-upgrade-to-the-companys-creditplus-solution-thats-being-dubbed/"),
	
	
	
	//*************  Credit Checking
	CREDITVUE								("NCCDirect credit check tool", "creditvue"),
	TRADEVUE								("NCCDirect trade credit tool", "tradevue"),
	
	
	
	//***General Credit Application Matches 		
	
	CREDIT_APPLICATION						("Credit application indicator", "credit application"),
	CREDIT_APPLICATION2						("Credit application indicator", "credit-application"),
	CREDIT_APPLICATION3						("Credit application indicator", "credit_application"),
	CREDIT_APPLICATION4						("Credit application indicator", "credit app"),
	CREDIT_APPLICATION5						("Credit application indicator", "credit-app"),
	CREDIT_APPLICATION6						("Credit application indicator", "credit_app"),
	CREDIT_APPLICATION7						("Credit application indicator", "finance application"),
	CREDIT_APPLICATION8						("Credit application indicator", "finance-application"),
	CREDIT_APPLICATION9						("Credit application indicator", "finance_application"),
	CREDIT_APPLICATION10					("Credit application indicator", "finance app"),
	CREDIT_APPLICATION11					("Credit application indicator", "finance-app"),
	CREDIT_APPLICATION12					("Credit application indicator", "finance_app"),
	CREDIT_APPLICATION13					("Credit application indicator", "apply for credit"),
	CREDIT_APPLICATION14					("Credit application indicator", "get approved"),
	CREDIT_APPLICATION15					("Credit application indicator", "apply now"),
	CREDIT_APPLICATION16					("Credit application indicator", "preapproved"),
	CREDIT_APPLICATION17					("Credit application indicator", "pre-approved"),
	
	
	 //********  CHAT 
     USES_CLIENT_CONNEXION					("Client Connexion", "clientconnexion.com", ""),
     SKYSA									("Skysa", "skysa.com", "Utility bar"),	//Utility bar used on forddirect
     CONTACTATONCE							("Contact At Once", "contactatonce.com", ""),
     ACTIVENGAGE							("ActivEngage", "activengage.com", ""),
     CUTECHAT								("CuteChat", "CuteSoft_Client/CuteChat", ""),
     BOLD_CHAT								("Bold Chat", "boldchat.com", ""),
     
     //******** Texting
     CARCODE_SMS							("Carcode SMS", "carcodesms.com", ""),
     
     //******** Shopping Assistant
     MYCARS									("MyCars", "mycars-toolbar", "Dealer.com Tool"),
     
     
   //********  Analytics and SEO
     VIN_LENS								("Vin Lens", "vinlens.com", ""),
     GOOGLE_ANALYTICS						("Google Analytics", "google-analytics.com", ""),
     GOOGLE_TAG_MANAGER						("Google Tag Manager", "googletagmanager.com", ""),
     INSPECTLET								("Inspectlet", "inspectlet.com", "GA analysis tool"),  	
     
     //******** CDN
     CLICK_MOTIVE							("Click Motive CDN", "assets.clickmotive.com"),
     DEALER_COM_PICTURES					("Dealer.com pictures CDN", "pictures.dealer.com", "Generally for dealers' own pictures, not dealer.com's generic ones"),
     DEALER_COM_IMAGES						("Dealer.com pictures CDN", "images.dealer.com", "Generic images for any/all dealer.com sites"),
     COBALT_NITRA							("Cobalt Nitra", "assets.cobaltnitra.com"),
     
     
     //******* Web Development
     JQUERY									("jQuery", "jquery", ""),
     
     //******* Social 
     GOOGLE_PLUS							("Google+", "plus.google.com", ""),
     PLUS_ONE_BUTTON						("+1 Button", "plusone.js", ""),
     
     
     //******* Other
     DEALER_COM_VERSION_9					("Dealer.com Version 9", "dealer.com/v9", ""),
     DEALER_COM_VERSION_8					("Dealer.com Version 8", "dealer.com/v8", ""),
     
     //******** Consumer Education
     MOTORWEBS_CREDIT_EDUCATOR				("MotorWebs 'understand your credit'", "pa.motorwebs.com", "May just be for Toyota"),
     AUTO_CONSUMER_INFO						("AutoConsumerInfo", "autoconsumerinfo.com"),
     
     DEMDEX									("DemDex", "demdex.com", "Adobe Audience Manager"),	//Adobe Audience Manager -- Audience Profiles -- found in js, not source
     DEALERVIDEOS							("Dealer Videos", "dealervideos.com", ""),		
     OUTSELLCAMPAIGNSTORE					("Outsell Campaign Store", "outsellcampaignstore.com", ""),
     CALL_MEASUREMENT						( "Call Measurement", "callmeasurement.com", ""),
     SHOWROOM_LOGIC							( "Showroom Logic", "showroomlogic.com", ""),
     COLLSERVE								( "Collserve", "collserve.com", ""),
     SPEEDSHIFTMEDIA						("Speedshift Media", "speedshiftmedia.com", ""),
     AKAMAI									("Akamai", "akamai.com", ""),
     YOAST									("Yoast", "yoast", "WordPress SEO plugin"),	
     LOT_LINX								("Lot Linx", "lotlinx.com", ""),
     
     MOTOFUZE								("Moto Fuze", "fzlnk.com", ""),	
     CLICKY									("Clicky", "getclicky.com", ""),	//Analytics tool
     GUBA_GOO_TRACKING						("Guba Goo Tracking", "gubagootracking.com", ""),
     ADD_THIS								("AddThis", "addthis.com", ""),
     E_CARLIST								("eCarList", "ecarlist.com", ""),	//Bought by Dealertrack
     WUFOO_FORMS							("WuFoo Forms", "wufoo.com", ""),
     DEALER_EPROCESS_CHAT					("Dealer eProcess Chat", "dealereprocesschat.com", ""),
     APPNEXUS								("App nexus", "ib.adnxs.com", ""),
     MY_VEHICLE_SITE						("My Vehicle Site", "myvehiclesite.com", ""),
     PURE_CARS								("Pure Cars", "purecars.com", ""),
     FETCHBACK								("Fetchback", "fetchback.com", ""),
     FORCETRAC								("ForceTrac", "forcetrac.com", ""),
     CLOUDFLARE								("Cloudflare", "cloudflare.com", ""),
     SOCIAL_CRM_360							("Social CRM 360", "socialcrm360.com", ""),
     BLACKBOOK_INFORMATION					("Blackbook Information", "blackbookinformation.com", ""),
     AD_ROLL								("Adroll.com", "adroll.com", ""),
     GHOSTERY								("Ghostery", "betrad.com", ""),
     GHOSTERY_SECOND						("Other Ghostery", "evidon.com", ""),
     NAKED_LIME_IMAGES						("Naked Lime Images (WebMaker X", "webmakerx.net", ""),
     
     SHARP_SPRING							("Sharp Spring", "sharpspring.com", ""),
     ADOBE_TAG_MANAGER						("Adobe Tag manager", "adobedtm.com", ""),
     DEALER_CENTRIC							("DealerCentric", "dealercentric.com", ""),
     AUTO_TRADER_PLUGIN						("AutoTrader Plugin", "tradein.autotrader.com/ATPages", ""),
     
     VOICESTAR								("Voice Star", "voicestar.com", ""),
     DEALERSHIP_INTEGRATED_DATA_SOLUTIONS	("Dealership Integrated Data Solutions", "dealershipids.com", ""),
     
     
     
     USES_GOOGLE_AD_SERVICES				("Google Ad Services", "googleadservices.com", ""),
     
     
     GOOGLE_MAPS							("Google Maps", "maps.google.com", ""),
     APPLE_APP								("iOS App link", "itunes.apple.com/us/app", ""),
     ANDROID_APP							("Android App link", "market.android.com", ""),
     POSTS_INSTAGRAM_TO_SITE				("Embedded Instagram", "cdninstagram.com", ""),
     GOOGLE_TRANSLATE						("Google Translate", "translate.google.com", ""),
     YOUTUBE_EMBEDDED						("Embedded YouTube", "youtube.com/embed", ""),
     AMAZON_HOSTING							("Amazon Hosting", "amazonaws.com", ""),
     UPTRACS								("Uptracs", "uptracs.com", ""),
     UNIFLIP								("Uniflip", "uniflip.com", ""),
     MAX_CDN								("Max CDN", "maxcdn.com", ""),
     VIMEO_EMBEDDED							("Embedded Vimeo Player", "player.vimeo.com", ""),
     MALSUP									("Malsup.com", "malsup.com", "Source for Cycle2"),
     BOOTSTRAP_CDN							("Bootstrap CDN", "bootstrapcdn.com", ""),
     WORDPRESS								("Wordpress CMS", "wp-content", "Basically checks for WP theme"),
     DEALER_RATER							("Dealer Rater", "dealerrater.com", ""),
     MB_SPRINTER							("MB Sprinter Dealer", "mbsprinter.com", "They sell Sprinters"),
     MERCEDES_DEALER						("Mercedes Dealer", "mercedesdealer.com", ""),
     ACCORDANT_PIXEL						("Accordant AutoNation Pixel", "go.accmgr.com", "Tracking Pixel by AutoNation"),
     AUTO_NATION							("Auto Nation", "autonation.com", ""),
     REPUTATION								("Reputation.com", "reputation.com", ""),
     BRIGHT_TAG								("The Bright Tag", "thebrighttag.com", ""),
     SITE_ENCORE							("Site Encore", "siteencore.com", ""),
     SCREEN_CRAFTERS						("Screen Crafters", "screencrafters.com", ""),
     C_AND_S_LEADS							("C and S Leads", "candsleads.com", ""),
     PLADOOGLE								("Pladoogle", "pladoogle", ""),
     ZEN_CDN								("Zen CDN", "zencdn.com", ""),
     CAR_NOW								("Car Now", "carnow.com", ""),
     POINT_ROLL								("Point Roll", "pointroll.com", ""),
     SIGHT_MAX								("Sight Max Chat", "sightmaxondemand.com", ""),
     MCAFEE_SECURE							("McAfee Secure Site", "mcafeesecure.com", ""),
     TALENT_NEST							("Talent Nest", "talentnest.com", ""),
     COMM_100								("Comm100 chat", "comm100", ""),
     CHROME_DATA							("Chrome Data", "chromedata.com", ""),
     
     CLICK_TRACKS							("Click Tracks", "clicktracks.com", ""),
     AUCTION_123							("Auction 123", "auction123.com", ""),
     VECHICLE_MALL							("Vehicle Mall CDN", "vehiclemall.com", ""),
     SERVING_SYS							( "Serving-sys", "serving-sys.com", "Maybe a CDN"),
     AUTO_499								("499 Auto financing", "499auto.com", ""),
     VINDICO_SUITE							("Vindico Suite", "vindicosuite.com", ""),
     CARWEEK								("Carweek SEO", "carweek.com", ""),
     DEALER_LEADS							("Dealer Leads SEO", "dealerleads.com", ""),
     OGG_CHAT								("Ogg Chat", "oggchat.com", ""),
     INTELLIPRICE							("Intelliprice", "intelliprice.com", ""),
     OLARK									("Olark chat", "olark.com", ""),
     EBAIT									("Ebait", "ebait.biz", ""),
     MONGOOSE_METRICS						("Mongoose Metrics", "mongoosemetrics.com", ""),
     LIVE_HELP_NOW							("Live Help Now", "livehelpnow.com", ""),
     STAT_COUNTER							("Stat Counter", "statcounter.com", ""),
     OPTIMIZELY								("Optimizely", "optimizely.com", ""),
     APOGEE_INVENT							("Apogee Invent", "apogeeinvent.com", ""),
     DYNAMIC_DRIVE							("Dynamic Drive", "dynamicdrive.com", ""),
     SMG_DEALER								("SMG Dealer by Sokal", "smgdealer.com", "Probably a CDN for Sokal"),
     
     
     GMPS_DEALER 							("GMPS Dealer", "gmpsdealer.com", ""),
     PORSCHE_DEALER 						("Porsche Dealer", "porchedealer.com", ""),
     
     PROOFPOINT_URL_DEFENSE					("Proofpoint's URL defense", "urldefense.proofpoint.com", ""),
     
     RND_INTERACTIVE_ADVERTISEMENTS			("RND Interactive advertisements", "rndinteractive.com/iframe", ""),
     
     DRIVE_DIGITAL_REVIEW_US				("'Review US' by Drive Digital Group", "reputation.drivedigitalgroup.com", ""),
     SLIPSTREAM_TESTIMONIALS				("Slipstream Testimonials iframe", "testimonials.slipstreamauto.com", ""),
     SM360_CHERRY_POPPER					("Solutions Media 360 Cherry Popper", "cherry.sm360.ca", "Awkwardly descriptiond JavaScript widget"),
     FLASH_TALKING							("Flash Talking", "flashtalking", ""),
     DRUPAL									("Drupal", "drupal.org", "Usually means no web provider"),
     A_WEBER								("AWeber", "aweber.com", "Email campaigns"),
     CUSTOMER_LOBBY							("Customer Lobby", "customerlobby.com", ""),
     TOUR_DASH								("Tour Dash", "tourdash.com", ""),
     CARLEAD								("Carlead", "carlead.com", "Coupons maybe"),
     HOME_NET_AUTO							("Home Net Auto", "homenetauto.com", ""),
     INVENTORY_ONLINE						("Inventory Online", "homenetiol.com", ""),
     DOUBLECLICK							("Double Click", "doubleclick.net", ""),
     WEBMAKER_X_MOBILE						("Web Maker X Mobile", "webmakerxmobile.com", "Free service by Reynolds"),
     EVOLIO									("Evolio", "evolio", ""),
     AUTO_123								("Auto 123", "Auto 123", "");
	
	
	
	
     
     
	
	 public final String description;
	 public final String definition;
	 public final String notes;
	 public final Set<StringMatch> offsetMatches = new HashSet<StringMatch>();
	
	 
 	private GeneralMatch(String description, String definition){
		this.description = description;
		this.definition = definition;
		this.notes = "";
	}
	
	private GeneralMatch(String description, String definition, String notes){
		this.description = description;
		this.definition = definition;
		this.notes = notes;
	}
	
	private GeneralMatch(String description, String definition, String notes, Set<StringMatch> offsetMatches){
		this.description = description;
		this.definition = definition;
		this.notes = notes;
		this.offsetMatches.addAll(offsetMatches);
	}
	
	public String getDescription(){
		return this.description;
	}
	
	public String getDefinition() {
		return this.definition;
	}
	
	public String getNotes() { 
		return this.notes;
	}
	public Set<StringMatch> getOffsetMatches(){
		return this.offsetMatches;
	}
	
	
	 public static final Set<GeneralMatch> creditAppMatches = new HashSet<GeneralMatch>();
	 public static final Set<GeneralMatch> creditAppGeneralMatches = new HashSet<GeneralMatch>();
	 static {
		//************ Credit Applications	
		creditAppMatches.add(DEALER_COM_CREDIT_APP);
		creditAppMatches.add(DEALER_COM_CREDIT_APP_2);
		creditAppMatches.add(BUYATOYOTA_COM_CREDIT_APP);
		creditAppMatches.add(DEALERTRACK_EBUSINESS);
		creditAppMatches.add(ROUTE_ONE);
		creditAppMatches.add(DEALER_CENTRIC_CREDIT_APP);
		creditAppMatches.add(DEALER_ON_CREDIT_APP_LINK);
		creditAppMatches.add(DEALER_ON_CREDIT_APP);
		creditAppMatches.add(DEALER_TRACK_CREDIT_APP);
		creditAppMatches.add(NCCI_CREDIT);
		creditAppMatches.add(EZAPP);
		creditAppMatches.add(FINANCE_CONNECT);
		creditAppMatches.add(DEALER_INSPIRE_CREDIT_APP);
		creditAppMatches.add(DEALER_700);
		creditAppMatches.add(QUICK_QUALIFY);
		creditAppMatches.add(CREDIT_700);
		creditAppMatches.add(SECURE_CLIENT_FORMS);
		creditAppMatches.add(DEALERTRACK_FINANCE);
		creditAppMatches.add(MOTORWEBS_FINANCE);
		creditAppMatches.add(CDK_FINANCE);
		creditAppMatches.add(DEALER_EPROCESS_CREDIT_APP);
		creditAppMatches.add(DEALERTRACK_FINANCE_SECONDARY);
		creditAppMatches.add(AUTO_REVO_CREDIT_APP);
		creditAppMatches.add(CREDITVUE);
		creditAppMatches.add(TRADEVUE);
		creditAppMatches.add(QUICK_APPLICATION);
			
		//**************  Possible Matches
		creditAppGeneralMatches.add(CREDIT_APPLICATION);
		creditAppGeneralMatches.add(CREDIT_APPLICATION2);
		creditAppGeneralMatches.add(CREDIT_APPLICATION3);
		creditAppGeneralMatches.add(CREDIT_APPLICATION4);
		creditAppGeneralMatches.add(CREDIT_APPLICATION5);
		creditAppGeneralMatches.add(CREDIT_APPLICATION6);
		creditAppGeneralMatches.add(CREDIT_APPLICATION7);
		creditAppGeneralMatches.add(CREDIT_APPLICATION8);
		creditAppGeneralMatches.add(CREDIT_APPLICATION9);
		creditAppGeneralMatches.add(CREDIT_APPLICATION10);
		creditAppGeneralMatches.add(CREDIT_APPLICATION11);
		creditAppGeneralMatches.add(CREDIT_APPLICATION12);
		creditAppGeneralMatches.add(CREDIT_APPLICATION13);
		creditAppGeneralMatches.add(CREDIT_APPLICATION14);
		creditAppGeneralMatches.add(CREDIT_APPLICATION15);
		creditAppGeneralMatches.add(CREDIT_APPLICATION16);
		creditAppGeneralMatches.add(CREDIT_APPLICATION17);
	 }
	

	
	
}
