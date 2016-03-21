package async.tools;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import analysis.PageCrawlAnalyzer;
import async.registration.ContextItem;
import async.work.WorkStatus;
import async.work.WorkType;
import crawling.HttpFetcher;
import datadefinitions.newdefinitions.InventoryType;
import global.Global;
import persistence.SiteCrawl;
import persistence.tasks.Task;
import play.db.jpa.JPA;
import persistence.PageCrawl;

public class InventoryTool extends TransactionTool { 
	


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
	protected Task doTaskInTransaction(Task task) throws Exception{
		
		Long siteCrawlId = Long.parseLong(task.getContextItem("siteCrawlId"));
		SiteCrawl siteCrawl = JPA.em().find(SiteCrawl.class, siteCrawlId);
		
		if(siteCrawl.getInventoryType() == null){
			return impossibleTask(task, "Can't fetch inventory pages without Inventory Type");
		}
		
//		if(siteCrawl.getNewInventoryPage() == null){
			PageCrawl newPage = fetchPage(false, siteCrawl);
			PageCrawlAnalyzer.fullAnalysis(newPage, siteCrawl);
			JPA.em().persist(newPage);
			siteCrawl.setNewInventoryPage(newPage);
			siteCrawl.addPageCrawl(newPage);
			
//		}
//		if(siteCrawl.getUsedInventoryPage() == null){
			PageCrawl usedPage = fetchPage(true, siteCrawl);
			PageCrawlAnalyzer.fullAnalysis(usedPage, siteCrawl);
//			System.out.println("pagecrawl's inv # : " + usedPage.getInventoryNumber());
			JPA.em().persist(usedPage);
			siteCrawl.setUsedInventoryPage(usedPage);
			siteCrawl.addPageCrawl(usedPage);
//		}
		JPA.em().getTransaction().commit();
		JPA.em().getTransaction().begin();
		JPA.em().detach(siteCrawl);
//		System.out.println("after : (" + usedPage.getPageCrawlId() + ") " + usedPage.getInventoryNumber() + " (" + usedPage.getInventoryNumber().getInventoryNumberId() + ")");

		task.setWorkStatus(WorkStatus.WORK_COMPLETED);
		return task;
	}
	
	
	public static PageCrawl fetchPage(boolean used, SiteCrawl siteCrawl) throws IOException {
		InventoryType invType = siteCrawl.getInventoryType();
		System.out.println("fetching inventory page : " + used + " " + siteCrawl.getSiteCrawlId() + " " + invType.name());
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
		PageCrawl pageCrawl = HttpFetcher.getPageCrawl(url, new File(Global.getCrawlStorageFolder() + "/" + siteCrawl.getStorageFolder()));
		if(pageCrawl == null || pageCrawl.getErrorMessage() != null || pageCrawl.getHttpStatus() != 200){
			throw new IllegalStateException("Error while fetching PageCrawl (" + pageCrawl.getHttpStatus() + ") : " + pageCrawl.getErrorMessage());
		}
		return pageCrawl;
	}
}