package controllers;

import java.util.List;

import dao.SalesforceDao;
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
import salesforce.persistence.SalesforceAccount;

public class SalesforceController extends Controller {
	
	final static Logger.ALogger dsLogger = Logger.of("ds");

	@Transactional
	public static Result siteMismatchSubmit(){
		DynamicForm data = Form.form().bindFromRequest();
    	Long salesforceAccountId = Long.parseLong(data.get("salesforceAccountId"));
    	SalesforceAccount account = JPA.em().find(SalesforceAccount.class, salesforceAccountId);
    	
		System.out.println("Marking site mismatch for salesforce account : " + account.getName() + "(" + account.getSalesforceAccountId() + ")");
		account.setSiteMismatch(true);
		return ok();
	}
	
	@Transactional
	public static Result siteMismatchForms(Long siteId){
		
		List<SalesforceAccount> accounts = SalesforceDao.findBySite(JPA.em().find(Site.class, siteId));
		return ok(views.html.salesforce.siteMismatchForms.render(accounts));
	}

}
