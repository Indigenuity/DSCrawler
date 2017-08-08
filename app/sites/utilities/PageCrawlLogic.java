package sites.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import persistence.PageCrawl;
import persistence.SiteCrawl;
import play.db.jpa.JPA;

public class PageCrawlLogic {
	
	public static void clearResults(PageCrawl pageCrawl){
		pageCrawl.setCrawlDate(null);
		pageCrawl.setStatusCode(null);
		pageCrawl.setLargeFile(false);
		pageCrawl.setUsedRoot(false);
		pageCrawl.setErrorMessage(null);
		pageCrawl.setRedirectedUrl(null);
		
		deleteStorageFile(pageCrawl);
		pageCrawl.setFilename(null);
	}
	
	public static void deleteStorageFile(PageCrawl pageCrawl){
		if(pageCrawl.getFilename() != null){
			File file = new File(pageCrawl.getFilename());
			if(file.exists()){
				file.delete();
			}
		}
	}
	
	public static boolean fileExists(PageCrawl pageCrawl){
		String filename = pageCrawl.getFilename();
		if(StringUtils.isEmpty(filename)){
			return false;
		}
		File file = new File(filename);
		return file.exists();
	}
	
	public static String getText(PageCrawl pageCrawl){
		String filename = pageCrawl.getFilename();
		if(StringUtils.isEmpty(filename)){
			return null;
		}
		try(FileInputStream inputStream = new FileInputStream(filename)){
			return IOUtils.toString(inputStream, "UTF-8");	
		} catch(FileNotFoundException e) {
			return null;
		} catch(IOException e){
			throw new RuntimeException(e);
		}
	}
	
	public static Document getDocument(PageCrawl pageCrawl){
		String text = getText(pageCrawl);
		if(text == null){
			return null;
		}
		Document doc = Jsoup.parse(text);
		doc.setBaseUri(pageCrawl.getUrl());
		return doc;
	}
	
	public static boolean isUncrawled(PageCrawl pageCrawl){
		if(pageCrawl.getCrawlDate() == null || pageCrawl.getStatusCode() == null || pageCrawl.getStatusCode() == 0){
			return true;
		}
		return false;
	}
	
	public static boolean isFailedCrawl(PageCrawl pageCrawl){
		Integer statusCode = pageCrawl.getStatusCode();
		if(!StringUtils.isEmpty(pageCrawl.getErrorMessage())){
			return true;
		}
		
		if(isUncrawled(pageCrawl) || isSuccessfulStatusCode(statusCode) || isRedirectStatusCode(statusCode)){
			return false;
		}
		return true;
	}
	
	public static boolean isInventoryPage(PageCrawl pageCrawl) {
		return pageCrawl.getPagedInventory() || pageCrawl.getNewRoot() || pageCrawl.getUsedRoot();
	}
	
	public static boolean isSuccessfulStatusCode(Integer statusCode){
		if(statusCode != null && statusCode == 200){
			return true;
		}
		return false;
	}
	
	public static boolean isRedirectStatusCode(Integer statusCode){
		if(statusCode != null && statusCode >= 300 && statusCode < 400){
			return true;
		}
		return false;
	}

	public static void removePageCrawl(PageCrawl pageCrawl){
		SiteCrawl siteCrawl = pageCrawl.getSiteCrawl();
		if(siteCrawl.getNewInventoryRoot() == pageCrawl){
			siteCrawl.setNewInventoryRoot(null);
		}
		if(siteCrawl.getUsedInventoryRoot() == pageCrawl){
			siteCrawl.setUsedInventoryRoot(null);
		}
		for(PageCrawl childPageCrawl : pageCrawl.getChildPages()){
			childPageCrawl.setParentPage(null);
		}
		JPA.em().remove(pageCrawl);
	}
	

}
