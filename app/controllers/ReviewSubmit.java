package controllers;

import datatransfer.reports.Report;
import datatransfer.reports.ReportRow;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class ReviewSubmit extends Controller {
	
	final static Logger.ALogger dsLogger = Logger.of("ds");

    public static Result index() {
        return ok(index.render("Your new application is ready.")); 
    }
    
    @Transactional
    public static Result reportRow() throws Exception {
    	System.out.println("in report row submit");
    	DynamicForm data = Form.form().bindFromRequest();
    	Long reportRowId = Long.parseLong(data.get("reportRowId"));
		String salesforceId = data.get("salesforceId");
		String action = data.get("action");
		String manualSeed = data.get("manualSeed");
		Boolean sharedSite = Boolean.parseBoolean(data.get("sharedSite"));
		
		System.out.println("reportRowId : " + reportRowId);
		System.out.println("salesforceId : " + salesforceId);
		System.out.println("action : " + action);
		System.out.println("sharedSite : " + sharedSite);
		ReportRow reportRow = JPA.em().find(ReportRow.class, reportRowId);
		
		if("APPROVE_RESOLVED".equals(action)){
			reportRow.putCell("Recommendation", "Accept Significant Update");
		} else if("MANUAL_SEED".equals(action)){
			reportRow.putCell("Recommendation", null);
			reportRow.putCell("Manual Seed", manualSeed);
		} else if("MARK_DEFUNCT".equals(action)){
			reportRow.putCell("Recommendation", "Defunct");
		} else if("RECHECK".equals(action)){
			reportRow.putCell("Recommendation", null);
		} else if("APPROVE_SHARED".equals(action)){
			reportRow.putCell("Recommendation", "Approve Shared");
		} else if("OTHER_ISSUE".equals(action)){
			reportRow.putCell("Recommendation", "Other Issue Requires Attention");
		}
		
    	return ok();
    }
}
