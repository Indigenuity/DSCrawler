package crawling;

import java.io.File;
import java.net.URI;

import com.google.common.util.concurrent.RateLimiter;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import async.async.Asyncleton;
import crawling.discovery.execution.Crawler;
import crawling.discovery.execution.EndWhenReady;
import crawling.discovery.execution.PlanId;
import crawling.discovery.execution.SeedWorkOrder;
import crawling.discovery.html.HttpConfig;
import crawling.discovery.html.HttpToFilePlan;
import crawling.discovery.local.RegularToInventoryDiscoveryTool;
import crawling.discovery.local.SiteCrawlPlan;
import crawling.discovery.local.PageCrawlTool;
import crawling.discovery.local.SiteCrawlTool;
import crawling.discovery.planning.CrawlPlan;
import crawling.discovery.planning.DiscoveryPlan;
import global.Global;
import newwork.StartWork;
import persistence.Site;
import play.db.jpa.JPA;

public class CrawlMaster extends UntypedActor{
	
	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof SiteCrawlOrder){
			processCrawlOrder((SiteCrawlOrder) message);
		}else if(message instanceof SiteCrawlPlan){
			processCrawlPlan((SiteCrawlPlan) message);
		}
		
	}
	
	private void processCrawlOrder(SiteCrawlOrder workOrder){
		JPA.withTransaction(() -> {
			Site site = JPA.em().find(Site.class, workOrder.getSiteId());
			System.out.println("CrawlMaster starting crawl for : " + site.getHomepage());
			CrawlPlan crawlPlan = new SiteCrawlPlan(site);
			ActorRef crawler = Asyncleton.getInstance().getMainSystem().actorOf(Props.create(Crawler.class, crawlPlan.generateContext()));
			crawler.tell(new StartWork(), getSelf());	
			crawler.tell(new EndWhenReady(), getSelf());	
		});
		
	}
	
	private void processCrawlPlan(SiteCrawlPlan crawlPlan){
		ActorRef crawler = Asyncleton.getInstance().getMainSystem().actorOf(Props.create(Crawler.class, crawlPlan.generateContext()));
		crawler.tell(new StartWork(), getSelf());	
		crawler.tell(new EndWhenReady(), getSelf());
	}
	
}
