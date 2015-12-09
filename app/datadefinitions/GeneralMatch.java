package datadefinitions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public enum GeneralMatch implements StringMatch{
	 
     USES_CLIENT_CONNEXION					(1, "Client Connexion", "clientconnexion.com", ""),
     VIN_LENS								(2, "Vin Lens", "vinlens.com", ""),
     SKYSA									(3, "Skysa", "skysa.com", "Utility bar"),	//Utility bar used on forddirect
     DEALER_COM_VERSION_9					(4, "Dealer.com Version 9", "dealer.com/v9", ""),
     DEALER_COM_VERSION_8					(5, "Dealer.com Version 8", "dealer.com/v8", ""),
     DEMDEX									(6, "DemDex", "demdex.com", "Adobe Audience Manager"),	//Adobe Audience Manager -- Audience Profiles -- found in js, not source
     CONTACTATONCE							(7, "Contact At Once", "contactatonce.com", ""),	//Chat system
     DEALERVIDEOS							(8, "Dealer Videos", "dealervideos.com", ""),		
     OUTSELLCAMPAIGNSTORE					(9, "Outsell Campaign Store", "outsellcampaignstore.com", ""),
     CALL_MEASUREMENT						(10, "Call Measurement", "callmeasurement.com", ""),
     SHOWROOM_LOGIC							(11, "Showroom Logic", "showroomlogic.com", ""),
     COLLSERVE								(12, "Collserve", "collserve.com", ""),
     SPEEDSHIFTMEDIA						(13, "Speedshift Media", "speedshiftmedia.com", ""),
     AKAMAI									(14, "Akamai", "akamai.com", ""),
     CUTECHAT								(15, "CuteChat", "CuteSoft_Client/CuteChat", ""),
     CARCODE_SMS							(16, "Carcode SMS", "carcodesms.com", ""),
     YOAST									(17, "Yoast", "yoast", "WordPress SEO plugin"),	
     LOT_LINX								(18, "Lot Linx", "lotlinx.com", ""),
     INSPECTLET								(19, "Inspectlet", "inspectlet.com", "GA analysis tool"), 	
     MOTOFUZE								(20, "Moto Fuze", "fzlnk.com", ""),	
     CLICKY									(21, "Clicky", "getclicky.com", ""),	//Analytics tool
     GUBA_GOO_TRACKING						(22, "Guba Goo Tracking", "gubagootracking.com", ""),
     ADD_THIS								(23, "AddThis", "addthis.com", ""),
     E_CARLIST								(24, "eCarList", "ecarlist.com", ""),	//Bought by Dealertrack
     WUFOO_FORMS							(25, "WuFoo Forms", "wufoo.com", ""),
     DEALER_EPROCESS_CHAT					(26, "Dealer eProcess Chat", "dealereprocesschat.com", ""),
     APPNEXUS								(27, "App nexus", "ib.adnxs.com", ""),
     MY_VEHICLE_SITE						(28, "My Vehicle Site", "myvehiclesite.com", ""),
     PURE_CARS								(29, "Pure Cars", "purecars.com", ""),
     FETCHBACK								(30, "Fetchback", "fetchback.com", ""),
     FORCETRAC								(31, "ForceTrac", "forcetrac.com", ""),
     CLOUDFLARE								(32, "Cloudflare", "cloudflare.com", ""),
     SOCIAL_CRM_360							(33, "Social CRM 360", "socialcrm360.com", ""),
     BLACKBOOK_INFORMATION					(34, "Blackbook Information", "blackbookinformation.com", ""),
     AD_ROLL								(35, "Adroll.com", "adroll.com", ""),
     GHOSTERY								(36, "Ghostery", "betrad.com", ""),
     GHOSTERY_SECOND						(37, "Other Ghostery", "evidon.com", ""),
     NAKED_LIME_IMAGES						(38, "Naked Lime Images (WebMaker X", "webmakerx.net", ""),
     ACTIVE_ENGAGE							(39, "Active Engage", "activengage.com", ""),
     SHARP_SPRING							(40, "Sharp Spring", "sharpspring.com", ""),
     ADOBE_TAG_MANAGER						(41, "Adobe Tag manager", "adobedtm.com", ""),
     DEALER_CENTRIC							(52, "DealerCentric", "dealercentric.com", ""),
     AUTO_TRADER_PLUGIN						(53, "AutoTrader Plugin", "tradein.autotrader.com/ATPages", ""),
     BOLD_CHAT								(54, "Bold Chat", "boldchat.com", ""),
     VOICESTAR								(55, "Voice Star", "voicestar.com", ""),
     DEALERSHIP_INTEGRATED_DATA_SOLUTIONS	(56, "Dealership Integrated Data Solutions", "dealershipids.com", ""),
     HAS_GOOGLE_PLUS						(57, "Google+", "plus.google.com", ""),
     PLUS_ONE_BUTTON						(58, "+1 Button", "plusone.js", ""),
     USES_GOOGLE_ANALYTICS					(59, "Google Analytics", "google-analytics.com", ""),
     USES_GOOGLE_AD_SERVICES				(60, "Google Ad Services", "googleadservices.com", ""),
     GOOGLE_TAG_MANAGER						(61, "Google Tag Manager", "googletagmanager.com", ""),
     JQUERY									(62, "jQuery", "jquery", ""),
     GOOGLE_MAPS							(63, "Google Maps", "maps.google.com", ""),
     APPLE_APP								(64, "iOS App link", "itunes.apple.com/us/app", ""),
     ANDROID_APP							(65, "Android App link", "market.android.com", ""),
     POSTS_INSTAGRAM_TO_SITE				(66, "Embedded Instagram", "cdninstagram.com", ""),
     GOOGLE_TRANSLATE						(67, "Google Translate", "translate.google.com", ""),
     YOUTUBE_EMBEDDED						(68, "Embedded YouTube", "youtube.com/embed", ""),
     AMAZON_HOSTING							(69, "Amazon Hosting", "amazonaws.com", ""),
     UPTRACS								(70, "Uptracs", "uptracs.com", ""),
     UNIFLIP								(71, "Uniflip", "uniflip.com", ""),
     MAX_CDN								(72, "Max CDN", "maxcdn.com", ""),
     VIMEO_EMBEDDED							(73, "Embedded Vimeo Player", "player.vimeo.com", ""),
     MALSUP									(74, "Malsup.com", "malsup.com", "Source for Cycle2"),
     BOOTSTRAP_CDN							(75, "Bootstrap CDN", "bootstrapcdn.com", ""),
     WORDPRESS								(76, "Wordpress CMS", "wp-content", "Basically checks for WP theme"),
     DEALER_RATER							(77, "Dealer Rater", "dealerrater.com", ""),
     MB_SPRINTER							(78, "MB Sprinter Dealer", "mbsprinter.com", "They sell Sprinters"),
     MERCEDES_DEALER						(79, "Mercedes Dealer", "mercedesdealer.com", ""),
     ACCORDANT_PIXEL						(80, "Accordant AutoNation Pixel", "go.accmgr.com", "Tracking Pixel by AutoNation"),
     AUTO_NATION							(81, "Auto Nation", "autonation.com", ""),
     REPUTATION								(82, "Reputation.com", "reputation.com", ""),
     BRIGHT_TAG								(83, "The Bright Tag", "thebrighttag.com", ""),
     SITE_ENCORE							(84, "Site Encore", "siteencore.com", ""),
     SCREEN_CRAFTERS						(85, "Screen Crafters", "screencrafters.com", ""),
     C_AND_S_LEADS							(86, "C and S Leads", "candsleads.com", ""),
     PLADOOGLE								(87, "Pladoogle", "pladoogle", ""),
     ZEN_CDN								(88, "Zen CDN", "zencdn.com", ""),
     CAR_NOW								(89, "Car Now", "carnow.com", ""),
     POINT_ROLL								(90, "Point Roll", "pointroll.com", ""),
     SIGHT_MAX								(91, "Sight Max Chat", "sightmaxondemand.com", ""),
     MCAFEE_SECURE							(92, "McAfee Secure Site", "mcafeesecure.com", ""),
     TALENT_NEST							(93, "Talent Nest", "talentnest.com", ""),
     COMM_100								(94, "Comm100 chat", "comm100", ""),
     CHROME_DATA							(95, "Chrome Data", "chromedata.com", ""),
     NCCI_CREDIT							(96, "NCCI Credit", "nccicredit.com", ""),
     CLICK_TRACKS							(97, "Click Tracks", "clicktracks.com", ""),
     AUCTION_123							(98, "Auction 123", "auction123.com", ""),
     VECHICLE_MALL							(99, "Vehicle Mall CDN", "vehiclemall.com", ""),
     SERVING_SYS							(100, "Serving-sys", "serving-sys.com", "Maybe a CDN"),
     AUTO_499								(101, "499 Auto financing", "499auto.com", ""),
     VINDICO_SUITE							(102, "Vindico Suite", "vindicosuite.com", ""),
     CARWEEK								(103, "Carweek SEO", "carweek.com", ""),
     DEALER_LEADS							(104, "Dealer Leads SEO", "dealerleads.com", ""),
     OGG_CHAT								(105, "Ogg Chat", "oggchat.com", ""),
     INTELLIPRICE							(106, "Intelliprice", "intelliprice.com", ""),
     OLARK									(107, "Olark chat", "olark.com", ""),
     EBAIT									(108, "Ebait", "ebait.biz", ""),
     MONGOOSE_METRICS						(109, "Mongoose Metrics", "mongoosemetrics.com", ""),
     LIVE_HELP_NOW							(110, "Live Help Now", "livehelpnow.com", ""),
     STAT_COUNTER							(111, "Stat Counter", "statcounter.com", ""),
     OPTIMIZELY								(112, "Optimizely", "optimizely.com", ""),
     APOGEE_INVENT							(113, "Apogee Invent", "apogeeinvent.com", ""),
     DYNAMIC_DRIVE							(114, "Dynamic Drive", "dynamicdrive.com", ""),
     SMG_DEALER								(115, "SMG Dealer by Sokal", "smgdealer.com", "Probably a CDN for Sokal"),
     DEALERTRACK_FINANCE					(116, "Dealertrack financing app", "dealertrack.com/consumerweb", ""),
     MOTORWEBS_FINANCE						(117, "MotorWebs financing app", "secure.motorwebs.com/default.aspx?", ""),
     MOTORWEBS_CREDIT_EDUCATOR				(118, "MotorWebs 'understand your credit'", "pa.motorwebs.com", "May just be for Toyota"),
     GMPS_DEALER 							(119, "GMPS Dealer", "gmpsdealer.com", ""),
     PORSCHE_DEALER 						(120, "Porsche Dealer", "porchedealer.com", ""),
     AUTO_REVO_CREDIT_APP					(121, "Auto Revo Credit App pdf", "autorevo.com/images/creditapp.pdf", ""),
     PROOFPOINT_URL_DEFENSE					(122, "Proofpoint's URL defense", "urldefense.proofpoint.com", ""),
     DEALER_EPROCESS_CREDIT_APP				(123, "DealerEProcess Credit App", "dealereprocess.com/virtualassistant", ""),
     DEALERTRACK_FINANCE_SECONDARY			(124, "DealerTrack's other credit app", "ebusiness.dealertrack.com", ""),
     RND_INTERACTIVE_ADVERTISEMENTS			(125, "RND Interactive advertisements", "rndinteractive.com/iframe", ""),
     DEALER_700								(126, "700 Dealer", "700dealer.com", ""),
     DRIVE_DIGITAL_REVIEW_US				(127, "'Review US' by Drive Digital Group", "reputation.drivedigitalgroup.com", ""),
     SLIPSTREAM_TESTIMONIALS				(128, "Slipstream Testimonials iframe", "testimonials.slipstreamauto.com", ""),
     SM360_CHERRY_POPPER					(129, "Solutions Media 360 Cherry Popper", "cherry.sm360.ca", "Awkwardly descriptiond JavaScript widget"),
     FLASH_TALKING							(130, "Flash Talking", "flashtalking", ""),
     DRUPAL									(131, "Drupal", "drupal.org", "Usually means no web provider"),
     A_WEBER								(132, "AWeber", "aweber.com", "Email campaigns"),
     CUSTOMER_LOBBY							(133, "Customer Lobby", "customerlobby.com", ""),
     TOUR_DASH								(134, "Tour Dash", "tourdash.com", ""),
     CARLEAD								(135, "Carlead", "carlead.com", "Coupons maybe"),
     HOME_NET_AUTO							(136, "Home Net Auto", "homenetauto.com", ""),
     INVENTORY_ONLINE						(137, "Inventory Online", "homenetiol.com", ""),
     DOUBLECLICK							(138, "Double Click", "doubleclick.net", ""),
     WEBMAKER_X_MOBILE						(139, "Web Maker X Mobile", "webmakerxmobile.com", "Free service by Reynolds");
     
     
	
     public final int id;
	 public final String description;
	 public final String definition;
	 public final String notes;
	 public final Set<StringMatch> offsetMatches = new HashSet<StringMatch>();
	
	
	private final static Map<Integer, GeneralMatch> enumIds = new HashMap<Integer, GeneralMatch>();
	static {
		for(GeneralMatch gm : GeneralMatch.values()){
			enumIds.put(gm.getId(), gm);
		}
	}
	
	public static GeneralMatch getTypeFromId(Integer id) {
		return enumIds.get(id);
	}
	
	private GeneralMatch(int id, String description, String definition, String notes){
		this.id = id;
		this.description = description;
		this.definition = definition;
		this.notes = notes;
	}
	
	private GeneralMatch(int id, String description, String definition, String notes, Set<StringMatch> offsetMatches){
		this.id = id;
		this.description = description;
		this.definition = definition;
		this.notes = notes;
		this.offsetMatches.addAll(offsetMatches);
	}
	
	public GeneralMatch getType(Integer id) {
		return getTypeFromId(id); 
	}
	
	public int getId() {
		return this.id;
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
	
}
