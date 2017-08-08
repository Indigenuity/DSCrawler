package datadefinitions.inventory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import datadefinitions.inventory.implementations.DealerCom;
import datadefinitions.newdefinitions.WebProvider;
import sites.persistence.Vehicle;

public abstract class InventoryTool {

	
	protected boolean requiresAjax;
	protected WebProvider probableWp;

	private static Set<InventoryTool> registeredTypes = new HashSet<InventoryTool>();
	static{
		registeredTypes.add(new DealerCom());
	}
	
	public static Set<InventoryTool> getInvTypes(){
		return registeredTypes;
	}
	 
	
	public abstract boolean isNewPath(Document doc, URI uri);
	public abstract boolean isNewRoot(Document doc, URI uri);
	public abstract boolean isUsedPath(Document doc, URI uri);
	public abstract boolean isUsedRoot(Document doc, URI uri);
	public abstract boolean isGeneralPath(Document doc, URI uri);
	public abstract boolean isGeneralRoot(Document doc, URI uri);
	public abstract boolean isNewPath(URI uri);
	public abstract boolean isNewRoot(URI uri);
	public abstract boolean isUsedPath(URI uri);
	public abstract boolean isUsedRoot(URI uri);
	public abstract boolean isGeneralPath(URI uri);
	public abstract boolean isGeneralRoot(URI uri);
	public abstract int getCount(Document doc);
	public abstract URI getNextPageLink(Document doc, URI currentUri);
	public abstract Set<URI> getPaginationLinks(Document doc, URI currentUri);
	public abstract Set<Vehicle> getVehicles(Document doc);
	
	public boolean isOfThisType(Document doc, URI uri){
		return isNewPath(doc, uri) || isUsedPath(doc, uri) || isGeneralPath(doc, uri);
	}
	
	public boolean isOfThisType(URI uri){
		return isNewPath(uri) || isUsedPath(uri) || isGeneralPath(uri);
	}
	
	
	/************  For inventory systems that require a crawl within the browser.
	 * This happens when they load their content dynamically with JavaScript, or if they somehow detect a non-browser and block the response.   
	 *
	public WebElement getNextPageLink(WebDriver driver, URI currentUri){
		throw new UnsupportedOperationException();
	}
	
	public int getCount(WebDriver driver){
		throw new UnsupportedOperationException();
	}
	*/
	
	/***************** Document tools *************************/
	
	public static URI getFirstAbsHref(String selector, Document doc){
		Elements elements = doc.select(selector);
		if(elements.size() < 1){
			return null;
		}
		try {
			URI uri = new URI(elements.get(0).attr("abs:href"));
			return uri;
		} catch (URISyntaxException e) {
			return null;
		}
	}
	
	public static Set<URI> getAllAHrefsContaining(Pattern pattern, Document doc){
		Set<URI> uris = new HashSet<URI>();
		for(Element element : doc.select("a[href]")){
			try {
				String uriString = element.absUrl("href"); 
				if(textContains(uriString, pattern)){
					uris.add(new URI(uriString));
				}
			} catch (URISyntaxException e) {}
		}
		return uris;
	}
	
	/****************** String parsing tools **********************/
	
	public static double doubleString(String doubleString){
		if(doubleString == null){
			return 0.0;
		}
		doubleString = doubleString.replaceAll("[^\\d.]+", "");
		Double money = Double.valueOf(doubleString);
		return money;
	}
	
	protected static String getPathAndQuery(URI uri){
		String pathAndQuery = uri.getPath();
		if(uri.getQuery() != null){
			pathAndQuery += "?" + uri.getQuery();
		}
		return pathAndQuery;
	}
	
	/******************** Matching Tools *********************/
	
	public static int findNumberMatch(String text, Pattern pattern, int group){
		Matcher matcher = pattern.matcher(text);
		if(matcher.find()){
//			System.out.println("found");
			return Integer.valueOf(matcher.group(group));
		}
		return 0;
	}
	
	public static boolean textContains(String text, Pattern pattern){
		return pattern.matcher(text).find();
	}
	
	public static boolean uriEquals(URI uri, String string){
		return StringUtils.equals(uri.toString(), string);
	}
	
	public static boolean pathAndQueryEquals(URI uri, String string){
		return StringUtils.equals(getPathAndQuery(uri), string);
	}
	
	public static boolean uriMatches(URI uri, Pattern pattern){
		Matcher matcher = pattern.matcher(uri.toString());
		return matcher.matches();
	}
	
	public static boolean pathAndQueryMatches(URI uri, Pattern pattern){
		Matcher matcher = pattern.matcher(getPathAndQuery(uri));
		return matcher.matches();
	}
	
	public static boolean uriContains(URI uri, Pattern pattern){
		Matcher matcher = pattern.matcher(uri.toString());
		return matcher.find();
	}
	
	public static boolean pathAndQueryContains(URI uri, Pattern pattern){
		Matcher matcher = pattern.matcher(getPathAndQuery(uri));
		return matcher.find();
	}
	
	public static boolean uriContains(URI uri, String string){
		return uri.toString().contains(string);
	}
	
	public static boolean pathAndQueryContains(URI uri, String string){
		return getPathAndQuery(uri).contains(string);
	}
	
	
	
}
