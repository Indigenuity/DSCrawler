package controllers;

import java.util.List;

import dao.GeneralDAO;
import dao.SalesforceDao;
import datatransfer.reports.Report;
import persistence.Site;
import persistence.Site.SiteStatus;
import persistence.salesforce.SalesforceAccount;
import play.Logger;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Review extends Controller {
	
	final static Logger.ALogger dsLogger = Logger.of("ds");

    public static Result index() {
        return ok(index.render("Your new application is ready.")); 
    }
    
    @Transactional
    public static Result report(long reportId) throws Exception {
    	Report report = JPA.em().find(Report.class, reportId);
//    	return ok();
    	return ok(views.html.reviewing.lists.report.render(report));
    }
    
    @Transactional
    public static Result salesforceSites() {
    	List<SalesforceAccount> accounts = SalesforceDao.getList("siteStatus", SiteStatus.NEEDS_REVIEW);
    	return ok(views.html.reviewing.lists.salesforceSites.render(accounts));
    }
    
}
