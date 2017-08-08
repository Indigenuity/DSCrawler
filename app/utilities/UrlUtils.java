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
	
	//Checks for http(s) additions/changes, www redirects, top-level (.com, .net) redirects, and language path and query string changes
	public static boolean isGenericRedirect(String redirectUrl, String original) {
		
		if(redirectUrl == null || original == null)
			return false;
		if(redirectUrl.equals(original))
			return true;
		String sansHttpRedirectUrl = removeHttp(redirectUrl);
		String sansHttpOriginal= removeHttp(original);
		if(sansHttpRedirectUrl.equals(sansHttpOriginal))
			return true;
		String sansWwwRedirectUrl = removeWww(sansHttpRedirectUrl);
		String sansWwwOriginal = removeWww(sansHttpOriginal);
		if(sansWwwRedirectUrl.equals(sansWwwOriginal))
			return true;
		
		String comRedirectUrl = toCom(sansWwwRedirectUrl);
		String comOriginal = toCom(sansWwwOriginal);
		
		if(comRedirectUrl.equals(comOriginal))
			return true;
		
		String langRedirectUrl = removeLanguage(comRedirectUrl);
		String langOriginal = removeLanguage(comOriginal);
		if(langRedirectUrl.equals(langOriginal))
			return true;
		
		String noTrailingSlashRedirectUrl = langRedirectUrl.replaceAll("/$", "");
		String noTrailingSlashOriginalUrl = langOriginal.replaceAll("/$", "");
		if(noTrailingSlashRedirectUrl.equals(noTrailingSlashOriginalUrl))
			return true;
		
		
		return false;
	}

}
