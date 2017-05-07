package controllers;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import akka.actor.ActorRef;
import async.async.Asyncleton;
import async.functionalwork.JpaFunctionalBuilder;
import audit.Standardizer;
import dao.GeneralDAO;
import dao.PlacesDealerDao;
import dao.SiteOwnerLogic;
import dao.SitesDAO;
import persistence.Site;
import places.CanadaPostal;
import places.DataBuilder;
import places.DetailsWorker;
import places.PlacesDealer;
import places.PlacesLogic;
import places.PostalSearchWorker;
import places.Retriever;
import places.ZipLocation;
import play.Logger;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import reporting.DashboardStats;
import reporting.StatsBuilder;
import salesforce.persistence.SalesforceAccount;
import views.html.index;

public class PlacesController extends Controller { 
	
	final static Logger.ALogger dsLogger = Logger.of("ds");

	
	@Transactional
    public static Result parseAddresses() throws IOException {
		List<Long> dealerIds = GeneralDAO.getAllIds(PlacesDealer.class);
		Asyncleton.getInstance().runConsumerMaster(50, 
				JpaFunctionalBuilder.wrapConsumerInFind(PlacesLogic::parseAddress, PlacesDealer.class), 
				dealerIds.stream(), 
				true);
		return ok("Queued " + dealerIds.size() + " dealers to have addresses parsed.");
	}
	
	@Transactional
    public static Result refreshZipcodeDatabase() throws IOException {
		DataBuilder.refreshZipcodeDatabase();
        return ok("US Zipcodes Refreshed"); 
    }
	
	@Transactional
	public static Result placesDashboard() {
		return ok(views.html.places.placesDashboard.render());
	}
	
	@Transactional
	public static Result placesDashboardStats() {
		
		return ok(views.html.viewstats.viewStats.render(StatsBuilder.placesDashboard()));
	}
	@Transactional
    public static Result classifyRecords() {
		List<Long> dealerIds = GeneralDAO.getAllIds(PlacesDealer.class);
		Asyncleton.getInstance().runConsumerMaster(50, 
				JpaFunctionalBuilder.wrapConsumerInFind(PlacesLogic::classifyRecordType, PlacesDealer.class), 
				dealerIds.stream(), 
				true);
		return ok("Queued " + dealerIds.size() + " for salesforce matching");
	}
	
	 @Transactional
    public static Result refreshRedirectPaths(){
    	List<Long> dealerIds = GeneralDAO.getAllIds(PlacesDealer.class);
    	Asyncleton.getInstance().runConsumerMaster(50, 
				JpaFunctionalBuilder.wrapConsumerInFind(SiteOwnerLogic::refreshRedirectPath, PlacesDealer.class), 
				dealerIds.stream(), 
				true);
    	return ok("Queued " + dealerIds.size() + " dealers to be assigned the most redirected Site objects");
    }
	
	@Transactional
    public static Result assignSiteless() {
		List<Long> dealerIds = PlacesDealerDao.siteless();
		System.out.println("Assigning siteless dealers : " + dealerIds.size());
		Asyncleton.getInstance().runConsumerMaster(25, 
				JpaFunctionalBuilder.wrapConsumerInFind(SiteOwnerLogic::assignUnresolvedSiteThreadsafe, PlacesDealer.class), 
				dealerIds.stream(),
				true);
		
		return ok(dealerIds.size() + " Places dealers assigned sites");
	}
	
	@Transactional
	public static Result importCanadaList() throws IOException {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		Date monthAgo = cal.getTime();
		
		String queryString = "from CanadaPostal cp where cp.dateFetched is null or cp.dateFetched < :monthAgo";
		List<CanadaPostal> codes = JPA.em().createQuery(queryString, CanadaPostal.class).setParameter("monthAgo", monthAgo).getResultList();
		System.out.println("size : " + codes.size());
		
		ActorRef master = Asyncleton.getInstance().getMonotypeMaster(50, PostalSearchWorker.class);
		for(CanadaPostal code : codes) {
			master.tell(code, ActorRef.noSender());
		}
		return ok("Queued " + codes.size() + " postal codes for importing.");
	}
	
	@Transactional
	public static Result importUsList() throws IOException {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		Date monthAgo = cal.getTime();
		
		String queryString = "from ZipLocation zl where zl.dateFetched is null or zl.dateFetched < :monthAgo";
		List<ZipLocation> zips = JPA.em().createQuery(queryString, ZipLocation.class).setParameter("monthAgo", monthAgo).getResultList();
		System.out.println("size : " + zips.size());
		
		ActorRef master = Asyncleton.getInstance().getMonotypeMaster(50, PostalSearchWorker.class);
		for(ZipLocation zip : zips) {
			master.tell(zip, ActorRef.noSender());
		}
		return ok("Queued " + zips.size() + " zip codes for importing.");
	}
	
	/*When Places dealers are discovered, they only have their Places id and no other info.  This fetches
	*the details for those places.  It fetches for any Place that hasn't had its details fetched in the last month
	*/	
	@Transactional
	public static Result fillBlankDetails() throws IOException {
		System.out.println("filling details of places");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		Date monthAgo = cal.getTime();
		int maxResults = 150000;		//150000 requests is the limit each day
		
		String queryString = "select pd.placesDealerId from PlacesDealer pd where pd.detailFetchDate is null or pd.detailFetchDate < :monthAgo";
		List<Long> ids = JPA.em().createQuery(queryString, Long.class).setMaxResults(maxResults).setParameter("monthAgo", monthAgo).getResultList();
		System.out.println("ids : " + ids.size());
		
		Asyncleton.getInstance().runConsumerMaster(25, 
				JpaFunctionalBuilder.wrapConsumerInFind(PlacesLogic::fillDetails, PlacesDealer.class), 
				ids.stream(), 
				true);
		
		return ok(ids.size() + " dealers queued for fetching details");
	}
	
	@Transactional
	public static Result expireDetails(){
		String queryString = "update PlacesDealer pd set pd.detailFetchDate = null";
		int affectedRows = JPA.em().createQuery(queryString).executeUpdate();
		return ok("Expired " + affectedRows + " records");
	}
	
	@Transactional
	public static Result standardizeFields(){
		Standardizer.standardizePlacesDealers();
		return ok("Queued up PlacesDealers for standardization");
	}
	
	@Transactional
	public static Result forwardSites(){
		List<Long> dealerIds = GeneralDAO.getAllIds(PlacesDealer.class);
		Asyncleton.getInstance().runConsumerMaster(50, 
				JpaFunctionalBuilder.wrapConsumerInFind(SiteOwnerLogic::forwardSite, PlacesDealer.class), 
				dealerIds.stream(), 
				true);
		return ok("Queued " + dealerIds.size() + " for site forwarding");
	}
	
	@Transactional
	public static Result salesforceMatching(){
		List<Long> dealerIds = GeneralDAO.getAllIds(PlacesDealer.class);
		Asyncleton.getInstance().runConsumerMaster(50, 
				JpaFunctionalBuilder.wrapConsumerInFind(PlacesLogic::salesforceMatch, PlacesDealer.class), 
				dealerIds.stream(), 
				true);
		return ok("Queued " + dealerIds.size() + " for salesforce matching");
	}
	
	@Transactional
	public static Result crawlCanada() throws IOException {
		
		return ok();
	}
	
	
}
