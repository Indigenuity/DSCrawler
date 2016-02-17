package async.work.crawling;

import java.util.HashSet;
import java.util.Set;

import crawling.DealerCrawlController;
import datadefinitions.NoCrawlDomain;
import persistence.CrawlSet;
import persistence.Site;
import persistence.SiteCrawl;
import persistence.SiteInformationOld;
import play.Logger;
import play.db.jpa.JPA;
import akka.actor.UntypedActor;
import async.Asyncleton;
import async.work.SingleStepWorker;
import async.work.SiteWork;
import async.work.WorkItem;
import async.work.WorkOrder;
import async.work.WorkResult;
import async.work.WorkStatus;
import async.work.WorkType;
import async.work.urlresolve.UrlResolveWorkResult;

//For sending sites to crawl to individual threads
public class CrawlingWorker extends SingleStepWorker { 
	
	private static int count = 0;
	@Override
	public WorkResult processWorkOrder(WorkOrder work) {
		return doWorkOrder(work);
	}
	
	public static SiteCrawlWorkResult doWorkOrder(WorkOrder work) {
		System.out.println("CrawlingWorker processing WorkOrder : " + work);
		SiteCrawlWorkResult result = new SiteCrawlWorkResult();
		try{
			SiteCrawlWorkOrder workOrder = (SiteCrawlWorkOrder) work;
			result.setUuid(workOrder.getUuid());
			String[] homepage = new String[1];
			JPA.withTransaction( () -> {
				Site temp = JPA.em().find(Site.class, workOrder.getSiteId());
				homepage[0] = temp.getHomepage();
			});
			boolean shouldCrawl = true;
			for(NoCrawlDomain domain : NoCrawlDomain.values()) {
				if(homepage[0].contains(domain.definition))
					shouldCrawl = false;
			}
			
			if(shouldCrawl){
				final SiteCrawl siteCrawl = DealerCrawlController.crawlSite(homepage[0]);
				
				JPA.withTransaction(() -> {
					Site site = JPA.em().getReference(Site.class, workOrder.getSiteId());
					siteCrawl.setSite(site);
					JPA.em().persist(siteCrawl);
				});
				result.setSiteCrawlId(siteCrawl.getSiteCrawlId());
				result.setWorkStatus(WorkStatus.WORK_COMPLETED);
			}
			else {
				result.setWorkStatus(WorkStatus.NEEDS_REVIEW);
				result.setNote("Site domain is in the NoCrawlDomains");
			}
			
		}
		catch(Exception e) {
//			System.out.println("Error while crawling : " + siteInfo.getSiteInformationId() + " " + e);
//			Logger.error("Error while crawling : " + siteInfo.getSiteInformationId() + " " + e);
			Logger.error("error while crawling : " + e);
			e.printStackTrace();
			result.setWorkStatus(WorkStatus.NEEDS_REVIEW);
			result.setNote(e.getMessage());
		}
		
		return result;
	}
	

}
