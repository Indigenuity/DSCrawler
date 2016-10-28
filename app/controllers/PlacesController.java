package controllers;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import akka.actor.ActorRef;
import async.async.Asyncleton;
import dao.PlacesDealerDao;
import dao.SitesDAO;
import persistence.Site;
import places.CanadaPostal;
import places.DataBuilder;
import places.DetailsWorker;
import places.PlacesDealer;
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
import views.html.index;

public class PlacesController extends Controller { 
	
	final static Logger.ALogger dsLogger = Logger.of("ds");

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
    public static Result assignSiteless() {
		List<PlacesDealer> dealers = PlacesDealerDao.siteless();
		System.out.println("Assigning siteless dealers : " + dealers.size());
		
		int count = 0;
		for(PlacesDealer dealer : dealers){
			Site site = SitesDAO.getFirst("homepage", dealer.getWebsite());
			if(site == null && dealer.getWebsite() != null) {
				System.out.println("Creating new site : " + dealer.getWebsite());
				site = new Site(dealer.getWebsite());
				JPA.em().persist(site);
			}
			dealer.setSite(site);
			
			if(count++ %50 == 0){
				System.out.println("Processed : " + count);
				JPA.em().getTransaction().commit();
				JPA.em().getTransaction().begin();
//				JPA.em().clear();
			}
		}
		
		
		
		return ok(dealers.size() + " Places dealers assigned sites");
	}
	
	@Transactional
	public static Result importCanadaList() throws IOException {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		Date monthAgo = cal.getTime();
		
		String queryString = "from CanadaPostal cp where cp.dateFetched is null or cp.dateFetched < :monthAgo";
		List<CanadaPostal> codes = JPA.em().createQuery(queryString, CanadaPostal.class).setParameter("monthAgo", monthAgo).getResultList();
		System.out.println("size : " + codes.size());
		
		ActorRef master = Asyncleton.getInstance().getGenericMaster(50, PostalSearchWorker.class);
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
		
		ActorRef master = Asyncleton.getInstance().getGenericMaster(50, PostalSearchWorker.class);
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
		
		ActorRef master = Asyncleton.getInstance().getGenericMaster(5, DetailsWorker.class);
		for(Long id : ids) {
			master.tell(id, ActorRef.noSender());
		}
		
		return ok(ids.size() + " dealers queued for fetching details");
	}
}
