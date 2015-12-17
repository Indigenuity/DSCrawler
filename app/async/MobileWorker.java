package async;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import crawling.MobileCrawler;
import persistence.CrawlSet;
import persistence.MobileCrawl;
import persistence.Site;
import persistence.SiteCrawl;
import play.Logger;
import play.db.jpa.JPA;
import akka.actor.UntypedActor;
import analysis.SiteCrawlAnalyzer;
import async.work.SiteWork;
import async.work.WorkItem;
import async.work.WorkStatus;
import async.work.WorkType;

public class MobileWorker extends UntypedActor {

	private static int count = 0;
	@Override
	public void onReceive(Object work) throws Exception {

		
		WorkItem workItem = (WorkItem) work;
		workItem.setWorkStatus(WorkStatus.WORK_IN_PROGRESS);
		System.out.println("Performing Mobile Crawl work : " + workItem.getSiteId());
		try{
			
			
			JPA.withTransaction( () -> {
				Site site = JPA.em().find(Site.class, workItem.getSiteId());
				CrawlSet crawlSet = JPA.em().find(CrawlSet.class, workItem.getCrawlSetId());
				System.out.println("crawl set : " + crawlSet + " ( " + workItem.getCrawlSetId());
				MobileCrawl mobileCrawl = MobileCrawler.defaultMobileCrawl(site.getHomepage());
				System.out.println("mobile crawl seed : " + mobileCrawl.getSeed());
				System.out.println("mobile crawl resolved seed : " + mobileCrawl.getResolvedSeed());
				mobileCrawl.setCrawlDate(new java.sql.Date(Calendar.getInstance().getTimeInMillis()));
				mobileCrawl.setSite(site);
				crawlSet.addMobileCrawl(mobileCrawl);
				crawlSet.getNeedMobile().remove(site);
				JPA.em().persist(mobileCrawl);
				workItem.setWorkStatus(WorkStatus.WORK_COMPLETED);
			});
		}
		catch(Exception e) {
			Logger.error("error while mobile crawling : " + e);
			e.printStackTrace();
		}
		getSender().tell(workItem, getSelf());
	}
	
	@Override
	public void postRestart(Throwable reason) throws Exception {
		Logger.error("Mobile worker restarting");
		preStart();
	}
	

}