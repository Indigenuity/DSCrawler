package async.tools;

import com.google.maps.GeoApiContext;
import com.google.maps.PlaceDetailsRequest;
import com.google.maps.PlacesApi;
import com.google.maps.model.PlaceDetails;

import agarbagefolder.googleplaces.PlacesPageWorkOrder;
import agarbagefolder.googleplaces.PlacesPageWorkResult;
import agarbagefolder.urlresolve.UrlResolveWorkOrder;
import agarbagefolder.urlresolve.UrlResolveWorkResult;
import async.work.SingleStepJPAWorker;
import async.work.WorkOrder;
import async.work.WorkResult;
import async.work.WorkStatus;
import global.Global;
import persistence.PlacesPage;
import persistence.UrlCheck;
import places.DataBuilder;
import play.Logger;
import play.db.jpa.JPA;
import utilities.UrlSniffer;

public class GooglePlacesWorker extends SingleStepJPAWorker {
	
	@Override
	protected WorkResult processWorkOrder(WorkOrder workOrder) {
		PlacesPageWorkResult result = new PlacesPageWorkResult();
		try{
			System.out.println("doing some places page work");
			PlacesPageWorkOrder order = (PlacesPageWorkOrder)workOrder;
			System.out.println("places ID : " + order.getPlacesId());
			result.setPlacesId(order.getPlacesId());
			GeoApiContext context = Global.getPlacesContext();
			
			PlaceDetailsRequest request = PlacesApi.placeDetails(context, order.getPlacesId());
			PlaceDetails details = request.await();
			PlacesPage page = DataBuilder.getPlacesDealer(details);
			JPA.em().persist(page);
			System.out.println("returning id : " + page.getPlacesPageId());
			result.setPlacesPageId(page.getPlacesPageId());
			result.setWorkStatus(WorkStatus.WORK_COMPLETED);
		}
		catch(Exception e) {
			Logger.error("Error in Google Places Worker: " + e);
			e.printStackTrace();
			result.setWorkStatus(WorkStatus.COULD_NOT_COMPLETE);
		}
		return result;
	}	
	

}