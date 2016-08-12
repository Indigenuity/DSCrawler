package controllers;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.restfb.Version;

import agarbagefolder.SiteWork;
import crawling.DealerCrawlController;
import crawling.Facebook;
import akka.actor.ActorRef;
import analysis.SiteCrawlAnalyzer;
import analysis.PageAnalyzer;
import analysis.SiteAnalyzer;
import analysis.SiteSummarizer;
import async.async.Asyncleton;
import async.monitoring.AsyncMonitor;
import audit.AuditDao;
import audit.map.MapControl;
import audit.map.SalesforceToSiteMapSession;
import audit.sync.SalesforceControl;
import audit.sync.Sync;
import audit.sync.SyncControl;
import audit.sync.SyncType;
import dao.GeneralDAO;
import dao.SiteCrawlDAO;
import dao.SiteInformationDAO;
import dao.SitesDAO;
import datatransfer.Amalgamater;
import datatransfer.CSVGenerator;
import datatransfer.CSVImporter;
import datatransfer.Cleaner;
import datatransfer.SourceSwapper;
import datatransfer.reports.Report;
import datatransfer.reports.ReportRow;
import persistence.CapEntry;
import persistence.CrawlSet;
import persistence.Dealer;
import persistence.GroupAccount;
import persistence.Dealer.Datasource;
import persistence.Site.SiteStatus;
import persistence.MobileCrawl;
import persistence.PageInformation;
import persistence.SFEntry;
import persistence.Site;
import persistence.SiteCrawl;
import persistence.SiteInformationOld;
import persistence.SiteSummary;
import persistence.Staff;
import persistence.Temp;
import persistence.TestEntity;
import persistence.salesforce.SalesforceAccount;
import places.PlacesPage;
import places.Retriever;
import places.ZipLocation;
import play.*;
import play.data.DynamicForm;
import play.data.Form;
import play.data.Form.Field;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import play.mvc.*;
import urlcleanup.ListCheck;
import urlcleanup.ListCheckConfig;
import urlcleanup.ListCheckFactory;
import urlcleanup.ListCheckConfig.InputType;
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
    public static Result sitesDashboard() {
    	return ok(views.html.sitesDashboard.render());
    }
    
    @Transactional
    public static Result validateSites() {
    	Cleaner.validateSites();
    	return ok();
    }
    
    @Transactional
    public static Result salesforceWebsiteReport() throws IOException {
    	SalesforceControl.printSignificantDifferenceReport();
    	return ok();
    }
    
    @Transactional
    public static Result assignChangedWebsites(long syncId){
    	Sync sync = JPA.em().find(Sync.class, syncId);
    	SalesforceControl.assignChangedWebsites(sync);
    	return ok();
    }
    
    @Transactional
    public static Result assignSiteless(){
    	SalesforceControl.assignSiteless();
    	return ok("Salesforce accounts successfully mapped to Site objects.");
    }
    
    @Transactional
    public static Result viewSync(long syncId) {
    	Sync sync = JPA.em().find(Sync.class, syncId);
    	
    	return ok(views.html.salesforcesync.sync.render(sync));
    }
    
    public static Result allSyncs(){
    	return ok(views.html.salesforcesync.allSyncs.render());
    }
    
    @Transactional
    public static Result syncList(String syncType){
    	List<Sync> syncs = AuditDao.getSyncsOfType(SyncType.valueOf(syncType), 20, 0);
    	return ok(views.html.salesforcesync.syncList.render(syncs));
//    	return ok();
    }
    
    @Transactional
    public static Result generateSyncReport(long syncId) throws IOException{
    	Sync sync = JPA.em().find(Sync.class, syncId);
    	if(sync.getSyncType() == SyncType.GROUP_ACCOUNTS){
    		SyncControl.generateAllReports(GroupAccount.class, sync);
    	} else if(sync.getSyncType() == SyncType.DEALERS){
    		SyncControl.generateAllReports(Dealer.class, sync);
    	} else if(sync.getSyncType() == SyncType.TEST){
    		SyncControl.generateAllReports(TestEntity.class, sync);
    	}
    	return ok("Generated All Reports");
    }
    
    @Transactional
    public static Result insertedAtSync(long syncId, int count, int offset){
    	Sync sync = JPA.em().find(Sync.class, syncId);
    	Integer revisionNumber = AuditDao.getRevisionOfSync(sync);
    	Class<?> clazz = AuditDao.getType(sync);
    	List<?> accounts = AuditDao.getInsertedAtRevision(clazz, revisionNumber, count, offset);
    	AuditDao.getInsertedAtRevision(AuditDao.getType(sync),  AuditDao.getRevisionOfSync(sync), 20, 0);
    	return ok();
    }
    @Transactional
    public static Result updatedAtSync(long syncId, int count, int offset){
    	Sync sync = JPA.em().find(Sync.class, syncId);
    	Integer revisionNumber = AuditDao.getRevisionOfSync(sync);
    	Class<?> clazz = AuditDao.getType(sync);
    	List<?> accounts = AuditDao.getUpdatedAtRevision(clazz, revisionNumber, count, offset);
    	
    	return ok();
    }
    @Transactional
    public static Result deletedAtSync(long syncId, int count, int offset){
    	Sync sync = JPA.em().find(Sync.class, syncId);
    	Integer revisionNumber = AuditDao.getRevisionOfSync(sync);
    	Class<?> clazz = AuditDao.getType(sync);
    	List<?> accounts = AuditDao.getDeletedAtRevision(clazz, revisionNumber, count, offset);
    	
    	return ok();
    }
    
    
    @Transactional
    public static Result runSalesforceSync() throws IOException {
    	DynamicForm data = Form.form().bindFromRequest();
		String inputFilename = data.get("inputFilename");
		Boolean generateReports = data.get("generateReports") == null ? false : true;
		SalesforceControl.sync(inputFilename, generateReports);
		
		return ok("Synced with salesforce accounts from file : " + inputFilename);
    }
    
    public static Result urlCleanupForm() {
    	File inputFolder = new File(Global.getWebsiteListInputFolder());
    	Arrays.sort(inputFolder.listFiles(), new Comparator<File>(){
    	    public int compare(File f1, File f2)
    	    {
    	        return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
    	    } });
    	return ok(views.html.tasks.urlCleanupForm.render());
    }
    
    @Transactional
    public static Result urlCleanupStart() throws IOException {
    	System.out.println("in cleanup start");
    	DynamicForm data = Form.form().bindFromRequest();
		String inputTypeString = data.get("inputType");
		String inputFilename = data.get("inputFilename");
		Boolean useProxy = Boolean.parseBoolean(data.get("useProxy"));
		String proxyUrl = data.get("proxyUrl");
		String proxyPort = data.get("proxyPort");
		
		ListCheckConfig config = new ListCheckConfig();
		config.setInputFilename(inputFilename);
		config.setUseProxy(useProxy);
		config.setProxyPort(proxyPort);
		config.setProxyUrl(proxyUrl);
		config.setInputType(InputType.valueOf(inputTypeString));
		
		ListCheck listCheck = ListCheckFactory.createListCheck(config);
		System.out.println("listCheck : " + listCheck);
		System.out.println("report size : " + listCheck.getReport().getReportRows().size());
		Report report = listCheck.getReport();
		System.out.println("primary : " + report.getKeyColumn());
		for(String columnLabel : report.getColumnLabels()){
			System.out.print(columnLabel + "       ");
		}
		System.out.println("");
//		for(ReportRow reportRow : report.getReportRows()){
//			for(String columnLabel : report.getColumnLabels()){
//				System.out.print(reportRow.getCells().get(columnLabel) + "     ");
//			}
//		}
		report.getColumnLabels();
		JPA.em().persist(listCheck);
    	return ok();
    }
    
    public static Result filePicker() {
    	String folderName = Global.getWebsiteListInputFolder();
    	File folder = new File(folderName);
    	File[] sortedFiles = folder.listFiles();
    	Arrays.sort(sortedFiles, new Comparator<File>(){
    	    public int compare(File f1, File f2)
    	    {
    	        return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
    	    } });
    	
    	JsonNode json = Json.toJson(sortedFiles);
    	System.out.println("json : " + json);
    	return ok();
    }
    
    public static Result getLocalFiles(String folderName) {
    	File folder = new File(folderName);
    	File[] sortedFiles = folder.listFiles();
    	Arrays.sort(sortedFiles, new Comparator<File>(){
    	    public int compare(File f1, File f2)
    	    {
    	        return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
    	    } });
    	return ok(Json.toJson(sortedFiles));
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
    
    public static Result createTaskSetForm(){
    	
    	return ok(views.html.tasks.newTaskSetForm.render());
    }
    
    @Transactional
    public static Result groupSites(int numToProcess, int offset) {
    	String query = "from Site s where s.groupSite = true and showToMatt = true";
    	List<Site> sites = JPA.em().createQuery(query, Site.class).getResultList();
    	
    	return ok(groupSites.render(sites));
    	
    }
    
    @Transactional
    public static Result runGc() {
    	System.out.println("Running Garbage Collector");
    	JPA.em().clear();
//    	Asyncleton.getInstance().restart();
    	System.gc(); 
    	return ok(); 
    }
    
  
}
