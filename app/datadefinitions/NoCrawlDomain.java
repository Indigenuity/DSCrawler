package datadefinitions;

public enum NoCrawlDomain implements StringMatch {
	ADESA			("adesa.com"),
	AUTOSHOPPER		("autoshopper.com"),
	AUTOTRADER		("autotrader.com"),
	AUTOTRADER_CA	("autotrader.ca"),
	CARHOP			("carhop.com"),
	CARMAX			("carmax.com"),
	CARS_COM		("cars.com"),
	CARS_ONLINE_FREE	("carsonlinefree.com"),
	CHEVRON			("chevronwithtechron.com"),
	CLICK_MOTIVE	("clickmotive.com"),
	COPART			("copart.com"),
	DRIVE_TIME		("drivetime.com"),
	FACEBOOK		("facebook.com"),
	GM				("gm.com"),
	GOOGLE_PLUS		("plus.google.com"),
	HERTZ			("hertzcarsales.com"),
	HONDA			("honda.com"),
	IAAI			("iaai.com"),
	JD_BYRIDER		("jdbyrider.com"),
	JIVE			("jive.com"),
	MANHEIM			("manheim.com"),
	PAACO			("paaco.com"),
	PINTEREST		("pinterest.com"),
	SMART			("smart.com"),
	TOYOTA			("toyota.ca");
	
	
	public final String definition;
	private NoCrawlDomain(String definition) {
		this.definition = definition;
	}
	public String getDefinition() {
		return definition;
	}
	@Override
	public String getDescription() {
		return "This signifies a domain that shouldn't be crawled for data";
	}
	@Override
	public String getNotes() {
		return "This signifies a domain that shouldn't be crawled for data";
	}
	
}