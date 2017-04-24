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

public class PageCrawlAnalyzer {
	
	public static final int TITLE_MAX_OPTIMAL_LENGTH = 65;
	public static final int TITLE_MIN_OPTIMAL_LENGTH = 50;
	public static final int META_DESCRIPTION_MAX_OPTIMAL_LENGTH = 120;
	
	private PageCrawlAnalysis pageAnalysis;
	private AnalysisConfig config;
	private SiteCrawlAnalysis siteAnalysis;
	private String text;
	private Document doc;
	List<String> cities;
	
	public PageCrawlAnalyzer(PageCrawlAnalysis pageAnalysis) {
		this.pageAnalysis = pageAnalysis;
		this.siteAnalysis = pageAnalysis.getSiteCrawlAnalysis();
		this.config = siteAnalysis.getConfig();
	}
	
	public static String getText(PageCrawl pageCrawl){
		String filename = pageCrawl.getFilename();
		try(FileInputStream inputStream = new FileInputStream(filename)){
			return IOUtils.toString(inputStream, "UTF-8");	
		} catch(IOException e){
			throw new RuntimeException(e);
		}
	}
	
	public synchronized String text()  {
		if(text == null) {
			try {
				String filename = pageAnalysis.getPageCrawl().getFilename();
				FileInputStream inputStream = new FileInputStream(filename);
		        text = IOUtils.toString(inputStream, "UTF-8");
		        inputStream.close();
			} catch(Exception e){
				throw new RuntimeException(e);
			}
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
	
	public synchronized List<String> cities() {
		if(cities == null) {
			cities = SitesDAO.findCities(siteAnalysis.getSiteCrawl().getSite().getSiteId());
		}
		return cities;
	}
	
	
	public PageCrawlAnalysis runAnalysis(){
		
		
        runTextAnalysis();
		if(config.needsDoc()){
			runDocAnalysis();
		}
		runMetaAnalysis();
		return pageAnalysis;
	}
	
	
	//****************  Text Analysis *************************
	
	public void runTextAnalysis() {
		if(config.getDoGeneralMatches()){
			pageAnalysis.getGeneralMatches().addAll(TextAnalyzer.getGeneralMatches(text()));
		} 
		if(config.getDoWpAttributionMatches()){
			pageAnalysis.getWpAttributions().addAll(TextAnalyzer.getMatches(text(), WPAttribution.values()));
		} 
		if(config.getDoTestMatches()){
			pageAnalysis.getTestMatches().addAll(TextAnalyzer.getMatches(text(), TestMatch.getCurrentMatches()));
		} 
		if(config.getDoBrandMatches()){
			oemCount();
		}
		if(config.getDoVehicles()){
			vins();
		}
		if(config.getDoCustomText()){
			customText();
		}
	}
	
	public void oemCount() {
		for(OEM oem : OEM.values()){
			Integer count = TextAnalyzer.countOccurrences(text(), oem.getPattern());
			pageAnalysis.getOemCounts().put(oem, count);
		}
	}
	
	public void vins(){
		pageAnalysis.setVins(TextAnalyzer.getVins(text()));
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
		pageAnalysis.setTitleContainsCity(TextAnalyzer.containsCity(title.text(), cities()));
		pageAnalysis.setTitleContainsState(TextAnalyzer.matchesPattern(StringExtraction.STATE_ABBR.getPattern(), title.text()));
		pageAnalysis.setTitleContainsMake(TextAnalyzer.matchesPattern(StringExtraction.MAKE.getPattern(), title.text()));
	}
	
	public void h1Score() {
		Element h1 = getH1();
		if(h1 == null){ return; }
		pageAnalysis.setH1Text(h1.text());
		pageAnalysis.setHasH1(true);
		pageAnalysis.setH1ContainsCity(TextAnalyzer.containsCity(h1.text(), cities()));
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
		pageAnalysis.setMetaDescriptionContainsCity(TextAnalyzer.containsCity(metaDescription.outerHtml(), cities()));
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
			pageAnalysis.setVehicles(tool.getVehicles(doc()));
		}
	}

	public void customDoc() {
		
	}
	
	//********************* Meta Analysis ***********************
	
	public void runMetaAnalysis() {
		
		if(config.getDoUrlScoring()){
			urlScoring();
		}
	}
	
	public void urlScoring() {
		String url = pageAnalysis.getPageCrawl().getUrl();
		pageAnalysis.setUrlContainsCity(TextAnalyzer.containsCity(url, cities()));
		pageAnalysis.setUrlContainsState(TextAnalyzer.matchesPattern(StringExtraction.STATE_ABBR.getPattern(), url));
		pageAnalysis.setUrlContainsMake(TextAnalyzer.matchesPattern(StringExtraction.MAKE.getPattern(), url));
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
	
	
	/************************  Legacy Text Analysis  **************************/
	
	public static void textAnalysis(PageCrawl pageCrawl, String text){
		getInventoryNumbers(pageCrawl, text);
	}	
	
	public static void getInventoryNumbers(PageCrawl pageCrawl, String text) {
//		System.out.println("getting inventory numbers");
		InventoryNumber invNumber = null;
		for(InventoryType enumElement : InventoryType.values()){
			Matcher matcher = enumElement.getPattern().matcher(text);
	    	while (matcher.find()) {
	    		if(invNumber != null){
	    			throw new IllegalStateException("Found multiple inventory values for a single page with id : " + pageCrawl.getPageCrawlId());
	    		}
	    		if(pageCrawl.getInventoryNumber() != null){
	    			invNumber = pageCrawl.getInventoryNumber();
	    			
	    		}else {
	    			invNumber = new InventoryNumber();
	    		}
	    		invNumber.setInventoryType(enumElement);
	    		invNumber.setCount(Integer.parseInt(matcher.group(1)));
	    		pageCrawl.setInventoryNumber(invNumber);
	    	}
		}
	}
	
	
	
	
	
	
	
	
	
	
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
