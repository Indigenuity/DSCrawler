package datadefinitions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;




import persistence.SiteCrawl;

import com.google.common.collect.Sets;

public class WebProviderInference {
	
	public static final Set<WebProviderInference> probableInferences = new HashSet<WebProviderInference>();
	
	public static WebProvider inferFromManualRules(SiteCrawl siteCrawl) {
		
//		Set<WebProvider> wps = siteCrawl.getWebProviders();
//		Set<GeneralMatch> gen = siteCrawl.getGeneralMatches();
//		
//		WebProvider result = null;
//		if(wps.contains(WebProvider.DEALER_DIRECT) && wps.contains(WebProvider.FORD_DIRECT) && wps.size() == 2){
//			return WebProvider.DEALER_DIRECT;
//		}
//		if(wps.contains(WebProvider.FORD_DIRECT) && wps.contains(WebProvider.DEALER_COM)){
//			if(wps.size() == 2 )
//				return WebProvider.DEALER_COM;
//			if(gen.contains(GeneralMatch.DEALER_COM_VERSION_8) || gen.contains(GeneralMatch.DEALER_COM_VERSION_9))
//				return WebProvider.DEALER_COM;	
//		}
//		if(wps.contains(WebProvider.FORD_DIRECT) && wps.contains(WebProvider.CLICK_MOTIVE) && wps.size() == 2){
//			return WebProvider.CLICK_MOTIVE;
//		}
//		if(gen.contains(GeneralMatch.DEALER_COM_VERSION_8) && gen.contains(GeneralMatch.DEALER_COM_VERSION_9)){
//			return WebProvider.DEALER_COM;
//		}
//		if(wps.contains(WebProvider.NAKED_LIME) && wps.contains(WebProvider.NAKED_LIME_SECONDARY) 
//				&& wps.contains(WebProvider.WEB_MAKER_X)){
//			return WebProvider.NAKED_LIME;
//		}
//		if(wps.contains(WebProvider.AUTO_REVO) && wps.contains(WebProvider.AUTO_REVO_SECONDARY) && wps.size() == 2) {
//			return WebProvider.AUTO_REVO;
//		}
//		if(wps.contains(WebProvider.VIN_SOLUTIONS) && wps.contains(WebProvider.DEALER_COM) && wps.size() ==2) {
//			return WebProvider.VIN_SOLUTIONS;
//		}
//		if(wps.contains(WebProvider.ELEAD_CONF) || wps.contains(WebProvider.ELEAD_CONF2)){
//			return WebProvider.ELEAD_DIGITAL_CRM;
//		}
//		if(wps.contains(WebProvider.DEALER_LAB) && wps.contains(WebProvider.DEALER_EPROCESS) && wps.size() == 2){
//			return WebProvider.DEALER_LAB;
//		}
//		if(wps.contains(WebProvider.DEALER_EPROCESS_PRIMARY) && wps.contains(WebProvider.DEALER_EPROCESS) && wps.size() == 2){
//			return WebProvider.DEALER_EPROCESS;
//		}
//		if(wps.contains(WebProvider.AUTOCON_X) && wps.contains(WebProvider.SECURITY_LABS) && wps.size() == 2){
//			return WebProvider.AUTOCON_X;
//		}
		return WebProvider.NONE;
	}
	
	private static WebProviderInference init(WebProvider primaryMatch, StringMatch... confirmingMatches){
		return new WebProviderInference(primaryMatch, confirmingMatches);
	}
	
	static {
		WebProviderInference temp;
		probableInferences.addAll(Sets.newHashSet(
				
				init(WebProvider.REMORA, WebProvider.REMORA, WebProvider.REMORA_SECONDARY),
				init(WebProvider.COBALT, WebProvider.COBALT, WebProvider.COBALT_GROUP, WebProvider.COBALT_NITRA),
				init(WebProvider.NET_LAB),
				init(WebProvider.SOUTHFIRE),
				init(WebProvider.DEALER_SPIKE),
				init(WebProvider.EPISODE_49),
				init(WebProvider.NORTHEAST_KINGDOM),
				init(WebProvider.AUTO_SEARCH_TECH),
				init(WebProvider.MANNING_AUTOMOTIVE, WebProvider.MANNING_AUTOMOTIVE, WebProvider.DEALER_DIRECT),
				init(WebProvider.AUTO_WALL),
				init(WebProvider.WEB_PRO_JOE),
				init(WebProvider.DEALER_DNA),
				init(WebProvider.IO_COM),
				init(WebProvider.SINGLE_THROW),
				init(WebProvider.BLU_SOLUTIONS, WebProvider.I_PIT_CREW, WebProvider.BLU_SOLUTIONS),
				init(WebProvider.DEALER_HD, WebProvider.DEALER_HD, WebProvider.DEALER_EPROCESS),
				init(WebProvider.ROGEE, WebProvider.ROGEE, WebProvider.AUTOPARK_DRIVE),
				init(WebProvider.NAKED_LIME, WebProvider.NAKED_LIME_CONF),
				init(WebProvider.DESIGN_HOUSE_5),
				init(WebProvider.DEALER_PEAK),
				init(WebProvider.WORLD_DEALER, WebProvider.WORLD_DEALER, WebProvider.WORLD_DEALER_SEC),
				init(WebProvider.STRING_AUTOMOTIVE, WebProvider.STRING_AUTOMOTIVE, WebProvider.STRING_AUTOMOTIVE_SEC),
				init(WebProvider.PIXEL_MOTION, WebProvider.PIXEL_MOTION_CONFIRM),
				init(WebProvider.PIXEL_MOTION, WebProvider.PIXEL_MOTION_CONF2),
				init(WebProvider.SIMPLE_BUSINESS_SOLUTIONS, WebProvider.SIMPLE_BUSINESS_SOLUTIONS, WebProvider.SIMPLE_BUSINESS_SOL_CONF),
				init(WebProvider.DEALER_ON, WebProvider.DEALER_ON_CONF),
				init(WebProvider.DEALER_COM, WebProvider.DEALER_COM_POWERED_BY),
				init(WebProvider.DEALER_COM, WebProvider.DEALER_COM_CONF),
				init(WebProvider.DEALER_DIRECT, WebProvider.DEALER_DIRECT_CONF),
				init(WebProvider.GARY_STOCK),
				init(WebProvider.DOMINION),
				init(WebProvider.MOTORTRAK, WebProvider.MOTORTRAK_CONF),
				init(WebProvider.JAZELAUTOS, WebProvider.JAZEL_CONF),
				init(WebProvider.VIN_SOLUTIONS, WebProvider.VIN_SOLUTIONS_CONF),
				init(WebProvider.VIN_SOLUTIONS, WebProvider.VIN_SOLUTIONS_CONF2),
				init(WebProvider.VIN_SOLUTIONS, WebProvider.VIN_SOLUTIONS_CONF3),
				init(WebProvider.VIN_SOLUTIONS, WebProvider.VIN_SOLUTIONS_CONF4),
				init(WebProvider.AUTO_ONE, WebProvider.AUTO_ONE_CONF),
				init(WebProvider.DEALER_APEX, WebProvider.DEALER_APEX_CONF),
				init(WebProvider.DEALERFIRE, WebProvider.DEALERFIRE_CONF),
				init(WebProvider.DEALERFIRE, WebProvider.DEALERFIRE_CONF2),
				init(WebProvider.DEALER_INSPIRE, WebProvider.DEALER_INSPIRE_CONF),
				init(WebProvider.DEALER_LAB, WebProvider.DEALER_LAB_CONF),
				init(WebProvider.DEALER_LAB, WebProvider.DEALER_LAB_CONF2),
				init(WebProvider.SOKAL_MEDIA_GROUP, WebProvider.SOKAL_CONF),
				init(WebProvider.MOTION_FUZE, WebProvider.MOTION_FUZE, WebProvider.SOKAL_SECONDARY),
				init(WebProvider.DEALER_TRACK, WebProvider.DEALER_TRACK_CONF),
				init(WebProvider.DEALER_TRACK, WebProvider.DEALER_TRACK_CONF2),
				init(WebProvider.DEALER_EPROCESS_SUBSID),
				init(WebProvider.DEALER_EPROCESS, WebProvider.DEALER_EPROCESS_CONF),
				init(WebProvider.DEALER_EPROCESS, WebProvider.DEALER_EPROCESS_CONF2),
				init(WebProvider.DEALER_ZOOM, WebProvider.DEALER_ZOOM_CONF),
				init(WebProvider.DEALER_ZOOM, WebProvider.DEALER_ZOOM_CONF2),
				init(WebProvider.FUSION_ZONE, WebProvider.FUSION_ZONE_CONF),
				init(WebProvider.LIQUID_MOTORS, WebProvider.LIQUID_MOTORS_CONF),
				init(WebProvider.LIQUID_MOTORS, WebProvider.LIQUID_MOTORS_CONF2),
				init(WebProvider.LIQUID_MOTORS, WebProvider.LIQUID_MOTORS_CONF3),
				init(WebProvider.POTRATZ, WebProvider.POTRATZ_CONFIRM),
				init(WebProvider.CARS_FOR_SALE, WebProvider.CARS_FOR_SALE_CONF),
				init(WebProvider.ELEAD_DIGITAL_CRM, WebProvider.ELEAD_CONF),
				init(WebProvider.ELEAD_DIGITAL_CRM, WebProvider.ELEAD_CONF2),
				init(WebProvider.EBIZ_AUTOS, WebProvider.EBIZ_CONF),
				init(WebProvider.DEALER_IMPACT, WebProvider.DEALER_IMPACT_CONF),
				init(WebProvider.MOTOR_WEBS, WebProvider.MOTOR_WEBS_CONF),
				init(WebProvider.DIGIGO, WebProvider.DIGIGO_CONF),
				init(WebProvider.CLICK_MOTIVE, WebProvider.CLICK_MOTIVE_CONF),
				init(WebProvider.RND_INTERACTIVE, WebProvider.RND_INT_CONFIRM),
				init(WebProvider.CHROMA_CARS, WebProvider.CHROMA_CONF),
				init(WebProvider.FUSION_ZONE),
				init(WebProvider.DEALER_X, WebProvider.DEALER_X, WebProvider.DEALER_X_SECONDARY),
				init(WebProvider.SEARCH_OPTICS, WebProvider.SEARCH_OPTICS_CONF),
				init(WebProvider.DRIVING_FORCE),
				init(WebProvider.MEDIA_GENUIS),
				init(WebProvider.AUTO_FUSION),
				init(WebProvider.DEALER_ACTIVE, WebProvider.DEALER_ACTIVE_CONF),
				init(WebProvider.DRIVE_DIGITAL_GROUP, WebProvider.DRIVE_DIGITAL_CONF),
				init(WebProvider.SERITAS_PIPELINE, WebProvider.SERITAS_CONF),
				init(WebProvider.SIMPLE_SOLUTIONS, WebProvider.SIMPLE_SOLUTIONS_CONF),
				init(WebProvider.BAYSHORE_SOLUTIONS, WebProvider.BAYSHORE_SOLUTIONS_CONF),
				init(WebProvider.LTTF, WebProvider.LTTF, WebProvider.LTTF_SECONDARY),
				init(WebProvider.AUTO_UPLINK),
				init(WebProvider.FLEX_DEALER, WebProvider.FLEX_DEALER_CONF),
				init(WebProvider.MACH_20),
				init(WebProvider.TRACTOR_HOUSE),
				init(WebProvider.AUTO_PUBLISHERS),
				init(WebProvider.SKY_CONCEPTS)
				
				
				
				
				
				
		
		
		
		));
	}
	
	
	
	private final Set<StringMatch> confirmingMatches = new HashSet<StringMatch>();
	private final WebProvider primaryMatch;

	private WebProviderInference(WebProvider primaryMatch, StringMatch... confirmingMatches) {
		this.primaryMatch = primaryMatch;
		if(confirmingMatches.length == 0){
			this.confirmingMatches.add(primaryMatch);
		}
		else{
			this.confirmingMatches.addAll(Arrays.asList(confirmingMatches));
		}
	}
	
	public WebProvider getPrimaryMatch() {
		return primaryMatch;
	}

	public Set<StringMatch> getConfirmingMatches() {
		return this.confirmingMatches;
	}
	
}
