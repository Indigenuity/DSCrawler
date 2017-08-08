package analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import datadefinitions.GeneralMatch;
import datadefinitions.OEM;
import datadefinitions.Scheduler;
import datadefinitions.StringExtraction;
import datadefinitions.StringMatch;
import datadefinitions.newdefinitions.TestMatch;
import persistence.ExtractedString;
import persistence.ExtractedUrl;
import utilities.DSFormatter;

public class TextAnalyzer {
	
	public static Set<String> getVins(String text){
		return getPatternMatches(text, StringExtraction.VIN.getPattern());
	}
	
	//Currently is only accurate on non-French sites in US and Canada, because some people are heathens and use commas instead of decimal points
	public static List<Double> getMoneyValues(String text){
		List<Double> moneyValues = new ArrayList<Double>();
		if(StringUtils.isEmpty(text)){
			return moneyValues;
		}
		Matcher matcher = StringExtraction.MONEY_STRING.getPattern().matcher(text);
		while(matcher.find()){
			String moneyString = matcher.group(1);
			moneyString = moneyString.replaceAll(",", "");
			moneyValues.add(Double.parseDouble(moneyString));
		}
		return moneyValues;
	}
	
	public static boolean containsCity(String text, Collection<String> customCities){
		if(text == null){
			return false;
		}
		for(String city : customCities){
			city = city.replaceAll(" ", "[ -]");
			city = city.replaceAll("\\.", "");
			Pattern pattern = Pattern.compile(city);
			if(TextAnalyzer.matchesPattern(pattern, text)){
				return true;
			}
		}
		if(TextAnalyzer.matchesPattern(StringExtraction.POPULOUS_US_CITIES.getPattern(), text)){
			return true;
		}
		if(TextAnalyzer.matchesPattern(StringExtraction.POPULOUS_CANADA_CITIES.getPattern(), text)){
			return true;
		}
		return false;
	}

	public static Set<Scheduler> getSchedulers(String text) {
		Set<Scheduler> matches = new HashSet<Scheduler>();
		for(Scheduler sched : Scheduler.values()){
			if(text.contains(sched.getDefinition()) && !matches.contains(sched.getDefinition())){
				matches.add(sched);
			}
		}
		return matches;
	}

	public static Set<GeneralMatch> getGeneralMatches(String text) {
		return getStringMatches(text, GeneralMatch.values());
	}
	
	public static Set<TestMatch> getCurrentTestMatches(String text) {
		return getStringMatches(text, TestMatch.getCurrentMatches());
	}
	
	public  static <T extends StringMatch> Set<T>  getStringMatches(String text, Collection<T> searchSet){
		Set<T> matches = new HashSet<T>();
		for(T match : searchSet){
			if(StringUtils.contains(text, match.getDefinition())){
				matches.add(match);
			}
		}
		return matches;
	}
	
	public  static <T extends StringMatch> Set<T>  getStringMatches(String text, T[] searchSet){
		return getStringMatches(text, new HashSet<T>(Arrays.asList(searchSet)));
	}
	
	public static Set<String> getPatternMatches(String text, Pattern pattern){
		Set<String> matches = new HashSet<String>();
		Matcher matcher = pattern.matcher(text);
		while(matcher.find()){
			matches.add(matcher.group(0));
		}
		return matches;
	}
	
	public static int countOccurrences(String text, Pattern pattern){
		Matcher matcher = pattern.matcher(text);
		int count = 0;
		while(matcher.find()){
			count++;
		}
		return count;
	}
	
	public static boolean hasOccurrence(String text, Pattern pattern){
		Matcher matcher = pattern.matcher(text);
		if(matcher.find()){
			return true;
		}
		return false;
	}
	
	public static boolean hasOccurrence(String text, Collection<Pattern> patterns){
		for(Pattern pattern : patterns){
			if(hasOccurrence(text, pattern)){
				return true;
			}
		}
		return false;
	}
	
	public static boolean hasOemOccurrence(String text){
		List<Pattern> oemPatterns = new ArrayList<Pattern>();
		for(OEM oem : OEM.values()){
			oemPatterns.add(oem.getPattern());
		}
		return hasOccurrence(text, oemPatterns);
	}

	public static Set<ExtractedString> extractStrings(File file) throws IOException{
		if(!file.exists()) {
			throw new IOException("File does not exist for page with path : " + file.getAbsolutePath());
		}
		FileInputStream inputStream = new FileInputStream(file.getAbsolutePath());
		
	    String text = IOUtils.toString(inputStream, "UTF-8");
	    inputStream.close();
	    
	    return TextAnalyzer.extractStrings(text);
	}

	public static Set<ExtractedString> extractStrings(String text){
			Set<ExtractedString> extractedStrings = new HashSet<ExtractedString>();
			for(StringExtraction enumElement : StringExtraction.values()){
				if(enumElement != StringExtraction.CITY){
					Matcher matcher = enumElement.getPattern().matcher(text);
	//				int count = 0;
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
		
	    String text = IOUtils.toString(inputStream, "UTF-8");
	    Document doc = Jsoup.parse(text);
	    inputStream.close();
	    
	    return DocAnalyzer.extractUrls(doc);
	}

	public static boolean matchesPattern(Pattern pattern, String original) {
		if(pattern == null || original == null)
			return false;
		Matcher matcher = pattern.matcher(original);
		if(matcher.find()) {
			return true;
		}
		return false;
	}

	public static boolean containsMake(String original) {
		if(StringUtils.isEmpty(original))
			return false;
		
		for(OEM oem : OEM.values()){
			if(StringUtils.contains(original, oem.getDefinition())){
				return true;
			}
		}
		
		return false;
	}

}
