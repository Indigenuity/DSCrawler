package audit.sync;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;

import com.google.common.base.Functions;

import audit.AuditDao;
import audit.map.SalesforceToSiteMapSession;
import dao.GeneralDAO;
import dao.SitesDAO;
import datatransfer.CSVGenerator;
import datatransfer.CSVImporter;
import datatransfer.reports.Report;
import datatransfer.reports.ReportFactory;
import datatransfer.reports.ReportRow;
import persistence.Dealer;
import persistence.GroupAccount;
import persistence.Site;
import persistence.Site.SiteStatus;
import persistence.salesforce.SalesforceAccount;
import play.db.jpa.JPA;
import utilities.UrlSniffer;

public class SalesforceControl {
	
	
	
	public static void sync(String filename, boolean generateReports) throws IOException{
		System.out.println("Running Salesforce Sync Session");
		Report report = CSVImporter.importReportWithKey(filename, "Salesforce Unique ID");
		List<SalesforceAccount> localAccountsList = JPA.em().createQuery("from SalesforceAccount sa", SalesforceAccount.class).getResultList();
		Map<String, SalesforceAccount> localAccounts = localAccountsList.stream().collect(Collectors.toMap(SalesforceAccount::getSalesforceId, Function.identity()));
		
		Map<String, ReportRow> remoteAccounts = report.getReportRows().entrySet().stream()
				.collect(Collectors.toMap( Entry::getKey, Entry::getValue));
		
		SalesforceSyncSession syncSession = new SalesforceSyncSession(remoteAccounts, localAccounts);
		syncSession.runSync();
		Sync sync = syncSession.getSync();
		
		assignSiteless();
		assignChangedWebsites(sync);
		
		if(generateReports){
			List<Report> reports = syncSession.getReports();
			for(Report printedReport : reports){
				CSVGenerator.printReport(printedReport);			
			}
			Report contrastReport = ReportFactory.contrastReports(reports.get(1), reports.get(2)).setName("Salesforce Import Contrast Report");
			CSVGenerator.printReport(contrastReport);
		}
		
	}
	
	public static void assignChangedWebsites(Sync sync) {
		System.out.println("Assigning Changed Websites");
		Integer revisionNumber = AuditDao.getRevisionOfSync(sync);
    	List<SalesforceAccount> accountList = AuditDao.getPropertyUpdatedAtRevision(SalesforceAccount.class, "salesforceWebsite", revisionNumber, 1000000, 0);
    	System.out.println("accountList : " + accountList.size());
    	
    	for(SalesforceAccount account : accountList) {
    		System.out.println("account : " + account);
    		SalesforceAccount actualAccount = JPA.em().find(SalesforceAccount.class, account.getSalesforceAccountId());
    		System.out.println("actualAccount : " + actualAccount);
    		String website = account.getSalesforceWebsite();
    		Site oldSite = actualAccount.getSite();
    		System.out.println("oldSite : " + oldSite);
    		Site newSite = GeneralDAO.getFirst(Site.class, "homepage", website);
    		if(oldSite == newSite){
    			System.out.println("Change has already been accounted for or has no effect : " + website);
    			continue;
    		}
    		
			if(newSite == null){
				System.out.println("Creating new site for : " + website);
				newSite = new Site(website);
    			newSite = JPA.em().merge(newSite);
			}
			System.out.println("created site");
			System.out.println("new site : " + newSite.getHomepage());
    		System.out.println("old Site : " + oldSite.getHomepage());
			if(oldSite != null && UrlSniffer.isGenericRedirect(newSite.getHomepage(), oldSite.getHomepage())){
				System.out.println("was generic redirect");
				oldSite.setSiteStatus(SiteStatus.REDIRECTS);
    			oldSite.setForwardsTo(newSite);
    		}
    		
    		account.setSite(newSite);
    	}
    	
    	Sync newSync = new Sync(SyncType.ASSIGN_CHANGED_SITES);
    	JPA.em().persist(newSync);
    	JPA.em().getTransaction().commit();
    	JPA.em().getTransaction().begin();
	}
	
	public static void assignSiteless() {
		System.out.println("Mapping siteless salesforce accounts to Site objects...");
    	List<SalesforceAccount> accountsList = JPA.em().createQuery("from SalesforceAccount sa where sa.site is null", SalesforceAccount.class).getResultList();
		System.out.println("salesforce accounts with null site :" +  accountsList.size());
		SalesforceToSiteMapSession session = new SalesforceToSiteMapSession(accountsList);
		session.runAssignments();
	}

	//Set an account to a new homepage and redirect the current site to it.
	public static void manuallyRedirectAccount(SalesforceAccount account, String newHomepage){
		Site newSite = SitesDAO.getOrNew(newHomepage);
		newSite = JPA.em().merge(newSite);
		account.setSite(newSite);
		
		Site site = account.getSite();
		if(site == null) {
			site.setForwardsTo(newSite);
		}
	}
	//Set an account to a new homepage and abandon the old one.
	public static void manuallySeedAccount(SalesforceAccount account, String newHomepage){
		Site newSite = SitesDAO.getOrNew(newHomepage);
		newSite = JPA.em().merge(newSite);
		account.setSite(newSite);
	}
	
	public static void markSignificantDifferences() {
		List<SalesforceAccount> accounts = JPA.em().createQuery("from SalesforceAccount sa", SalesforceAccount.class).getResultList();
		System.out.println("Marking significant differences in " + accounts.size() + " accounts");
		AtomicInteger count = new AtomicInteger();
		accounts.stream().forEach( (account) -> {
			if(account.getSite() == null){
				return;
			}
			if(UrlSniffer.isGenericRedirect(account.getSite().getHomepage(), account.getSalesforceWebsite())){
				account.setSignificantDifference(true);
			} else {
				account.setSignificantDifference(false);
			}
			if(count.getAndIncrement() % 500 == 0){
				System.out.println("Accounts Processed : " + count);
			}
		});
	}

	public static void printSignificantDifferenceReport() throws IOException {
		List<SalesforceAccount> accounts = JPA.em()
				.createQuery("from SalesforceAccount sa", SalesforceAccount.class)
				.getResultList();
		Map<String, ReportRow> reportRows = accounts.stream().map( (account) -> {
			ReportRow reportRow = ReportFactory.fromObject(account);
			if(account.getSite() != null){
				reportRow.putCell("Crawler Website", account.getSite().getHomepage());
				reportRow.putCell("Crawler Domain", account.getSite().getDomain());
				reportRow.putCell("Crawler Site Status", account.getSite().getSiteStatus() + "");
				reportRow.putCell("Crawler Site Id", account.getSite().getSiteId() + "");
			}
			reportRow.putCell("Status", getRecommendation(account));
			reportRow.putCell("Account Type", account.getAccountType() + "");
			return reportRow;
		})
		.collect(Collectors.toMap( (reportRow) -> reportRow.getCell("salesforceId"), (reportRow) -> reportRow));
		
		Report report = new Report();
		report.setName("Salesforce Website Report");
		report.setReportRows(reportRows);
		
		CSVGenerator.printReport(report);
	}
	
	public static String getRecommendation(SalesforceAccount account) {
		Site site = account.getSite();
		if(site == null) {
			return "Needs Site";
		}
		if(site.getSiteStatus() == SiteStatus.DEFUNCT){
			return "Defunct";
		}
		if(site.getSiteStatus() == SiteStatus.OTHER_ISSUE){
			return "Crawler problem";
		}
		if(site.getSiteStatus() == SiteStatus.REDIRECTS){
			return "Update Crawler redirects";
		}
		if(site.getSiteStatus() == SiteStatus.NEEDS_REVIEW){
			return "Update Crawler reviews";
		}
		if(site.getSiteStatus() == SiteStatus.UNVALIDATED){
			return "Update Crawler Validation";
		}
		if(site.getSiteStatus() == SiteStatus.INVALID){
			return "Invalid Site";
		}
		if(site.getSiteStatus() == SiteStatus.SUSPECTED_DUPLICATE){
			return "Shared Website or Duplicate";
		}
		
		if(site.getSiteStatus() == SiteStatus.APPROVED){
			if(site.getHomepage().equals(account.getSalesforceWebsite())){
				return "No Change";
			}
			if(UrlSniffer.isGenericRedirect(site.getHomepage(), account.getSalesforceWebsite())){
				return "Accept Generic Redirect";
			}
			return "Accept Significant Change";
		}
		return "Site Status : " + site.getSiteStatus();
	}
	
}
