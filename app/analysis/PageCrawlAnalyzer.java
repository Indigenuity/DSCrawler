package analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import datadefinitions.OEM;
import datadefinitions.StringExtraction;
import datadefinitions.newdefinitions.InventoryType;
import global.Global;
import persistence.ImageTag;
import persistence.InventoryNumber;
import persistence.Metatag;
import persistence.PageCrawl;

public class PageCrawlAnalyzer {
	
	public static void fullAnalysis(PageCrawl pageCrawl) throws IOException{
		if(StringUtils.isEmpty(pageCrawl.getFilename())){
			return;
		}
		
		File file = new File(Global.getCrawlStorageFolder() + "/" + pageCrawl.getSiteCrawl().getStorageFolder() + "/" + pageCrawl.getFilename());
		if(file.isFile() && !FilenameUtils.getExtension(file.getName()).equals("ser")) {
			FileInputStream inputStream = new FileInputStream(file.getAbsolutePath());
	        String text = IOUtils.toString(inputStream, "UTF-8");
	        inputStream.close();
	        
	        pageCrawl.setLargeFile(file.length() > Global.getLargeFileThreshold());
	        
	        textAnalysis(pageCrawl, text);
	        docAnalysis(pageCrawl, text);
	        metaAnalysis(pageCrawl);
		}
	}
	

	
	
	
	/************************  Text Analysis  **************************/
	
	public static void textAnalysis(PageCrawl pageCrawl, String text){
		getInventoryNumbers(pageCrawl, text);
		getBrandMatchCounts(pageCrawl, text);
	}	
	
	public static void getInventoryNumbers(PageCrawl pageCrawl, String text) {
		for(InventoryType enumElement : InventoryType.values()){
			Matcher matcher = enumElement.getPattern().matcher(text);
	    	while (matcher.find()) {
	    		InventoryNumber invNumber = new InventoryNumber();
	    		invNumber.setInventoryType(enumElement);
	    		invNumber.setCount(Integer.parseInt(matcher.group(1)));
	    		pageCrawl.getInventoryNumbers().add(invNumber);
	    	}
		}
	}
	
	public static void getBrandMatchCounts(PageCrawl pageCrawl, String text){
		for(OEM oem : OEM.values()){
			Integer count = StringUtils.countMatches(text, oem.definition);
			pageCrawl.getBrandMatchCounts().put(oem, count);
		}
	}
	
	
	
	
	
	
	
	/************************  Doc Analysis *****************************/
	
	public static void docAnalysis(PageCrawl pageCrawl, String text) {
		Document doc = Jsoup.parse(text);
		fillImages(pageCrawl, doc);
		fillMetatags(pageCrawl, doc);
	}
	
	public static void fillImages(PageCrawl pageCrawl, Document doc) {
		Elements images = doc.select("img");
		pageCrawl.setNumImages(images.size());
		for(Element image : images) {
			ImageTag imageTag = new ImageTag();
			imageTag.setRaw(image.outerHtml());
			imageTag.setAlt(image.attr("alt"));
			imageTag.setSrc(image.attr("src"));
			pageCrawl.getImageTags().add(imageTag);
		}
	}
	
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
			pageCrawl.getMetatags().add(tagEntity);
			
			if(StringUtils.equals(tagEntity.getName(), "description") && pageCrawl.getMetaDescription() == null){
				pageCrawl.setMetaDescription(tagEntity);
			}
			if(StringUtils.equals(tagEntity.getName(), "title") && pageCrawl.getMetaTitle() == null){
				pageCrawl.setMetaTitle(tagEntity);
			}
		}
	}
	
	public static void fillH1AndTitle(PageCrawl pageCrawl, Document doc) {
		Elements headers = doc.select("h1");
		if(headers.size() > 0) {
			Element h1 = headers.first();
			pageCrawl.setH1(h1.text());
		}
		Elements titles = doc.select("title");
		if(titles.size() > 0) {
			Element title = titles.first();
			pageCrawl.setTitle(title.text());
		}
	}
	
	
	
	
	
	
	/*************  Meta Analysis ******************************/
	public static void metaAnalysis(PageCrawl pageCrawl){
		countImages(pageCrawl);
		checkForMakes(pageCrawl);
		checkForStates(pageCrawl);
		checkForCities(pageCrawl);
		checkLengths(pageCrawl);
	}
	
	public static void countImages(PageCrawl pageCrawl){
		int total = 0;
		int alt = 0;
		for(ImageTag image : pageCrawl.getImageTags()){
			total++;
			if(!StringUtils.isEmpty(image.getAlt())){
				alt++;
			}
		}
		pageCrawl.setNumImages(total);
		pageCrawl.setNumAltImages(alt);
	}
	
	public static void checkLengths(PageCrawl pageCrawl) {
		int length = StringUtils.length(pageCrawl.getTitle());
		if(pageCrawl.getTitle() != null && length >=50 && length <= 65) {
			pageCrawl.setTitleLength(true);
		}
		if(pageCrawl.getMetaDescription() != null){
			if(pageCrawl.getMetaDescription().getContent() != null && StringUtils.length(pageCrawl.getMetaDescription().getContent()) < 120){
				pageCrawl.setDescriptionLength(true);
			}
		}
	}
	
	public static void checkForMakes(PageCrawl pageCrawl){
		pageCrawl.setUrlMakeQualifier(AnalysisUtils.matchesPattern(StringExtraction.MAKE.getPattern(), pageCrawl.getUrl()));
		pageCrawl.setTitleMakeQualifier(AnalysisUtils.matchesPattern(StringExtraction.MAKE.getPattern(), pageCrawl.getTitle()));
		pageCrawl.setH1MakeQualifier(AnalysisUtils.matchesPattern(StringExtraction.MAKE.getPattern(), pageCrawl.getH1()));
		if(pageCrawl.getMetaDescription() != null){
			pageCrawl.setMetaDescriptionMakeQualifier(AnalysisUtils.matchesPattern(StringExtraction.MAKE.getPattern(), pageCrawl.getMetaDescription().getContent()));
		}
		else {
			pageCrawl.setMetaDescriptionMakeQualifier(false);
		}
	}
	
	public static void checkForStates(PageCrawl pageCrawl) {
		pageCrawl.setUrlStateQualifier(AnalysisUtils.matchesPattern(StringExtraction.STATE_ABBR.getPattern(), pageCrawl.getUrl()));
		pageCrawl.setTitleStateQualifier(AnalysisUtils.matchesPattern(StringExtraction.STATE_ABBR.getPattern(), pageCrawl.getTitle()));
		pageCrawl.setH1StateQualifier(AnalysisUtils.matchesPattern(StringExtraction.STATE_ABBR.getPattern(), pageCrawl.getH1()));
		if(pageCrawl.getMetaDescription() != null){
			pageCrawl.setMetaDescriptionMakeQualifier(AnalysisUtils.matchesPattern(StringExtraction.MAKE.getPattern(), pageCrawl.getMetaDescription().getContent()));
		}
		else {
			pageCrawl.setMetaDescriptionMakeQualifier(false);
		}
	}
	
	public static void checkForCities(PageCrawl pageCrawl){
		Set<String> cities = pageCrawl.getSiteCrawl().getSite().getCities();
		for(String city : cities) {
			Pattern cityPattern = Pattern.compile(city);
			boolean url = AnalysisUtils.matchesPattern(cityPattern, pageCrawl.getUrl());
			boolean title = AnalysisUtils.matchesPattern(cityPattern, pageCrawl.getTitle());
			boolean h1 = AnalysisUtils.matchesPattern(cityPattern, pageCrawl.getH1());
			boolean meta = pageCrawl.getMetaDescription() != null && AnalysisUtils.matchesPattern(cityPattern, pageCrawl.getMetaDescription().getContent());

			if(url){
				pageCrawl.setUrlCityQualifier(true);
			}
			if(title){
				pageCrawl.setTitleCityQualifier(true);
			}
			if(h1){
				pageCrawl.setH1CityQualifier(true);
			}
			if(meta){
				pageCrawl.setMetaDescriptionCityQualifier(true);
			}
		}
	}
}
