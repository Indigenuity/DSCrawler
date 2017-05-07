package crawling;

import java.io.File;
import java.net.URI;

import com.google.common.util.concurrent.RateLimiter;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import async.async.Asyncleton;
import async.async.TypedMaster;
import crawling.discovery.execution.CrawlOrder;
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
import newwork.WorkOrder;
import persistence.Site;
import play.db.jpa.JPA;

public class CrawlMaster extends TypedMaster<Crawler>{
	
	public CrawlMaster(int numWorkers) {
		super(numWorkers);
	}

	@Override
	public Class<Crawler> getType() {
		return Crawler.class;
	}

	@Override
	protected WorkOrder generateWorkOrder(Object message) {
		if(message instanceof CrawlPlan){
			return new CrawlOrder((CrawlPlan)message);
		} 
		throw new IllegalArgumentException("Can't generate CrawlOrder for object of unknown type : " + message);
	}
	
}
