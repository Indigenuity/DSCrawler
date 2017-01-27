package controllers;

import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import dao.GeneralDAO;
import dao.SitesDAO;
import persistence.Site;
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
		} else if(action.equals("Manual Redirect")){
			SiteLogic.manuallyRedirect(site, manualRedirect);
		} else if(action.equals("Disapprove")){
			SiteLogic.disapprove(site);
		} else if(action.equals("Redirect and Disapprove")){
			Site endpoint = SiteLogic.manuallyRedirect(site, manualRedirect);
			SiteLogic.disapprove(endpoint);
		}
		return ok();
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
}
