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

import datadefinitions.newdefinitions.WebProvider;
import sites.persistence.Vehicle;

public abstract class InventoryTool {

	protected String newPathString = "#NeverMatch#";
	protected String usedPathString = "#NeverMatch#";
	protected String paginationLinkString = "#NeverMatch#";
	protected String countRegexString = "#NeverMatch#";
	
	protected String nextPageSelector = "#NeverMatch#";
	
	protected boolean requiresAjax;
	protected WebProvider probableWp;

	protected Pattern newPath;
	protected Pattern usedPath;
	protected Pattern paginationLink;
	protected Pattern countRegex;
	
	private static Set<InventoryTool> registeredTypes = new HashSet<InventoryTool>();
	static{
		registeredTypes.add(new DealerCom());
	}
	 
	public InventoryTool(){
		declareValues();
		compilePatterns();
	}
	
	public static Set<InventoryTool> getInvTypes(){
		return registeredTypes;
	}
	
	protected abstract void declareValues();
	
	public abstract Set<Vehicle> getVehicles(Document doc);
	
	protected void compilePatterns(){
		newPath = Pattern.compile(newPathString);
		usedPath = Pattern.compile(usedPathString);
		paginationLink = Pattern.compile(paginationLinkString);
		countRegex = Pattern.compile(countRegexString);
	}
	
	public boolean matchesType(Document doc, URI uri){
		return isNewPath(uri) || isUsedPath(uri);
	}
	
	public boolean isNewRoot(URI uri){
		return pathAndQueryEquals(uri, newPathString);
	}
	public boolean isUsedRoot(URI uri){
		return pathAndQueryEquals(uri, usedPathString);
	}
	public boolean isNewPath(URI uri){
//		System.out.println("pathAndQuery : " + getPathAndQuery(uri));
//		System.out.println("contains : " + pathAndQueryContains(uri, newPathString));
		return pathAndQueryContains(uri, newPathString);
	}
	public boolean isUsedPath(URI uri){
		return pathAndQueryContains(uri, usedPathString);
	}
	public boolean isPaginationLink(URI uri){
		return pathAndQueryMatches(uri, paginationLink);
	}
	
	public URI getNextPageLink(Document doc, URI currentUri){
		Elements nextPageLinks = doc.select(nextPageSelector);
//		System.out.println("nextPageLinks: " + nextPageLinks);
		if(nextPageLinks.size() < 1){
			return null;
		}
		try {
			URI uri = new URI(nextPageLinks.get(0).attr("abs:href"));
			return uri;
		} catch (URISyntaxException e) {
			return null;
		}
	}
	
	public Set<URI> getPaginationLinks(Document doc){
		Set<URI> uris = new HashSet<URI>();
		for(Element element : doc.select("a[href]")){
			try {
				URI uri = new URI(element.absUrl("href"));
				if(isPaginationLink(uri)){
					uris.add(uri);
				}
			} catch (URISyntaxException e) {}
		}
		return uris;
	}
	public int getCount(Document doc){
		Matcher matcher = countRegex.matcher(doc.text());
		if(matcher.find()){
			return Integer.valueOf(matcher.group(1));
		}
		return 0;
	}
	
	public WebElement getNextPageLink(WebDriver driver, URI currentUri){
		throw new UnsupportedOperationException();
	}
	public int getCount(WebDriver driver){
		throw new UnsupportedOperationException();
	}
	
	protected String getPathAndQuery(URI uri){
		String pathAndQuery = uri.getPath();
		if(uri.getQuery() != null){
			pathAndQuery += "?" + uri.getQuery();
		}
		return pathAndQuery;
	}
	
	public boolean uriEquals(URI uri, String string){
		return StringUtils.equals(uri.toString(), string);
	}
	
	public boolean pathAndQueryEquals(URI uri, String string){
		return StringUtils.equals(getPathAndQuery(uri), string);
	}
	
	public boolean uriMatches(URI uri, Pattern pattern){
		Matcher matcher = pattern.matcher(uri.toString());
		return matcher.matches();
	}
	
	public boolean pathAndQueryMatches(URI uri, Pattern pattern){
		Matcher matcher = pattern.matcher(getPathAndQuery(uri));
		return matcher.matches();
	}
	
	public boolean uriMatches(URI uri, String regex){
		return uriMatches(uri, Pattern.compile(regex));
	}
	
	public boolean pathAndQueryMatches(URI uri, String regex){
		return pathAndQueryMatches(uri, Pattern.compile(regex));
	}
	
	public boolean uriContains(URI uri, Pattern pattern){
		Matcher matcher = pattern.matcher(uri.toString());
		return matcher.find();
	}
	
	public boolean pathAndQueryContains(URI uri, Pattern pattern){
		Matcher matcher = pattern.matcher(getPathAndQuery(uri));
		return matcher.find();
	}
	
	public boolean uriContains(URI uri, String string){
		return uri.toString().contains(string);
	}
	
	public boolean pathAndQueryContains(URI uri, String string){
		return getPathAndQuery(uri).contains(string);
	}
	
	public double moneyString(String moneyString){
		if(moneyString == null){
			return 0.0;
		}
		moneyString = moneyString.replaceAll("[^\\d.]+", "");
//		System.out.println("moneyString : " + moneyString);
		Double money = Double.valueOf(moneyString);
//		System.out.println("money : " + money);
		return money;
	}
	
	public double mileageString(String mileageString){
		if(mileageString == null){
			return 0.0;
		}
		mileageString = mileageString.replaceAll("[^\\d.]+", "");
//		System.out.println("mileageString : " + mileageString);
		Double mileage = Double.valueOf(mileageString);
//		System.out.println("mileage : " + mileage);
		return mileage;
	}
	
}
