package datatransfer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import dao.CrawlSetDAO;
import dao.DealerDAO;
import dao.GeneralDAO;
import dao.SitesDAO;
import datadefinitions.OEM;
import persistence.CrawlSet;
import persistence.Dealer;
import persistence.PageCrawl;
import persistence.SFEntry;
import persistence.Site;
import persistence.SiteCrawl;
import persistence.Staff;
import persistence.Utility;
import persistence.salesforce.SalesforceAccount;
import play.Logger;
import play.db.DB;
import play.db.jpa.JPA;
import utilities.DSFormatter;

public class Cleaner {
	
	public static void combinePageCrawls(PageCrawl p1, PageCrawl p2) {
//		if(!p1.getUrl().equalsIgnoreCase((p2.getUrl())){
//			System.out.println("unequal pages");
//			return;
//		}
//		
		
	}
	
	public static void combineOnDomain(Site primary) {
		System.out.println("Combining all sites for domain : " + primary.getDomain() + " homepage : " + primary.getHomepage());
		List<Site> sites = SitesDAO.getList("domain", primary.getDomain(), 0, 0);
		for(Site site : sites) {
			if(site.getSiteId() == primary.getSiteId()) {
				continue;
			}
			primary.addRedirectUrl(site.getHomepage());
			combineSites(primary, site);
		}
	}
	
	public static int cleanDuplicateHomepages() {
		
		List<String> dups = SitesDAO.getDuplicateHomepages(0,0);
		
		for(String homepage : dups) {
			System.out.println("Cleaning up for homepage : " + homepage);
			List<Site> sites = SitesDAO.getList("homepage", homepage, 0, 0);
			while(sites.size() > 1) {
				Cleaner.combineSites(sites.get(0), sites.get(1));
				JPA.em().getTransaction().commit();
				runGc();
				JPA.em().getTransaction().begin();
				sites = SitesDAO.getList("homepage", homepage, 0, 0);
			}
		}
		return dups.size();
	}
	
	public static Site combineSites(Site site1, Site site2) {
		if(!site1.getHomepage().equalsIgnoreCase(site2.getHomepage())){
			Logger.warn("site1 home page " + site1.getHomepage() + " does not equal site2 homepage " + site2.getHomepage());
		}
		System.out.println("Combining sites. Site1 : " + site1.getSiteId() + ". Site2 : " + site2.getSiteId());
	
		site1.getCrawls().addAll(site2.getCrawls());
		site2.getCrawls().clear();
		
		site1.getMobileCrawls().addAll(site2.getMobileCrawls());
		site2.getMobileCrawls().clear();
		
		site1.getRedirectUrls().addAll(site2.getRedirectUrls());
		site2.getRedirectUrls().clear();
		
		site1.getGroupUrls().addAll(site2.getGroupUrls());
		site2.getGroupUrls().clear();
		
		List<Dealer> dealers = DealerDAO.bySite(site2);
		for(Dealer dealer : dealers) {
			System.out.println("found dealer");
			dealer.setMainSite(site1);
		}
		
		List<SFEntry> entries = GeneralDAO.getList(SFEntry.class, "mainSite", site2);
		for(SFEntry entry : entries) {
			System.out.println("found entry");
			entry.setMainSite(site1);
		}
		
		List<SalesforceAccount> accounts = GeneralDAO.getList(SalesforceAccount.class, "site", site2);
		for(SalesforceAccount account : accounts) {
			System.out.println("Setting site for salesforce account : " + account.getSalesforceAccountId());
			account.setSite(site1);
		}
		
		List<Site> forwarders = GeneralDAO.getList(Site.class, "forwardsTo", site2);
		for(Site forwarder : forwarders) {
			System.out.println("found forwarder");
			forwarder.setForwardsTo(site1);
		}
		
		List<CrawlSet> crawlSets = CrawlSetDAO.bySite(site2);
		for(CrawlSet crawlSet : crawlSets) {
			System.out.println("found crawlset : " + crawlSet.getCrawlSetId());
			System.out.println("removed : " + crawlSet.getSites().remove(site2));
			System.out.println("added : " + crawlSet.getSites().add(site1));
			if(crawlSet.getUncrawled().remove(site2)){
				crawlSet.getUncrawled().add(site1);
			}
			if(crawlSet.getNeedMobile().remove(site2)){
				crawlSet.getNeedMobile().add(site1);
			}
			if(crawlSet.getNeedRedirectResolve().remove(site2)){
				crawlSet.getNeedRedirectResolve().add(site1);
			}
		}
		site1.setCrawlerProtected(site1.isCrawlerProtected() || site2.isCrawlerProtected());		
		
		
		JPA.em().remove(site2);
		return site1;
	}
	
	public static void cleanDomains(){
		String query = "from Site s where s.domain like '%www%'";
		List<Site> sites = JPA.em().createQuery(query, Site.class).getResultList();
		
		System.out.println("Num sites : "+ sites.size());
		for(Site site : sites) {
//			site.setDomain(DSFormatter.removeWww(site.getDomain()));
		}
	}
	
	
	
public static void markOems() {
		
		String query = "from Dealer d where d.mainSite.homepage like ? or d.dealerName like ?";
		javax.persistence.Query q = JPA.em().createQuery(query);
		
		for(OEM maker : OEM.values()) {
			System.out.println("checking for oem : " + maker.definition);
			q.setParameter(1, "%" + maker.definition + "%");
			q.setParameter(2, "%" + maker.definition + "%");
			List<Dealer> dealers = q.getResultList();
			System.out.println("dealers size : " + dealers.size());
			for(Dealer dealer : dealers) {
				dealer.setOemDealer(true);
			}
			JPA.em().getTransaction().commit();
			JPA.em().getTransaction().begin();
		}
	}
	
	public static void clearBlankStaff(){
		String query = "from SiteCrawl sc where sc.allStaff is not empty";
		List<SiteCrawl> crawls  = JPA.em().createQuery(query, SiteCrawl.class).getResultList();
		
		int count = 0;
		for(SiteCrawl siteCrawl : crawls) {
			List<Staff> remove = new ArrayList<Staff>();
			for(Staff staff : siteCrawl.getAllStaff()) {
				if(DSFormatter.isBlank(staff.getCell())
						&& DSFormatter.isBlank(staff.getEmail())
						&& DSFormatter.isBlank(staff.getFn())
						&& DSFormatter.isBlank(staff.getName())
						&& DSFormatter.isBlank(staff.getOther())
						&& DSFormatter.isBlank(staff.getPhone())
						&& DSFormatter.isBlank(staff.getTitle())
						){
					remove.add(staff);
				}
			}
			
			for(Staff staff : remove) {
//				System.out.println("removing : " + staff);
				siteCrawl.getAllStaff().remove(staff);
				count++;
			}
		}
		System.out.println("removed : " + count);
	}
	
	
	public static void removeSpecialPartials() {
		
		String query = "from SFEntry sf where sf.mainSite is not null";
		List<SFEntry> entries = JPA.em().createQuery(query, SFEntry.class).getResultList();
		System.out.println("entries : " + entries.size());
		int count = 0;
		for(SFEntry entry : entries) {
			Site site = entry.getMainSite();
			if(site.getCrawls().size() > 0 && count < 50000) {
				count++;
				SiteCrawl siteCrawl = site.getLatestCrawl();
				if(siteCrawl.getNumRetrievedFiles() < 2) {
					System.out.println("site : " + site.getHomepage());
					System.out.println("small crawl************");
					site.getCrawls().remove(siteCrawl);
					JPA.em().remove(siteCrawl);
//					site.setHomepageNeedsReview(false);
					site.setRedirectResolveDate(null);
//					site.setReviewLater(false);
//					site.setSuggestedHomepage(null);
				}
				else{
//					System.out.println("not small");
				}
					
			}
		}
	}
	
	public static void resolveByPastRedirects() {
		String query = "from Site s where s.homepageNeedsReview = true";
		List<Site> sites = JPA.em().createQuery(query, Site.class).getResultList();
		int count = sites.size();
		System.out.println("count : " + count);
		for(Site site : sites) {
			System.out.println("Site (" + count-- + ": " + site.getSiteId() + " " + site.getHomepage());
			String pastRedirect = Utility.getPastRedirect(site.getHomepage());
			
			if(!DSFormatter.isEmpty(pastRedirect)){
				System.out.println("Filling by past redirect : " + pastRedirect);
				site.addRedirectUrl(site.getHomepage());
				site.setHomepage(pastRedirect);
//				site.setHomepageNeedsReview(false);
			}
//			else if(StringUtils.isNotBlank(site.getSuggestedHomepage())) {
//				String pastApproved = Utility.getPastApproval(site.getSuggestedHomepage());
//				if(StringUtils.isNotBlank(pastApproved)){
//					site.addRedirectUrl(site.getHomepage());
//					site.setHomepage(pastApproved);
//					site.setHomepageNeedsReview(false);
//					site.setRedirectResolveDate(new java.sql.Date(Calendar.getInstance().getTimeInMillis()));
//				}
//			}
					
		}
		
	}
	
	public static void combineDuplicateSites() throws SQLException {
		Connection connection = DB.getConnection();
		
		String query = "SELECT homepage, COUNT(*) c FROM site GROUP BY homepage HAVING c > 1 order by homepage";
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery(query);
		
		List<String> urls = new ArrayList<String>();
		List<Site> sites = new ArrayList<Site>();
		List<Dealer> dealers = new ArrayList<Dealer>();
		while(rs.next()) {
			urls.add(rs.getString("homepage"));
		}
		
		rs.close();
		statement.close();
		connection.close();
		
		query = "from Site s where s.homepage = ?";
		String subQuery = "from Dealer d where d.mainSite.siteId = ?";
		Query q = JPA.em().createQuery(query, Site.class);
		
		for(String url : urls) {
			dealers.clear();
			System.out.println("Combining sites for url : " + url);
			q.setParameter(1, url);
			sites = q.getResultList();
			System.out.println("Found sites : " + sites.size());
			Site theSite = null;
			
			//Not the most efficient way, but clean
			for(Site site : sites) {
				if(site.getCrawls().size() > 0){
					System.out.println("Site has crawls");
					if(theSite == null) {
						theSite = site;
					}
					else {
						theSite.addCrawls(site.getCrawls());
						theSite.getRedirectUrls().addAll(site.getRedirectUrls());
						site.getCrawls().clear();
					}
				}
				dealers.addAll(JPA.em().createQuery(subQuery).setParameter(1, site.getSiteId()).getResultList());
			}
			if(theSite == null) {
				theSite = sites.get(0);
			}
			System.out.println("Found dealers : " + dealers.size());
			for(Dealer dealer : dealers) {
				System.out.println("Setting dealer's site : " + dealer.getDealerId());
				dealer.setMainSite(theSite);
			}
			
			for(Site site : sites) {
				if(site == theSite) {
					System.out.println("Not removing : " + site.getSiteId());
				}
				else {
					System.out.println("Removing : " + site.getSiteId());
					JPA.em().remove(site);
				}
			}
			
			JPA.em().getTransaction().commit();
			JPA.em().getTransaction().begin();
		}
	}

	public static void runGc() {
		System.out.println("Running garbage collection");
		System.gc();
	}
	
}
