package analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import analysis.AnalysisConfig.AnalysisMode;
import datadefinitions.OEM;
import datadefinitions.newdefinitions.InventoryType;
import datadefinitions.newdefinitions.WPAttribution;
import datadefinitions.newdefinitions.WPClue;
import datadefinitions.newdefinitions.WebProvider;
import datatransfer.Amalgamater;
import global.Global;
import persistence.InventoryNumber;
import persistence.PageCrawl;
import persistence.SiteCrawl;
import persistence.Staff;
import utilities.DSFormatter;

public class SiteCrawlAnalyzer {
	
	public static void runSiteCrawlAnalysis(SiteCrawlAnalysis analysis) throws IOException {
		
		if(analysis.getConfig().getAnalysisMode() == AnalysisMode.PAGED){
			pagedMode(analysis);
		}
	}
	
	public static void blobMode(SiteCrawlAnalysis analysis) {
		
		
	}
	
	public static void pagedMode(SiteCrawlAnalysis analysis) throws IOException {
		System.out.println("running paged Sitecrawl analysis");
		SiteCrawl siteCrawl = analysis.getSiteCrawl();
		for(PageCrawl pageCrawl : siteCrawl.getPageCrawls()){
			if(pageCrawl.getFilename() != null){
				PageCrawlAnalysis pageAnalysis = analysis.getForPageCrawl(pageCrawl);
				if(pageAnalysis == null) {
					pageAnalysis = new PageCrawlAnalysis(pageCrawl);
					analysis.getPageAnalyses().add(pageAnalysis);
				} 
				PageCrawlAnalyzer.runPageCrawlAnalysis(analysis, pageAnalysis);
			}
		}
		aggregatePageAnalyses(analysis);
	}
	
	public static void aggregatePageAnalyses(SiteCrawlAnalysis analysis) {
		for(PageCrawlAnalysis pageAnalysis : analysis.getPageAnalyses()){
			analysis.getGeneralMatches().addAll(pageAnalysis.getGeneralMatches());
			analysis.getLinkTextMatches().addAll(pageAnalysis.getLinkTextMatches());
			analysis.getTestMatches().addAll(pageAnalysis.getTestMatches());
			analysis.getWpAttributions().addAll(pageAnalysis.getWpAttributions());
		}
	}
	
	public static void doFull(SiteCrawl siteCrawl) throws IOException { 
		doPageCrawlAnalysis(siteCrawl);
		doBlobTextAnalysis(siteCrawl);
		aggregatePageCrawlData(siteCrawl);
		metaAnalysis(siteCrawl);
	}
	
	public static void doCustom(SiteCrawl siteCrawl) throws IOException {
		doBlobTextAnalysis(siteCrawl);
	}
	
	public static void doPageCrawlAnalysis(SiteCrawl siteCrawl) throws IOException{
		for(PageCrawl pageCrawl : siteCrawl.getPageCrawls()) {
			PageCrawlAnalyzer.fullAnalysis(pageCrawl);
		}
//		if(siteCrawl.getNewInventoryPage() != null){
//			PageCrawlAnalyzer.fullAnalysis(siteCrawl.getNewInventoryPage());
//		}
//		if(siteCrawl.getUsedInventoryPage() != null){
//			PageCrawlAnalyzer.fullAnalysis(siteCrawl.getUsedInventoryPage());
//		}
	}
	
	
	
	
	/*****************************   Text Analysis ********************************/
	
	//Only analyzes amalgamated files.  
	public static void doBlobTextAnalysis(SiteCrawl siteCrawl) throws IOException {
		System.out.println("blob text analysis : " + siteCrawl.getSiteCrawlId());
		File storageFolder = new File(Global.getCombinedStorageFolder() + "/" + siteCrawl.getStorageFolder());
		
		if(!storageFolder.exists() || !storageFolder.isDirectory()){
			throw new IllegalArgumentException("No combined data found at location : " + storageFolder.getAbsolutePath());
		}
		
		for(File file : storageFolder.listFiles()) {
			if(file.isFile() && Amalgamater.isAmalgamation(file)){
				FileInputStream inputStream = new FileInputStream(file.getAbsolutePath());
		        String text = IOUtils.toString(inputStream, "UTF-8");
		        inputStream.close();

		        getWebProviderAttributions(siteCrawl, text);
		        getWebProviderClues(siteCrawl, text);
//		        siteCrawl.addExtractedStrings(extractStrings(text));
//		        System.out.println("old web providers");
//		        siteCrawl.setWebProviders(getWebProviders(text));
//		        siteCrawl.setSchedulers(getSchedulers(text));
		        siteCrawl.setGeneralMatches(TextAnalyzer.getGeneralMatches(text));
			}
				
		}
	}
	
	public static void getWebProviderAttributions(SiteCrawl siteCrawl, String text) {
		for(WPAttribution wp : WPAttribution.values()){
			if(text.contains(wp.getDefinition())){
				siteCrawl.addWpAttribution(wp);
			}
		}
	}
	
	public static void getWebProviderClues(SiteCrawl siteCrawl, String text) {
		for(WPClue wp : WPClue.values()){
			if(text.contains(wp.getDefinition())){
				siteCrawl.addWpClue(wp);
			}
		}
	}
		
	
	
	
	/**************************** Aggregation  ************************************/
	
	public static void aggregatePageCrawlData(SiteCrawl siteCrawl){
		getBrandMatchAverages(siteCrawl);
//		aggregateInventoryNumbers(siteCrawl);
	}
	
	public static void getBrandMatchAverages(SiteCrawl siteCrawl){
		Map<OEM, Integer> counts = new HashMap<OEM, Integer>();
		Map<OEM, Integer> metaCounts = new HashMap<OEM, Integer>();
		for(PageCrawl pageCrawl : siteCrawl.getPageCrawls()){
			for(Entry<OEM, Integer> entry : pageCrawl.getBrandMatchCounts().entrySet()){
				if(counts.containsKey(entry.getKey())){
					counts.put(entry.getKey(), entry.getValue() + counts.get(entry.getKey()));
				}
				else{
					counts.put(entry.getKey(), entry.getValue());
				}
			}
			for(Entry<OEM, Integer> entry : pageCrawl.getMetaBrandMatchCounts().entrySet()){
				if(metaCounts.containsKey(entry.getKey())){
					metaCounts.put(entry.getKey(), entry.getValue() + metaCounts.get(entry.getKey()));
				}
				else{
					metaCounts.put(entry.getKey(), entry.getValue());
				}
			}
		}
		Map<OEM, Double> averages = siteCrawl.getBrandMatchAverages();
		Map<OEM, Double> metaAverages = siteCrawl.getMetaBrandMatchAverages();
		Double size = siteCrawl.getPageCrawls().size() * 1.0;
		for(Entry<OEM, Integer> entry : counts.entrySet()){
			Double average = entry.getValue() / size;
			averages.put(entry.getKey(), average);
		}
		for(Entry<OEM, Integer> entry : metaCounts.entrySet()){
			Double average = entry.getValue() / size;
			metaAverages.put(entry.getKey(), average);
		}
	}
	
	public static void aggregateInventoryNumbers(SiteCrawl siteCrawl) {
		for(PageCrawl pageCrawl : siteCrawl.getPageCrawls()){
			InventoryNumber invNumber = pageCrawl.getInventoryNumber();
			if(invNumber != null){
				siteCrawl.getInventoryNumbers().add(pageCrawl.getInventoryNumber());
			}
		}
	}
	
	public static void aggregateImageTags(SiteCrawl siteCrawl){
		Integer urlCityQualifier = 0;
		Integer urlStateQualifier = 0;
		Integer urlMakeQualifier = 0;
		Integer titleCityQualifier = 0;
		Integer titleStateQualifier = 0;
		Integer titleMakeQualifier = 0;
		Integer h1CityQualifier = 0;
		Integer h1StateQualifier = 0;
		Integer h1MakeQualifier = 0;
		Integer metaDescriptionCityQualifier = 0;
		Integer metaDescriptionStateQualifier = 0;
		Integer metaDescriptionMakeQualifier = 0;
		Integer descriptionLength = 0;
		Integer titleLength = 0;
		Integer titleKeywordStuffing = 0;
		Integer urlClean = 0;
		Integer totalImages = 0;
		Integer altImages = 0;
		
		for(PageCrawl pageCrawl : siteCrawl.getPageCrawls()){
			totalImages += pageCrawl.getNumImages();
			altImages += pageCrawl.getNumAltImages();
			urlCityQualifier += pageCrawl.isUrlCityQualifier() ? 1 : 0;
			urlStateQualifier += pageCrawl.isUrlStateQualifier() ? 1 : 0;
			urlMakeQualifier += pageCrawl.isUrlMakeQualifier() ? 1 : 0;
			titleCityQualifier += pageCrawl.isTitleCityQualifier() ? 1 : 0;
			titleStateQualifier += pageCrawl.isTitleStateQualifier() ? 1 : 0;
			titleMakeQualifier += pageCrawl.isTitleMakeQualifier() ? 1 : 0;
			h1CityQualifier += pageCrawl.isH1CityQualifier() ? 1 : 0;
			h1StateQualifier += pageCrawl.isH1StateQualifier() ? 1 : 0;
			h1MakeQualifier += pageCrawl.isH1MakeQualifier() ? 1 : 0;
			metaDescriptionCityQualifier += pageCrawl.isMetaDescriptionCityQualifier() ? 1 : 0;
			metaDescriptionStateQualifier += pageCrawl.isMetaDescriptionStateQualifier() ? 1 : 0;
			metaDescriptionMakeQualifier += pageCrawl.isMetaDescriptionMakeQualifier() ? 1 : 0;
			descriptionLength += pageCrawl.isDescriptionLength() ? 1 : 0;
			titleLength += pageCrawl.isTitleLength() ? 1 : 0;
			titleKeywordStuffing += pageCrawl.isTitleKeywordStuffing() ? 1 : 0;
			urlClean += pageCrawl.isUrlClean() ? 1 : 0;
		}
		siteCrawl.getSiteCrawlStats().setTotalImages(totalImages);
		siteCrawl.getSiteCrawlStats().setAltImages(altImages);
		siteCrawl.getSiteCrawlStats().setUrlCityQualifier(urlCityQualifier);
		siteCrawl.getSiteCrawlStats().setUrlStateQualifier(urlStateQualifier);
		siteCrawl.getSiteCrawlStats().setUrlMakeQualifier(urlMakeQualifier);
		siteCrawl.getSiteCrawlStats().setTitleCityQualifier(titleCityQualifier);
		siteCrawl.getSiteCrawlStats().setTitleStateQualifier(titleStateQualifier);
		siteCrawl.getSiteCrawlStats().setTitleMakeQualifier(titleMakeQualifier);
		siteCrawl.getSiteCrawlStats().setH1CityQualifier(h1CityQualifier);
		siteCrawl.getSiteCrawlStats().setH1StateQualifier(h1StateQualifier);
		siteCrawl.getSiteCrawlStats().setH1MakeQualifier(h1MakeQualifier);
		siteCrawl.getSiteCrawlStats().setMetaDescriptionCityQualifier(metaDescriptionCityQualifier);
		siteCrawl.getSiteCrawlStats().setMetaDescriptionStateQualifier(metaDescriptionStateQualifier);
		siteCrawl.getSiteCrawlStats().setMetaDescriptionMakeQualifier(metaDescriptionMakeQualifier);
		siteCrawl.getSiteCrawlStats().setDescriptionLength(descriptionLength);
		siteCrawl.getSiteCrawlStats().setTitleLength(titleLength);
		siteCrawl.getSiteCrawlStats().setTitleKeywordStuffing(titleKeywordStuffing);
		siteCrawl.getSiteCrawlStats().setUrlClean(urlClean);
		
	}
	
	
	
	
	/*****************************   Meta Analysis  ********************************/
	
	
	public static void metaAnalysis(SiteCrawl siteCrawl) throws MalformedURLException {
//		System.out.println("meta analysis : " + siteCrawl.getSiteCrawlId());
//		checkUniques(siteCrawl);
//		checkLength(siteCrawl);
//		checkContent(siteCrawl);
//		checkImages(siteCrawl);
//		
		inferWP(siteCrawl);
		getExistingInventoryPage(true, siteCrawl);
		getExistingInventoryPage(false, siteCrawl);
		getMaxInventoryCount(siteCrawl);
		
//		uniqueContentScores(siteCrawl);
//		contentLengthScores(siteCrawl);
	}
	
	public static void inferWP(SiteCrawl siteCrawl) {
		if(siteCrawl.getWpAttributions().size() == 1){
			WPAttribution wpAttribution = (WPAttribution)siteCrawl.getWpAttributions().toArray()[0];
			siteCrawl.setWebProvider(wpAttribution.getWp());
		}
		else if(siteCrawl.getWpAttributions().size() > 1){
			siteCrawl.setWebProvider(WebProvider.MULTIPLE);
		}
		else{
			inferFromClues(siteCrawl);
		}
	}
	
	public static void inferFromClues(SiteCrawl siteCrawl){
		if(siteCrawl.getWpClues().size() == 1){
			siteCrawl.setWebProvider(((WPClue)siteCrawl.getWpClues().toArray()[0]).getWp());
		}
		else if(siteCrawl.getWpClues().size() > 1){
			//TODO fill in some nice inferences here
		}
	}
	
	public static void getInventoryType(SiteCrawl siteCrawl) {
		List<InventoryType> invTypes = new ArrayList<InventoryType>();
		for(InventoryNumber num : siteCrawl.getInventoryNumbers()){
			InventoryType temp = num.getInventoryType();
			if(!invTypes.contains(temp)){
				invTypes.add(temp);
			}
		}
		siteCrawl.setInventoryType(inferInvTypeFromMultiple(invTypes));
	}
	
	protected static InventoryType inferInvTypeFromMultiple(List<InventoryType> invTypes){
		if(invTypes.size() == 0)
			return null;
		if(invTypes.size() == 1){
			return invTypes.get(0);
		}
		
		//General patterns -- e.g. Sites often have a secondary page that matches auto trader, but their main inventory is dealer.com
		boolean dealerCom = invTypes.contains(InventoryType.DEALER_COM);
		boolean autoTrader = invTypes.contains(InventoryType.AUTO_TRADER_CA);
		if(invTypes.size() == 2 && dealerCom && autoTrader){
			return InventoryType.DEALER_COM;
		}
		return InventoryType.MULTIPLE;
	}
	
	public static void getExistingInventoryPage(boolean used, SiteCrawl siteCrawl) throws MalformedURLException{
		InventoryType invType = siteCrawl.getInventoryType();
		if(invType == null){
			return;
		}
		for(PageCrawl pageCrawl : siteCrawl.getPageCrawls()){
			String path = pageCrawl.getPath();
			String query = pageCrawl.getQuery();
			if(query != null){
				query = "?" + query;
			}
			String pathAndQuery = path + query;
			if(used && invType.getUsedPath().equals(pathAndQuery)){ 
				siteCrawl.setUsedInventoryPage(pageCrawl);
			}
			if(!used && invType.getNewPath().equals(pathAndQuery)){
				siteCrawl.setNewInventoryPage(pageCrawl);
			}
		}
	}
	
	public static void getMaxInventoryCount(SiteCrawl siteCrawl) {
		InventoryNumber max = null; 
		for(InventoryNumber invNumber: siteCrawl.getInventoryNumbers()){
			if(max == null || invNumber.getCount() > max.getCount()){
				max = invNumber;
			}
		}
		siteCrawl.setMaxInventoryCount(max);
	}
	
	public static void calculateScores(SiteCrawl siteCrawl){
//		SiteCrawlStats stats = siteCrawl.getSiteCrawlStats();
//		stats.setAltImageScore(Math.round(stats.getAltImages() / stats.getTotalImages() * 100));
//		stats.setc;
//		siteCrawl.getSiteCrawlStats().setUrlCityQualifier(urlCityQualifier);
//		siteCrawl.getSiteCrawlStats().setUrlStateQualifier(urlStateQualifier);
//		siteCrawl.getSiteCrawlStats().setUrlMakeQualifier(urlMakeQualifier);
//		siteCrawl.getSiteCrawlStats().setTitleCityQualifier(titleCityQualifier);
//		siteCrawl.getSiteCrawlStats().setTitleStateQualifier(titleStateQualifier);
//		siteCrawl.getSiteCrawlStats().setTitleMakeQualifier(titleMakeQualifier);
//		siteCrawl.getSiteCrawlStats().setH1CityQualifier(h1CityQualifier);
//		siteCrawl.getSiteCrawlStats().setH1StateQualifier(h1StateQualifier);
//		siteCrawl.getSiteCrawlStats().setH1MakeQualifier(h1MakeQualifier);
//		siteCrawl.getSiteCrawlStats().setMetaDescriptionCityQualifier(metaDescriptionCityQualifier);
//		siteCrawl.getSiteCrawlStats().setMetaDescriptionStateQualifier(metaDescriptionStateQualifier);
//		siteCrawl.getSiteCrawlStats().setMetaDescriptionMakeQualifier(metaDescriptionMakeQualifier);
//		siteCrawl.getSiteCrawlStats().setDescriptionLength(descriptionLength);
//		siteCrawl.getSiteCrawlStats().setTitleLength(titleLength);
//		siteCrawl.getSiteCrawlStats().setTitleKeywordStuffing(titleKeywordStuffing);
//		siteCrawl.getSiteCrawlStats().setUrlClean(urlClean);
	}
	
	
	public static void uniqueContentScores(SiteCrawl siteCrawl) {
		Set<String> h1s = new HashSet<String>();
		Set<String> titles = new HashSet<String>();
		Set<String> urls = new HashSet<String>();
		Set<String> metas = new HashSet<String>();
		float total = siteCrawl.getPageCrawls().size();
		for(PageCrawl outer : siteCrawl.getPageCrawls()){
			h1s.add(outer.getH1());
			titles.add(outer.getTitle());
			urls.add(DSFormatter.removeQueryString(outer.getUrl()));
			if(outer.getMetaDescription() != null){
				metas.add(outer.getMetaDescription().getContent());
			}
		}
		siteCrawl.getSiteCrawlStats().setUniqueH1Score(Math.round((h1s.size() / total) * 100));
		siteCrawl.getSiteCrawlStats().setUniqueTitleScore(Math.round((titles.size() / total) * 100));
		siteCrawl.getSiteCrawlStats().setUniqueUrlScore(Math.round((urls.size() / total) * 100));
		siteCrawl.getSiteCrawlStats().setUniqueMetaDescriptionScore(Math.round((metas.size() / total) * 100));
	}
	
	public static void contentLengthScores(SiteCrawl siteCrawl) {
//		System.out.println("checking length : " + siteCrawl.getSiteCrawlId());
		float total = siteCrawl.getPageCrawls().size();
		float titleTotal = 0;
		float metaTotal = 0;
		for(PageCrawl pageCrawl : siteCrawl.getPageCrawls()){
			if(pageCrawl.isTitleLength()) {
				titleTotal++;
			}
			if(pageCrawl.isDescriptionLength()) {
				metaTotal++;
			}
		}
//		siteCrawl.setLengthTitleScore(Math.round((1 - (titleTotal/total)) * 100));
//		siteCrawl.setLengthMetaDescriptionScore(Math.round((1 - (metaTotal/total)) * 100));
	}
	
	public static void checkContent(SiteCrawl siteCrawl) {
		System.out.println("checking content : " + siteCrawl.getSiteCrawlId());
		float total = siteCrawl.getPageCrawls().size() * 3;	//The number of elements in the content
		float url = 0;
		float title = 0;
		float h1 = 0;
		float meta = 0;
		for(PageCrawl pageCrawl : siteCrawl.getPageCrawls()){
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
		
//		siteCrawl.setContentUrlScore(Math.round((url/total) * 100));
//		siteCrawl.setContentTitleScore(Math.round(title/total * 100));
//		siteCrawl.setContentH1Score(Math.round(h1/total * 100));
//		siteCrawl.setContentMetaDescriptionScore(Math.round(meta/total * 100));
		
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
		File storageFolder = new File(Global.getCrawlStorageFolder() + "/" + siteCrawl.getStorageFolder());
		
		if(!storageFolder.exists() || !storageFolder.isDirectory()){
			throw new IllegalArgumentException("No crawl found at location : " + storageFolder.getAbsolutePath());
		}
		
		int numFiles = 0;
		int numLargeFiles = 0;
		for(PageCrawl pageCrawl : siteCrawl.getPageCrawls()) {
			if(StringUtils.isEmpty(pageCrawl.getFilename())){
				continue;
			}
			File file = new File(Global.getCrawlStorageFolder() + "/" + siteCrawl.getStorageFolder() + "/" + pageCrawl.getFilename());
			if(file.isFile() && !FilenameUtils.getExtension(file.getName()).equals("ser")) {
//				System.out.println("checking pagecrawl  : " + pageCrawl.getUrl());
				FileInputStream inputStream = new FileInputStream(file.getAbsolutePath());
		        String text = IOUtils.toString(inputStream, "UTF-8");
		        inputStream.close();
		        Document doc = Jsoup.parse(text);
		        
		        
		        Set<Staff> allStaff = StaffExtractor.extractStaff(doc, siteCrawl.getWebProviders());
		        siteCrawl.addStaff(allStaff);
//		        System.out.println("allstaff after set : " + siteCrawl.getAllStaff().size());
		        siteCrawl.addExtractedUrls(TextAnalyzer.extractUrls(doc));
		        
//		        siteCrawl.addInventoryNumbers(invNumbers);
		        
//		        pageCrawl.setBrandMatchCounts(getBrandMatchCounts(text));
		        
		        numFiles++;
		        
			}
		}
		
//		siteCrawl.setBrandMatchAverages(getBrandMatchAverages(siteCrawl));
		siteCrawl.setNumRetrievedFiles(numFiles);
		siteCrawl.setNumLargeFiles(numLargeFiles);
	}
	
	
	
//	public static Set<WebProvider> getWebProviders(String text) {
//		Set<WebProvider> matches = new HashSet<WebProvider>();
//		for(WebProvider wp : WebProvider.values()){
//			if(text.contains(wp.getDefinition()) && !matches.contains(wp.getDefinition())){
////				System.out.println("matched : " + wp);
//				matches.add(wp);
//			}
//		}
//		return matches;
//	}
	
	
	
//	public static WebProvider inferWebProvider(SiteCrawl siteCrawl) {
//		Set<WebProvider> wps = siteCrawl.getWebProviders();
////		System.out.println("wp size : " + wps.size());
////		for(WebProvider wp : wps) {
////			System.out.println("wp : " + wp);
////		}
//		
//		if(wps.size() == 1) {
//			WebProvider wp = wps.iterator().next();
//			if(!wp.equals(WebProvider.NONE)){
//				System.out.println("returning");
//				return wp;
//			}
//		}
//		
//		WebProvider result = WebProviderInference.inferFromManualRules(siteCrawl);
//		if(result != null && result != WebProvider.NONE) {
//			System.out.println("Inferred from manual rules");
//			return result;
//		}
//		for(WebProviderInference criteria : WebProviderInference.probableInferences) {
////			System.out.println("checking criteria : " + criteria.getConfirmingMatches());
//			boolean criteriaMet = true;
//			for(StringMatch sm : criteria.getConfirmingMatches()){
////				System.out.println("checking : " + sm);
//				if(!wps.contains(sm)){
//					criteriaMet = false;
//				}
//			}
//			
//			if(criteriaMet) {
////				System.out.println("criteria met");
//				if(result != null && result != WebProvider.NONE){
//					return WebProvider.NONE;
//				}
//				result = criteria.getPrimaryMatch();
//			}
//		}
//		
//		return result;
//	}
	
	public static void textAnalysis(SiteCrawl siteCrawl) throws IOException {
		
		
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
