package crawling.discovery.local;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import crawling.discovery.entities.FlushableResource;
import crawling.discovery.entities.Resource;
import crawling.discovery.entities.ResourceId;
import crawling.discovery.execution.CrawlContext;
import crawling.discovery.execution.PlanId;
import crawling.discovery.execution.ResourceWorkResult;
import crawling.discovery.html.HttpResponseFile;
import crawling.discovery.planning.CrawlTool;
import crawling.discovery.results.CrawlReport;
import crawling.discovery.results.ResourceReport;
import global.Global;
import newwork.WorkStatus;
import persistence.PageCrawl;
import persistence.Site;
import persistence.SiteCrawl;
import play.Logger;
import play.db.jpa.JPA;
import sites.crawling.SiteCrawlLogic;
import sites.utilities.PageCrawlLogic;
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
			siteCrawl.getSite().setLastCrawl(siteCrawl);
			persistSettings(crawlContext, siteCrawl);
			crawlContext.put("crawlStorageFolder", generateStorageFolder(siteCrawl));
		});
	}

	@Override
	public void preCrawl(CrawlContext crawlContext) {
		Object newRootPageCrawlId = crawlContext.get("newRootPageCrawlId");
		Object usedRootPageCrawlId = crawlContext.get("usedRootPageCrawlId");
		for(Resource resource : crawlContext.getResources()){
			if(((PageCrawlResource)resource).getPageCrawlId().equals(newRootPageCrawlId)){
				crawlContext.put("newRootResourceId", resource.getResourceId());
			}
			if(((PageCrawlResource)resource).getPageCrawlId().equals(usedRootPageCrawlId)){
				crawlContext.put("usedRootResourceId", resource.getResourceId());
			}
		}
	}
	
	protected SiteCrawl generateSiteCrawl(CrawlContext crawlContext){
		Object siteId =  crawlContext.get("siteId");
		if(siteId== null){
			throw new IllegalStateException("Can't crawl site with no siteId.  Put siteId into crawl context before starting crawl");
		}
		Site site = JPA.em().find(Site.class, siteId);
		SiteCrawl siteCrawl = new SiteCrawl(site);
		siteCrawl = JPA.em().merge(siteCrawl);
		return siteCrawl;
	}
	
	protected void persistSettings(CrawlContext crawlContext, SiteCrawl siteCrawl){
		siteCrawl.setMaxDepth(crawlContext.getMaxDepth());
		siteCrawl.setMaxPages(crawlContext.getMaxFetches());
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
				toPageCrawls((PageCrawlResource)resource, null, siteCrawl);
			}
			
			System.out.println("Finished SiteCrawl " + siteCrawl.getSiteCrawlId() + " : " + siteCrawl.getSeed() + " ...with " + siteCrawl.getPageCrawls().size() + " PageCrawls");
//			System.out.println("after crawl pagecrawls : " + siteCrawl.getPageCrawls().size());
			siteCrawl.setMaxPagesReached(crawlReport.isMaxResourcesFetched());
			siteCrawl.setInventoryCrawlSuccess(true);		//Reset to default value, which is true
			
			for(ResourceReport resourceReport : crawlReport.getResourceReports().values()){
//				Set<Resource> results = resourceReport.getResources();
				if(crawlContext.get("inventoryPlanId").equals(resourceReport.getPlanId())){
//					processInventoryResults(results);
				} else {
//					processRegularResults(results);
				}
			}
			
			siteCrawl.setCrawlDate(new Date());
			SiteCrawlLogic.updateErrorStatus(siteCrawl);
		});
	}
	
	protected void toPageCrawls(PageCrawlResource resource, PageCrawl parent, SiteCrawl siteCrawl){
		
		PageCrawl pageCrawl = getPageCrawlFromResource(resource, parent, siteCrawl);
		if(pageCrawl == null){
			String idString = parent == null ? "null" : (parent.getPageCrawlId() + parent.getUrl());
			Logger.warn("After crawl received resource that can't be parsed to pageCrawl.  This probably means a null URI made it through discovery.  Parent PageCrawl : " + idString);
			return;
		}
		
		if(resource.getFetchStatus() == WorkStatus.ERROR || resource.getDiscoveryStatus() == WorkStatus.ERROR){
			pageCrawl = processErrorResult(resource, pageCrawl, siteCrawl);
		} else if(resource.getFetchStatus() == WorkStatus.ABORTED){
			pageCrawl = processAbortedFetch(pageCrawl, siteCrawl);
		} else if(resource.getFetchStatus() == WorkStatus.COMPLETE){
			pageCrawl = processCompletedFetch(resource, pageCrawl);
		} else if(resource.getFetchStatus() != WorkStatus.PRECOMPLETED){
			pageCrawl.setErrorMessage("Unknown fetch status after crawl : " + resource.getFetchStatus());
		} 
		
		if((resource.getFetchStatus() == WorkStatus.COMPLETE 
				|| resource.getFetchStatus() == WorkStatus.PRECOMPLETED)
				&& (resource.getDiscoveryStatus() != WorkStatus.COMPLETE 
				&& resource.getDiscoveryStatus() != WorkStatus.PRECOMPLETED)){
			pageCrawl.setErrorMessage("Unknown discovery status after crawl : " + resource.getDiscoveryStatus());
		}
		
		for(Resource child : resource.getChildren()){
			toPageCrawls((PageCrawlResource)child, pageCrawl, siteCrawl);
		}
	}	
	
	protected PageCrawl processCompletedFetch(PageCrawlResource resource, PageCrawl pageCrawl){
		pageCrawl.setErrorMessage(null);
		return transferData(resource, pageCrawl);
	}
	
	protected PageCrawl processAbortedFetch(PageCrawl pageCrawl, SiteCrawl siteCrawl){
		PageCrawlLogic.clearResults(pageCrawl);
		return pageCrawl;
	}
	
	protected PageCrawl processErrorResult(PageCrawlResource resource, PageCrawl pageCrawl, SiteCrawl siteCrawl){
		pageCrawl.setStatusCode(0);
		if(resource.getFetchException()!= null){
			pageCrawl.setErrorMessage(DSFormatter.toString(resource.getFetchException()));
		} else if(resource.getDiscoveryException()!= null){
			pageCrawl.setErrorMessage(DSFormatter.toString(resource.getDiscoveryException()));
			pageCrawl = transferData(resource, pageCrawl);	//If the error was just in discovery, we still want the data from a fetch
		} else {
			pageCrawl.setErrorMessage("Unknown error.  Resource was set to WorkStatus.ERROR with no exceptions saved.");
		}
		return pageCrawl;
	}
	
	protected PageCrawl getPageCrawlFromResource(PageCrawlResource resource, PageCrawl parent, SiteCrawl siteCrawl){
		PageCrawl pageCrawl = findOriginal(resource, siteCrawl);
		 
		if(pageCrawl == null){
			if(resource.getSource() == null || StringUtils.isEmpty(resource.getSource().toString())){
				Logger.warn("Found null URI when converting crawl resources to pagecrawls.  Not creating PageCrawl.");
				return null;
			}
			pageCrawl = new PageCrawl(resource.getSource().toString());
			pageCrawl.setParentPage(parent);
			pageCrawl = JPA.em().merge(pageCrawl);
			siteCrawl.addPageCrawl(pageCrawl);
		}
		return pageCrawl;
	}
	
	protected PageCrawl findOriginal(PageCrawlResource resource, SiteCrawl siteCrawl){
//		System.out.println("Finding original for responseFile : " + responseFile);
//		System.out.println("response id :  " + responseFile.getPageCrawlId());
		if(resource.getPageCrawlId() == null){
			return null;
		}
		for(PageCrawl pageCrawl : siteCrawl.getPageCrawls()){
			if(pageCrawl.getPageCrawlId() == resource.getPageCrawlId()){
				return pageCrawl;
			}
		}
		return null;
	}
	
	protected PageCrawl transferData(PageCrawlResource resource, PageCrawl pageCrawl){
		HttpResponseFile responseFile = resource.getResponseFile();
		if(responseFile != null){
			pageCrawl.setUrl(responseFile.getUri().toString());
			pageCrawl.setFilename(responseFile.getFile().getAbsolutePath());
			pageCrawl.setStatusCode(responseFile.getStatusCode());
			if(responseFile.getRedirectedUri() == null){
				pageCrawl.setRedirectedUrl(null);
			}else {
				pageCrawl.setRedirectedUrl(responseFile.getRedirectedUri().toString());
			}
		}
		
		pageCrawl.setPagedInventory(resource.isInventory());
		pageCrawl.setUsedRoot(resource.isUsedRoot());
		pageCrawl.setNewRoot(resource.isNewRoot());
		pageCrawl.setGeneralRoot(resource.isGeneralRoot());
		pageCrawl.setInvType(resource.getInvType());
		
		return pageCrawl;
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
