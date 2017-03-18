package datadefinitions.newdefinitions;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public enum InventoryPage {

	
	MULTIPLE			(true, true, "###### Never match","###### Never match","###### Never match", WebProvider.NONE),
	DEALER_COM			("/new-inventory/index.htm", "/used-inventory/index.htm", "vehicle-count[^0-9<(]+([0-9]+)", WebProvider.DEALER_COM), 
	VIN_SOLUTIONS 		(true, false, "/Inventory/?cid=2", "/Inventory/?cid=5", "data-reactid[^>]+>[0-9]+[^0-9<]+[0-9]+[^0-9<]+([0-9]+) matched vehicle", WebProvider.VIN_SOLUTIONS),
	CDK_GLOBAL			("/VehicleSearchResults?search=new", "/VehicleSearchResults?search=preowned","inv_search_count_container[^0-9<]+([0-9]+)",  WebProvider.CDK_COBALT),
	DEALER_DIRECT		("/new-inventory/?vehicle_type=All", "/used-inventory/?vehicle_type=All", "([0-9]+) exact matches", WebProvider.DEALER_DIRECT),
	DEALER_ON			("/searchnew.aspx", "/searchused.aspx", "srpVehicleCount[\\s\">(]+[0-9]+[^0-9<]+[0-9]+[^0-9<]+([0-9]+)", WebProvider.DEALER_ON),
	AUTO_TRADER_CA		("/new-inventory/index.htm", "/used-inventory/index.htm", "([0-9]+) Items Matching", WebProvider.AUTO_TRADER_CA),
	E_DEALER_CA			("/new/", "/used/", "total-vehicle-number[^0-9<]+([0-9]+)", WebProvider.E_DEALER_CA),
	DEALER_DNA			("/new-inventory", "/used-inventory", "Showing [0-9]+[^0-9]+([0-9]+)", WebProvider.DEALER_DNA),
	DEALER_INSPIRE		("/new-vehicles/", "/used-vehicles/", "total-found[^0-9<]+([0-9]+)", WebProvider.DEALER_INSPIRE),
	
	
	
	
	;
	
	
	private final String newPath;
	public final Pattern newPathPattern;
	private final String usedPath;
	public final Pattern usedPathPattern;
	private final String countRegex;
	private final Boolean ajax;
	private final Boolean localUrl;
	private final WebProvider wp;
	private final String description;
	private final String notes;
	private final Pattern pattern;
	
	private InventoryPage(boolean ajax, boolean localUrl, String newPath, String usedPath, String regex, WebProvider wp) {
		this.ajax = ajax;
		this.localUrl = localUrl;
		this.newPath = newPath;
		this.usedPath = usedPath;
		this.countRegex = regex;
		this.description = "";
		this.notes = "";
		this.wp = wp;
		this.pattern = Pattern.compile(regex);
		this.newPathPattern = Pattern.compile(newPath);
		this.usedPathPattern = Pattern.compile(usedPath);
	}
	
	private InventoryPage(String newPath, String usedPath, String regex, WebProvider wp) {
		this(false, false, newPath, usedPath, regex, wp);
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
		return countRegex;
	}

	public Boolean getAjax() {
		return ajax;
	}

	public String getUsedPath() {
		return usedPath;
	}
	
	public Boolean getLocalUrl() {
		return localUrl;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public static List<InventoryPage> byWP(WebProvider wp) {
		List<InventoryPage> matches = new ArrayList<InventoryPage>();
		for(InventoryPage ipt : values()){
			if(wp == ipt.wp){
				matches.add(ipt);
			}
		}
		return matches;
	}
}
