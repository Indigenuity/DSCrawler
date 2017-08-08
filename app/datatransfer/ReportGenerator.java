package datatransfer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import akka.util.Timeout;
import analysis.AnalysisDao;
import analysis.SiteCrawlAnalysis;
import async.async.Asyncleton;
import async.functionalwork.JpaFunctionalBuilder;
import dao.SalesforceDao;
import datatransfer.reports.Report;
import datatransfer.reports.ReportRow;
import persistence.Site;
import persistence.SiteCrawl;
import play.Logger;
import play.db.jpa.JPA;
import salesforce.persistence.SalesforceAccount;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;
import sites.persistence.SiteSet;
import sites.utilities.SiteLogic;
import sites.utilities.SiteSetDao;

public class ReportGenerator {
	
	public static ReportRow combine(ReportRow first, ReportRow second){
		first.putCells(second.getCells());
		return first;
	}
	
	/********************************* Website URL report *******************************************/
	
	public static Report generateWebsiteReport() throws Exception{
		System.out.println("Generating Website Report for Salesforce Accounts");
		System.out.println("Fetching accounts...");
		List<Long> accountIds = SalesforceDao.findAccountsWithNonEmptySites();
		Function<Long, ReportRow> reportRowFunction = (accountId) -> {
//			Logger.debug("Creating report row for accountid : " + accountId);
			ReportRow reportRow = toWebsiteReportRow(JPA.em().find(SalesforceAccount.class, accountId));
			return reportRow;
		};
		
		Function<List<ReportRow>, Report> accumulator = (reportRows) -> {
			Report report = new Report("Website Report for Salesforce Accounts");
			for(ReportRow reportRow : reportRows){
				report.addReportRow(reportRow.getCell("Salesforce Unique ID"), reportRow);
			}
			return report;
		};
		Future<Object> future = Asyncleton.getInstance().runResultAccumulator(
				reportRowFunction, 
				accumulator, 
				accountIds.stream(), 
				10, 
				true);
		return (Report)Await.result(future, FiniteDuration.create(1, TimeUnit.DAYS));
	}
	
	public static ReportRow toWebsiteReportRow(SalesforceAccount account){
		ReportRow reportRow = new ReportRow();
		reportRow.putCell("Salesforce Unique ID", account.getSalesforceId());
		reportRow.putCell("Account Name", account.getName());
		reportRow.putCell("Current Salesforce Website", account.getWebsiteString());
		reportRow.putCell("Trivial Difference", SiteLogic.redirectIsTrivial(account.getUnresolvedSite()));
		return combine(reportRow, toWebsiteReportRow(account.getSite()));
	}
	
	public static ReportRow toWebsiteReportRow(Site site){
		ReportRow reportRow = new ReportRow();
		reportRow.putCell("Website (Primary) URL", site.getHomepage());
		reportRow.putCell("badUrlStructure", site.getBadUrlStructure());
		reportRow.putCell("defunctDomain", site.getDefunctDomain());
		reportRow.putCell("defunctPath", site.getDefunctPath());
		reportRow.putCell("defunctContent", site.getDefunctContent());
		reportRow.putCell("uncrawlableDomain", site.getUncrawlableDomain());
		reportRow.putCell("uncrawlablePath", site.getUncrawlablePath());
		reportRow.putCell("Http status code", site.getUrlCheck().getStatusCode());
		reportRow.putCell("Site is kosher", site.getFullyKosher());
		return reportRow;
	}
	
	
	/*********************************  DealerFire Reporting *********************************************/
	
	public static Report generateDealerFireReport(List<Long> siteIds) throws Exception{
		System.out.println("Generating DealerFire Report for " + siteIds.size() + " sites");
		
		Function<Long, List<ReportRow>> reportRowFunction = (siteId) -> {
			Site site = JPA.em().find(Site.class, siteId);
			return toDealerFireReportRows(site);
		};
		
		Function<List<List<ReportRow>>, Report> accumulator = (reportRowLists) -> {
			Report report = new Report("DealerFire report");
			for(List<ReportRow> reportRowList : reportRowLists){
				for(ReportRow reportRow : reportRowList){
					report.addReportRow(reportRow.getCell("Salesforce Unique ID"), reportRow);
				}
			}
			return report;
		};
		Future<Object> future = Asyncleton.getInstance().runResultAccumulator(
				reportRowFunction, 
				accumulator, 
				siteIds.stream(), 
				5, 
				true);
		return (Report)Await.result(future, FiniteDuration.create(1, TimeUnit.DAYS));
	}
	
	public static List<ReportRow> toDealerFireReportRows(Site site){
		List<ReportRow> reportRows = new ArrayList<ReportRow>();
		List<SalesforceAccount> accounts = SalesforceDao.findBySite(site);
		SiteCrawl siteCrawl = site.getLastCrawl();
		SiteCrawlAnalysis analysis = AnalysisDao.get(siteCrawl.getSiteCrawlId());
		for(SalesforceAccount account : accounts) {
			ReportRow reportRow = toDealerFireReportRow(account);
			reportRow = combine(reportRow, toDealerFireReportRow(analysis));
			reportRows.add(reportRow);
		}	
		return reportRows;
	}
	
	public static ReportRow toDealerFireReportRow(SalesforceAccount account){
		ReportRow reportRow = new ReportRow();
		reportRow.putCell("Salesforce Unique ID", account.getSalesforceId());
		reportRow.putCell("Account Name", account.getName());
		reportRow.putCell("Website (Primary) URL", account.getSalesforceWebsite());
//		reportRow.putCell("Google Rating*", ""); 		Can get from linked Places accounts if/when we scrape them
//		reportRow.putCell("Responsive?", "");			Can get from mobile crawl.  
//		reportRow.putCell("Mobile Redirect Site", "");	Can get from mobile crawl.
		return reportRow;
	}
	
	public static ReportRow toDealerFireReportRow(SiteCrawlAnalysis analysis){
//		reportRow.putCell("Primary Website Provider", analysis.);
		ReportRow reportRow = new ReportRow();
		reportRow.putCell("URL Used for Analysis", analysis.getSiteCrawl().getSeed());
//		reportRow.putCell("OEM Mandated Website URL", "");		Don't have a way of retrieving this at the moment
		reportRow.putCell("OEM Mandated Website", analysis.getOemMandated());
		reportRow.putCell("Primary Website Provider", analysis.getPrimaryWebProvider());
		reportRow.putCell("Unique URL Score", analysis.getUrlUniqueScore());
		reportRow.putCell("URL Location Qualifier Score", analysis.getUrlLocationScore());
		reportRow.putCell("URL Clean/Readable Score", analysis.getUrlCleanScore());
		reportRow.putCell("Unique Title Tag Score", analysis.getTitleUniqueScore());
		reportRow.putCell("Title Tag Length Score", analysis.getTitleLengthScore());
		reportRow.putCell("Title Tag Content Score", analysis.getTitleContentScore());
		reportRow.putCell("Alt. Image Tag Score", analysis.getAltImageScore());
		reportRow.putCell("Unique H1 Score", analysis.getH1UniqueScore());
		reportRow.putCell("SRP H1 Content Score", analysis.getH1ContentScore());
		reportRow.putCell("Unique Meta Description Score", analysis.getMetaDescriptionUniqueScore());
		reportRow.putCell("Meta Description Length Score", analysis.getMetaDescriptionLengthScore());
		reportRow.putCell("Meta Description Content Score", analysis.getMetaDescriptionContentScore());
		return reportRow;
	}

}
