package datatransfer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;


import akka.actor.ActorRef;
import async.async.Asyncleton;
import async.work.Order;
import dao.GeneralDAO;
import dao.SitesDAO;
import persistence.PageCrawl;
import persistence.Site;
import persistence.SiteCrawl;
import persistence.Staff;
import places.PlacesDealer;
import persistence.Site.SiteStatus;
import play.Logger;
import play.db.DB;
import play.db.jpa.JPA;
import salesforce.persistence.SalesforceAccount;
import urlcleanup.SiteCheckWorker;
import utilities.DSFormatter;

public class Cleaner {
	
	public static void combinePageCrawls(PageCrawl p1, PageCrawl p2) {
//		if(!p1.getUrl().equalsIgnoreCase((p2.getUrl())){
//			System.out.println("unequal pages");
//			return;
//		}
//		
		
	}
	
	public static void validateSites(){
		System.out.println("Validating sites");
		List<Site> sites = JPA.em()
				.createQuery("from Site s where s.siteStatus = :siteStatus", Site.class)
				.setParameter("siteStatus", SiteStatus.UNVALIDATED)
				.getResultList();
		
		runUrlChecks(sites);
	}
	
	public static void runUrlChecks(List<Site> sites) {
		System.out.println("running url checks on " + sites.size() + " sites");
		ActorRef master = Asyncleton.getInstance().getMonotypeMaster(25, SiteCheckWorker.class);
		
		sites.stream().forEach( (site) -> {
			master.tell(new Order<Site>(site), ActorRef.noSender());
		});
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
		
		List<SalesforceAccount> accounts = GeneralDAO.getList(SalesforceAccount.class, "site", site2);
		for(SalesforceAccount account : accounts) {
			System.out.println("Setting site for salesforce account : " + account.getSalesforceAccountId());
			account.setSite(site1);
		}
		
		List<PlacesDealer> dealers = GeneralDAO.getList(PlacesDealer.class, "site", site2);
		for(PlacesDealer dealer : dealers) {
			System.out.println("Setting site for Places Dealer: " + dealer.getPlacesDealerId());
			dealer.setSite(site1);
		}
		
		dealers = GeneralDAO.getList(PlacesDealer.class, "unresolvedSite", site2);
		for(PlacesDealer dealer : dealers) {
			System.out.println("Setting unresolvedSite for Places Dealer: " + dealer.getPlacesDealerId());
			dealer.setUnresolvedSite(site1);
		}
		
		List<Site> forwarders = GeneralDAO.getList(Site.class, "forwardsTo", site2);
		for(Site forwarder : forwarders) {
			System.out.println("found forwarder");
			forwarder.setForwardsTo(site1);
		}
		
		List<Site> redirecters = GeneralDAO.getList(Site.class, "redirectsTo", site2);
		for(Site redirecter : redirecters) {
			System.out.println("found redirecter");
			redirecter.setRedirectsTo(site1);
		}
		
		JPA.em().remove(site2);
		return site1;
	}
	
	public static void clearBlankStaff(){
		String query = "from SiteCrawl sc where sc.allStaff is not empty";
		List<SiteCrawl> crawls  = JPA.em().createQuery(query, SiteCrawl.class).getResultList();
		
		int count = 0;
		for(SiteCrawl siteCrawl : crawls) {
			List<Staff> remove = new ArrayList<Staff>();
//			for(Staff staff : siteCrawl.getAllStaff()) {
//				if(DSFormatter.isBlank(staff.getCell())
//						&& DSFormatter.isBlank(staff.getEmail())
//						&& DSFormatter.isBlank(staff.getFn())
//						&& DSFormatter.isBlank(staff.getName())
//						&& DSFormatter.isBlank(staff.getOther())
//						&& DSFormatter.isBlank(staff.getPhone())
//						&& DSFormatter.isBlank(staff.getTitle())
//						){
//					remove.add(staff);
//				}
//			}
//			
//			for(Staff staff : remove) {
////				System.out.println("removing : " + staff);
//				siteCrawl.getAllStaff().remove(staff);
//				count++;
//			}
		}
		System.out.println("removed : " + count);
	}
	
	@SuppressWarnings("unchecked")
	public static void combineDuplicateSites() throws SQLException {
		Connection connection = DB.getConnection();
		
		String query = "SELECT homepage, COUNT(*) c FROM site GROUP BY homepage HAVING c > 1 order by homepage";
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery(query);
		
		List<String> urls = new ArrayList<String>();
		List<Site> sites = new ArrayList<Site>();
		while(rs.next()) {
			urls.add(rs.getString("homepage"));
		}
		
		rs.close();
		statement.close();
		connection.close();
		
		query = "from Site s where s.homepage = ?";
		Query q = JPA.em().createQuery(query, Site.class);
		
		for(String url : urls) {
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
			}
			if(theSite == null) {
				theSite = sites.get(0);
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
