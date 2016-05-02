package datadefinitions;

import java.util.HashSet;
import java.util.Set;


public enum WebProvider implements StringMatch {
	
	NONE					( "Unknown Web Provider", "Intentionally non-matching regex", "This signifies that the web crawler could not find a known web provider for this site"),
	DEALER_COM				( "Dealer.com", "dealer.com", "Not a strict match"),
	DEALER_COM_POWERED_BY	( "Powered by Dealer.com", "powered by dealer.com", "A stricter match than simply 'dealer.com'"),
	DEALER_COM_CONF			( "Dealer.com", "Website by Dealer.com", "A stricter match than simply 'dealer.com'"),
    FRESH_INPUT				( "Fresh Input", "freshinput.com", ""),
    JAZELAUTOS				( "Jazel Auto", "jazelauto.com", ""),
    JAZEL_CONF				( "Jazel Auto", "Jazel.Orchard.Themes", ""),
    FORD_DIRECT				( "Ford Direct", "forddirect", ""),
    CLICK_MOTIVE			( "Click Motive", "clickmotive.com", ""),
    VIN_SOLUTIONS			( "Vin Solutions", "vinsolutions.com", ""),
    VIN_SOLUTIONS_CONF		( "Vin Solutions", "Website Powered By <a href=\"http://www.vinsolutions.com", ""),
    VIN_SOLUTIONS_CONF2		( "Vin Solutions", "Powered By Vinsolutions.com", ""),
    COBALT					( "Cobalt", "cobalt.com", ""),
    COBALT_NITRA			( "Cobalt Nitra", "cobaltnitra.com", ""),
    COBALT_GROUP			( "Cobalt Group", "cobaltgroup.com", ""),
    DEALER_ON				( "Dealer On", "dealeron.com", ""),
    DEALER_ON_SECONDARY		( "Dealer On", "dlron.us", ""),	//Maybe just a CDN URL
    DEALER_ON_CONF			( "Dealer On", "cdn.dlron.us/assets/logos", ""),
    AUTO_ONE				( "Auto One Media", "autoonemedia.com", ""),
    AUTO_ONE_CONF			( "Auto One Media", "AutoONE", ""),
    DEALER_APEX				( "Dealer Apex", "dealerapex.com", ""),
    DEALER_APEX_CONF		( "Dealer Apex", "Dealer Web Site Design by <span id=\"txtDealerApex\">Dealer Apex", ""),
    DEALER_CAR_SEARCH		( "Dealer Car Search", "dealercarsearch.com", ""),
    DEALERFIRE				( "DealerFire", "dealerfire.com", ""),
    DEALERFIRE_CONF			( "DealerFire", "by DealerFire", ""),
    DEALER_INSPIRE			( "Dealer Inspire", "dealerinspire.com", ""),
    DEALER_INSPIRE_CONF		( "Dealer Inspire", "Visit http://www.dealerinspire.com to Inspire your visitors", ""),
    DEALER_LAB				( "Dealer Lab", "dealerlab.com", "Subsidiary of Dealer eProcess"),
    DEALER_LAB_CONF			( "Dealer Lab", "dealerlab_logo.png", ""),
    DEALER_LAB_CONF2		( "Dealer Lab", "dealerlab_logo-white.png", ""),
    SOKAL_MEDIA_GROUP		( "Sokal Media Group", "sokalmediagroup.com", ""),
    SOKAL_CONF				( "Sokal Media Group", "smglogo", ""),
    SOKAL_SECONDARY			( "Sokal subsidiary", "smgdealer.com", ""),
    MOTION_FUZE				( "Motion Fuze", "motionfuze.com", "Maybe subsidiary of Sokal"),
    DEALER_TRACK			( "Dealer Track", "dealertrack.com", ""),
	DEALER_TRACK_CONF		( "Dealer Track", "dealertrack.com/portal/dealers/digital-marketing", ""),
    DEALER_EPROCESS			( "Dealer eProcess", "dealereprocess.com", ""),
    DEALER_EPROCESS_PRIMARY	( "Dealer eProcess", "www.dealereprocess.com", ""),	//Providers like dealer lab use cdn.dealereprocess.com
    DEALER_EPROCESS_CONF	( "Dealer eProcess", "DealerEProcess_logo-white.png", ""),
    DEALER_EPROCESS_CONF2	( "Dealer eProcess", "DealerEProcess_logo.png", ""),
    DEALER_EPROCESS_SUBSID	( "Dealer EProcess", "Powered By Dealer e-Process", ""),
    DEALER_ZOOM				( "Dealer Zoom", "dealerzoom.com", ""),
    DEALER_ZOOM_SECONDARY	( "Dealer Zoom", "dealerzoom.wufoo.com", ""),
    DEALER_ZOOM_CONF		( "Dealer Zoom", "DZ_png.png", ""),
    DEALER_ZOOM_CONF2		( "Dealer Zoom", "Website services provided by <a href=\"http://www.dealerzoom.com", ""),
    DOMINION				( "Dominion", "Dominion Dealer Solutions", ""),
    DRIVE_WEBSITE			( "Drive Website", "drivewebsite.com", "Likely subsidiary of Dominion"),
    FUSION_ZONE				( "Fusion Zone", "fzautomotive.com", ""),
    FUSION_ZONE_CONF		( "Fusion Zone", "fusionZONE Automotive, Inc.", ""),
    INTERACTIVE_360			( "Interactive 360", "interactive360.com", ""),
    LIQUID_MOTORS			( "Liquid Motors", "liquidmotors.com", ""),
    LIQUID_MOTORS_CONF		( "Liquid Motors", "Website Powered by Liquid Motors Inc", ""),
    LIQUID_MOTORS_CONF2		( "Liquid Motors", "POWERED BY LIQUID MOTORS", ""),
    LIQUID_MOTORS_CONF3		( "Liquid Motors", "Powered by Liquid Motors", ""),
    NAKED_LIME				( "Naked Lime", "nlmkt.com", ""),
    NAKED_LIME_SECONDARY	( "Naked Lime", "nakedlime.com", ""),
    NAKED_LIME_CONF			( "Powered by Naked Lime", "Powered by Naked Lime Marketing", ""),
    POTRATZ					( "Potratz", "potratzdev.com", ""),
    POTRATZ_SECONDARY		( "Potratz", "exclusivelyautomotive.com", ""),
    POTRATZ_CONFIRM			( "Potratz", "potratz.png", ""),
    CARS_FOR_SALE			( "CarsForSale.com", "carsforsale.com", ""),
    CARS_FOR_SALE_CONF		( "Carsforsale.com", "Powered by <a target=\"blank\" href=\"http://www.carsforsale.com", ""),
    CARBASE					( "Carbase", "carbase.com", ""),
    EBIZ_AUTOS				( "EBiz Autos", "ebizautos.com", ""),
    ELEAD_DIGITAL_CRM		( "eLEAD Digital CRM", "elead-crm.com", "Uses Dealer On resources"),	
    ELEAD_CONF				( "eLEAD Digital", "Elead Digital", ""),
    ELEAD_CONF2				( "eLEAD Digital", "eLEAD Digital", ""),
    SMART_DEALER_SITES		( "Smart Dealer Sites", "smartdealersites.com", ""),
    DIGIGO					( "DigiGo", "digigo.com", ""),
    WAYNE_REAVES			( "Wayne Reaves", "waynereaves.com", ""),
    AREA_CARS				( "Area Cars", "areacars.com", ""),
    GARY_STOCK				( "The Gary Stock Company", "The Gary Stock Company", ""),
    REYNOLDS				( "Reynolds", "reyrey.com", ""),
    DEALER_DIRECT			( "Dealer Direct", "Dealer Direct", ""),
    DEALER_DIRECT_CONF		( "Dealer Direct", "Dealer Direct LLC", ""),
    
	SEARCH_OPTICS			( "Search Optics", "searchoptics.com", ""),
	DEALER_X				( "Dealer X", "cardealermarketing.com", ""),
	DEALER_X_SECONDARY		( "Dealer X secondary", "DealerX.com", "this url forwards to other url"),
	MOTORTRAK				( "MotorTrak", "motortrak.com", ""),
	MOTORTRAK_CONF			( "MotorTrak", "Developed by Motortrak", ""),
	STERLING_EMARKETING		( "Sterling eMarketing", "sterlingemarketing.com", ""),
	GS_MARKETING			( "GS Marketing", "GSMarketing", ""),
	GS_MARKETING_SECONDARY	( "GS Marketing secondary", "GSM_Logo.png", ""),
	AUTO_REVO				( "AutoRevo", "autorevo-powersites.com", ""),
	AUTO_REVO_SECONDARY		( "AutoRevo Secondary", "autorevo.com", ""),
	SECURITY_LABS			( "Security Labs design", "securitylabs.com", ""),
	AUTOCON_X				( "Autocon X", "autoconx.com", ""),
	MOTOR_WEBS				( "Motor Webs", "motorwebs.com", ""),
	PIXEL_MOTION			( "Pixel Motion", "pixelmotion.com", ""),
	PIXEL_MOTION_CONFIRM	( "Definitely Pixel Motion", "pixelmotion.png", ""),
	STRING_AUTOMOTIVE		( "String Automotive", "stringautomotive.com", ""),
	STRING_AUTOMOTIVE_SEC	( "String Automotive Secondary", "stringcontent.com", ""),
	WORLD_DEALER			( "World Dealer", "worlddealer.net", ""),
	WORLD_DEALER_SEC		( "World Dealer Secondary", "wdautos.com", ""),
	DEALER_PEAK				( "Dealer Peak", "dealerpeak.com", ""),
	LA_DOLCE				( "La Dolce Video", "ladolcevideo.com", ""),
	SLIPSTREAM_AUTO			( "Slipstream Auto", "slipstreamauto.com", ""),
	LK_PRO					( "LK Pro", "lkpro.com", ""),
	GIOVATTO				( "Giovatto", "giovatto.com", ""),
	DESIGN_HOUSE_5			( "Design House 5", "designhouse5.com", ""),
	FOX_DEALER_INTERACTIVE	( "Fox Dealer Interactive", "foxdealerinteractive.com", ""),
	WEB_MAKER_X				( "Web Maker X", "webmakerx.net", "Naked Lime"),
	ROGEE					( "Rogee", "rogee.com", ""),
	ROGEE_SECONDARY			( "Rogee Secondary", "rogeeauto.com", ""),
	AUTOPARK_DRIVE			( "AutoPark Drive (Dealer Serve)", "autoparkdrive.com", "Owned by Rogee"),
	DRIVING_FORCE			( "Driving Force", "drivingforceauto.com", ""),
	ALL_AUTO_NETWORK		( "All Auto Network", "allautonetwork.com", ""),
	IDEAS_360				( "360 Ideas", "360ideas.com", ""),
	DEALER_HD				( "Dealer HD (Dealer eProcess", "dealerhd.com", "Dealer eprocess"),
	AUTO_JINI				( "Auto Jini", "autojini.com", ""),
	AUTO_FUSION				( "Auto Fusion", "autofusion.com", ""),
	PROMAX					( "ProMax Unlimited", "promaxunlimited.com", ""),
	REMORA					( "Remora", "remorainc.com", ""),
	REMORA_SECONDARY		( "Remora Secondary", "remora.co", ""),
	BLU_SOLUTIONS			( "Blu Solutions", "blusolutions.com", "Sometimes just SEO"),
	I_PIT_CREW				( "iPitCrew", "ipitcrew.com", "Maybe owned by blu solutions"),
	SINGLE_THROW			( "Single Throw", "singlethrow.com", ""),
	IO_COM					( "I/O com", "i-ocom.com", ""),
	SPEEDSHIFT_MEDIA		( "Speedshift Media", "speedshiftmedia.com", ""),
	EDGE_ONE				( "Edge One Technologies", "edgeonetechnologies.com", ""),
	CHROMA_CARS				( "Chroma Cars", "chromacars.com", ""),
	LTTF					( "LTTF", "lttf.com", ""),
	LTTF_CONF				( "LTTF", "Copyright © 2015 LTTF", "This match must be refreshed every year, and is only for convenience"),
	LTTF_SECONDARY			( "LTTF", "LTTF", "A catch-all for the LTTF sites that don't actually link"),
	BIZ_BOOST				( "Biz Boost Marketing", "bizboostmarketconsulting.com", ""),
	DEALER_DNA				( "Dealer DNA", "dealerdna.com", ""),
	IPS_SOLUTIONS			( "IPS Solutions", "ipssolutions.com", ""),
	DEALER_TRACTION			( "Dealer Traction", "dealertraction.com", ""),
	QMASS					( "QMaSS", "QMaSS", "maybe defunct"),
	IPUBLISHERS				( "IPublishers", "ipublishers.com", ""),
	WEB_PRO_JOE				( "Web Pro Joe", "webprojoe.com", ""),
	VISION_AMP				( "Vision Amp", "visionamp.com", ""),
	ARI_NET					( "ARI Network Services", "arinet.com", ""),
	DEALER_IMPACT			( "Dealer Impact Systems", "dealerimpactsystems.com", ""),
	AUTO_WALL				( "Auto Wall/Gratis Tech", "gratistech.com", ""),
	JKR_ADVERTISING			( "JKR Automotive", "jkradvertising.com", ""),
	DRIVE_DIGITAL_GROUP		( "Drive Digital Group", "drivedigitalgroup.com", ""),
	AUTO_DEALER_WEBSITES	( "Auto Dealer Websites", "autodealerwebsites.com", ""),
	VERSION2				( "Version2 Marketing", "version2group.com", ""),
	DEALER_HOSTS			( "Dealer Hosts", "dealerhosts.com", ""),
	MANNING_AUTOMOTIVE		( "Manning Automotive Marketing", "manningi.com", ""),
	SERITAS_PIPELINE		( "Seritas Pipeline", "seritas.com", ""),
	LOT_WIZARD				( "Lot Wizard/Friday Net", "fridaynet.com", ""),
	LOT_WIZARD_SECONDARY	( "Lot Wizard secondary", "lotwizard", ""),
	PASSING_LANE			( "Passing Lane", "passinglane.com", ""),
	SIMPLE_SOLUTIONS		( "Simple Solutions (Dealer Trend)", "simple101.com", ""),
	DEALER_ACTIVE			( "Dealer Active", "dealeractive.com", ""),
	MASTERS_HAND			( "Master's Hand Web Design", "mastershand.net", ""),
	JTZ_ENTERPRISE			( "JTZ Enterprise", "jtzenterprise.com", ""),
	MOTOPRESS				( "MotoPress", "motopress", "Self-Editor for WordPress"),
	STEVENS_COMPANY			( "The Stevens Company", "thestevenscompany.com", ""),
	FIRST_FUNNEL			( "First Funnel", "firstfunnel.com", ""),
	CENTRAL_TECH			( "Central Tech Solutions", "centraltechsolutions.com", ""),
	WILLETTS				( "Willetts", "willetts.com", ""),
	MAGNET_INTERACTIVE		( "Magnet Interactive", "Magnet Interactive", ""),
	DEALER_TREND			( "Dealer Trend", "dealertrend.com", ""),
	AUTO_SEARCH_TECH		( "Auto Search Tech", "autosearchtech.com", ""),
	KC_NET					( "KC Net", "kcnet.org", "not-for-profit"),
	MGM_DESIGN				( "MGM Design", "mgmdesign.com", ""),
	IGNITE_XDS				( "Ignite XDS", "ignitexds.net",""),
	MADER_WEB				( "Mader Web", "maderweb.net", ""),
	LTI_MEDIA				( "LTI Media", "ltimedia.com", ""),
	ONLINE_EMPIRE_BUILDER	( "Online Empire Builder", "onlineempirebuilder.com", ""),
	POWER_SERVE				( "Power Serve", "powerserve.net", ""),
	AUTO_TORQ				( "Auto Torq", "autotorq.com", ""),
	NORTHEAST_KINGDOM		( "Northeast Kingdom Online", "northeastkingdomonline.com", ""),
	EPISODE_49				( "Episode 49", "episode49.com", ""),
	DEALER_SPIKE			( "Dealer Spike", "dealerspike.com", ""),
	DEALER_SPIKE_TRUCK		( "Dealer Spike Truck", "dealerspiketruck.com", ""),
	SOUTHFIRE				( "Southfire", "dealersitepro.com", ""),
	NET_LAB					( "The Net Lab", "thenetlab.com", ""),
	SOLUTIONS_MEDIA_360		( "Solutions Medias 360", "sm360.ca", ""),
	SIMPLE_BUSINESS_SOLUTIONS ( "Simple Business Solutions", "cutcostsgrowsales.com", ""),
	SIMPLE_BUSINESS_SOL_CONF ( "Simple Business Solutions", "Simple Business Solutions", ""),
	DOT_NET_NUKE			( "Dot Net Nuke", "DotNetNuke", ""),
	MID_AMERICA_WEB			( "Mid America Web", "midamericaweb.com", ""),
	BAYSHORE_SOLUTIONS		( "Bayshore Solutions", "bayshoresolutions.com", ""),
	AUCTION_123				( "Auction 123", "webstatic.auction123.com", ""),
	ADVANCED_AUTO_DEALERS	( "Advanced Auto Dealers", "advancedautodealers.com", ""),
	DEALER_LEADS			( "Dealer Leads", "dealerleads.com", "SEO company may provide websites as well"),
	RND_INTERACTIVE			( "RND Interactive", "rndinteractive.com", "Texas compliance marketing"),
	RND_INT_CONFIRM			( "Primary RND Interactive", "rnd_home", ""),
	NAKED_LIME_CONF2		( "Naked Lime", "Powered By <a href=\"http://www.nlmkt.com", ""),
	EBIZ_CONF				( "Ebiz Auto", "ebizautos.com/sp.gif", ""),
	DEALER_IMPACT_CONF		( "Dealer Impact", "Dealer Impact logo", ""),
	DEALER_TRACK_CONF2		( "Dealertrack", "DealerTrack Technologies, Inc", ""),
	MOTOR_WEBS_CONF			( "MotorWebs", "Powered By Motorwebs", ""),
	DIGIGO_CONF				( "DigiGo", "powered-by-digigo", ""),
//	DIGIGO_CONF2			( "DigoGo", "powered-by-digigo", ""),
	VIN_SOLUTIONS_CONF3		( "Vin Solutions", "Powered By VinSolutions", ""),
	CLICK_MOTIVE_CONF		( "Click Motive", "clickmotive.com/static", "Catches theme-loading for full sites"),
	VIN_SOLUTIONS_CONF4		( "Vin Solutions", "VinSolutionsSupportInfo", ""),
	CHROMA_CONF				( "Chroma Cars", "by Chroma Cars", ""),
	SEARCH_OPTICS_CONF		( "Search Optics", "Search Optics LLC", ""),
	DEALERFIRE_CONF2		( "Dealerfire", "dealerfire-logo", ""),
	MEDIA_GENUIS			( "Media Genius", "by MediaGenius.biz", ""),
	PIXEL_MOTION_CONF2		( "Pixel Motion", "Powered by Pixel", ""),
	DEALER_ACTIVE_CONF		( "Dealer Active", "dealeractive.jpg", ""),
	DRIVE_DIGITAL_CONF		( "Drive Digital", "Drive Digital Group", ""),
	SERITAS_CONF			( "Seritas Pipeline", "Seritas Pipeline", ""),
	SIMPLE_SOLUTIONS_CONF	( "Simple Solutions", "Simple Solutions", ""),
	BAYSHORE_SOLUTIONS_CONF	( "Bayshore Solutions", "Bayshore Solutions", ""),
	OTHER					( "Other", "this regex will not match", ""),
	AUTO_UPLINK				( "Auto Uplink", "autouplink.com", "Dealer eProcess subsidiary"),
	FLEX_DEALER				( "Flex Dealer", "flexdealer.com", ""),
	FLEX_DEALER_CONF		( "Flex Dealer", "by FlexDealer", ""),
	SAC_AUTOS				( "Sac Autos", "by SacAutos", ""),
	HIGHER_TURNOVER			( "Higher Turnover", "higherturnover.com", ""),
	KRACK_MEDIA				( "Krack Media", "krackmedia.com", ""),
	ENOCH_CREATIVE			( "Enoch Creative", "enochcreative.com", ""),
	TRUCK_PAPER				( "Truck Paper", "truckpaper.com", ""),
	CLICK_HERE_PUBLISHING	( "Click Here Publishing", "clickherepublishing.com", ""),
	OEM						( "Site probably created by OEM", "This regex intentionally will not match", "Most common for luxury cars or Kia"),
	WHIPPET_CREATIVE		( "Whippet Creative", "whippetcreative.com", ""),
	MACH_20					( "Mach 20 Autos", "by mach20autos.com", ""),
	TRACTOR_HOUSE			( "Tractor House", "TractorHouse.com", ""),
	AUTO_PUBLISHERS			( "Auto Publishers", "Powered by: Auto Publishers", ""),
	SKY_CONCEPTS			( "Sky Concepts", "hosted by <a href=\"http://skyconcepts.co", ""),
	AUTO_COMPARISON			( "Auto Comparison", "AutoComparison.com", "Domain predators that buy old dealer URLs.  This would mean the site is DEFUNCT.");
	
	
	
	
	
	 
	
	
	private String description;
	private String definition;
	private String notes;
	public final Set<StringMatch> offsetMatches = new HashSet<StringMatch>();
	
	private WebProvider(String description, String definition, String notes){
		this.description = description;
		this.definition = definition;
		this.notes = notes;
	}
	
	private WebProvider(String description, String definition, String notes, StringMatch[] offsetMatches){
		this.description = description;
		this.definition = definition;
		this.notes = notes;
		for(StringMatch match : offsetMatches){
			this.offsetMatches.add(match);
		}
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
