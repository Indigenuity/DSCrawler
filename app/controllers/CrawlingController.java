package controllers;

import java.io.IOException;
import java.util.List;

import async.async.Asyncleton;
import async.functionalwork.JpaFunctionalBuilder;
import dao.GeneralDAO;
import dao.PlacesDealerDao;
import dao.SitesDAO;
import persistence.Site;
import persistence.SiteCrawl;
import places.DataBuilder;
import places.PlacesDealer;
import play.Logger;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import sites.crawling.SiteCrawlLogic;

public class CrawlingController extends Controller {

	
	final static Logger.ALogger dsLogger = Logger.of("ds");

	@Transactional
    public static Result dashboard() {
        return ok(views.html.crawling.crawlingDashboard.render()); 
    }
	
	@Transactional
    public static Result updateErrorStatus() {
		System.out.print("Fetching SiteCrawl Ids for updating error status");
		List<Long> siteCrawlIds = GeneralDAO.getAllIds(SiteCrawl.class);
		System.out.println("...done");
		
		Asyncleton.getInstance().runConsumerMaster(50, 
				JpaFunctionalBuilder.wrapConsumerInFind(SiteCrawlLogic::updateErrorStatus, SiteCrawl.class), 
				siteCrawlIds.stream(), 
				true);
        return ok("Submitted " + siteCrawlIds.size() + " for updating error status"); 
    }
	
}
