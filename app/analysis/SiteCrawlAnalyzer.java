package analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
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
import dao.SitesDAO;
import datadefinitions.OEM;
import datadefinitions.inventory.InvType;
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
import play.db.jpa.JPA;
import utilities.Tim;
import utilities.UrlUtils;

public class SiteCrawlAnalyzer {
	
	private final SiteCrawlAnalysis analysis;
	private final SiteCrawl siteCrawl;
	private final AnalysisConfig config;
	
	private Set<String> cities;
	
	public SiteCrawlAnalyzer(SiteCrawlAnalysis analysis){
		this.analysis = analysis;
		this.siteCrawl = analysis.getSiteCrawl();
		this.config = analysis.getConfig();
	}
	
	public SiteCrawlAnalysis runAnalysis(){
		analysis.setAnalysisDate(new Date());
		if(config.getAnalysisMode() == AnalysisMode.PAGED){
			pagedMode();
		}
		return analysis;
	}
	
	public void pagedMode() {
//		Tim.start();
		System.out.println("running paged Sitecrawl analysis : " + analysis.getSiteCrawl().getSeed());
		try{
			analysis.addCities(SitesDAO.findCities(siteCrawl.getSite().getSiteId()));
			runPageAnalyses();
			clearAggregations();
			aggregatePageAnalyses();
			aggregateInventory(analysis);
			calculateCapDbScores(analysis);
		} catch(Exception e){
			System.out.println("Exception in analysis : " + e.getClass() + " : " + e.getMessage());
			e.printStackTrace(); 
		}
//		Tim.end();
//		System.out.println("finished");
	}
	
	private void runPageAnalyses(){
		for(PageCrawl pageCrawl : siteCrawl.getPageCrawls()){
			if(pageCrawl.getFilename() != null){
				runPageAnalysis(pageCrawl);
			}
		}
	}
	
	private void runPageAnalysis(PageCrawl pageCrawl){
		PageCrawlAnalysis pageAnalysis = analysis.getForPageCrawl(pageCrawl);
		if(pageAnalysis != null && config.isExcludePageAnalysisIfPresent()){
			return;
		} else if(pageAnalysis == null) {
			pageAnalysis = new PageCrawlAnalysis(pageCrawl);
			pageAnalysis.setSiteCrawlAnalysis(analysis);
			pageAnalysis = JPA.em().merge(pageAnalysis);
			analysis.addPageCrawlAnalysis(pageAnalysis);
		} 
		
		PageCrawlAnalyzer pageAnalyzer = new PageCrawlAnalyzer(pageAnalysis, analysis);
		pageAnalyzer.runAnalysis();
	}
	
	//TODO finish all the clearing
	private void clearAggregations() {
		analysis.getVehicles().clear();
		analysis.getVins().clear();
		analysis.setInvTypes(null);
	}
	
	private void aggregatePageAnalyses() {
		int numGoodCrawls = 0;
		
		int numHaveTitle = 0;
		int numTitleGoodLength = 0;
		int numTitleContainsCity = 0;
		int numTitleContainsState = 0;
		int numTitleContainsMake = 0;
		
		int numUrlContainsCity = 0;
		int numUrlContainsState = 0;
		int numUrlContainsMake = 0;
		
		int numHaveH1 = 0;
		int numH1ContainsCity = 0;
		int numH1ContainsState = 0;
		int numH1ContainsMake = 0;
		
		int numHaveMetaDescription = 0;
		int numMetaDescriptionGoodLength = 0;
		int numMetaDescriptionContainsCity = 0;
		int numMetaDescriptionContainsState = 0;
		int numMetaDescriptionContainsMake = 0;
		
		int totalImages = 0;
		int totalAltImages = 0;
		
		Set<String> h1s = new HashSet<String>();
		Set<String> titles = new HashSet<String>();
		Set<String> urls = new HashSet<String>();
		Set<String> metas = new HashSet<String>();
		
		for(OEM oem : OEM.values()){
			analysis.getOemCounts().remove(oem);
			analysis.getOemMetaCounts().remove(oem);
		}
		for(PageCrawlAnalysis pageAnalysis : analysis.getPageAnalyses()){
			analysis.getGeneralMatches().addAll(pageAnalysis.getGeneralMatches());
			analysis.getLinkTextMatches().addAll(pageAnalysis.getLinkTextMatches());
			analysis.getTestMatches().addAll(pageAnalysis.getTestMatches());
			analysis.getWpAttributions().addAll(pageAnalysis.getWpAttributions());
			
			for(OEM oem : OEM.values()){
				Integer pageOemCount = pageAnalysis.getOemCounts().get(oem);
				Integer pageOemMetaCount = pageAnalysis.getOemMetaCounts().get(oem);
				Integer siteOemCount = analysis.getOemCounts().get(oem);
				Integer siteOemMetaCount = analysis.getOemMetaCounts().get(oem);
				if(pageOemCount != null && pageOemCount > 0){
					siteOemCount = pageOemCount + (siteOemCount == null ? 0 : siteOemCount);
					analysis.getOemCounts().put(oem, siteOemCount);
				}
				if(pageOemCount != null && pageOemCount > 0){
					siteOemMetaCount = pageOemMetaCount + (siteOemMetaCount == null ? 0 : siteOemMetaCount);
					analysis.getOemMetaCounts().put(oem, siteOemMetaCount);
				}
				
			}
			numGoodCrawls += (pageAnalysis.getGoodCrawl() ? 1 : 0);
			
			numHaveTitle += (pageAnalysis.getHasTitle() ? 1 : 0);
			numTitleGoodLength += (pageAnalysis.getTitleGoodLength() ? 1 : 0);
			numTitleContainsCity += (pageAnalysis.getTitleContainsCity() ? 1 : 0);
			numTitleContainsState +=  (pageAnalysis.getTitleContainsState() ? 1 : 0);
			numTitleContainsMake += (pageAnalysis.getTitleContainsMake() ? 1 : 0);
			
			numUrlContainsCity += (pageAnalysis.getUrlContainsCity() ? 1 : 0);
			numUrlContainsState += (pageAnalysis.getUrlContainsState() ? 1 : 0);
			numUrlContainsMake += (pageAnalysis.getUrlContainsMake() ? 1 : 0);
			
			numHaveH1 += (pageAnalysis.getHasH1() ? 1 : 0);
			numH1ContainsCity += (pageAnalysis.getH1ContainsCity() ? 1 : 0);
			numH1ContainsState += (pageAnalysis.getH1ContainsState() ? 1 : 0);
			numH1ContainsMake += (pageAnalysis.getH1ContainsMake() ? 1 : 0);
			
			numHaveMetaDescription += (pageAnalysis.getHasMetaDescription() ? 1 : 0);
			numMetaDescriptionGoodLength += (pageAnalysis.getMetaDescriptionGoodLength() ? 1 : 0);
			numMetaDescriptionContainsCity += (pageAnalysis.getMetaDescriptionContainsCity() ? 1 : 0);
			numMetaDescriptionContainsState += (pageAnalysis.getMetaDescriptionContainsState() ? 1 : 0);
			numMetaDescriptionContainsMake += (pageAnalysis.getMetaDescriptionContainsMake() ? 1 : 0);
			
			totalImages += pageAnalysis.getNumImages();
			totalAltImages += pageAnalysis.getNumAltImages();
			
			h1s.add(pageAnalysis.getH1Text());
			titles.add(pageAnalysis.getTitleText());
			urls.add(UrlUtils.removeQueryString(pageAnalysis.getPageCrawl().getUrl()));
			metas.add(pageAnalysis.getMetaDescriptionText());
		}
		analysis.setNumCrawls(analysis.getPageAnalyses().size());
		analysis.setNumGoodCrawls(numGoodCrawls);
		
		analysis.setNumHaveTitle(numHaveTitle);
		analysis.setNumTitleGoodLength(numTitleGoodLength);
		analysis.setNumTitleContainsCity(numTitleContainsCity);
		analysis.setNumTitleContainsState(numTitleContainsState);
		analysis.setNumTitleContainsMake(numTitleContainsMake);
		analysis.setNumUniqueTitles(titles.size());
		
		analysis.setNumUrlContainsCity(numUrlContainsCity);
		analysis.setNumUrlContainsState(numUrlContainsState);
		analysis.setNumUrlContainsMake(numUrlContainsMake);
		analysis.setNumUniqueUrls(urls.size());
		
		analysis.setNumHaveH1(numHaveH1);
		analysis.setNumH1ContainsCity(numH1ContainsCity);
		analysis.setNumH1ContainsState(numH1ContainsState);
		analysis.setNumH1ContainsMake(numH1ContainsMake);
		analysis.setNumUniqueH1s(h1s.size());
		
		analysis.setNumHaveMetaDescription(numHaveMetaDescription);
		analysis.setNumMetaDescriptionGoodLength(numMetaDescriptionGoodLength);
		analysis.setNumMetaDescriptionContainsCity(numMetaDescriptionContainsCity);
		analysis.setNumMetaDescriptionContainsState(numMetaDescriptionContainsState);
		analysis.setNumMetaDescriptionContainsMake(numMetaDescriptionContainsMake);
		analysis.setNumUniqueMetaDescriptions(metas.size());
		
		analysis.setTotalImages(totalImages);
		analysis.setTotalAltImages(totalAltImages);
	}
	
	public static void aggregateInventory(SiteCrawlAnalysis analysis){
		int maxCount = 0;
		Set<InvType> invTypes = new HashSet<InvType>();
		for(PageCrawlAnalysis pageAnalysis : analysis.getPageAnalyses()){
			if(pageAnalysis.getPageCrawl().getInvType() != null){
				invTypes.add(pageAnalysis.getPageCrawl().getInvType());
			}
			analysis.addVehicles(pageAnalysis.getVehicles());
			analysis.addVins(pageAnalysis.getVins());
			if(pageAnalysis.getInventoryCount() > maxCount){
				maxCount = pageAnalysis.getInventoryCount();
			}
			
			PageCrawl newRoot = analysis.getSiteCrawl().getNewInventoryRoot();
			PageCrawl usedRoot = analysis.getSiteCrawl().getUsedInventoryRoot();
			if(newRoot != null && pageAnalysis.getPageCrawl().getPageCrawlId() == newRoot.getPageCrawlId()){
				analysis.setNewRootInventoryCount(pageAnalysis.getInventoryCount());
//				System.out.println("new Root count " + analysis.getNewRootInventoryCount() + "  of invtype " + newRoot.getInvType() + " at " + pageAnalysis.getPageCrawl().getUrl());
			}
			if(usedRoot != null && pageAnalysis.getPageCrawl().getPageCrawlId() == usedRoot.getPageCrawlId()){
				analysis.setUsedRootInventoryCount(pageAnalysis.getInventoryCount());
//				System.out.println("usedRoot count " + analysis.getUsedRootInventoryCount() + "  of invtype " + usedRoot.getInvType() + " at " + pageAnalysis.getPageCrawl().getUrl());
			}
			
		}
		if(invTypes.size() > 0){
			analysis.setInvTypes(invTypes.toString());
		}
		analysis.setNumVehicles(analysis.getVehicles().size());
		analysis.setNumVins(analysis.getVins().size());
		analysis.setHighestInventoryCount(maxCount);
		analysis.setCombinedRootInventoryCount(analysis.getNewRootInventoryCount() + analysis.getUsedRootInventoryCount());
		aggregatePriceData(analysis);
	}
	
	public static void aggregatePriceData(SiteCrawlAnalysis analysis) {
		double highestPrice = 0;
		double totalHighestPrices = 0;
		int numHighestPrices = 0;
		double totalPrices = 0;
		double totalNumPrices = 0;
		for(PageCrawlAnalysis pageAnalysis : analysis.getPageAnalyses()){
			if(pageAnalysis.getHighestPrice() > highestPrice){
				highestPrice = pageAnalysis.getHighestPrice();
			}
			if(pageAnalysis.getHighestPrice() > 0){
				numHighestPrices++;
			}
			totalHighestPrices += pageAnalysis.getHighestPrice();
			totalNumPrices += pageAnalysis.getNumPrices();
			totalPrices += pageAnalysis.getAveragePrice() * pageAnalysis.getNumPrices();
		}
		analysis.setHighestPrice(highestPrice);
		analysis.setNumPrices(totalNumPrices);
		if(numHighestPrices > 0){
			analysis.setAverageHighestPrice(totalHighestPrices / numHighestPrices);
		}
		if(totalNumPrices > 0){
			analysis.setAveragePrice(totalPrices / totalNumPrices);
		}
		
	}
	
	public static void calculateCapDbScores(SiteCrawlAnalysis analysis) {
		int numPages = analysis.getPageAnalyses().size();
		if(numPages == 0){
			numPages = 1;
		}
		if(numPages > 0){ //Avoid division by 0.  Default scores are 0, so no need to set anything else
			analysis.setNumGoodPercentage((analysis.getNumGoodCrawls() * 100) / (numPages));
			
			analysis.setUrlUniqueScore((analysis.getNumUniqueUrls() * 100) / (numPages));
			analysis.setUrlLocationScore(((analysis.getNumUrlContainsCity() + analysis.getNumUrlContainsState() + analysis.getNumUrlContainsMake() ) * 100) / (numPages * 3));
			analysis.setUrlCleanScore(0);
			
			analysis.setTitleUniqueScore((analysis.getNumUniqueTitles() * 100) / (numPages));
			analysis.setTitleLengthScore((analysis.getNumTitleGoodLength() * 100) / (numPages ));
			analysis.setTitleContentScore(((analysis.getNumTitleContainsCity() + analysis.getNumTitleContainsState() + analysis.getNumTitleContainsMake() ) * 100) / (numPages * 3));
			
			analysis.setH1UniqueScore((analysis.getNumUniqueH1s() * 100) / (numPages));
			analysis.setH1ContentScore(((analysis.getNumH1ContainsCity() + analysis.getNumH1ContainsState() + analysis.getNumH1ContainsMake() ) * 100) / (numPages * 3));
			
			
			analysis.setMetaDescriptionUniqueScore((analysis.getNumUniqueMetaDescriptions() * 100) / (numPages));
			analysis.setMetaDescriptionLengthScore((analysis.getNumMetaDescriptionGoodLength() * 100) / (numPages));
			analysis.setMetaDescriptionContentScore(((analysis.getNumMetaDescriptionContainsCity() + analysis.getNumMetaDescriptionContainsState() + analysis.getNumMetaDescriptionContainsMake() ) * 100) / (numPages * 3));
		}
		if(analysis.getTotalImages() > 0){	//Avoid division by 0 again
			analysis.setAltImageScore((analysis.getTotalAltImages() * 100) / (analysis.getTotalImages()));
		} else{
			analysis.setAltImageScore(0);
		}
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
//		        siteCrawl.setGeneralMatches(TextAnalyzer.getGeneralMatches(text));
			}
				
		}
	}
	
	public static void getWebProviderAttributions(SiteCrawl siteCrawl, String text) {
//		for(WPAttribution wp : WPAttribution.values()){
//			if(text.contains(wp.getDefinition())){
//				siteCrawl.addWpAttribution(wp);
//			}
//		}
	}
	
	public static void getWebProviderClues(SiteCrawl siteCrawl, String text) {
//		for(WPClue wp : WPClue.values()){
//			if(text.contains(wp.getDefinition())){
//				siteCrawl.addWpClue(wp);
//			}
//		}
	}
		
	
	
	
	/**************************** Aggregation  ************************************/
	
	
	public static void aggregateInventoryNumbers(SiteCrawl siteCrawl) {
//		for(PageCrawl pageCrawl : siteCrawl.getPageCrawls()){
//			InventoryNumber invNumber = pageCrawl.getInventoryNumber();
//			if(invNumber != null){
//				siteCrawl.getInventoryNumbers().add(pageCrawl.getInventoryNumber());
//			}
//		}
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
//		if(siteCrawl.getWpAttributions().size() == 1){
//			WPAttribution wpAttribution = (WPAttribution)siteCrawl.getWpAttributions().toArray()[0];
//			siteCrawl.setWebProvider(wpAttribution.getWp());
//		}
//		else if(siteCrawl.getWpAttributions().size() > 1){
//			siteCrawl.setWebProvider(WebProvider.MULTIPLE);
//		}
//		else{
//			inferFromClues(siteCrawl);
//		}
	}
	
	public static void inferFromClues(SiteCrawl siteCrawl){
//		if(siteCrawl.getWpClues().size() == 1){
//			siteCrawl.setWebProvider(((WPClue)siteCrawl.getWpClues().toArray()[0]).getWp());
//		}
//		else if(siteCrawl.getWpClues().size() > 1){
//			//TODO fill in some nice inferences here
//		}
	}
	
	public static void getInventoryType(SiteCrawl siteCrawl) {
//		List<InventoryType> invTypes = new ArrayList<InventoryType>();
//		for(InventoryNumber num : siteCrawl.getInventoryNumbers()){
//			InventoryType temp = num.getInventoryType();
//			if(!invTypes.contains(temp)){
//				invTypes.add(temp);
//			}
//		}
//		siteCrawl.setInventoryType(inferInvTypeFromMultiple(invTypes));
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
//		InventoryType invType = siteCrawl.getInventoryType();
//		if(invType == null){
//			return;
//		}
//		for(PageCrawl pageCrawl : siteCrawl.getPageCrawls()){
//			String path = pageCrawl.getPath();
//			String query = pageCrawl.getQuery();
//			if(query != null){
//				query = "?" + query;
//			}
//			String pathAndQuery = path + query;
//			if(used && invType.getUsedPath().equals(pathAndQuery)){ 
//				siteCrawl.setUsedInventoryPage(pageCrawl);
//			}
//			if(!used && invType.getNewPath().equals(pathAndQuery)){
//				siteCrawl.setNewInventoryPage(pageCrawl);
//			}
//		}
	}
	
	public static void getMaxInventoryCount(SiteCrawl siteCrawl) {
//		InventoryNumber max = null; 
//		for(InventoryNumber invNumber: siteCrawl.getInventoryNumbers()){
//			if(max == null || invNumber.getCount() > max.getCount()){
//				max = invNumber;
//			}
//		}
//		siteCrawl.setMaxInventoryCount(max);
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
		        
		        
//		        Set<Staff> allStaff = StaffExtractor.extractStaff(doc, siteCrawl.getWebProviders());
//		        siteCrawl.addStaff(allStaff);
////		        System.out.println("allstaff after set : " + siteCrawl.getAllStaff().size());
//		        siteCrawl.addExtractedUrls(DocAnalyzer.extractUrls(doc));
		        
//		        siteCrawl.addInventoryNumbers(invNumbers);
		        
//		        pageCrawl.setBrandMatchCounts(getBrandMatchCounts(text));
		        
		        numFiles++;
		        
			}
		}
		
//		siteCrawl.setBrandMatchAverages(getBrandMatchAverages(siteCrawl));
//		siteCrawl.setNumRetrievedFiles(numFiles);
//		siteCrawl.setNumLargeFiles(numLargeFiles);
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
