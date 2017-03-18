package crawling.discovery.html;

import java.net.URI;
import java.text.SimpleDateFormat;

import crawling.discovery.execution.CrawlContext;
import crawling.discovery.planning.CrawlPlan;
import dao.SitesDAO;
import global.Global;
import persistence.Site;
import persistence.SiteCrawl;
import play.db.jpa.JPA;
import utilities.DSFormatter;

public class SiteCrawlPlan extends CrawlPlan {

//	protected Long siteId;
//	public SiteCrawlPlan(CrawlContext crawlContext, Site site) {
//		super(crawlContext);
//		this.siteId = site.getSiteId();
//	}
//
//	@Override
//	public void preCrawl() {
//		JPA.withTransaction(() -> {
//			Site site = JPA.em().find(Site.class, siteId);
//			URI uri = new URI(site.getHomepage());
//			setSeed(uri);
//			SiteCrawl siteCrawl = new SiteCrawl(site);
//			JPA.em().persist(siteCrawl);
//			crawlContext.putContextObject("siteCrawlId", siteCrawl.getSiteCrawlId());
//			
//			String date = new SimpleDateFormat("MM-dd-yyyy").format(new java.util.Date());  
//			crawlContext.putContextObject("storageFolderName", Global.getCrawlStorageFolder() + "/" + date + "/" + DSFormatter.makeSafeFilePath(site.getHomepage()));
//			crawlContext.putContextObject("seedHost", site.getDomain());
//			crawlContext.putContextObject("baseUriString", uri.getScheme() + "://" + uri.getHost());
//		});
//	}
	
	

}
