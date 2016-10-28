package controllers;

import java.io.IOException;
import java.util.List;

import dao.PlacesDealerDao;
import dao.SitesDAO;
import persistence.Site;
import places.DataBuilder;
import places.PlacesDealer;
import play.Logger;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

public class CrawlingController extends Controller {

	
	final static Logger.ALogger dsLogger = Logger.of("ds");

	@Transactional
    public static Result dashboard() {
        return ok(views.html.crawling.crawlingDashboard.render()); 
    }
	
	
}
