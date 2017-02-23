package controllers;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;



import com.fasterxml.jackson.databind.JsonNode;

import audit.AuditDao;
import audit.sync.SalesforceControl;
import audit.sync.Sync;
import audit.sync.SyncControl;
import audit.sync.SyncType;
import dao.GeneralDAO;
import datatransfer.reports.Report;
import persistence.GroupAccount;
import persistence.Site;
import play.*;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.*;
import salesforce.SalesforceLogic;
import salesforce.persistence.SalesforceAccount;
import urlcleanup.ListCheck;
import urlcleanup.ListCheckConfig;
import urlcleanup.ListCheckFactory;
import urlcleanup.ListCheckConfig.InputType;
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
    	return ok(views.html.sites.sitesDashboard.render());
    }
    
  
    
    @Transactional
    public static Result assignChangedWebsites(long syncId){
    	Sync sync = JPA.em().find(Sync.class, syncId);
    	SalesforceControl.assignChangedWebsites(sync);
    	return ok();
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
    
    
    
    
    public static Result urlCleanupForm() {
    	File inputFolder = new File(Global.getWebsiteListInputFolder());
    	Arrays.sort(inputFolder.listFiles(), new Comparator<File>(){
    	    public int compare(File f1, File f2)
    	    {
    	        return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
    	    } });
//    	return ok(views.html.tasks.urlCleanupForm.render());
    	return ok();
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
