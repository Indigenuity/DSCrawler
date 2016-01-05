package analysis;

import org.apache.commons.lang3.StringUtils;

import persistence.MobileCrawl;

public class MobileCrawlAnalyzer {
	
	public static void analyzeMobileCrawl(MobileCrawl crawl) {
		checkMobileSite(crawl);
		analyzeResponsiveAdaptive(crawl);
	}
	
	public static void analyzeResponsiveAdaptive(MobileCrawl crawl) {
		checkResponsive(crawl);
		checkAdaptive(crawl);
		checkAlmostResponsive(crawl);
		checkAlmostAdaptive(crawl);
	}

	public static void checkMobileSite(MobileCrawl crawl) {
		crawl.setMobiSite(false);
		if(!StringUtils.equals(crawl.getSeed(), crawl.getResolvedSeed()) && StringUtils.equals(crawl.getSeed(), crawl.getFauxResolvedSeed())){
			crawl.setMobiSite(true);
		}
	}
	
	public static void checkResponsive(MobileCrawl crawl) {
		crawl.setResponsive(false);
		if(crawl.getFauxWindowWidth() >= crawl.getFauxScrollWidth()){
			crawl.setResponsive(true);
		}
	}
	
	public static void checkAdaptive(MobileCrawl crawl) {
		crawl.setAdaptive(false);
		if(crawl.getWindowWidth() >= crawl.getScrollWidth()){
			crawl.setAdaptive(true);
		}
	}
	
	public static void checkAlmostResponsive(MobileCrawl crawl) {
		crawl.setMostlyResponsive(false);
		if(!crawl.isResponsive() && crawl.getFauxScrollWidth() < 724){
			crawl.setMostlyResponsive(true);
		}
	}
	
	public static void checkAlmostAdaptive(MobileCrawl crawl) {
		crawl.setMostlyAdaptive(false);
		if(!crawl.isAdaptive() && crawl.getScrollWidth() < 724){
			crawl.setMostlyAdaptive(true);
		}
	}
}
