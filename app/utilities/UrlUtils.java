package utilities;

import datadefinitions.HomepagePath;
import datadefinitions.ValidQueryMatch;

public class UrlUtils {

	public static String removeLanguage(String original) {
			String returned = original.toLowerCase();
			for(HomepagePath langPath : HomepagePath.langValues()){
				returned = returned.replaceAll(langPath.definition + "$", "/");	//We only want to replace paths at the end of the string
			}
			for(ValidQueryMatch langQuery : ValidQueryMatch.langValues()){
				returned = returned.replace("?" + langQuery.definition, "");			//Leave rest of query intact, excluding '?' character.  This may result in invalid URL.
			}
	//		System.out.println("original :" + original);
	//		System.out.println("removed : " + returned);
			return returned;
		}

	public static String removeHttp(String original) {
		
		String returned = original.toLowerCase();
		returned = returned.replaceAll("http://", "");
		returned = returned.replaceAll("https://", "");
		
		return returned;
	}

	public static String toCom(String original){
		String returned = original.replaceAll(DSFormatter.SLASHED_DOMAIN, ".com/");
		returned = returned.replaceAll(DSFormatter.SLASHLESS_DOMAIN, ".com");
		
		return returned;
	}

	public static String removeWww(String original) {
		
		String returned = original.toLowerCase();
		returned = returned.replaceAll("www3.", "");
		returned = returned.replaceAll("www.", "");
		returned = returned.replaceAll("ww12.", "");
		returned = returned.replaceAll("ww1.", "");
		returned = returned.replaceAll("ww2.", "");
		returned = returned.replaceAll("ww3.", "");
		
		return returned;
	}

	public static String removeQueryString(String original) {
		int queryPosition = original.indexOf('?');
		if(queryPosition > 0){
			return original.substring(0, queryPosition);
		}
		else
			return original;
	}

	public static String toHttp(String original) {
		String destination;
		if(!original.matches(DSFormatter.HAS_HTTP_REGEX)){
			destination = "http://" + original;
		}
		else {
			destination = original;
		}
		return destination;
	}

}
