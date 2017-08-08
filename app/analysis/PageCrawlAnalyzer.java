package analysis;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import dao.SitesDAO;
import datadefinitions.OEM;
import datadefinitions.StringExtraction;
import datadefinitions.inventory.InvType;
import datadefinitions.inventory.InventoryTool;
import datadefinitions.newdefinitions.InventoryType;
import datadefinitions.newdefinitions.TestMatch;
import datadefinitions.newdefinitions.WPAttribution;
import global.Global;
import persistence.InventoryNumber;
import persistence.Metatag;
import persistence.PageCrawl;
import sites.persistence.Vehicle;
import sites.utilities.PageCrawlLogic;

public class PageCrawlAnalyzer {
	
	public static final int TITLE_MAX_OPTIMAL_LENGTH = 65;
	public static final int TITLE_MIN_OPTIMAL_LENGTH = 50;
	public static final int META_DESCRIPTION_MAX_OPTIMAL_LENGTH = 120;
	
	private final PageCrawl pageCrawl;
	private final AnalysisConfig config;
	private final SiteCrawlAnalysis siteAnalysis;
	
	private final PageCrawlAnalysis pageAnalysis;
	
	private String text;
	private Document doc;
	List<String> cities;
	
	
	//**** Benchmarking
	long start = 0;
	long last = 0;
	
	public PageCrawlAnalyzer(PageCrawl pageCrawl, AnalysisConfig config) {
		this.pageCrawl = pageCrawl;
		this.pageAnalysis = new PageCrawlAnalysis(pageCrawl);
		this.config = config;
		this.siteAnalysis = null;
	}
	
	public PageCrawlAnalyzer(PageCrawlAnalysis pageAnalysis, SiteCrawlAnalysis siteAnalysis){
		this.pageAnalysis = pageAnalysis;
		this.pageCrawl = pageAnalysis.getPageCrawl();
		this.siteAnalysis = siteAnalysis;
		this.config = siteAnalysis.getConfig();
	}
	
	public synchronized String text()  {
		if(text == null) {
			text = PageCrawlLogic.getText(pageAnalysis.getPageCrawl());
		}
		return text;
	}
	
	public synchronized Document doc() {
		if(doc == null){
			doc = Jsoup.parse(text());
			doc.setBaseUri(pageAnalysis.getPageCrawl().getUrl());
		}
		return doc;
	}
	
	public PageCrawlAnalysis runAnalysis(){
		start = System.currentTimeMillis();
		if(!PageCrawlLogic.fileExists(pageCrawl)){
			return null;
		}
		runBasicAnalysis();
		last = System.currentTimeMillis();
		text();
//		System.out.println("text() elapsed : " + (System.currentTimeMillis() - last));
		last = System.currentTimeMillis();
		doc();
//		System.out.println("doc() elapsed : " + (System.currentTimeMillis() - last));
		last = System.currentTimeMillis();
		runTextAnalysis();
//		System.out.println("text analysis elapsed : " + (System.currentTimeMillis() - last));
		last = System.currentTimeMillis();
		if(config.needsDoc()){
			runDocAnalysis();
		}
//		System.out.println("doc analysis elapsed : " + (System.currentTimeMillis() - last));
		last = System.currentTimeMillis();
		runMetaAnalysis();
		return pageAnalysis;
	}
	
	public void runBasicAnalysis(){
		pageAnalysis.setGoodCrawl(!PageCrawlLogic.isFailedCrawl(pageCrawl));
	}
	
	
	//****************  Text Analysis *************************
	
	public void runTextAnalysis() {
		if(config.getDoGeneralMatches()){
			pageAnalysis.getGeneralMatches().addAll(TextAnalyzer.getGeneralMatches(text()));
		} 
		if(config.getDoWpAttributionMatches()){
			pageAnalysis.getWpAttributions().addAll(TextAnalyzer.getStringMatches(text(), WPAttribution.values()));
		}
		if(config.getDoTestMatches()){
			pageAnalysis.getTestMatches().addAll(TextAnalyzer.getCurrentTestMatches(text));
		}
		if(config.getDoBrandMatches()){
			oemCount();
		}
		if(config.getDoVehicles()){
			vins();
		}
		if(config.getDoPrices()){
			rawPrices();
		}
		if(config.getDoCustomText()){
			customText();
		}
		
	}
	
	public void oemCount() {
		for(OEM oem : OEM.values()){
			Integer count = StringUtils.countMatches(text(), oem.getDefinition());
			pageAnalysis.getOemCounts().put(oem, count);
		}
	}
	
	public void vins(){
		Set<String> vins = TextAnalyzer.getVins(text());
		pageAnalysis.addVins(vins);
	}
	
	//Get price data from raw string values.  May be overridden later in the analysis if an invtype is detected
	public void rawPrices() {
		List<Double> moneyValues = TextAnalyzer.getMoneyValues(text());
		Double highest = 0.0;
		Double total = 0.0;
		for(Double value : moneyValues){
			if(value > highest){
				highest = value;
			}
			total += value;
		}
		if(moneyValues.size() > 0){
			pageAnalysis.setAveragePrice(total/moneyValues.size());
		} else {
			pageAnalysis.setAveragePrice(0);
		}
		pageAnalysis.setNumPrices(moneyValues.size());
		pageAnalysis.setHighestPrice(highest);
	}
	
	public void customText() {
		Set<WPAttribution> canadaProviders = new HashSet<WPAttribution>();
		canadaProviders.add(WPAttribution.VIN_SOLUTIONS);
		canadaProviders.add(WPAttribution.VIN_SOLUTIONS2);
		canadaProviders.add(WPAttribution.E_DEALER_CA);
	}
	
	
	//****************** Document Analysis ************************
	
	private void runDocAnalysis() {
		if(config.getDoLinkTextMatches()){
			pageAnalysis.getLinkTextMatches().addAll(DocAnalyzer.getLinkTextMatches(doc()));
		} 
		if(config.getDoTitleTagScoring()){
			titleTagScore();
		} 
		if(config.getDoH1Score()){
			h1Score();
		} 
		if(config.getDoMetaDescriptionScore()){
			metaDescriptionScore();
		}
		if(config.getDoAltImageTagScore()){
			imageScore();
		}
		if(config.getDoMetaBrandMatches()){
			oemMetaCount();
		}
		if(config.getDoVehicles()){
			vehicles();
		}
		if(config.getDoInventoryNumbers()){
			inventoryCounts();
		}
		if(config.getDoCustomDoc()){
			customDoc();
		}
		
		
	}
	
	public void titleTagScore() {
		Element title = getTitle();
		if(title == null){ return; }
		pageAnalysis.setTitleText(title.text());
		pageAnalysis.setHasTitle(true);
		int length = StringUtils.length(title.text());
		if(length >= TITLE_MIN_OPTIMAL_LENGTH && length <= TITLE_MAX_OPTIMAL_LENGTH) {
			pageAnalysis.setTitleGoodLength(true);
		}
		pageAnalysis.setTitleContainsCity(TextAnalyzer.containsCity(title.text(), siteAnalysis.getCities()));
		pageAnalysis.setTitleContainsState(TextAnalyzer.matchesPattern(StringExtraction.STATE_ABBR.getPattern(), title.text()));
		pageAnalysis.setTitleContainsMake(TextAnalyzer.matchesPattern(StringExtraction.MAKE.getPattern(), title.text()));
	}
	
	public void h1Score() {
		Element h1 = getH1();
		if(h1 == null){ return; }
		pageAnalysis.setH1Text(h1.text());
		pageAnalysis.setHasH1(true);
		pageAnalysis.setH1ContainsCity(TextAnalyzer.containsCity(h1.text(), siteAnalysis.getCities()));
		pageAnalysis.setH1ContainsState(TextAnalyzer.matchesPattern(StringExtraction.STATE_ABBR.getPattern(), h1.text()));
		pageAnalysis.setH1ContainsMake(TextAnalyzer.matchesPattern(StringExtraction.MAKE.getPattern(), h1.text()));
	}
	
	public void metaDescriptionScore() {
		Element metaDescription = getMetaDescription();
		if(metaDescription == null){ return; }
		pageAnalysis.setMetaDescriptionText(metaDescription.attr("content"));
		pageAnalysis.setHasMetaDescription(true);
		int length = StringUtils.length(metaDescription.outerHtml());
		if(length <= META_DESCRIPTION_MAX_OPTIMAL_LENGTH ) {
			pageAnalysis.setMetaDescriptionGoodLength(true);
		}
		pageAnalysis.setMetaDescriptionContainsCity(TextAnalyzer.containsCity(metaDescription.outerHtml(), siteAnalysis.getCities()));
		pageAnalysis.setMetaDescriptionContainsState(TextAnalyzer.matchesPattern(StringExtraction.STATE_ABBR.getPattern(), metaDescription.outerHtml()));
		pageAnalysis.setMetaDescriptionContainsMake(TextAnalyzer.matchesPattern(StringExtraction.MAKE.getPattern(), metaDescription.outerHtml()));
	}
	
	public void imageScore() {
		Elements images = doc.select("img");
		int numAltImages = 0;
		for(Element image : images) {
			if(!StringUtils.isEmpty(image.attr("alt"))){
				numAltImages++;
			}
		}
		pageAnalysis.setNumAltImages(numAltImages);
		pageAnalysis.setNumImages(images.size());
	}
	
	public void oemMetaCount() {
		Elements metatags = doc().select("meta");
		for(OEM oem : OEM.values()){
			Integer metaCount = 0;
			for(Element metatag : metatags){
				metaCount += TextAnalyzer.hasOccurrence(metatag.outerHtml(), oem.getPattern()) ? 1 : 0;
			}
			pageAnalysis.getOemMetaCounts().put(oem, metaCount);
		}
	}
	
	public void vehicles() {
		InvType invType = pageAnalysis.getPageCrawl().getInvType();
		if(invType != null){
			InventoryTool tool = invType.getTool();
			Set<Vehicle> vehicles = tool.getVehicles(doc());
//			System.out.println("got vehicles for pageCrawl " + pageCrawl.getPageCrawlId() + " : " + vehicles.size());
			for(Vehicle vehicle : vehicles){
				pageAnalysis.addOrUpdateVehicle(vehicle);
			}
//			pageAnalysis.setVehicles(vehicles);
		}
	}
	
	public void inventoryCounts() {
		InvType invType = pageAnalysis.getPageCrawl().getInvType();
		if(invType != null){
			InventoryTool tool = invType.getTool();
			pageAnalysis.setInventoryCount(tool.getCount(doc()));
		}
	}

	public void customDoc() {
		
	}
	
	//********************* Meta Analysis ***********************
	
	public void runMetaAnalysis() {
		
		if(config.getDoUrlScoring()){
			urlScoring();
		}
		if(config.getDoPrices()){
			invPrices();
		}
	}
	
	public void urlScoring() {
		String url = pageAnalysis.getPageCrawl().getUrl();
		pageAnalysis.setUrlContainsCity(TextAnalyzer.containsCity(url, siteAnalysis.getCities()));
		pageAnalysis.setUrlContainsState(TextAnalyzer.matchesPattern(StringExtraction.STATE_ABBR.getPattern(), url));
		pageAnalysis.setUrlContainsMake(TextAnalyzer.matchesPattern(StringExtraction.MAKE.getPattern(), url));
	}
	
	//Overrides prices gotten through raw dollar amount strings, and instead uses price data from parsed vehicles, if present
	public void invPrices() {
		if(pageCrawl.getInvType() == null || pageAnalysis.getVehicles().size() < 1){
			return;
		}
		double highest = 0.0;
		double total = 0.0;
		for(Vehicle vehicle : pageAnalysis.getVehicles()){
			double value = vehicle.getOfferedPrice();
			if(!(value > 0)){
				value = vehicle.getMsrp();
			}
			if(value > highest){
				highest = value;
			}
			total += value;
		}
		if(pageAnalysis.getVehicles().size() > 0){
			pageAnalysis.setAveragePrice(total/pageAnalysis.getVehicles().size());
		} else {
			pageAnalysis.setAveragePrice(0);
		}
		pageAnalysis.setNumPrices(pageAnalysis.getVehicles().size());
		pageAnalysis.setHighestPrice(highest);
		
	}
	
	//********************* Helpers *********************
	
	public Element getTitle(){
		Elements titles = doc().select("title");
		if(titles.size() > 0) {
			return titles.first();
		}
		return null;
	}
	
	public Element getH1(){
		Elements titles = doc().select("h1");
		if(titles.size() > 0) {
			return titles.first();
		}
		return null;
	}
	
	public Element getMetaDescription() {
		Elements metatags = doc().select("meta");
		for(Element tagElement : metatags) {
			if(StringUtils.equals(tagElement.attr("name"), "description")){
				return tagElement;
			}
		}
		return null;
	}
	
	
	//***************  Legacy code ****************************
	
	
	/************************  Legacy Doc Analysis *****************************/
	
	public static void fillMetatags(PageCrawl pageCrawl, Document doc) {
		Elements metatags = doc.select("meta");
		for(Element tagElement : metatags) {
			Metatag tagEntity = new Metatag();
			tagEntity.setContent(tagElement.attr("content"));
			tagEntity.setHttpEquiv(tagElement.attr("http-equiv"));
			tagEntity.setItemprop(tagElement.attr("itemprop"));
			tagEntity.setName(tagElement.attr("name"));
			tagEntity.setProperty(tagElement.attr("property"));
			tagEntity.setCharset(tagElement.attr("character_set"));
			tagEntity.setScheme(tagElement.attr("scheme"));
			tagEntity.setRaw(tagElement.outerHtml());
		}
	}
	
}
