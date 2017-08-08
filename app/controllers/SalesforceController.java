package controllers;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import async.async.Asyncleton;
import async.functionalwork.JpaFunctionalBuilder;
import audit.Standardizer;
import audit.sync.SalesforceControl;
import audit.sync.Sync;
import audit.sync.SyncControl;
import dao.GeneralDAO;
import dao.SalesforceDao;
import dao.SiteOwnerLogic;
import dao.SitesDAO;
import datatransfer.CSVGenerator;
import datatransfer.CSVImporter;
import datatransfer.ReportGenerator;
import datatransfer.reports.Report;
import datatransfer.reports.ReportRow;
import persistence.Site;
import persistence.Site.SiteStatus;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import salesforce.SalesforceLogic;
import salesforce.persistence.SalesforceAccount;
import sites.crawling.SiteCrawlLogic;
import sites.persistence.SiteSet;

public class SalesforceController extends Controller {
	
	final static Logger.ALogger dsLogger = Logger.of("ds");

	@Transactional
	public static Result siteMismatchSubmit(){
		DynamicForm data = Form.form().bindFromRequest();
    	Long salesforceAccountId = Long.parseLong(data.get("salesforceAccountId"));
    	SalesforceAccount account = JPA.em().find(SalesforceAccount.class, salesforceAccountId);
    	
		System.out.println("Marking site mismatch for salesforce account : " + account.getName() + "(" + account.getSalesforceAccountId() + ")");
		account.setSiteMismatch(true);
		return ok();
	}
	
	@Transactional
	public static Result siteMismatchForms(Long siteId){
		
		List<SalesforceAccount> accounts = SalesforceDao.findBySite(JPA.em().find(Site.class, siteId));
		return ok(views.html.salesforce.siteMismatchForms.render(accounts));
	}
	
	@Transactional
	public static Result standardizeAccounts(){
		Standardizer.standardizeSalesforceAccounts();
		return ok("Accounts standardized");
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
    public static Result refreshRedirectPaths(){
    	List<Long> accountIds = GeneralDAO.getAllIds(SalesforceAccount.class);
    	Asyncleton.getInstance().runConsumerMaster(50, 
				JpaFunctionalBuilder.wrapConsumerInFind(SiteOwnerLogic::refreshRedirectPath, SalesforceAccount.class), 
				accountIds.stream(), 
				true);
    	return ok("Queued " + accountIds.size() + " accounts to be assigned the most redirected Site objects");
    }
    
    @Transactional
    public static Result assignSiteless(){
    	String queryString = "select sa.salesforceAccountId from SalesforceAccount sa where sa.site is null";
    	List<Long> accountIds = JPA.em().createQuery(queryString, Long.class).getResultList();
    	SalesforceLogic.resetSites(accountIds);
    	return ok("Queued " + accountIds.size() + " accounts to be assigned Site objects");
    }
    
    @Transactional
    public static Result salesforceWebsiteReport() throws Exception {
		Report report = ReportGenerator.generateWebsiteReport();
		File csvReport = CSVGenerator.printReport(report);
		return ok("Website report generated at file location " + csvReport.getAbsolutePath());
    }
    
    @Transactional
    public static Result runSalesforceSync() throws IOException {
    	DynamicForm data = Form.form().bindFromRequest();
		String inputFilename = data.get("inputFilename");
		Boolean generateReports = data.get("generateReports") == null ? false : true;
		Boolean refreshRedirects = data.get("refreshRedirects") == null ? false : true;
		
		Report report = CSVImporter.importReportWithKey(inputFilename, "Salesforce Unique ID");
		
		Consumer<ReportRow> consumer = (reportRow) -> {
			SalesforceAccount account = SalesforceLogic.importReportRow(reportRow);
			if(refreshRedirects){
				SiteOwnerLogic.refreshRedirectPath(account);
			}
		};
		Asyncleton.getInstance().runConsumerMaster(50, 
				consumer, 
				report.getReportRows().values().stream(), 
				true);
		
//		int count = 0;
//		for(ReportRow reportRow : report.getReportRows().values()){
//			SalesforceLogic.importReportRow(reportRow);
//			if(count ++ % 500 == 0){
//				System.out.println("imported : " + count);
//			}
//		}
//		System.out.println("imported");
		
		
//		System.out.println("Running Salesforce Sync Session...");
//		Sync importSync = SalesforceControl.sync(inputFilename, generateReports);
//		
//		System.out.println("Mapping siteless salesforce accounts to Site objects...");
//		Sync sitelessSync = SalesforceControl.assignSiteless();
//		
//		System.out.println("Assigning new Site objects to salesforce accounts with changed websites...");
//		SalesforceControl.assignChangedWebsites(importSync);
//		if(generateReports){
//			System.out.println("Generating reports");
//			SyncControl.generateAllReports(SalesforceAccount.class, importSync);
//		}
		
		return ok("Synced with salesforce accounts from file : " + inputFilename);
    }
    
    @Transactional
    public static Result generateSiteSet(){
    	SiteSet siteSet = new SiteSet("Unique sites in Salesforce " + new Date());
    	siteSet = JPA.em().merge(siteSet);
    	List<Site> sites = SalesforceDao.getUniqueSites();
    	siteSet.addSites(sites);
    	
    	return redirect("/sites/siteSets/viewSiteSet/" + siteSet.getSiteSetId());
    }
    
}
