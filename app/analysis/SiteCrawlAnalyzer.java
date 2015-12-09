package analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import datadefinitions.GeneralMatch;
import datadefinitions.OEM;
import datadefinitions.Scheduler;
import datadefinitions.StringExtraction;
import datadefinitions.StringMatch;
import datadefinitions.UrlExtraction;
import datadefinitions.WebProvider;
import datadefinitions.WebProviderInference;
import datatransfer.Amalgamater;
import datatransfer.FileMover;
import global.Global;
import persistence.ExtractedString;
import persistence.ExtractedUrl;
import persistence.ImageTag;
import persistence.Metatag;
import persistence.PageCrawl;
import persistence.SiteCrawl;
import persistence.Staff;
import utilities.DSFormatter;
import utilities.Tim;

public class SiteCrawlAnalyzer {
	
	public static void doFull(SiteCrawl siteCrawl) {
		
	}
	
	
	public static void metaAnalysis(SiteCrawl siteCrawl) {
//		System.out.println("meta analysis : " + siteCrawl.getSiteCrawlId());
		checkUniques(siteCrawl);
//		checkLength(siteCrawl);
//		checkContent(siteCrawl);
//		checkImages(siteCrawl);
//		inferWebProvider(siteCrawl);
	}
	
	public static void checkImages(SiteCrawl siteCrawl) {
		float total = 0;
		float alt = 0;
		
		for(ImageTag image : siteCrawl.getImageTags()) {
			total++;
			if(!StringUtils.isEmpty(image.getAlt())){
				alt++;
			}
		}
//		System.out.println("alt : " + alt);
		siteCrawl.setAltImageScore(Math.round(alt/total * 100));
//		System.out.println("alt score : " + siteCrawl.getAltImageScore());
	}
	
	public static void checkUniques(SiteCrawl siteCrawl) {
//		System.out.println("checking uniques: " + siteCrawl.getSiteCrawlId());
		Set<String> h1s = new HashSet<String>();
		Set<String> titles = new HashSet<String>();
		Set<String> urls = new HashSet<String>();
		Set<String> metas = new HashSet<String>();
		float total = siteCrawl.getPageCrawls().size();
		float h1Total = 0;
		float titleTotal = 0;
		float urlTotal = 0;
		float metaTotal = 0;
		for(PageCrawl outer : siteCrawl.getPageCrawls()){
			h1s.add(outer.getH1());
			titles.add(outer.getTitle());
			urls.add(DSFormatter.removeQueryString(outer.getUrl()));
			if(outer.getMetaDescription() != null){
				metas.add(outer.getMetaDescription().getContent());
			}
		}
		
		
				
			
//		System.out.println("new h1 score : " + h1s.size() / total);
//		System.out.println("unique h1 : " + Math.round((1 - (h1Total/total)) * 100));
		siteCrawl.setUniqueH1Score(Math.round((h1s.size() / total) * 100));
		siteCrawl.setUniqueTitleScore(Math.round((titles.size() / total) * 100));
		siteCrawl.setUniqueUrlScore(Math.round((urls.size() / total) * 100));
		siteCrawl.setUniqueMetaDescriptionScore(Math.round((metas.size() / total) * 100));
		
	}
	
	public static void checkLength(SiteCrawl siteCrawl) {
//		System.out.println("checking length : " + siteCrawl.getSiteCrawlId());
		float total = siteCrawl.getPageCrawls().size();
		float titleTotal = 0;
		float metaTotal = 0;
		for(PageCrawl pageCrawl : siteCrawl.getPageCrawls()){
			PageCrawlAnalyzer.checkLength(pageCrawl);
			if(pageCrawl.isTitleLength()) {
				titleTotal++;
			}
			if(pageCrawl.isDescriptionLength()) {
				metaTotal++;
			}
		}
		siteCrawl.setLengthTitleScore(Math.round((1 - (titleTotal/total)) * 100));
		siteCrawl.setLengthMetaDescriptionScore(Math.round((1 - (metaTotal/total)) * 100));
	}
	
	public static void checkContent(SiteCrawl siteCrawl) {
		System.out.println("checking content : " + siteCrawl.getSiteCrawlId());
		float total = siteCrawl.getPageCrawls().size() * 3;	//The number of elements in the content
		float url = 0;
		float title = 0;
		float h1 = 0;
		float meta = 0;
		for(PageCrawl pageCrawl : siteCrawl.getPageCrawls()){
			PageCrawlAnalyzer.checkContent(pageCrawl, siteCrawl.getSite().getCities());
//			System.out.println("page url : " + pageCrawl.getUrl());
			if(pageCrawl.isUrlCityQualifier()){
//				System.out.println("url city");
				url++;
			}
			if(pageCrawl.isUrlMakeQualifier()){
//				System.out.println("url make");
				url++;
			}
			if(pageCrawl.isUrlStateQualifier()){
//				System.out.println("url state");
				url++;
			}
			
//			System.out.println("page title : " + pageCrawl.getTitle());
			if(pageCrawl.isTitleCityQualifier()){
//				System.out.println("title city");
				title++;
			}
			if(pageCrawl.isTitleMakeQualifier()){
//				System.out.println("title make");
				title++;
			}
			if(pageCrawl.isTitleStateQualifier()){
//				System.out.println("title state");
				title++;
			}
			
//			System.out.println("page h1: " + pageCrawl.getH1());
			if(pageCrawl.isH1CityQualifier()){
//				System.out.println("h1 city");
				h1++;
			}
			if(pageCrawl.isH1MakeQualifier()){
//				System.out.println("h1 make");
				h1++;
			}
			if(pageCrawl.isH1StateQualifier()){
//				System.out.println("h1 state");
				h1++;
			}
			
//			System.out.println("page meta: " + pageCrawl.getMetaDescription().getContent());
			if(pageCrawl.isMetaDescriptionCityQualifier()){
//				System.out.println("meta city");
				meta++;
			}
			if(pageCrawl.isMetaDescriptionMakeQualifier()){
//				System.out.println("meta make");
				meta++;
			}
			if(pageCrawl.isMetaDescriptionStateQualifier()){
//				System.out.println("meta state");
				meta++;
			}
		}
		
//		System.out.println("url : " + url);
//		System.out.println("title : " + title);
//		System.out.println("h1 : " + h1);
//		System.out.println("meta : " + meta);
		
		siteCrawl.setContentUrlScore(Math.round((url/total) * 100));
		siteCrawl.setContentTitleScore(Math.round(title/total * 100));
		siteCrawl.setContentH1Score(Math.round(h1/total * 100));
		siteCrawl.setContentMetaDescriptionScore(Math.round(meta/total * 100));
		
//		System.out.println("url score : " + siteCrawl.getContentUrlScore());
//		System.out.println("title score : " + siteCrawl.getContentTitleScore());
//		System.out.println("h1 score : " + siteCrawl.getContentH1Score());
//		System.out.println("meta score : " + siteCrawl.getContentMetaDescriptionScore());
		
	}
	
	public static void docAnalysis(SiteCrawl siteCrawl) throws IOException {
		System.out.println("doc analysis : " + siteCrawl.getSiteCrawlId());
//		if(siteCrawl.isFilesMoved() || !FileMover.crawlIsOnLocal(siteCrawl)){
//			throw new IllegalArgumentException("Crawl files are not found on local storage : " + siteCrawl.getStorageFolder());
//		}
		File storageFolder = new File(Global.CRAWL_STORAGE_FOLDER + "/" + siteCrawl.getStorageFolder());
		
		if(!storageFolder.exists() || !storageFolder.isDirectory()){
			throw new IllegalArgumentException("No crawl found at location : " + storageFolder.getAbsolutePath());
		}
		
		int numFiles = 0;
		int numLargeFiles = 0;
		for(PageCrawl pageCrawl : siteCrawl.getPageCrawls()) {
			if(StringUtils.isEmpty(pageCrawl.getFilename())){
				continue;
			}
			File file = new File(Global.CRAWL_STORAGE_FOLDER + "/" + siteCrawl.getStorageFolder() + "/" + pageCrawl.getFilename());
			if(file.isFile() && !FilenameUtils.getExtension(file.getName()).equals("ser")) {
				FileInputStream inputStream = new FileInputStream(file.getAbsolutePath());
		        String text = IOUtils.toString(inputStream);
		        inputStream.close();
		        Document doc = Jsoup.parse(text);
		        
		        PageCrawlAnalyzer.fillPageStats(siteCrawl, pageCrawl, doc);
		        Set<Staff> allStaff = StaffExtractor.extractStaff(doc, siteCrawl.getWebProviders());
		        siteCrawl.addStaff(allStaff);
//		        System.out.println("allstaff after set : " + siteCrawl.getAllStaff().size());
		        siteCrawl.addExtractedUrls(extractUrls(doc));
		        
		        numFiles++;
		        
		        if(file.length() > Global.LARGE_FILE_THRESHOLD){
		        	numLargeFiles++;
		        }
			}
		}
		
		siteCrawl.setNumRetrievedFiles(numFiles);
		siteCrawl.setNumLargeFiles(numLargeFiles);
	}
	
	//Only analyzes amalgamated files.  
	public static void textAnalysis(SiteCrawl siteCrawl) throws IOException {
		System.out.println("text analysis : " + siteCrawl.getSiteCrawlId());
		File storageFolder = new File(Global.COMBINED_STORAGE_FOLDER + "/" + siteCrawl.getStorageFolder());
		
		if(!storageFolder.exists() || !storageFolder.isDirectory()){
			throw new IllegalArgumentException("No combined data found at location : " + storageFolder.getAbsolutePath());
		}
		
		for(File file : storageFolder.listFiles()) {
			if(file.isFile() && Amalgamater.isAmalgamation(file)){
				FileInputStream inputStream = new FileInputStream(file.getAbsolutePath());
		        String text = IOUtils.toString(inputStream);
		        inputStream.close();

		        System.out.println("extracting strings");
		        Tim.start();
		        siteCrawl.addExtractedStrings(extractStrings(text));
		        
		        System.out.println("web providers");
		        Tim.intermediate();
		        siteCrawl.setWebProviders(getWebProviders(text));
		        System.out.println("schedulers");
		        Tim.intermediate();
		        siteCrawl.setSchedulers(getSchedulers(text));
		        System.out.println("general matches");
		        Tim.intermediate();
		        siteCrawl.setGeneralMatches(getGeneralMatches(text));
			}
				
		}
		
	}
	
	private static Set<WebProvider> getWebProviders(String text) {
		Set<WebProvider> matches = new HashSet<WebProvider>();
		for(WebProvider wp : WebProvider.values()){
			if(text.contains(wp.getDefinition()) && !matches.contains(wp.getDefinition())){
//				System.out.println("matched : " + wp);
				matches.add(wp);
			}
		}
		return matches;
	}
	
	private static Set<Scheduler> getSchedulers(String text) {
		Set<Scheduler> matches = new HashSet<Scheduler>();
		for(Scheduler sched : Scheduler.values()){
			if(text.contains(sched.getDefinition()) && !matches.contains(sched.getDefinition())){
				matches.add(sched);
			}
		}
		return matches;
	}
	
	private static Set<GeneralMatch> getGeneralMatches(String text) {
		Set<GeneralMatch> matches = new HashSet<GeneralMatch>();
		for(GeneralMatch gm : GeneralMatch.values()){
			if(text.contains(gm.getDefinition()) && !matches.contains(gm.getDefinition())){
				matches.add(gm);
			}
		}
		return matches;
	}
	
	
	public static Set<ExtractedString> extractStrings(File file) throws IOException{
		if(!file.exists()) {
			throw new IOException("File does not exist for page with path : " + file.getAbsolutePath());
		}
		FileInputStream inputStream = new FileInputStream(file.getAbsolutePath());
		
        String text = IOUtils.toString(inputStream);
        inputStream.close();
        
        return extractStrings(text);
	}

	public static Set<ExtractedString> extractStrings(String text){
		Set<ExtractedString> extractedStrings = new HashSet<ExtractedString>();
		for(StringExtraction enumElement : StringExtraction.values()){
			if(enumElement != StringExtraction.CITY){
				Matcher matcher = enumElement.getPattern().matcher(text);
				int count = 0;
		    	while (matcher.find()) {
	//	    		System.out.println("found count : " + ++count);
		    		ExtractedString item = new ExtractedString(DSFormatter.truncate(matcher.group(0), 255), enumElement);
	    			extractedStrings.add(item);
		    	}
			}
		}
		return extractedStrings;
	}
	
	public static Set<ExtractedUrl> extractUrls(File file) throws IOException{
		if(!file.exists()) {
			throw new IOException("File does not exist for page with path : " + file.getAbsolutePath());
		}
		FileInputStream inputStream = new FileInputStream(file.getAbsolutePath());
		
        String text = IOUtils.toString(inputStream);
        Document doc = Jsoup.parse(text);
        inputStream.close();
        
        return extractUrls(doc);
	}
	
	public static Set<ExtractedUrl> extractUrls(Document doc) {
		Set<ExtractedUrl> extractedUrls = new HashSet<ExtractedUrl>();
		for(UrlExtraction enumElement : UrlExtraction.values()){
			Elements links = doc.select("a[href*=" +enumElement.getDefinition() + "]");
			for(Element element : links) {
				if(element.attr("href") != null){
					ExtractedUrl item = new ExtractedUrl(element.attr("href"), enumElement);
					extractedUrls.add(item);
				}
			}
		}
		return extractedUrls;
	}
	
	public static WebProvider inferWebProvider(SiteCrawl siteCrawl) {
		Set<WebProvider> wps = siteCrawl.getWebProviders();
//		System.out.println("wp size : " + wps.size());
//		for(WebProvider wp : wps) {
//			System.out.println("wp : " + wp);
//		}
		
		if(wps.size() == 1) {
			WebProvider wp = wps.iterator().next();
			if(!wp.equals(WebProvider.NONE))
				return wp;
		}
		
		WebProvider result = WebProviderInference.inferFromManualRules(siteCrawl);
		if(result != null && result != WebProvider.NONE) {
			return result;
		}
		for(WebProviderInference criteria : WebProviderInference.probableInferences) {
//			System.out.println("checking criteria : " + criteria.getConfirmingMatches());
			boolean criteriaMet = true;
			for(StringMatch sm : criteria.getConfirmingMatches()){
//				System.out.println("checking : " + sm);
				if(!wps.contains(sm)){
					criteriaMet = false;
				}
			}
			
			if(criteriaMet) {
//				System.out.println("criteria met");
				if(result != null && result != WebProvider.NONE){
					return WebProvider.NONE;
				}
				result = criteria.getPrimaryMatch();
			}
		}
		
		return result;
	}
	
	
//	public static Set<ExtractedString> extractLineStrings(File file) throws IOException {
//		if(!file.exists()) {
//			throw new IOException("File does not exist for page with path : " + file.getAbsolutePath());
//		}
//		
//		int numLines = 0;
//		Set<ExtractedString> extractedStrings = new HashSet<ExtractedString>();
//		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
//		    String line;
//		    while ((line = br.readLine()) != null) {
//		    	extractedStrings.addAll(extractStrings(line));
//		    	numLines++;
////		    	System.out.println("numLines : " + numLines);
//		    }
//		}
//		
//		return extractedStrings;
//	}
	
}
