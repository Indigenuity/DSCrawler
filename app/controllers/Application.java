package controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.persistence.Query;


import com.fasterxml.jackson.databind.JsonNode;

import audit.AuditDao;
import audit.sync.SalesforceControl;
import audit.sync.Sync;
import audit.sync.SyncControl;
import audit.sync.SyncType;
import dao.GeneralDAO;
import dao.SitesDAO;
import datatransfer.reports.Report;
import persistence.Dealer;
import persistence.GroupAccount;
import persistence.Site.SiteStatus;
import persistence.Site;
import persistence.Temp;
import persistence.TestEntity;
import persistence.salesforce.SalesforceAccount;
import play.*;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.*;
import salesforce.SalesforceLogic;
import sites.SiteLogic;
import urlcleanup.ListCheck;
import urlcleanup.ListCheckConfig;
import urlcleanup.ListCheckFactory;
import urlcleanup.ListCheckConfig.InputType;
import viewmodels.SharedEntity;
import views.html.*;
import experiment.Experiment;
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
    	System.out.println("Validating sites");
		List<Site> sites = JPA.em()
				.createQuery("from Site s where s.siteStatus = :siteStatus", Site.class)
				.setParameter("siteStatus", SiteStatus.UNVALIDATED)
				.getResultList();
		
		SiteLogic.validateSites(sites);
    	return ok("Queued URL checks for " + sites.size() + " sites");
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
    public static Result resetSites(){
    	List<Long> accountIds = GeneralDAO.getFieldList(Long.class, SalesforceAccount.class, "salesforceAccountId");
    	SalesforceLogic.resetSites(accountIds);
    	return ok("Queued " + accountIds.size() + " accounts to have Site objects reset");
    }
    
    @Transactional
    public static Result forwardSites(){
    	List<Long> accountIds = GeneralDAO.getFieldList(Long.class, SalesforceAccount.class, "salesforceAccountId");
    	SalesforceLogic.forwardSites(accountIds);
    	return ok("Queued " + accountIds.size() + " accounts to be assigned the most redirected Site objects");
    }
    
    @Transactional
    public static Result assignSiteless(){
    	List<Long> accountIds = GeneralDAO.getFieldList(Long.class, SalesforceAccount.class, "salesforceAccountId", null);
    	SalesforceLogic.resetSites(accountIds);
    	return ok("Queued " + accountIds.size() + " accounts to be assigned Site objects");
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
    	} else if(sync.getSyncType() == SyncType.SALESFORCE_ACCOUNTS){
    		SyncControl.generateAllReports(SalesforceAccount.class, sync);
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
		
		System.out.println("Running Salesforce Sync Session...");
		Sync importSync = SalesforceControl.sync(inputFilename, generateReports);
		
		System.out.println("Mapping siteless salesforce accounts to Site objects...");
		Sync sitelessSync = SalesforceControl.assignSiteless();
		
		System.out.println("Assigning new Site objects to salesforce accounts with changed websites...");
		SalesforceControl.assignChangedWebsites(importSync);
		if(generateReports){
			System.out.println("Generating reports");
			SyncControl.generateAllReports(SalesforceAccount.class, importSync);
		}
		
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
