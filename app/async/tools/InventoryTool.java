package async.tools;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import async.registration.ContextItem;
import async.work.WorkStatus;
import async.work.WorkType;
import crawling.DealerCrawlController;
import crawling.HttpFetcher;
import datadefinitions.NoCrawlDomain;
import datadefinitions.newdefinitions.InventoryType;
import global.Global;
import persistence.Site;
import persistence.SiteCrawl;
import persistence.tasks.Task;
import play.Logger;
import play.db.jpa.JPA;
import persistence.InventoryNumber;
import persistence.PageCrawl;

public class InventoryTool extends Tool { 
	


	protected final static Set<ContextItem> requiredContextItems = new HashSet<ContextItem>();
	static{
		ContextItem item = new ContextItem("siteCrawlId", String.class, false);
		requiredContextItems.add(item);
	}
	
	protected final static Set<ContextItem> resultContextItems = new HashSet<ContextItem>();
	
	protected final static Set<WorkType> abilities = new HashSet<WorkType>();
	static{
		abilities.add(WorkType.INVENTORY_COUNT);
	}
	
	@Override
	public  Set<WorkType> getAbilities() {
		return abilities;
	}
	
	@Override
	public Set<ContextItem> getRequiredItems(WorkType workType) {
		return requiredContextItems;
	}


	@Override
	public Set<ContextItem> getResultItems(WorkType workType) {
		return resultContextItems;
	}	
	
	public static Task doathing(Task task) throws Exception{
		return (new InventoryTool()).safeDoTask(task);
	}
	@Override
	protected Task safeDoTask(Task task) throws Exception{
		System.out.println("InventoryTool processing task : " + task);
		Long siteCrawlId = Long.parseLong(task.getContextItem("siteCrawlId"));
		SiteCrawl siteCrawl = fetchSiteCrawl(siteCrawlId);
		List<InventoryType> invTypes = getInventoryTypes(siteCrawl);
		InventoryType invType = inferInvType(invTypes);
		
		if(invType == null){
			if(invTypes.size() > 1){
				return incompleteTask(task, "SiteCrawl has multiple inventory types; could not infer single inventory type");
			}
			return incompleteTask(task, "SiteCrawl has no detected inventory type");
		}
		siteCrawl.setInventoryType(invType);
		siteCrawl.setMaxInventoryCount(getMaxCount(siteCrawl));
		
		
		
		siteCrawl.setNewInventoryPage(getInventoryPage(false, siteCrawl));
		siteCrawl.setUsedInventoryPage(getInventoryPage(true, siteCrawl));
		
		siteCrawl = saveSiteCrawl(siteCrawl);
		
		task.setWorkStatus(WorkStatus.WORK_COMPLETED);
		return task;
	}
	
	protected static SiteCrawl fetchSiteCrawl(Long siteCrawlId) {
		
		SiteCrawl[] temp = new SiteCrawl[1];
		JPA.withTransaction( () -> {
			temp[0]= JPA.em().find(SiteCrawl.class, siteCrawlId);
			temp[0].initAll();
		});
		return temp[0];
	}
	
	protected static PageCrawl savePageCrawl(PageCrawl pageCrawl) {
		PageCrawl[] temp = new PageCrawl[1];
		
		JPA.withTransaction( () -> {
			temp[0] = JPA.em().merge(pageCrawl);
		});
		return temp[0];
	}
	
	protected static SiteCrawl saveSiteCrawl(SiteCrawl siteCrawl) {
		SiteCrawl[] temp = new SiteCrawl[1];
		
		JPA.withTransaction( () -> {
			temp[0] = JPA.em().merge(siteCrawl);
		});
		return temp[0];		
	}
	
	protected static List<InventoryType> getInventoryTypes(SiteCrawl siteCrawl) {
		List<InventoryType> invTypes = new ArrayList<InventoryType>();
		for(InventoryNumber num : siteCrawl.getInventoryNumbers()){
			InventoryType temp = num.getInventoryType();
			if(!invTypes.contains(num.getInventoryType())){
				invTypes.add(num.getInventoryType());
			}
		}
		return invTypes;
	}
	
	//For sites with more than one inventory type, there can be some confusion.  Some are not confusing, and this solves those.
	protected static InventoryType inferInvType(List<InventoryType> invTypes){
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
	
	protected static PageCrawl getInventoryPage(boolean used, SiteCrawl siteCrawl) throws IOException{
		PageCrawl pageCrawl = getExisting(used, siteCrawl);
		if(pageCrawl == null){
			pageCrawl = fetchPage(used, siteCrawl);
			return savePageCrawl(pageCrawl);
		}
		return null;
	}
	
	public static PageCrawl getExisting(boolean used, SiteCrawl siteCrawl) throws MalformedURLException{
		InventoryType invType = siteCrawl.getInventoryType();
//		System.out.println("checking for pre-existing inventory page : " + used + " " + siteCrawl.getSiteCrawlId() + " " + invType.name());
		if(invType == null){
			return null;
		}
		for(PageCrawl pageCrawl : siteCrawl.getPageCrawls()){
//			System.out.println("from pagecrawl : " + pageCrawl);
//			System.out.println("making url from pagecrawl url : " + pageCrawl.getUrl());
			URL url = new URL(pageCrawl.getUrl());
			String path = url.getPath();
			String query = "";
			if(url.getQuery() != null){
				query = "?" + url.getQuery();
			}
			String pathAndQuery = path + query;
			if(used && invType.getUsedPath().equals(pathAndQuery)){
				return pageCrawl;
			}
			if(!used && invType.getNewPath().equals(pathAndQuery)){
				return pageCrawl;
			}
		}
		return null;
	}
	
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
	
	public static InventoryNumber getMaxCount(SiteCrawl siteCrawl) {
		InventoryNumber max = null; 
		for(InventoryNumber invNumber: siteCrawl.getInventoryNumbers()){
			if(max == null || invNumber.getCount() > max.getCount()){
				max = invNumber;
			}
		}
		return max;
	}

}