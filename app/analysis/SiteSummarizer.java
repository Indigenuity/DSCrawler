package analysis;

import java.lang.reflect.Field;
import java.util.List;

import datadefinitions.GeneralMatch;
import datadefinitions.Scheduler;
import datadefinitions.WebProvider;
import persistence.ExtractedString;
import persistence.ExtractedUrl;
import persistence.PageInformation;
import persistence.SiteInformationOld;
import persistence.SiteSummary;
import persistence.Staff;

public class SiteSummarizer {

	public static SiteSummary summarizeSite(SiteInformationOld siteInfo) throws IllegalArgumentException, IllegalAccessException{
		SiteSummary summary = new SiteSummary(siteInfo);
		
		copyFields(summary);
		countPages(summary);
		summarizeMatches(summary);
		summarizeStringExtractions(summary);
		summarizeStaffExtractions(summary);
//		inferWebProvider(summary);
		return summary;
	}
	
	public static void copyFields(SiteSummary summary) {
		SiteInformationOld siteInfo = summary.getSiteInfo();
		summary.setSiteName(siteInfo.getSiteName());
		summary.setCapdb(siteInfo.getCapdb());
		summary.setCrawlDate(siteInfo.getCrawlDate());
		summary.setCrawlStorageFolder(siteInfo.getCrawlStorageFolder());
		summary.setGivenUrl(siteInfo.getGivenUrl());
		summary.setFranchise(siteInfo.isFranchise());
		summary.setCrawlFromGivenUrl(siteInfo.isCrawlFromGivenUrl());
		summary.setNiada(siteInfo.getNiada());
		summary.setIntermediateUrl(siteInfo.getIntermediateUrl());
		summary.setRedirectUrl(siteInfo.getRedirectUrl());
		summary.setSiteUrl(siteInfo.getSiteUrl());
		summary.setEmptySite(siteInfo.isEmptySite());
	}
	
	public static void countPages(SiteSummary summary) {
		int numPages = summary.getSiteInfo().getPages().size();
		summary.setNumPages(numPages);
	}
	
	public static void summarizeMatches(SiteSummary summary) {
		SiteInformationOld siteInfo = summary.getSiteInfo();
		List<WebProvider> wps = summary.getWebProviders();
		List<Scheduler> scheds = summary.getSchedulers();
		List<GeneralMatch> gms = summary.getGeneralMatches();
		
		for(PageInformation pageInfo : siteInfo.getPages()){
			for(WebProvider wp : pageInfo.getWebProviders()){
				if(!wps.contains(wp)){
					wps.add(wp);
				}
			}
			for(Scheduler sched : pageInfo.getSchedulers()){
				if(!scheds.contains(sched)){
					scheds.add(sched);
				} 
			}
			for(GeneralMatch gm : pageInfo.getGeneralMatches()){
				if(!gms.contains(gm)){
					gms.add(gm);
				}
			}
		}
	}
	
	public static void summarizeStringExtractions(SiteSummary summary) {
		SiteInformationOld siteInfo = summary.getSiteInfo();
		
		List<ExtractedString> extractedStrings = summary.getExtractedStrings();
		List<ExtractedUrl> extractedUrls = summary.getExtractedUrls();
		
		for(PageInformation pageInfo : siteInfo.getPages()){
			for(ExtractedString es : pageInfo.getExtractedStrings()){
				if(!extractedStrings.contains(es)){
					extractedStrings.add(es);
				}
			}
			for(ExtractedUrl eu : pageInfo.getExtractedUrls()){
				if(!extractedUrls.contains(eu)){
					extractedUrls.add(eu);
				}
			}
		}
	}
	
	public static void summarizeStaffExtractions(SiteSummary summary) {
		SiteInformationOld siteInfo = summary.getSiteInfo();
		
		List<Staff> allStaff = summary.getAllStaff();
		for(PageInformation pageInfo : siteInfo.getPages()) {
			for(Staff staff : pageInfo.getAllStaff()) {
				if(!allStaff.contains(staff)) {
					allStaff.add(staff);
				}
			}
		}
	}
	
//	public static void inferWebProvider(SiteSummary summary) throws IllegalArgumentException, IllegalAccessException {
//		WebProviderStats wp = summary.getWpStats();
//		Field[] fields = wp.getClass().getDeclaredFields();
//		StringBuilder sb = new StringBuilder();
//		for(Field f : fields) {
//			if(f.getType() == boolean.class && f.getBoolean(wp)) {
//				sb.append(f.getName());
//				sb.append(", ");
//			}
//		}
//	
//		summary.setWebsiteProvider(sb.toString());
//	}
//	
//	public static void inferScheduler(SiteSummary summary) throws IllegalArgumentException, IllegalAccessException {
//		SchedulerStats sched = summary.getSchedulerStats();
//		Field[] fields = sched.getClass().getDeclaredFields();
//		StringBuilder sb = new StringBuilder();
//		for(Field f : fields) {
//			if(f.getType() == boolean.class && f.getBoolean(sched)) {
//				sb.append(f.getName());
//				sb.append(", ");
//			}
//		}
//	
//		summary.setScheduler(sb.toString());
//	}
	
	
	
}
