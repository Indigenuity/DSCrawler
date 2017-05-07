package crawling.discovery.local;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import crawling.discovery.entities.FlushableResource;
import crawling.discovery.entities.Resource;
import crawling.discovery.execution.CrawlContext;
import crawling.discovery.execution.PlanId;
import crawling.discovery.execution.ResourceWorkResult;
import crawling.discovery.planning.CrawlTool;
import crawling.discovery.results.CrawlReport;
import crawling.discovery.results.ResourceReport;
import global.Global;
import newwork.WorkStatus;
import persistence.PageCrawl;
import persistence.Site;
import persistence.SiteCrawl;
import play.db.jpa.JPA;

public class SiteCrawlTool extends CrawlTool {

	
	
	@Override
	public void preCrawl(CrawlContext crawlContext) {
//		System.out.println("Sitecrawltrool precrawl");
		JPA.withTransaction(() -> {
			Object siteCrawlId = crawlContext.getContextObject("siteCrawlId");
			SiteCrawl siteCrawl;
			if(siteCrawlId == null){
				siteCrawl = generateSiteCrawl(crawlContext);
				crawlContext.putContextObject("siteCrawlId", siteCrawl.getSiteCrawlId());
			} else {
				siteCrawl = JPA.em().find(SiteCrawl.class, siteCrawlId);
			}
			persistSettings(crawlContext, siteCrawl);
			crawlContext.putContextObject("crawlStorageFolder", generateStorageFolder(siteCrawl));
		});
		
	}
	
	protected SiteCrawl generateSiteCrawl(CrawlContext crawlContext){
		Object siteId =  crawlContext.getContextObject("siteId");
		if(siteId== null){
			throw new IllegalStateException("Can't crawl site with no siteId.  Put siteId into crawl context before starting crawl");
		}
		Site site = JPA.em().find(Site.class, siteId);
		SiteCrawl siteCrawl = new SiteCrawl(site);
		JPA.em().persist(siteCrawl);
		return siteCrawl;
	}
	
	protected File generateStorageFolder(SiteCrawl siteCrawl){
		File crawlStorageFolder;
		if(siteCrawl.getStorageFolder() == null){
			File rootStorageFolder = new File(Global.getTodaysCrawlStorageFolder());
			crawlStorageFolder = new File(rootStorageFolder, siteCrawl.getLocalFolderName());
			siteCrawl.setStorageFolder(crawlStorageFolder.getAbsolutePath());
		} else {
			crawlStorageFolder = new File(siteCrawl.getStorageFolder());
		}
		
		if(!crawlStorageFolder.exists()){
			crawlStorageFolder.mkdirs();
		}
		
		return crawlStorageFolder;
	}
	
	protected void persistSettings(CrawlContext crawlContext, SiteCrawl siteCrawl){
		siteCrawl.setMaxDepth(crawlContext.getMaxDepth());
		siteCrawl.setMaxPages(crawlContext.getMaxPages());
//		siteCrawl.setStorageFolder(crawlContext.getContextObject("crawlStorageFolder").toString());
	}

	@Override
	public void postCrawl(CrawlContext crawlContext, CrawlReport crawlReport) {
		JPA.withTransaction(() -> {
			Long siteCrawlId = (Long) crawlContext.getContextObject("siteCrawlId");
			SiteCrawl siteCrawl = JPA.em().find(SiteCrawl.class, siteCrawlId);
			siteCrawl.setMaxPagesReached(crawlReport.isMaxPagesReached());
			siteCrawl.setInventoryCrawlSuccess(true);		//Reset to default value, which is true
			
			for(ResourceReport resourceReport : crawlReport.getResourceReports().values()){
				Set<ResourceWorkResult> results = resourceReport.getResults();
				if(crawlContext.getContextObject("inventoryPlanId").equals(resourceReport.getPlanId())){
					processInventoryResults(results);
				} else {
					processRegularResults(results);
				}
			}
		});
	}
	
	protected void processInventoryResults(Set<ResourceWorkResult> results){
		for(ResourceWorkResult result : results){
			Resource parent = result.getParent();
			if(parent != null){
				PageCrawl pageCrawl = (PageCrawl)result.getParent().getValue();
				if(result.getWorkStatus() == WorkStatus.ERROR){
					pageCrawl.addFailedInventoryUrl(result.getSource().toString());
					pageCrawl.getSiteCrawl().setInventoryCrawlSuccess(false);
				} else if(result.getWorkStatus() == WorkStatus.NOT_STARTED){
					pageCrawl.addUncrawledInventoryUrl(result.getSource().toString());
					pageCrawl.getSiteCrawl().setInventoryCrawlSuccess(false);
				}
	//			((FlushableResource)result.getParent()).flush();
			}
		}
	}
	
	protected void processRegularResults(Set<ResourceWorkResult> results){
		for(ResourceWorkResult result : results){
			Resource parent = result.getParent();
			if(parent != null){
				PageCrawl pageCrawl = (PageCrawl)result.getParent().getValue();
				if(result.getWorkStatus() == WorkStatus.ERROR){
					pageCrawl.addFailedUrl(result.getSource().toString());
				} else if(result.getWorkStatus() == WorkStatus.NOT_STARTED){
					pageCrawl.addUncrawledUrl(result.getSource().toString());
				}
	//			((FlushableResource)result.getParent()).flush();
			}
		}
	}
	
	
}
