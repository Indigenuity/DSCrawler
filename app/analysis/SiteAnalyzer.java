package analysis;

import global.Global;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;

import persistence.PageInformation;
import persistence.SiteInformationOld;

public class SiteAnalyzer {

	
	
	
	public static void analyzeMatches(SiteInformationOld siteInfo) throws IOException {
		fetchPagesConditionally(siteInfo);
		
		for(PageInformation pageInfo : siteInfo.getPages()){
			File in = new File(Global.getCrawlStorageFolder() + siteInfo.getCrawlStorageFolder() + "/" + pageInfo.getFileLocation());
//			System.out.println(siteInfo.getId() + " : " + pageInfo.getPath());
			PageAnalyzer.analyzeMatches(pageInfo, in);
		}
	}
	
	public static void analyzeStringExtractions(SiteInformationOld siteInfo) throws IOException {
		fetchPagesConditionally(siteInfo);
		
		for(PageInformation pageInfo : siteInfo.getPages()){
			File in = new File(Global.getCrawlStorageFolder() + siteInfo.getCrawlStorageFolder() + "/" + pageInfo.getFileLocation());
			PageAnalyzer.analyzeStringExtractions(pageInfo, in);
		}
	}
	
	public static void analyzeStaff(SiteInformationOld siteInfo) throws IOException {
		fetchPagesConditionally(siteInfo);
		
		for(PageInformation pageInfo : siteInfo.getPages()){
			File in = new File(Global.getCrawlStorageFolder() + siteInfo.getCrawlStorageFolder() + "/" + pageInfo.getFileLocation());
			PageAnalyzer.analyzeStaff(pageInfo, in);
		}
	}
	
	public static void fetchPagesConditionally(SiteInformationOld siteInfo) throws IOException {
		if(siteInfo.getPages().size() < 1 && !siteInfo.isEmptySite()){
			fetchPages(siteInfo);
		}
	}
	
	public static void fetchPages(SiteInformationOld siteInfo) throws IOException {
		
		File siteFolder = new File(Global.getCrawlStorageFolder() + siteInfo.getCrawlStorageFolder());
		if (!(siteFolder.exists()  && siteFolder.isDirectory())) {
			System.out.println("But it doesn't exist : " + siteFolder.getAbsolutePath());
	          throw new IOException("There is no site information at this location");
	    }
		 
		PageInformation pageInfo;
		String pagePath;
		for(File pageFile : siteFolder.listFiles()) {
			if(pageFile.isFile() && !FilenameUtils.getExtension(pageFile.getName()).equals("ser")) {
				pagePath = pageFile.getName().replaceAll("HTML-", "");
				pageInfo = new PageInformation(pagePath);
				pageInfo.setFileLocation(pagePath);
				siteInfo.addPageInformation(pageInfo);
			}
		}
		if(siteInfo.getPages().size() < 1) {
			siteInfo.setEmptySite(true);
		}
	}
	
}
