package agarbagefolder.googleplaces;

import async.work.TypedWorkResult;
import async.work.WorkType;

public class PlacesPageWorkResult extends TypedWorkResult{
	
	protected Long placesPageId;
	protected String placesId;
	
	public PlacesPageWorkResult(){
		super(WorkType.PLACES_PAGE_FETCH);
		placesPageId = null;
	}
	
	public PlacesPageWorkResult(Long placesPageId){
		super(WorkType.PLACES_PAGE_FETCH);
		this.placesPageId = placesPageId;
	}

	public Long getPlacesPageId() {
		return placesPageId;
	}

	public void setPlacesPageId(Long placesPageId) {
		this.placesPageId = placesPageId;
	}

	public String getPlacesId() {
		return placesId;
	}

	public void setPlacesId(String placesId) {
		this.placesId = placesId;
	}
	
	
	
}
