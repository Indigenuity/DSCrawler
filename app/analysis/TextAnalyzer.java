package analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
	
	public static boolean containsCity(String text, List<String> customCities){
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
		Set<GeneralMatch> matches = new HashSet<GeneralMatch>();
		for(GeneralMatch gm : GeneralMatch.values()){
			if(text.contains(gm.getDefinition()) && !matches.contains(gm.getDefinition())){
				matches.add(gm);
			}
		}
		return matches;
	}
	
	public static Set<TestMatch> getCurrentTestMatches(String text) {
		return getMatches(text, TestMatch.getCurrentMatches());
	}
	
	public  static <T extends StringMatch> Set<T>  getMatches(String text, Collection<T> searchSet){
		Set<T> matches = new HashSet<T>();
		for(T match : searchSet){
			if(text.contains(match.getDefinition()) && !matches.contains(match.getDefinition())){
				matches.add(match);
			}
		}
		return matches;
	}
	
	public  static <T extends StringMatch> Set<T>  getMatches(String text, T[] searchSet){
		return getMatches(text, new HashSet<T>(Arrays.asList(searchSet)));
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
