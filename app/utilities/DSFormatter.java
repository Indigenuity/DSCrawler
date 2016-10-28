package utilities;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.commons.lang3.StringUtils;

import datadefinitions.InvalidDomain;
import datadefinitions.ValidPathMatch;
import datadefinitions.ValidQueryMatch;
import play.Logger;

public class DSFormatter {

	private final static String HAS_HTTP_REGEX= "(?i:http.*)";
	private final static String OK_ENDINGS_REGEX = ".*(?i:index\\.htm|index\\.html|index\\.cfm|index\\.asp|index\\.php|index\\.shtml|default\\.htm|default\\.html|"
			+ "default\\.asp|default\\.aspx|default\\.cfm|default\\.php|home/|Home\\.aspx|home\\.html)";
	private final static String SLASHLESS_DOMAIN = "(?i:\\.com|\\.net|\\.biz|\\.us|\\.cc|\\.org|\\.info|\\.ca|\\.me|\\.car)";
	private final static String SLASHED_DOMAIN = "(?i:\\.com/|\\.net/|\\.biz/|\\.us/|\\.cc/|\\.org/|\\.info/|\\.ca/|\\.me/|\\.car/)";
	private final static String SLASHLESS_DOMAIN_ENDING = ".*" + SLASHLESS_DOMAIN + "$";
	private final static String SLASHED_DOMAIN_ENDING = ".*" + SLASHED_DOMAIN + "$";
	
	private final static String WINDOWS_ILLEGAL_CHARACTERS = "[\\/:\"*?<>|]+";
	
	public static boolean equals(String first, String second) {
		if(first == null && second == null)
			return true;
		if(first != null & second == null)
			return false;
		return first.equals(second);
	}
	
	public static String standardizeStreetAddress(String street){
		if(street == null) {
			return null;
		}
		street = street.toLowerCase();
		street = street.replaceAll("\\bave\\b", "avenue");
		street = street.replaceAll("\\bav\\b", "avenue");
		street = street.replaceAll("\\brd\\b", "road");
		street = street.replaceAll("\\bpkwy\\b", "parkway");
		street = street.replaceAll("\\bst$", "street");
		street = street.replaceAll("\\bct$", "court");
		street = street.replaceAll("\\bblvd\\b", "boulevard");
		street = street.replaceAll("\\brte\\b", "route");
		street = street.replaceAll("\\brt\\b", "route");
		street = street.replaceAll("\\bhwy\\b", "highway");
		street = street.replaceAll("\\btpke\\b", "turnpike");
		street = street.replaceAll("\\btpk\\b", "turnpike");
		street = street.replaceAll("\\bpl\\b", "place");
		street = street.replaceAll("\\bcntry\\b", "country");
		street = street.replaceAll("\\bterr\\b", "terrace");
		street = street.replaceAll("\\bn\\b", "north");
		street = street.replaceAll("\\bs\\b", "south");
		street = street.replaceAll("\\be\\b", "east");
		street = street.replaceAll("\\bw\\b", "west");
		
		
		
		return street;
	}
	
	
	public static String getLastSegment(String original) {
		int lastIndex = original.lastIndexOf("/");
		if(lastIndex > 0){
			return original.substring(lastIndex + 1, original.length());
		}
		else
			return "";
	}
	
	public static String truncate(String original, int length) {
		if(original == null)
			return original;
		
		if(needsTruncation(original, length)){

			for(char testing : original.toCharArray()){
				if(Character.isSurrogate(testing))
					Logger.error("Found surrogate character!");
			}
			if(original.length() > length){
				String trunc = "TRUNCATED" + original.substring(0, length-9); 
//				Logger.info("Truncating String : " + trunc);
				return trunc;
			}
		}
		
		
		return original;
	}
	
	public static boolean isTruncated(String original) {
		if(original.startsWith("TRUNCATED")){
			return true;
		}
		return false;
	}
	
	public static String standardizeUrl(String original) {
		try {
			URL url = new URL(original);
			String rebuilt = url.getProtocol() + "://" + url.getHost() + "/";
			System.out.println("rebuilt : " + rebuilt);
			return rebuilt;
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Illegal url");
		}
	}
	
	public static String standardize(String original) {
		try {
			URL url = new URL(original);
			String rebuilt = url.getProtocol() + "://" + url.getHost();
			if(StringUtils.isEmpty(url.getPath())){
				rebuilt = rebuilt + "/";
			}
			else {
				rebuilt = rebuilt + url.getPath();
			}
			if(!StringUtils.isEmpty(url.getQuery())){
				rebuilt += url.getQuery();	
			}
			return rebuilt;
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Illegal url");
		}
	}
	
	
	public static boolean needsTruncation(String original, int length) {
		if(original == null || original.length() <= length)
			return false;
		return true;
	}
	
	public static boolean isEmpty(String original) {
		if(original == null || original.equals(""))
			return true;
		return false;
	}
	
	public static boolean isBlank(String original) {
		if(isEmpty(original) || isEmpty(original.trim()))
			return true;
		return false;
	}
	
	public static String toHttp(String original) {
		String destination;
		if(!original.matches(HAS_HTTP_REGEX)){
			destination = "http://" + original;
		}
		else {
			destination = original;
		}
		return destination;
	}
	
	public static String removeQueryString(String original) {
		int queryPosition = original.indexOf('?');
		if(queryPosition > 0){
			return original.substring(0, queryPosition);
		}
		else
			return original;
	}
	
	public static String removeWww(String original) {
		
		String returned = original.toLowerCase();
		returned = returned.replaceAll("www.", "");
		returned = returned.replaceAll("ww2.", "");
		returned = returned.replaceAll("ww3.", "");
		
		return returned;
	}
	
	public static String removeHttp(String original) {
		
		String returned = original.toLowerCase();
		returned = returned.replaceAll("http://", "");
		returned = returned.replaceAll("https://", "");
		
		return returned;
	}
	
	public static String removeLanguage(String original) {
		String returned = original.toLowerCase();
		for(ValidPathMatch langPath : ValidPathMatch.langValues()){
			returned = returned.replaceAll(langPath.definition + "$", "/");	//We only want to replace paths at the end of the string
		}
		for(ValidQueryMatch langQuery : ValidQueryMatch.langValues()){
			returned = returned.replace("?" + langQuery.definition, "");			//Leave rest of query intact, excluding '?' character.  This may result in invalid URL.
		}
//		System.out.println("original :" + original);
//		System.out.println("removed : " + returned);
		return returned;
	}
	
	public static String toCom(String original){
		String returned = original.replaceAll(SLASHED_DOMAIN, ".com/");
		returned = returned.replaceAll(SLASHLESS_DOMAIN, ".com");
		
		return returned;
	}
	
	public static String regularize(String original) {
		String changed = toHttp(original);
		changed = removeQueryString(changed);
		return changed;
	}
	
	public static boolean isApprovedUrl(String original) {
		if(original.matches(OK_ENDINGS_REGEX))
			return true;
		if(original.matches(SLASHLESS_DOMAIN_ENDING))
			return true;
		if(original.matches(SLASHED_DOMAIN_ENDING))
			return true;
		
		return false;
	}
	
	public static boolean isApprovedPath(String original) {
		if(original == null) {
			return false;
		}
		String path = original.toLowerCase();
		for(ValidPathMatch match : ValidPathMatch.values()) {
			if(path.equals(match.definition)) {
				return true;
			}
		}
		return false;
	}
	public static boolean isApprovedPath(URL url) {
		return isApprovedPath(url.getPath());
	}
	public static boolean isApprovedQuery(String original) {
		if(original == null) {
			return true;
		}
		for(ValidQueryMatch match : ValidQueryMatch.values()) {
			if(original.matches(match.definition)) {
				return true;
			}
		}
		return false;
	}
	public static boolean isApprovedQuery(URL url) {
		return isApprovedQuery(url.getQuery());
	}
	
	public static boolean isLanguagePath(String original) {
		if(original == null) {
			return false;
		}
		String path = original.toLowerCase();
		for(ValidPathMatch match : ValidPathMatch.langValues()) {
			if(path.equals(match.definition)) {
				return true;
			}
		}
		return false;
	}
	public static boolean isLanguagePath(URL url) {
		return isLanguagePath(url.getPath());
	}
	public static boolean isLanguageQuery(String original) {
		if(original == null) {
			return false;
		}
		String query = original.toLowerCase();
		for(ValidQueryMatch match : ValidQueryMatch.langValues()) {
			if(query.equals(match.definition)) {
				return true;
			}
		}
		return false;
	}
	public static boolean isLanguageQuery(URL url) {
		return isLanguageQuery(url.getQuery());
	}
	
	public static boolean isApprovedDomain(String original) {
		if(original == null) {
			return false;
		}
		String domain = original.toLowerCase();
		for(InvalidDomain match : InvalidDomain.values()) {
			if(domain.matches(match.definition)) {
				return false;
			}
		}
		return true;
	}
	public static boolean isApprovedDomain(URL url) {
		return isApprovedDomain(url.getHost());
	}
	
	public static String sqlify(String original) {
		
		String formatted = original.replaceAll("'", "\\'");original.replaceAll("\\", "\\\\");
		formatted = formatted.replaceAll("\\", "\\\\");
		
		return formatted;
	}
	
	public static String deSqlify(String original) {
		String formatted = original.replaceAll("\\\\", "\\");
		formatted = formatted.replaceAll("\\'", "'");
		
		return formatted;
	}
	
	public static String encode(String url) {
		try {
			return URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new AssertionError("UTF-8 is unknown");
		}
	}
	
	public static String decode(String url) {
		try {
			return URLDecoder.decode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			System.out.println("error decoding");
			throw new AssertionError("UTF-8 is unknown");
		}
	}
	
	public static String makeSafePath(String path) {
		if(path == null || "".equals(path)){
			path = "nofilename" + String.valueOf(System.currentTimeMillis());
		}
		String safe = encode(path);
		safe = safe.replaceAll(WINDOWS_ILLEGAL_CHARACTERS, "_");
		safe = truncate(safe);
		safe = safe.toLowerCase();
		return safe;
	}
	
	public static String truncate(String path) {
		return truncate(path, 80);
	}
	
	public static String getDomain(String url) {
		String domain;
		url = toHttp(url);
		
		try {
			URL formal = new URL(url);
			domain = formal.getHost();
			domain = removeWww(domain);
		} catch (MalformedURLException e) {
			domain = "ERROR";
		}
		
		return domain;
	}
}
