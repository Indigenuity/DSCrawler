package datadefinitions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public enum WebProvider implements StringMatch {
	
	NONE					(0, "Unknown Web Provider", "Intentionally non-matching regex", "This signifies that the web crawler could not find a known web provider for this site"),
	DEALER_COM				(1, "Dealer.com", "dealer.com", "Not a strict match"),
	DEALER_COM_POWERED_BY	(2, "Powered by Dealer.com", "powered by dealer.com", "A stricter match than simply 'dealer.com'"),
	DEALER_COM_CONF			(3, "Dealer.com", "Website by Dealer.com", ""),
    FRESH_INPUT				(4, "Fresh Input", "freshinput.com", ""),
    JAZELAUTOS				(5, "Jazel Auto", "jazelauto.com", ""),
    JAZEL_CONF				(6, "Jazel Auto", "Jazel.Orchard.Themes", ""),
    FORD_DIRECT				(7, "Ford Direct", "forddirect", ""),
    CLICK_MOTIVE			(8, "Click Motive", "clickmotive.com", ""),
    VIN_SOLUTIONS			(9, "Vin Solutions", "vinsolutions.com", ""),
    VIN_SOLUTIONS_CONF		(10, "Vin Solutions", "Website Powered By <a href=\"http://www.vinsolutions.com", ""),
    VIN_SOLUTIONS_CONF2		(11, "Vin Solutions", "Powered By Vinsolutions.com", ""),
    COBALT					(12, "Cobalt", "cobalt.com", ""),
    COBALT_NITRA			(13, "Cobalt Nitra", "cobaltnitra.com", ""),
    COBALT_GROUP			(14, "Cobalt Group", "cobaltgroup.com", ""),
    DEALER_ON				(15, "Dealer On", "dealeron.com", ""),
    DEALER_ON_SECONDARY		(16, "Dealer On", "dlron.us", ""),	//Maybe just a CDN URL
    DEALER_ON_CONF			(17, "Dealer On", "cdn.dlron.us/assets/logos", ""),
    AUTO_ONE				(18, "Auto One Media", "autoonemedia.com", ""),
    AUTO_ONE_CONF			(19, "Auto One Media", "AutoONE", ""),
    DEALER_APEX				(20, "Dealer Apex", "dealerapex.com", ""),
    DEALER_APEX_CONF		(21, "Dealer Apex", "Dealer Web Site Design by <span id=\"txtDealerApex\">Dealer Apex", ""),
    DEALER_CAR_SEARCH		(22, "Dealer Car Search", "dealercarsearch.com", ""),
    DEALERFIRE				(23, "DealerFire", "dealerfire.com", ""),
    DEALERFIRE_CONF			(24, "DealerFire", "by DealerFire", ""),
    DEALER_INSPIRE			(25, "Dealer Inspire", "dealerinspire.com", ""),
    DEALER_INSPIRE_CONF		(26, "Dealer Inspire", "Visit http://www.dealerinspire.com to Inspire your visitors", ""),
    DEALER_LAB				(27, "Dealer Lab", "dealerlab.com", "Subsidiary of Dealer eProcess"),
    DEALER_LAB_CONF			(28, "Dealer Lab", "dealerlab_logo.png", ""),
    DEALER_LAB_CONF2		(29, "Dealer Lab", "dealerlab_logo-white.png", ""),
    SOKAL_MEDIA_GROUP		(30, "Sokal Media Group", "sokalmediagroup.com", ""),
    SOKAL_CONF				(31, "Sokal Media Group", "smglogo", ""),
    SOKAL_SECONDARY			(32, "Sokal subsidiary", "smgdealer.com", ""),
    MOTION_FUZE				(33, "Motion Fuze", "motionfuze.com", "Maybe subsidiary of Sokal"),
    DEALER_TRACK			(34, "Dealer Track", "dealertrack.com", ""),
	DEALER_TRACK_CONF		(35, "Dealer Track", "dealertrack.com/portal/dealers/digital-marketing", ""),
    DEALER_EPROCESS			(36, "Dealer eProcess", "dealereprocess.com", ""),
    DEALER_EPROCESS_PRIMARY	(37, "Dealer eProcess", "www.dealereprocess.com", ""),	//Providers like dealer lab use cdn.dealereprocess.com
    DEALER_EPROCESS_CONF	(38, "Dealer eProcess", "DealerEProcess_logo-white.png", ""),
    DEALER_EPROCESS_CONF2	(39, "Dealer eProcess", "DealerEProcess_logo.png", ""),
    DEALER_EPROCESS_SUBSID	(40, "Dealer EProcess", "Powered By Dealer e-Process", ""),
    DEALER_ZOOM				(41, "Dealer Zoom", "dealerzoom.com", ""),
    DEALER_ZOOM_SECONDARY	(42, "Dealer Zoom", "dealerzoom.wufoo.com", ""),
    DEALER_ZOOM_CONF		(43, "Dealer Zoom", "DZ_png.png", ""),
    DEALER_ZOOM_CONF2		(44, "Dealer Zoom", "Website services provided by <a href=\"http://www.dealerzoom.com", ""),
    DOMINION				(45, "Dominion", "Dominion Dealer Solutions", ""),
    DRIVE_WEBSITE			(46, "Drive Website", "drivewebsite.com", "Likely subsidiary of Dominion"),
    FUSION_ZONE				(47, "Fusion Zone", "fzautomotive.com", ""),
    FUSION_ZONE_CONF		(48, "Fusion Zone", "fusionZONE Automotive, Inc.", ""),
    INTERACTIVE_360			(49, "Interactive 360", "interactive360.com", ""),
    LIQUID_MOTORS			(50, "Liquid Motors", "liquidmotors.com", ""),
    LIQUID_MOTORS_CONF		(51, "Liquid Motors", "Website Powered by Liquid Motors Inc", ""),
    LIQUID_MOTORS_CONF2		(52, "Liquid Motors", "POWERED BY LIQUID MOTORS", ""),
    LIQUID_MOTORS_CONF3		(53, "Liquid Motors", "Powered by Liquid Motors", ""),
    NAKED_LIME				(54, "Naked Lime", "nlmkt.com", ""),
    NAKED_LIME_SECONDARY	(55, "Naked Lime", "nakedlime.com", ""),
    NAKED_LIME_CONF			(56, "Powered by Naked Lime", "Powered by Naked Lime Marketing", ""),
    POTRATZ					(57, "Potratz", "potratzdev.com", ""),
    POTRATZ_SECONDARY		(58, "Potratz", "exclusivelyautomotive.com", ""),
    POTRATZ_CONFIRM			(59, "Potratz", "potratz.png", ""),
    CARS_FOR_SALE			(60, "CarsForSale.com", "carsforsale.com", ""),
    CARS_FOR_SALE_CONF		(61, "Carsforsale.com", "Powered by <a target=\"blank\" href=\"http://www.carsforsale.com", ""),
    CARBASE					(62, "Carbase", "carbase.com", ""),
    EBIZ_AUTOS				(63, "EBiz Autos", "ebizautos.com", ""),
    ELEAD_DIGITAL_CRM		(64, "eLEAD Digital CRM", "elead-crm.com", "Uses Dealer On resources"),	
    ELEAD_CONF				(65, "eLEAD Digital", "Elead Digital", ""),
    ELEAD_CONF2				(66, "eLEAD Digital", "eLEAD Digital", ""),
    SMART_DEALER_SITES		(67, "Smart Dealer Sites", "smartdealersites.com", ""),
    DIGIGO					(68, "DigiGo", "digigo.com", ""),
    WAYNE_REAVES			(69, "Wayne Reaves", "waynereaves.com", ""),
    AREA_CARS				(70, "Area Cars", "areacars.com", ""),
    GARY_STOCK				(71, "The Gary Stock Company", "The Gary Stock Company", ""),
    REYNOLDS				(72, "Reynolds", "reyrey.com", ""),
    DEALER_DIRECT			(73, "Dealer Direct", "Dealer Direct", ""),
    DEALER_DIRECT_CONF		(74, "Dealer Direct", "Dealer Direct LLC", ""),
    
	SEARCH_OPTICS			(75, "Search Optics", "searchoptics.com", ""),
	DEALER_X				(76, "Dealer X", "cardealermarketing.com", ""),
	DEALER_X_SECONDARY		(77, "Dealer X secondary", "DealerX.com", "this url forwards to other url"),
	MOTORTRAK				(78, "MotorTrak", "motortrak.com", ""),
	MOTORTRAK_CONF			(79, "MotorTrak", "Developed by Motortrak", ""),
	STERLING_EMARKETING		(80, "Sterling eMarketing", "sterlingemarketing.com", ""),
	GS_MARKETING			(81, "GS Marketing", "GSMarketing", ""),
	GS_MARKETING_SECONDARY	(82, "GS Marketing secondary", "GSM_Logo.png", ""),
	AUTO_REVO				(83, "AutoRevo", "autorevo-powersites.com", ""),
	AUTO_REVO_SECONDARY		(84, "AutoRevo Secondary", "autorevo.com", ""),
	SECURITY_LABS			(85, "Security Labs design", "securitylabs.com", ""),
	AUTOCON_X				(86, "Autocon X", "autoconx.com", ""),
	MOTOR_WEBS				(87, "Motor Webs", "motorwebs.com", ""),
	PIXEL_MOTION			(88, "Pixel Motion", "pixelmotion.com", ""),
	PIXEL_MOTION_CONFIRM	(89, "Definitely Pixel Motion", "pixelmotion.png", ""),
	STRING_AUTOMOTIVE		(90, "String Automotive", "stringautomotive.com", ""),
	STRING_AUTOMOTIVE_SEC	(91, "String Automotive Secondary", "stringcontent.com", ""),
	WORLD_DEALER			(92, "World Dealer", "worlddealer.net", ""),
	WORLD_DEALER_SEC		(93, "World Dealer Secondary", "wdautos.com", ""),
	DEALER_PEAK				(94, "Dealer Peak", "dealerpeak.com", ""),
	LA_DOLCE				(95, "La Dolce Video", "ladolcevideo.com", ""),
	SLIPSTREAM_AUTO			(96, "Slipstream Auto", "slipstreamauto.com", ""),
	LK_PRO					(97, "LK Pro", "lkpro.com", ""),
	GIOVATTO				(98, "Giovatto", "giovatto.com", ""),
	DESIGN_HOUSE_5			(99, "Design House 5", "designhouse5.com", ""),
	FOX_DEALER_INTERACTIVE	(100, "Fox Dealer Interactive", "foxdealerinteractive.com", ""),
	WEB_MAKER_X				(101, "Web Maker X", "webmakerx.net", "Naked Lime"),
	ROGEE					(102, "Rogee", "rogee.com", ""),
	ROGEE_SECONDARY			(103, "Rogee Secondary", "rogeeauto.com", ""),
	AUTOPARK_DRIVE			(104, "AutoPark Drive (Dealer Serve)", "autoparkdrive.com", "Owned by Rogee"),
	DRIVING_FORCE			(105, "Driving Force", "drivingforceauto.com", ""),
	ALL_AUTO_NETWORK		(106, "All Auto Network", "allautonetwork.com", ""),
	IDEAS_360				(107, "360 Ideas", "360ideas.com", ""),
	DEALER_HD				(108, "Dealer HD (Dealer eProcess", "dealerhd.com", "Dealer eprocess"),
	AUTO_JINI				(109, "Auto Jini", "autojini.com", ""),
	AUTO_FUSION				(110, "Auto Fusion", "autofusion.com", ""),
	PROMAX					(111, "ProMax Unlimited", "promaxunlimited.com", ""),
	REMORA					(112, "Remora", "remorainc.com", ""),
	REMORA_SECONDARY		(113, "Remora Secondary", "remora.co", ""),
	BLU_SOLUTIONS			(114, "Blu Solutions", "blusolutions.com", "Sometimes just SEO"),
	I_PIT_CREW				(115, "iPitCrew", "ipitcrew.com", "Maybe owned by blu solutions"),
	SINGLE_THROW			(116, "Single Throw", "singlethrow.com", ""),
	IO_COM					(117, "I/O com", "i-ocom.com", ""),
	SPEEDSHIFT_MEDIA		(118, "Speedshift Media", "speedshiftmedia.com", ""),
	EDGE_ONE				(119, "Edge One Technologies", "edgeonetechnologies.com", ""),
	CHROMA_CARS				(120, "Chroma Cars", "chromacars.com", ""),
	LTTF					(121, "LTTF", "lttf.com", ""),
	LTTF_CONF				(123, "LTTF", "Copyright © 2015 LTTF", "This match must be refreshed every year, and is only for convenience"),
	LTTF_SECONDARY			(124, "LTTF", "LTTF", "A catch-all for the LTTF sites that don't actually link"),
	BIZ_BOOST				(125, "Biz Boost Marketing", "bizboostmarketconsulting.com", ""),
	DEALER_DNA				(126, "Dealer DNA", "dealerdna.com", ""),
	IPS_SOLUTIONS			(127, "IPS Solutions", "ipssolutions.com", ""),
	DEALER_TRACTION			(128, "Dealer Traction", "dealertraction.com", ""),
	QMASS					(129, "QMaSS", "QMaSS", "maybe defunct"),
	IPUBLISHERS				(130, "IPublishers", "ipublishers.com", ""),
	WEB_PRO_JOE				(131, "Web Pro Joe", "webprojoe.com", ""),
	VISION_AMP				(132, "Vision Amp", "visionamp.com", ""),
	ARI_NET					(133, "ARI Network Services", "arinet.com", ""),
	DEALER_IMPACT			(134, "Dealer Impact Systems", "dealerimpactsystems.com", ""),
	AUTO_WALL				(135, "Auto Wall/Gratis Tech", "gratistech.com", ""),
	JKR_ADVERTISING			(136, "JKR Automotive", "jkradvertising.com", ""),
	DRIVE_DIGITAL_GROUP		(137, "Drive Digital Group", "drivedigitalgroup.com", ""),
	AUTO_DEALER_WEBSITES	(138, "Auto Dealer Websites", "autodealerwebsites.com", ""),
	VERSION2				(139, "Version2 Marketing", "version2group.com", ""),
	DEALER_HOSTS			(140, "Dealer Hosts", "dealerhosts.com", ""),
	MANNING_AUTOMOTIVE		(141, "Manning Automotive Marketing", "manningi.com", ""),
	SERITAS_PIPELINE		(142, "Seritas Pipeline", "seritas.com", ""),
	LOT_WIZARD				(143, "Lot Wizard/Friday Net", "fridaynet.com", ""),
	LOT_WIZARD_SECONDARY	(144, "Lot Wizard secondary", "lotwizard", ""),
	PASSING_LANE			(145, "Passing Lane", "passinglane.com", ""),
	SIMPLE_SOLUTIONS		(146, "Simple Solutions (Dealer Trend)", "simple101.com", ""),
	DEALER_ACTIVE			(147, "Dealer Active", "dealeractive.com", ""),
	MASTERS_HAND			(148, "Master's Hand Web Design", "mastershand.net", ""),
	JTZ_ENTERPRISE			(149, "JTZ Enterprise", "jtzenterprise.com", ""),
	MOTOPRESS				(150, "MotoPress", "motopress", "Self-Editor for WordPress"),
	STEVENS_COMPANY			(151, "The Stevens Company", "thestevenscompany.com", ""),
	FIRST_FUNNEL			(152, "First Funnel", "firstfunnel.com", ""),
	CENTRAL_TECH			(153, "Central Tech Solutions", "centraltechsolutions.com", ""),
	WILLETTS				(154, "Willetts", "willetts.com", ""),
	MAGNET_INTERACTIVE		(155, "Magnet Interactive", "Magnet Interactive", ""),
	DEALER_TREND			(156, "Dealer Trend", "dealertrend.com", ""),
	AUTO_SEARCH_TECH		(157, "Auto Search Tech", "autosearchtech.com", ""),
	KC_NET					(158, "KC Net", "kcnet.org", "not-for-profit"),
	MGM_DESIGN				(159, "MGM Design", "mgmdesign.com", ""),
	IGNITE_XDS				(160, "Ignite XDS", "ignitexds.net",""),
	MADER_WEB				(161, "Mader Web", "maderweb.net", ""),
	LTI_MEDIA				(162, "LTI Media", "ltimedia.com", ""),
	ONLINE_EMPIRE_BUILDER	(163, "Online Empire Builder", "onlineempirebuilder.com", ""),
	POWER_SERVE				(164, "Power Serve", "powerserve.net", ""),
	AUTO_TORQ				(165, "Auto Torq", "autotorq.com", ""),
	NORTHEAST_KINGDOM		(166, "Northeast Kingdom Online", "northeastkingdomonline.com", ""),
	EPISODE_49				(167, "Episode 49", "episode49.com", ""),
	DEALER_SPIKE			(168, "Dealer Spike", "dealerspike.com", ""),
	DEALER_SPIKE_TRUCK		(169, "Dealer Spike Truck", "dealerspiketruck.com", ""),
	SOUTHFIRE				(170, "Southfire", "dealersitepro.com", ""),
	NET_LAB					(171, "The Net Lab", "thenetlab.com", ""),
	SOLUTIONS_MEDIA_360		(172, "Solutions Medias 360", "sm360.ca", ""),
	SIMPLE_BUSINESS_SOLUTIONS (173, "Simple Business Solutions", "cutcostsgrowsales.com", ""),
	SIMPLE_BUSINESS_SOL_CONF (174, "Simple Business Solutions", "Simple Business Solutions", ""),
	DOT_NET_NUKE			(175, "Dot Net Nuke", "DotNetNuke", ""),
	MID_AMERICA_WEB			(176, "Mid America Web", "midamericaweb.com", ""),
	BAYSHORE_SOLUTIONS		(177, "Bayshore Solutions", "bayshoresolutions.com", ""),
	AUCTION_123				(178, "Auction 123", "webstatic.auction123.com", ""),
	ADVANCED_AUTO_DEALERS	(179, "Advanced Auto Dealers", "advancedautodealers.com", ""),
	DEALER_LEADS			(180, "Dealer Leads", "dealerleads.com", "SEO company may provide websites as well"),
	RND_INTERACTIVE			(181, "RND Interactive", "rndinteractive.com", "Texas compliance marketing"),
	RND_INT_CONFIRM			(182, "Primary RND Interactive", "rnd_home", ""),
	NAKED_LIME_CONF2		(183, "Naked Lime", "Powered By <a href=\"http://www.nlmkt.com", ""),
	EBIZ_CONF				(184, "Ebiz Auto", "ebizautos.com/sp.gif", ""),
	DEALER_IMPACT_CONF		(185, "Dealer Impact", "Dealer Impact logo", ""),
	DEALER_TRACK_CONF2		(186, "Dealertrack", "DealerTrack Technologies, Inc", ""),
	MOTOR_WEBS_CONF			(187, "MotorWebs", "Powered By Motorwebs", ""),
	DIGIGO_CONF				(188, "DigiGo", "powered-by-digigo", ""),
//	DIGIGO_CONF2			(189, "DigoGo", "powered-by-digigo", ""),
	VIN_SOLUTIONS_CONF3		(190, "Vin Solutions", "Powered By VinSolutions", ""),
	CLICK_MOTIVE_CONF		(191, "Click Motive", "clickmotive.com/static", "Catches theme-loading for full sites"),
	VIN_SOLUTIONS_CONF4		(192, "Vin Solutions", "VinSolutionsSupportInfo", ""),
	CHROMA_CONF				(193, "Chroma Cars", "by Chroma Cars", ""),
	SEARCH_OPTICS_CONF		(194, "Search Optics", "Search Optics LLC", ""),
	DEALERFIRE_CONF2		(195, "Dealerfire", "dealerfire-logo", ""),
	MEDIA_GENUIS			(196, "Media Genius", "by MediaGenius.biz", ""),
	PIXEL_MOTION_CONF2		(197, "Pixel Motion", "Powered by Pixel", ""),
	DEALER_ACTIVE_CONF		(198, "Dealer Active", "dealeractive.jpg", ""),
	DRIVE_DIGITAL_CONF		(199, "Drive Digital", "Drive Digital Group", ""),
	SERITAS_CONF			(200, "Seritas Pipeline", "Seritas Pipeline", ""),
	SIMPLE_SOLUTIONS_CONF	(201, "Simple Solutions", "Simple Solutions", ""),
	BAYSHORE_SOLUTIONS_CONF	(202, "Bayshore Solutions", "Bayshore Solutions", ""),
	OTHER					(203, "Other", "this regex will not match", ""),
	AUTO_UPLINK				(204, "Auto Uplink", "autouplink.com", "Dealer eProcess subsidiary"),
	FLEX_DEALER				(205, "Flex Dealer", "flexdealer.com", ""),
	FLEX_DEALER_CONF		(206, "Flex Dealer", "by FlexDealer", ""),
	SAC_AUTOS				(207, "Sac Autos", "by SacAutos", ""),
	HIGHER_TURNOVER			(208, "Higher Turnover", "higherturnover.com", ""),
	KRACK_MEDIA				(209, "Krack Media", "krackmedia.com", ""),
	ENOCH_CREATIVE			(210, "Enoch Creative", "enochcreative.com", ""),
	TRUCK_PAPER				(211, "Truck Paper", "truckpaper.com", ""),
	CLICK_HERE_PUBLISHING	(212, "Click Here Publishing", "clickherepublishing.com", ""),
	OEM						(213, "Site probably created by OEM", "This regex intentionally will not match", "Most common for luxury cars or Kia"),
	WHIPPET_CREATIVE		(214, "Whippet Creative", "whippetcreative.com", ""),
	MACH_20					(215, "Mach 20 Autos", "by mach20autos.com", ""),
	TRACTOR_HOUSE			(216, "Tractor House", "TractorHouse.com", ""),
	AUTO_PUBLISHERS			(217, "Auto Publishers", "Powered by: Auto Publishers", ""),
	SKY_CONCEPTS			(218, "Sky Concepts", "hosted by <a href=\"http://skyconcepts.co", "");
	
	
	
	
	
	 
	
	
	private int id;
	private String description;
	private String definition;
	private String notes;
	public final Set<StringMatch> offsetMatches = new HashSet<StringMatch>();
	
	private final static Map<Integer, WebProvider> enumIds = new HashMap<Integer, WebProvider>();
	static {
		for(WebProvider sm : WebProvider.values()){
			enumIds.put(sm.getId(), sm);
		}
	}
	
	public static WebProvider getTypeFromId(Integer id) {
		return enumIds.get(id);
	}
	
	private WebProvider(int id, String description, String definition, String notes){
		this.id = id;
		this.description = description;
		this.definition = definition;
		this.notes = notes;
	}
	
	private WebProvider(int id, String description, String definition, String notes, StringMatch[] offsetMatches){
		this.id = id;
		this.description = description;
		this.definition = definition;
		this.notes = notes;
		for(StringMatch match : offsetMatches){
			this.offsetMatches.add(match);
		}
	}
	
	public WebProvider getType(Integer id) {
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
