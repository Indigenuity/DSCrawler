package controllers;

import datatransfer.reports.Report;
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
    
}
