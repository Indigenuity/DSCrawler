package analysis;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import datadefinitions.StringExtraction;
import persistence.ImageTag;
import persistence.Metatag;
import persistence.PageCrawl;
import persistence.SiteCrawl;

public class PageCrawlAnalyzer {
	
	public static void checkLength(PageCrawl pageCrawl) {
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
	
	public static void checkContent(PageCrawl pageCrawl, Set<String> cities) {
		pageCrawl.setUrlMakeQualifier(AnalysisUtils.matchesPattern(StringExtraction.MAKE.getPattern(), pageCrawl.getUrl()));
		pageCrawl.setUrlStateQualifier(AnalysisUtils.matchesPattern(StringExtraction.STATE_ABBR.getPattern(), pageCrawl.getUrl()));
		
		pageCrawl.setTitleMakeQualifier(AnalysisUtils.matchesPattern(StringExtraction.MAKE.getPattern(), pageCrawl.getTitle()));
		pageCrawl.setTitleStateQualifier(AnalysisUtils.matchesPattern(StringExtraction.STATE_ABBR.getPattern(), pageCrawl.getTitle()));
		
		pageCrawl.setH1MakeQualifier(AnalysisUtils.matchesPattern(StringExtraction.MAKE.getPattern(), pageCrawl.getH1()));
		pageCrawl.setH1StateQualifier(AnalysisUtils.matchesPattern(StringExtraction.STATE_ABBR.getPattern(), pageCrawl.getH1()));
	
		if(pageCrawl.getMetaDescription() != null){
			pageCrawl.setMetaDescriptionMakeQualifier(AnalysisUtils.matchesPattern(StringExtraction.MAKE.getPattern(), pageCrawl.getMetaDescription().getContent()));
			pageCrawl.setMetaDescriptionStateQualifier(AnalysisUtils.matchesPattern(StringExtraction.STATE_ABBR.getPattern(), pageCrawl.getMetaDescription().getContent()));
		}
		else {
			pageCrawl.setMetaDescriptionMakeQualifier(false);
			pageCrawl.setMetaDescriptionStateQualifier(false);
		}
		
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
	
	public static void fillPageStats(SiteCrawl siteCrawl, PageCrawl pageCrawl, Document doc) {
		fillMetatags(siteCrawl, pageCrawl, doc);
		fillH1AndTitle(siteCrawl, pageCrawl, doc);
		fillImages(siteCrawl, pageCrawl, doc);
	}
 
	public static void fillMetatags(SiteCrawl siteCrawl, PageCrawl pageCrawl, Document doc) {
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
			int index = siteCrawl.getMetatags().indexOf(tagEntity);
			if(index > -1) {
//				System.out.println("already contains: " + tagEntity.getRaw());
				tagEntity = siteCrawl.getMetatags().get(index);
			}
			else {
				
				siteCrawl.getMetatags().add(tagEntity);
			}
			index = pageCrawl.getMetatags().indexOf(tagEntity);
			
			if(index == -1){	//Important because pages can have duplicate tags
//				System.out.println("adding : " + tagEntity.getRaw());
				pageCrawl.addMetatag(tagEntity); 
			}
			else {
//				System.out.println("already contains: " + tagEntity.getRaw());
			}
			
			if(StringUtils.equals(tagEntity.getName(), "description") && pageCrawl.getMetaDescription() == null){
				pageCrawl.setMetaDescription(tagEntity);
			}
			if(StringUtils.equals(tagEntity.getName(), "title") && pageCrawl.getMetaTitle() == null){
				pageCrawl.setMetaTitle(tagEntity);
			}
			
		}
		
//		for(Metatag tag : pageCrawl.getMetatags()) {
//			System.out.println("raw : " + tag.getRaw());
//		}
	}
	
	public static void fillH1AndTitle(SiteCrawl siteCrawl, PageCrawl pageCrawl, Document doc) {
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
	
	public static void fillImages(SiteCrawl siteCrawl, PageCrawl pageCrawl, Document doc) {
		Elements images = doc.select("img");
		pageCrawl.setNumImages(images.size());
		
		int altCount = 0;
		for(Element image : images) {
			ImageTag imageTag = new ImageTag();
			imageTag.setRaw(image.outerHtml());
			imageTag.setAlt(image.attr("alt"));
			imageTag.setSrc(image.attr("src"));
			if(!StringUtils.isEmpty(image.attr("alt"))){
				altCount++;
			}
			int index = siteCrawl.getImageTags().indexOf(imageTag);
			if(index >= 0) {
//				System.out.println("already contains");
				imageTag = siteCrawl.getImageTags().get(index);
			}
			else {
				siteCrawl.getImageTags().add(imageTag);
			}
			if(!pageCrawl.getImageTags().contains(imageTag)){
				pageCrawl.addImageTag(imageTag);
			}
		}
		pageCrawl.setNumAltImages(altCount);
	}
}
