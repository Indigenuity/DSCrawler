package datadefinitions.newdefinitions;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public enum InventoryType {

	DEALER_COM			("/new-inventory/index.htm", "/used-inventory/index.htm", "vehicle-count[^0-9<]+([0-9]+)", WebProvider.DEALER_COM), 
	VIN_SOLUTIONS 		(true, "/Inventory/?cid=2", "/Inventory/?cid=5", "data-reactid[^>]+>[0-9]+[^0-9<]+[0-9]+[^0-9<]+([0-9]+) matched vehicle", WebProvider.VIN_SOLUTIONS),
	CDK_GLOBAL			("/VehicleSearchResults?search=new", "/VehicleSearchResults?search=preowned","inv_search_count_container[^0-9<]+([0-9]+)",  WebProvider.CDK_COBALT),
	DEALER_DIRECT		("/new-inventory/?vehicle_type=All", "/used-inventory/?vehicle_type=All", "([0-9]+) exact matches", WebProvider.DEALER_DIRECT),
	DEALER_ON			("/searchnew.aspx", "/searchused.aspx", "srpVehicleCount[^0-9<]+[0-9]+[^0-9<]+[0-9]+[^0-9<]+([0-9]+)", WebProvider.DEALER_ON),
	AUTO_TRADER_CA		("/new-inventory/index.htm", "used-inventory/index.htm", "([0-9]+) Items Matching", WebProvider.AUTO_TRADER_CA)
	
	
	
	
	;
	
	
	private final String newPath;
	private final String usedPath;
	private final String regex;
	private final Boolean ajax;
	private final WebProvider wp;
	private final String description;
	private final String notes;
	private final Pattern pattern;
	
	private InventoryType(boolean ajax, String newPath, String usedPath, String regex, WebProvider wp) {
		this.ajax = ajax;
		this.newPath = newPath;
		this.usedPath = usedPath;
		this.regex = regex;
		this.description = "";
		this.notes = "";
		this.wp = wp;
		this.pattern = Pattern.compile(regex);
	}
	
	private InventoryType(String newPath, String usedPath, String regex, WebProvider wp) {
		this.ajax = false;
		this.newPath = newPath;
		this.usedPath = usedPath;
		this.regex = regex;
		this.description = "";
		this.notes = "";
		this.wp = wp;
		this.pattern = Pattern.compile(regex);
	}
	
	private InventoryType(String newPath, String usedPath, String regex, WebProvider wp, String description, String notes) {
		this.ajax = false;
		this.newPath = newPath;
		this.usedPath = usedPath;
		this.regex = regex;
		this.description = description;
		this.notes = notes;
		this.wp = wp;
		this.pattern = Pattern.compile(regex);
	}

	public String getDescription() {
		return description;
	}

	public String getNewPath() {
		return newPath;
	}

	public String getNotes() {
		return notes;
	}
	
	public WebProvider getWp() {
		return wp;
	}
	
	public String getRegex() {
		return regex;
	}

	public Boolean getAjax() {
		return ajax;
	}

	public String getUsedPath() {
		return usedPath;
	}
	
	

	public Pattern getPattern() {
		return pattern;
	}

	public static List<InventoryType> byWP(WebProvider wp) {
		List<InventoryType> matches = new ArrayList<InventoryType>();
		for(InventoryType ipt : values()){
			if(wp == ipt.wp){
				matches.add(ipt);
			}
		}
		return matches;
	}
}
