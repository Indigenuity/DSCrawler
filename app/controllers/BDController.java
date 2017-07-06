package controllers;

import java.io.IOException;
import java.util.List;

import async.async.Asyncleton;
import async.functionalwork.JpaFunctionalBuilder;
import audit.Standardizer;
import crawling.projects.BasicDealer;
import crawling.projects.BasicDealerLogic;
import dao.GeneralDAO;
import dao.PlacesDealerDao;
import dao.SiteOwnerLogic;
import places.DataBuilder;
import places.PlacesDealer;
import places.PlacesLogic;
import play.Logger;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import salesforce.persistence.SalesforceAccount;

public class BDController extends Controller { 
	
	final static Logger.ALogger dsLogger = Logger.of("ds");

	@Transactional
	public static Result bdDashboard() {
		return ok(views.html.basicdealer.bdDashboard.render());
	}
	
	@Transactional
    public static Result parseAddresses() throws IOException {
		List<Long> dealerIds = GeneralDAO.getAllIds(BasicDealer.class);
		Asyncleton.getInstance().runConsumerMaster(50, 
				JpaFunctionalBuilder.wrapConsumerInFind(BasicDealerLogic::parseAddress, BasicDealer.class), 
				dealerIds.stream(), 
				true);
		return ok("Queued " + dealerIds.size() + " dealers to have addresses parsed.");
	}
	
	@Transactional
    public static Result assignSiteless() {
		String queryString = "select bd.basicDealerId from BasicDealer bd where bd.site is null";
		List<Long> dealerIds = JPA.em().createQuery(queryString, Long.class).getResultList();
		System.out.println("Assigning siteless dealers : " + dealerIds.size());
		Asyncleton.getInstance().runConsumerMaster(25, 
				JpaFunctionalBuilder.wrapConsumerInFind(SiteOwnerLogic::assignUnresolvedSiteThreadsafe, BasicDealer.class), 
				dealerIds.stream(),
				true);
		
		return ok(dealerIds.size() + " Basic dealers queued up to be assigned sites");
	}	
	
	@Transactional
	public static Result refreshRedirectPaths(){
		List<Long> dealerIds = GeneralDAO.getAllIds(BasicDealer.class);
		Asyncleton.getInstance().runConsumerMaster(50, 
				JpaFunctionalBuilder.wrapConsumerInFind(SiteOwnerLogic::refreshRedirectPath, BasicDealer.class), 
				dealerIds.stream(), 
				true);
		return ok("Queued " + dealerIds.size() + " dealers to be assigned the most redirected Site objects");
	}
	
	@Transactional
	public static Result standardizeFields(){
		List<Long> dealerIds = GeneralDAO.getAllIds(BasicDealer.class);
		Asyncleton.getInstance().runConsumerMaster(50, 
				JpaFunctionalBuilder.wrapConsumerInFind(Standardizer::standardize, BasicDealer.class), 
				dealerIds.stream(), 
				true);
		return ok("Queued " + dealerIds.size() + " dealers to standardize fields");
	}
	
	@Transactional
	public static Result salesforceMatching(){
		List<Long> dealerIds = GeneralDAO.getAllIds(BasicDealer.class);
		Asyncleton.getInstance().runConsumerMaster(1, 
				JpaFunctionalBuilder.wrapConsumerInFind(BasicDealerLogic::salesforceMatch, BasicDealer.class), 
				dealerIds.stream(), 
				true);
		return ok("Queued " + dealerIds.size() + " for salesforce matching");
	}

	 
	 
	 
	
}
