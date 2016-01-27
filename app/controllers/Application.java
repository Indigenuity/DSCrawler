package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Hibernate;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.restfb.Version;

import crawling.DealerCrawlController;
import crawling.Facebook;
import akka.actor.ActorRef;
import analysis.SiteCrawlAnalyzer;
import analysis.PageAnalyzer;
import analysis.SiteAnalyzer;
import analysis.SiteSummarizer;
import async.Asyncleton;
import async.monitoring.AsyncMonitor;
import async.work.SiteWork;
import dao.SiteCrawlDAO;
import dao.SiteInformationDAO;
import dao.SitesDAO;
import datatransfer.Amalgamater;
import datatransfer.CSVGenerator;
import datatransfer.CSVImporter;
import datatransfer.Cleaner;
import datatransfer.SourceSwapper;
import persistence.CapEntry;
import persistence.CrawlSet;
import persistence.Dealer;
import persistence.Dealer.Datasource;
import persistence.MobileCrawl;
import persistence.PageInformation;
import persistence.PlacesPage;
import persistence.SFEntry;
import persistence.Site;
import persistence.SiteCrawl;
import persistence.SiteInformationOld;
import persistence.SiteSummary;
import persistence.Staff;
import persistence.Temp;
import persistence.ZipLocation;
import places.Retriever;
import play.*;
import play.data.DynamicForm;
import play.data.Form;
import play.data.Form.Field;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import play.mvc.*;
import utilities.DSFormatter;
import utilities.UrlSniffer;
import viewmodels.SharedEntity;
import views.html.*;
import edu.uci.ics.crawler4j.crawler.*;
import experiment.ApiExperiment;
import experiment.Experiment;
import experiment.Storage;
import global.Global;

public class Application extends Controller {
	
	final static Logger.ALogger dsLogger = Logger.of("ds");

    public static Result index() {
        return ok(index.render("Your new application is ready.")); 
    }
    
    @Transactional
    public static Result runExperiment() throws Exception 
    {
    	
    	Experiment.runExperiment();
    	return ok();
    }
    
    @Transactional
    public static Result runExpeasdfasfdriment() throws IOException {
    	
    	List<SiteCrawl> sites = SiteCrawlDAO.getFilesMoved(2L, 500000, 0);
    	System.out.println("result : " + sites.size());
    	for(SiteCrawl site : sites) {
//    		System.out.println("crawls :: " + site.getLatestCrawl())
    	}
    	return ok(sites.size() + "");
    }
    
    @Transactional
    public static Result runExperimenasdfasdft() {
    	String query = "from "
    			+ "Site s where s.redirectResolveDate is null and s.suggestedHomepage is not null";
    	List<Site> sites = JPA.em().createQuery(query, Site.class).setMaxResults(7000).getResultList();
    	System.out.println("Sites : " + sites.size());
    	for(Site site : sites) {
    		System.out.println("Original : " + site.getHomepage());
    		System.out.println("Redirect : " + site.getSuggestedHomepage());
    		if(UrlSniffer.isGenericRedirect(site.getSuggestedHomepage(), site.getHomepage())){
    			System.out.println("approved");
    			site.getRedirectUrls().add(site.getHomepage());
    			site.setHomepage(site.getSuggestedHomepage());
    			site.setHomepageNeedsReview(false);
    			site.setQueryStringApproved(true);
    			site.setHompageValidUrlConfirmed(true);
    			site.setRedirectResolveDate(new java.sql.Date(Calendar.getInstance().getTimeInMillis()));
    		}
    		else if(site.getSuggestedHomepage().contains("UnusedDomains")) {
    			System.out.println("maybe defunct");
    			site.setMaybeDefunct(true);
				site.setHomepageNeedsReview(false);
				site.setRedirectResolveDate(new java.sql.Date(Calendar.getInstance().getTimeInMillis()));
    		}
    	}
    	return ok();
    }
    
    @Transactional
    public static Result duplicateDomains(int numToProcess, int offset){
    	List<SharedEntity> items = new ArrayList<SharedEntity>();
    	Query q = JPA.em().createQuery("from Temp t where t.intermediateUrl is null and t.suggestedUrl is null and t.domain is not null and t.domain != ''").setMaxResults(20);
    	List<Temp> sfs = new ArrayList<Temp>();
    	do {
    		sfs = q.getResultList();
    		for(Temp temp : sfs) {
    			List<Site> sites = SitesDAO.getSitesWithRedirectUrl(temp.getDomain(), 20, 0);
    			if(sites.size() > 0){
    				SharedEntity item = new SharedEntity();
    				item.setSites(sites);
    				item.setTemp(temp);
    				items.add(item);
//    				System.out.println("found one ");
    			}
    			else{
    				temp.setIntermediateUrl("checked");
    			}
    		}
    		JPA.em().getTransaction().commit();
    		JPA.em().getTransaction().begin();
    		
    	}
    	while(items.size() < 20 && sfs.size() > 1);
    	 
		System.out.println("sfs : " + sfs.size());
		
		
		
		
//    	List<String> domains = SitesDAO.getDuplicateDomains(numToProcess, offset);
//    	
//    	for(String domain : domains){
//    		List<Site> sites = SitesDAO.getList("domain", domain, numToProcess, offset);
//    		SharedEntity item = new SharedEntity();
//    		item.setSites(sites);
//    		item.setUrl(domain);
//    		items.add(item);
//    	}
    	
    	return ok(views.html.duplicateDomains.render(items));
    }
    
    
    
    @Transactional 
    public static Result continueCrawlSet(int numToProcess, long crawlSetId) {
    	CrawlSet crawlSet = JPA.em().find(CrawlSet.class, crawlSetId);
    	int count = 0;
    	Site hardcode = JPA.em().find(Site.class, 38552L);
    	for(Site site : crawlSet.getUncrawled()){
    		if(count++ < numToProcess) {
	    			
				JPA.em().detach(site);
				SiteWork work = new SiteWork();
				work.setSite(site);
				work.setCrawlWork(SiteWork.DO_WORK);
//				work.setCrawlSet(crawlSet);
	//			work.setDocAnalysisWork(SiteWork.DO_WORK);
	//			work.setAmalgamationWork(SiteWork.DO_WORK);
	//			work.setTextAnalysisWork(SiteWork.DO_WORK);
				System.out.println("site id " + site.getSiteId());
				Asyncleton.instance().getMainMaster().tell(work, ActorRef.noSender());
    		}
		}
    	return ok();
    }
    
    @Transactional
    public static Result groupSites(int numToProcess, int offset) {
    	String query = "from Site s where s.groupSite = true and showToMatt = true";
    	List<Site> sites = JPA.em().createQuery(query, Site.class).getResultList();
    	
    	return ok(groupSites.render(sites));
    	
    }
    
    @Transactional 
    public static Result sitelessDealers(int numToProcess, int offset) {
    	String query = "from Dealer d where d.mainSite is null and d.franchise = true";
    	List<Dealer> dealers = JPA.em().createQuery(query, Dealer.class).setMaxResults(numToProcess).setFirstResult(offset).getResultList();
    	
    	return ok(sitelessDealers.render(dealers));
    }
    
    @Transactional
    public static Result inferWebProviders(int numToProcess, int offset) {
    	String query = "from SiteCrawl sc where sc.inferredWebProvider = 0 order by sc.siteCrawlId desc";
    	List<SiteCrawl> siteCrawls = JPA.em().createQuery(query, SiteCrawl.class).setMaxResults(numToProcess).setFirstResult(offset).getResultList();
    	
//    	return ok();
    	return ok(inferWebProviders.render(siteCrawls));
    }
    
    @Transactional
    public static Result reviewSmallCrawls(int numToProcess, int offset) {

    	String query = "select d from Dealer d join d.mainSite s "
    			+ "join s.crawls c where d.datasource = 'SalesForce' and c.numRetrievedFiles < 10 "
    			+ "and c.smallCrawlApproved = false and s.reviewLater = false "
    			+ "and s.crawlerProtected = false and c.crawlDate > '2015-07-16' "
    			+ "and s.groupSite = false and s.recrawl = false "
    			+ "order by d.dealerId";
//    	String query = "from Site s where exists (from SiteCrawl sc where sc.site = s and sc.numRetrievedFiles = 0)";
    	numToProcess = 1000;
		List<Dealer> dealers = JPA.em().createQuery(query, Dealer.class).setMaxResults(numToProcess).setFirstResult(offset).getResultList();
		System.out.println("dealers : " + dealers.size());
    	return ok(confirmHomepages.render(dealers));
    }
    
    @Transactional
    public static Result tempConfirmHomepages(int numToProcess, int offset, long crawlSetId) {

    	String query = "from Dealer d where d.mainSite.homepageNeedsReview = true and "
    			+ "d.mainSite.maybeDefunct != true and d.mainSite.defunct != true";
//    	String query = "from Site s where exists (from SiteCrawl sc where sc.site = s and sc.numRetrievedFiles = 0)";
		List<Dealer> dealers = JPA.em().createQuery(query, Dealer.class).setMaxResults(numToProcess).setFirstResult(offset).getResultList();
    	return ok(confirmHomepages.render(dealers));
    }
    
    @Transactional
    public static Result confirmHomepages(int numToProcess, int offset, long crawlSetId) {

    	List<Site> sites;
    	if(crawlSetId > -1) {
    		sites = SitesDAO.getCrawlSetList(crawlSetId, "homepageNeedsReview", true, numToProcess, offset);
    	}
    	else {
    		String query = "from Site s where s.homepageNeedsReview = true";
    		sites = JPA.em().createQuery(query, Site.class).setMaxResults(numToProcess).setFirstResult(offset).getResultList();
    	}
//    	return ok(confirmHomepages.render(sites));
		return ok();
    }
    
    @Transactional
    public static Result crawlSets(int numToProcess, int offset) {
    	String query = "from CrawlSet cs";
    	List<CrawlSet> crawlSets = JPA.em().createQuery(query, CrawlSet.class).setMaxResults(numToProcess).setFirstResult(offset).getResultList();
    	return ok(crawlsets.render(crawlSets));
    }
    
    
    @Transactional
    public static Result testAmalgamation() throws Exception {
    	SiteCrawl siteCrawl = JPA.em().find(SiteCrawl.class, 10L);
    	System.out.println("site : " + siteCrawl);
    	
    	System.out.println("Doing doc analysis");
    	SiteCrawlAnalyzer.docAnalysis(siteCrawl);
    	siteCrawl.setDocAnalysisDone(true);
    	
		File storageFolder = new File(Global.CRAWL_STORAGE_FOLDER + "/" + siteCrawl.getStorageFolder());
		File destination = new File(Global.COMBINED_STORAGE_FOLDER + "/" + siteCrawl.getStorageFolder());
		System.out.println("destination: " + destination);
		File amalgamatedFile = Amalgamater.amalgamateFiles(storageFolder, destination);
		siteCrawl.setAmalgamationDone(true);
		
    	System.out.println("Doing text analysis");
    	SiteCrawlAnalyzer.textAnalysis(siteCrawl);
    	siteCrawl.setTextAnalysisDone(true);
    	
    	
    	return ok();
    }
    
    
    
    @Transactional
    public static Result makeDealers() throws Exception {
    	int numToProcess = 18000;
    	String query = "from SiteInformation si where si.redirectUrl is not null and si.urlRequiresReview = 0 and si.redirectUrl not in ('ERROR', 'TIMEOUT')";
    	List<SiteInformationOld> sites = JPA.em().createQuery(query, SiteInformationOld.class).setMaxResults(numToProcess).getResultList();
		System.out.println("sites size : " + sites.size());
		for(SiteInformationOld siteInfo : sites){
			
			Site site = new Site();
			site.setHomepage(siteInfo.getRedirectUrl());
			Dealer dealer = new Dealer();
			dealer.setCapdb(siteInfo.getCapdb());
			dealer.setNiada(siteInfo.getNiada());
			dealer.setDealerName(siteInfo.getSiteName());
			dealer.setFranchise(true);
			dealer.setMainSite(site);
			System.out.println("saving site: " + siteInfo.getSiteInformationId());
			JPA.em().persist(site);
			JPA.em().persist(dealer);
		}
    	return ok();
    }
    
    
    public static Result runGc() {
    	System.out.println("Running Garbage Collector");
    	System.gc();
    	return ok(); 
    }
    
    @Transactional
    public static Result experiment() throws IOException {
    	Experiment.doClear();
    	return ok();
    }
    
//    given.matches("(com|net|com/|net/|index.htm|index.html|index.cfm|home.aspx|Default.aspx|default.htm|default.html)$"
  
}
