package analysis;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import crawling.HttpFetcher;
import datadefinitions.newdefinitions.InventoryType;
import global.Global;
import persistence.InventoryNumber;
import persistence.PageCrawl;
import persistence.SiteCrawl;

public class InventoryAnalyzer {

	
	
	
	
	public static PageCrawl fetchPage(boolean used, SiteCrawl siteCrawl) throws IOException {
		InventoryType invType = siteCrawl.getInventoryType();
//		System.out.println("fetching inventory page : " + used + " " + siteCrawl.getSiteCrawlId() + " " + invType.name());
		if(invType.getAjax() || invType.getLocalUrl()){
			throw new UnsupportedOperationException("Can't yet fetch inventory pages for inventory type : " + invType.name());
		}
		URL homepageUrl= new URL(siteCrawl.getSeed());
		String urlString = homepageUrl.getProtocol() + "://" + homepageUrl.getHost();
		if(used){
			urlString = urlString + invType.getUsedPath();
		}
		else{
			urlString = urlString + invType.getNewPath();
		}
//		System.out.println("base Url : " + urlString);
		URL url = new URL(urlString);
//		return null;
		PageCrawl pageCrawl = HttpFetcher.getPageCrawl(url, new File(Global.getCrawlStorageFolder() + siteCrawl.getStorageFolder()));
		if(pageCrawl == null || pageCrawl.getErrorMessage() != null || pageCrawl.getHttpStatus() != 200){
			throw new IllegalStateException("Error while fetching PageCrawl (" + pageCrawl.getHttpStatus() + ") : " + pageCrawl.getErrorMessage());
		}
		return pageCrawl;
	}
	
	public static InventoryType inferInvTypeFromMultiple(List<InventoryType> invTypes){
		boolean dealerCom = false;
		boolean autoTrader = false;
		if(invTypes.size() == 1){
			return invTypes.get(0);
		}
		for(InventoryType invType : invTypes){
			if(invType == InventoryType.AUTO_TRADER_CA){
				autoTrader = true;
			}
			else if(invType == InventoryType.DEALER_COM){
				dealerCom = true;
			}
		
		}
		if(invTypes.size() == 2 && dealerCom && autoTrader){
			return InventoryType.DEALER_COM;
		}
		
		return null;
	}
}
