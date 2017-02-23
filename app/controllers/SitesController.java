package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import async.async.Asyncleton;
import async.functionalwork.JpaFunctionalBuilder;
import dao.GeneralDAO;
import dao.SitesDAO;
import persistence.Site;
import persistence.Site.RedirectType;
import persistence.Site.SiteStatus;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import sites.SiteLogic;

public class SitesController extends Controller {
	
	final static Logger.ALogger dsLogger = Logger.of("ds");

	@Transactional
	public static Result reviewSites(){
		List<Site> sites = SitesDAO.getList("siteStatus", SiteStatus.NEEDS_REVIEW, 20, 0);
		return ok(views.html.sites.reviewSites.render(sites));
	}

	@Transactional
	public static Result siteReviewSubmit(){
		DynamicForm data = Form.form().bindFromRequest();
    	Long siteId = Long.parseLong(data.get("siteId"));
    	Site site = JPA.em().find(Site.class, siteId);
		String manualRedirect = data.get("manualRedirect");
		String action = data.get("actionType");

		if(action.equals("Approve Redirect")){
			SiteLogic.acceptUrlCheck(site, false);
		} else if(action.equals("Approve Shared Site")) {
			SiteLogic.acceptUrlCheck(site, true);
		} else if(action.equals("Mark Defunct")){
			SiteLogic.markDefunct(site);
		} else if(action.equals("Other Issue")){
			SiteLogic.markError(site);
		}  else if(action.equals("Disapprove")){
			SiteLogic.disapprove(site);
		} else if(action.equals("Redirect and Disapprove")){
			Site endpoint = SiteLogic.manuallyRedirect(site, manualRedirect);
			SiteLogic.disapprove(endpoint);
		} else if(action.equals("Inference Redirect")){
			Site newSite = SitesDAO.getOrNewThreadsafe(manualRedirect);
			SiteLogic.redirect(site, newSite, RedirectType.INFERENCE);
		} else if(action.equals("Path Paring")){
			SiteLogic.parePath(site);
		} else if(action.equals("Attempt Path Paring")){
			SiteLogic.attemptParePath(site);
		} else if(action.equals("Approve Path")){
			SiteLogic.approvePath(site);
		} else if(action.equals("Unapprove Site")){
			SiteLogic.unapproveSite(site);
		} else {
			System.out.println("oops.  Action not understood");
		}
		return ok();
	}
	
	@Transactional
	public static Result reviewNotStandardPaths(){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("notStandardHomepagePath", true);
		parameters.put("approvedHomepagePath", false);
		parameters.put("uncrawlableDomain", false);
		parameters.put("defunctPath", false);
		parameters.put("defunctDomain", false);
		parameters.put("unapproved", false);
		parameters.put("httpError", false);
		parameters.put("redirects", false);
		List<Site> sites = SitesDAO.getList(parameters, 50, 0);
//		System.out.println("sites : " + sites.size());
		return ok(views.html.sites.reviewSites.render(sites));
	}
	
	@Transactional
	public static Result reviewNotStandardQueries(){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("notStandardQuery", true);
		parameters.put("approvedQuery", false);
		parameters.put("uncrawlableDomain", false);
		parameters.put("defunctPath", false);
		parameters.put("defunctDomain", false);
		parameters.put("unapproved", false);
		parameters.put("httpError", false);
		parameters.put("redirects", false);
		List<Site> sites = SitesDAO.getList(parameters, 50, 0);
//		System.out.println("sites : " + sites.size());
		return ok(views.html.sites.reviewSites.render(sites));
	}
	
	@Transactional
	public static Result checkUnvalidated() {
		System.out.println("Validating sites");
		List<Site> sites = GeneralDAO.getList(Site.class, "siteStatus", SiteStatus.UNVALIDATED);
		SiteLogic.validateSites(sites);
    	return ok("Queued URL checks for " + sites.size() + " sites");
	}
	
	@Transactional
	public static Result validateAll() {
		Multimap<String, Object> parameters = HashMultimap.create();
		parameters.put("siteStatus", SiteStatus.UNVALIDATED);
		parameters.put("siteStatus", SiteStatus.NEEDS_REVIEW);
		parameters.put("siteStatus", SiteStatus.DEFUNCT);
		parameters.put("siteStatus", SiteStatus.APPROVED);
		parameters.put("siteStatus", SiteStatus.SUSPECTED_DUPLICATE);
		parameters.put("siteStatus", SiteStatus.OTHER_ISSUE);
		List<Long> siteIds = GeneralDAO.getKeyListOr(Site.class, "siteId", parameters);
		SiteLogic.validateSitesById(siteIds);
    	return ok("Queued URL checks for " + siteIds.size() + " siteIds");
	}
	
	@Transactional
	public static Result validationSubmit() {
		DynamicForm data = Form.form().bindFromRequest();
		String[] siteStatusStrings =  request().body().asFormUrlEncoded().get("siteStatuses[]");
		
		Multimap<String, Object> parameters = HashMultimap.create();
		for(String siteStatusString : siteStatusStrings){
			parameters.put("siteStatus", SiteStatus.valueOf(siteStatusString));
		}
		List<Long> siteIds = GeneralDAO.getKeyListOr(Site.class, "siteId", parameters);
		SiteLogic.validateSitesById(siteIds);
		return ok("Queued URL checks for " + siteIds.size() + " sites");
	}
	
	@Transactional
	public static Result validateUrls() {
		List<Long> siteIds = GeneralDAO.getAllIds(Site.class);
		Asyncleton.getInstance().runConsumerMaster(50, 
				JpaFunctionalBuilder.wrapConsumerInFind(SiteLogic::validateUrl, Site.class), 
				siteIds.stream(), 
				true);
		return ok("Queued URL validation for " + siteIds.size() + " sites");
	}
	
	@Transactional
	public static Result logicalRedirects() {
		Multimap<String, Object> parameters = ArrayListMultimap.create();
		parameters.put("badUrlStructure", true);
		parameters.put("redirects", false);
		List<Long> badUrlIds = GeneralDAO.getKeyListAnd(Site.class, "siteId", parameters);
		
		parameters = ArrayListMultimap.create();
		parameters.put("notStandardQuery", true);
		parameters.put("redirects", false);
		List<Long> badQueryIds = GeneralDAO.getKeyListAnd(Site.class, "siteId", parameters);
		
		badUrlIds.addAll(badQueryIds);
		Asyncleton.getInstance().runConsumerMaster(50, 
				JpaFunctionalBuilder.wrapConsumerInFind(SiteLogic::logicalRedirect, Site.class), 
				badUrlIds.stream().distinct(), 
				true);
		return ok("Queued URL validation for " + badUrlIds.size() + " sites");
	}
	
	@Transactional
	public static Result httpRedirects() {
		String queryString = "select s.siteId from Site s join s.urlCheck uc where uc.noChange = false and s.redirects = false";
		List<Long> siteIds = JPA.em().createQuery(queryString, Long.class).getResultList();
		Asyncleton.getInstance().runConsumerMaster(50, 
				JpaFunctionalBuilder.wrapConsumerInFind(SiteLogic::httpRedirect, Site.class), 
				siteIds.stream(), 
				true);
		return ok("Queued URL validation for " + siteIds.size() + " sites");
	}
	
	@Transactional
	public static Result checkUrls() {
		List<Long> siteIds = SitesDAO.staleUrlChecks();
//		List<Long> siteIds = GeneralDAO.getAllIds(Site.class);
		Asyncleton.getInstance().runConsumerMaster(50, 
				JpaFunctionalBuilder.wrapConsumerInFind(SiteLogic::checkUrl, Site.class), 
				siteIds.stream(), 
				true);
		return ok("Queued URL checking for " + siteIds.size() + " sites");
	}
}
