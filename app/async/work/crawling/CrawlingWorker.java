package async.work.crawling;

import java.util.HashSet;
import java.util.Set;

import crawling.DealerCrawlController;
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
	
	public static final Set<String> NO_CRAWL_DOMAINS = new HashSet<String>();
	static {
		NO_CRAWL_DOMAINS.add("facebook.com");
		NO_CRAWL_DOMAINS.add("plus.google.com");
		NO_CRAWL_DOMAINS.add("jdbyrider.com");
		NO_CRAWL_DOMAINS.add("carhop.com");
		NO_CRAWL_DOMAINS.add("autotrader.com");
		NO_CRAWL_DOMAINS.add("manheim.com");
		NO_CRAWL_DOMAINS.add("adesa.com");
		NO_CRAWL_DOMAINS.add("chevronwithtechron.com");
		NO_CRAWL_DOMAINS.add("drivetime.com");
		NO_CRAWL_DOMAINS.add("www.cars.com");
		NO_CRAWL_DOMAINS.add("iaai.com");
		NO_CRAWL_DOMAINS.add("www.honda.com");
		NO_CRAWL_DOMAINS.add("www.hertzcarsales.com");
		NO_CRAWL_DOMAINS.add("copart.com");
		NO_CRAWL_DOMAINS.add("www.gm.com");
		NO_CRAWL_DOMAINS.add("carmax.com");
		NO_CRAWL_DOMAINS.add("paaco.com");
		
		
	}
	
	private static int count = 0;
	@Override
	public WorkResult processWorkOrder(WorkOrder work) {
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
			for(String url : NO_CRAWL_DOMAINS) {
				if(homepage[0].contains(url))
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
				result.setNote("Site domain is in the NO_CRAWL_DOMAINS");
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
