package crawling.discovery.local;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import crawling.discovery.entities.FlushableResource;
import crawling.discovery.entities.Resource;
import crawling.discovery.entities.ResourceId;
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
import utilities.DSFormatter;

public class SiteCrawlTool extends CrawlTool {

	@Override
	public void preResourcePopulation(CrawlContext crawlContext){
		JPA.withTransaction(() -> {
			Object siteCrawlId = crawlContext.get("siteCrawlId");
			SiteCrawl siteCrawl;
			if(siteCrawlId == null){
				siteCrawl = generateSiteCrawl(crawlContext);
				crawlContext.put("siteCrawlId", siteCrawl.getSiteCrawlId());
			} else {
				siteCrawl = JPA.em().find(SiteCrawl.class, siteCrawlId);
			}
			persistSettings(crawlContext, siteCrawl);
			crawlContext.put("crawlStorageFolder", generateStorageFolder(siteCrawl));
		});
	}

	@Override
	public void preCrawl(CrawlContext crawlContext) {
		
		
	}
	
	protected SiteCrawl generateSiteCrawl(CrawlContext crawlContext){
		Object siteId =  crawlContext.get("siteId");
		if(siteId== null){
			throw new IllegalStateException("Can't crawl site with no siteId.  Put siteId into crawl context before starting crawl");
		}
		Site site = JPA.em().find(Site.class, siteId);
		SiteCrawl siteCrawl = new SiteCrawl(site);
		JPA.em().persist(siteCrawl);
		return siteCrawl;
	}
	
	protected void persistSettings(CrawlContext crawlContext, SiteCrawl siteCrawl){
		siteCrawl.setMaxDepth(crawlContext.getMaxDepth());
		siteCrawl.setMaxPages(crawlContext.getMaxPages());
//		siteCrawl.setStorageFolder(crawlContext.getContextObject("crawlStorageFolder").toString());
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
	
	@Override
	public void beforeFinish(CrawlContext crawlContext){
		JPA.withTransaction(() -> {
			Long siteCrawlId = (Long) crawlContext.get("siteCrawlId");
			SiteCrawl siteCrawl = JPA.em().find(SiteCrawl.class, siteCrawlId);
		});
	}
	
	@Override
	public void postCrawl(CrawlContext crawlContext, CrawlReport crawlReport) {
		JPA.withTransaction(() -> {
			Long siteCrawlId = (Long) crawlContext.get("siteCrawlId");
			SiteCrawl siteCrawl = JPA.em().find(SiteCrawl.class, siteCrawlId);
			
			List<Resource> resources = new ArrayList<Resource>(crawlReport.getAllResources());
			List<Resource> roots = new ArrayList<Resource>();
			for(Resource resource : resources){
				if(resource.getParent() == null){
					roots.add(resource);
				}
			}
			for(Resource resource : roots){
				toPageCrawls(resource, null, siteCrawl);
			}
			
			System.out.println("Finished SiteCrawl " + siteCrawl.getSiteCrawlId() + " : " + siteCrawl.getSeed() + " ...with " + siteCrawl.getPageCrawls().size() + " PageCrawls");
//			System.out.println("after crawl pagecrawls : " + siteCrawl.getPageCrawls().size());
			siteCrawl.setMaxPagesReached(crawlReport.isMaxPagesReached());
			siteCrawl.setInventoryCrawlSuccess(true);		//Reset to default value, which is true
			
			for(ResourceReport resourceReport : crawlReport.getResourceReports().values()){
//				Set<Resource> results = resourceReport.getResources();
				if(crawlContext.get("inventoryPlanId").equals(resourceReport.getPlanId())){
//					processInventoryResults(results);
				} else {
//					processRegularResults(results);
				}
			}
		});
	}
	
	protected void toPageCrawls(Resource resource, PageCrawl parent, SiteCrawl siteCrawl){
		DSResponseFile responseFile = (DSResponseFile) resource.getValue();
		PageCrawl pageCrawl;
		if(responseFile == null || responseFile.getPageCrawlId() == null){
			pageCrawl = new PageCrawl();
			pageCrawl = JPA.em().merge(pageCrawl);
			siteCrawl.addPageCrawl(pageCrawl);
		}else {
			pageCrawl = findOriginal(responseFile, siteCrawl);
		}
		
		if(responseFile!= null){
			transferData(responseFile, pageCrawl);
			if(responseFile.isDiscoveredNewRoot()){
				siteCrawl.setNewInventoryRoot(pageCrawl);
			}
			if(responseFile.isDiscoveredUsedRoot()){
				siteCrawl.setUsedInventoryRoot(pageCrawl);
			}
		}
		
		if(resource.getFetchException()!= null){
			pageCrawl.setErrorMessage(DSFormatter.toString(resource.getFetchException()));
		} else if(resource.getDiscoveryException()!= null){
			pageCrawl.setErrorMessage(DSFormatter.toString(resource.getDiscoveryException()));
		}
		
		pageCrawl.setParentPage(parent);
		
		for(Resource child : resource.getChildren()){
			toPageCrawls(child, pageCrawl, siteCrawl);
		}
	}	
	
	protected PageCrawl findOriginal(DSResponseFile responseFile, SiteCrawl siteCrawl){
		System.out.println("Finding original for responseFile : " + responseFile);
		System.out.println("response id :  " + responseFile.getPageCrawlId());
		if(responseFile.getPageCrawlId() == null){
			return null;
		}
		for(PageCrawl pageCrawl : siteCrawl.getPageCrawls()){
			if(responseFile.getPageCrawlId() == pageCrawl.getPageCrawlId()){
				return pageCrawl;
			}
		}
		return null;
	}
	
	protected void transferData(DSResponseFile responseFile, PageCrawl pageCrawl){
		pageCrawl.setUrl(responseFile.getUri().toString());
		pageCrawl.setFilename(responseFile.getFile().getAbsolutePath());
		pageCrawl.setPagedInventory(responseFile.isInventory());
		pageCrawl.setUsedRoot(responseFile.isUsedRoot());
		pageCrawl.setNewRoot(responseFile.isNewRoot());
		pageCrawl.setInvType(responseFile.getInvType());
		pageCrawl.setStatusCode(responseFile.getStatusCode());
		if(responseFile.getRedirectedUri() == null){
			pageCrawl.setRedirectedUrl(null);
		}else {
			pageCrawl.setRedirectedUrl(responseFile.getRedirectedUri().toString());
		}
	}
		
	
	
	
//	protected void processInventoryResults(Set<ResourceWorkResult> results){
//		for(ResourceWorkResult result : results){
//			Resource parent = result.getParent();
//			if(parent != null){
//				PageCrawl pageCrawl = (PageCrawl)result.getParent().getValue();
//				if(result.getWorkStatus() == WorkStatus.ERROR){
//					pageCrawl.addFailedInventoryUrl(result.getSource().toString());
//					pageCrawl.getSiteCrawl().setInventoryCrawlSuccess(false);
//				} else if(result.getWorkStatus() == WorkStatus.UNASSIGNED){
//					pageCrawl.addUncrawledInventoryUrl(result.getSource().toString());
//					pageCrawl.getSiteCrawl().setInventoryCrawlSuccess(false);
//				}
//	//			((FlushableResource)result.getParent()).flush();
//			}
//		}
//	}
	
//	protected void processRegularResults(Set<ResourceWorkResult> results){
//		for(ResourceWorkResult result : results){
//			Resource parent = result.getParent();
//			if(parent != null){
//				PageCrawl pageCrawl = (PageCrawl)result.getParent().getValue();
//				if(result.getWorkStatus() == WorkStatus.ERROR){
//					pageCrawl.addFailedUrl(result.getSource().toString());
//				} else if(result.getWorkStatus() == WorkStatus.UNASSIGNED){
//					pageCrawl.addUncrawledUrl(result.getSource().toString());
//				}
//	//			((FlushableResource)result.getParent()).flush();
//			}
//		}
//	}
	
	
}
