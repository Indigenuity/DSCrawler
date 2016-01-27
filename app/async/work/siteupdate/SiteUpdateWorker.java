package async.work.siteupdate;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.util.StringUtil;

import com.google.maps.GeoApiContext;
import com.google.maps.PlaceDetailsRequest;
import com.google.maps.PlacesApi;
import com.google.maps.model.PlaceDetails;

import async.work.SingleStepJPAWorker;
import async.work.WorkOrder;
import async.work.WorkResult;
import async.work.WorkStatus;
import async.work.googleplaces.PlacesPageWorkOrder;
import async.work.googleplaces.PlacesPageWorkResult;
import global.Global;
import persistence.PlacesPage;
import persistence.Site;
import persistence.UrlCheck;
import places.DataBuilder;
import play.Logger;
import play.db.jpa.JPA;
import utilities.DSFormatter;
import utilities.UrlSniffer;

public class SiteUpdateWorker extends SingleStepJPAWorker {
	
	@Override
	protected WorkResult processWorkOrder(WorkOrder workOrder) {
		SiteUpdateWorkResult result = new SiteUpdateWorkResult();
		try{
			System.out.println("doing some site updatework");
			SiteUpdateWorkOrder order = (SiteUpdateWorkOrder)workOrder;
			result.setSiteId(order.getSiteId());
			result.setUrlCheckId(order.getUrlCheckId());
			UrlCheck urlCheck = JPA.em().find(UrlCheck.class, order.getUrlCheckId());
			Site site = JPA.em().find(Site.class, order.getSiteId());
			
			if(urlCheck.getStatusCode() == 200) {
				if(!DSFormatter.equals(urlCheck.getResolvedSeed(), site.getHomepage())){
					if(UrlSniffer.isGenericRedirect(urlCheck.getResolvedSeed(), site.getHomepage())){
						site.setHomepage(urlCheck.getResolvedSeed());
						urlCheck.setAccepted(true);
					}
				}
			}
			result.setWorkStatus(WorkStatus.WORK_COMPLETED);
		}
		catch(Exception e) {
			Logger.error("Error in Site Update Worker: " + e);
			e.printStackTrace();
			result.setWorkStatus(WorkStatus.COULD_NOT_COMPLETE);
		}
		return result;
	}	
	

}