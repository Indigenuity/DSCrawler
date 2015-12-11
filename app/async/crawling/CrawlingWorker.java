package async.crawling;

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
import async.work.SiteWork;
import async.work.WorkItem;
import async.work.WorkStatus;
import async.work.WorkType;

//For sending sites to crawl to individual threads
public class CrawlingWorker extends UntypedActor {

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
	public void onReceive(Object work) throws Exception {

		WorkItem workItem = (WorkItem) work;
		workItem.setWorkStatus(WorkStatus.WORK_IN_PROGRESS);
		System.out.println("Performing crawling work : " + workItem.getSiteId());
		try{
			String[] homepage = new String[1];
			JPA.withTransaction( () -> {
				Site temp = JPA.em().find(Site.class, workItem.getSiteId());
				homepage[0] = temp.getHomepage();
			});
			boolean shouldCrawl = true;
			for(String url : NO_CRAWL_DOMAINS) {
				if(homepage[0].contains(url))
					shouldCrawl = false;
			}
			SiteCrawl siteCrawlTemp = null;
			if(shouldCrawl){
				if(workItem.getWorkType() == WorkType.SMALL_CRAWL){
					System.out.println("doing small crawl");
					siteCrawlTemp = DealerCrawlController.crawlHomepage(homepage[0]);
				}
				else{
					siteCrawlTemp = DealerCrawlController.crawlSite(homepage[0]);
				}
			}
			
			workItem.setWorkStatus(WorkStatus.WORK_COMPLETED);
			final SiteCrawl siteCrawl = siteCrawlTemp;
			JPA.withTransaction(() -> {
				Site site = JPA.em().find(Site.class, workItem.getSiteId());
				CrawlSet crawlSet = JPA.em().find(CrawlSet.class, workItem.getCrawlSetId());
				if(siteCrawl != null){
					siteCrawl.setSite(site);
					JPA.em().persist(siteCrawl);
					crawlSet.finishCrawl(site, siteCrawl);
				}
				else{
					site.setReviewLater(true);
					site.setReviewReason("No crawl attempted; url is on No Crawl list");
					crawlSet.getUncrawled().remove(site);
					crawlSet.getNeedMobile().remove(site);
				}
			});
			
		}
		catch(Exception e) {
//			System.out.println("Error while crawling : " + siteInfo.getSiteInformationId() + " " + e);
//			Logger.error("Error while crawling : " + siteInfo.getSiteInformationId() + " " + e);
			Logger.error("error while crawling : " + e);
			e.printStackTrace();
		}
		
		getSender().tell(workItem, getSelf());
	}
	
	@Override
	public void postRestart(Throwable reason) throws Exception {
		Logger.error("Crawling worker restarting");
		preStart();
	}
	

}
