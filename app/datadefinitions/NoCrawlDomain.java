package datadefinitions;

public enum NoCrawlDomain {
	FACEBOOK		("facebook.com"),
	GOOGLE_PLUS		("plus.google.com"),
	JD_BYRIDER		("jdbyrider.com"),
	CARHOP			("carhop.com"),
	AUTOTRADER		("autotrader.com"),
	MANHEIM			("manheim.com"),
	ADESA			("adesa.com"),
	CHEVRON			("chevronwithtechron.com"),
	DRIVE_TIME		("drivetime.com"),
	CARS_COM		("cars.com"),
	IAAI			("iaai.com"),
	HONDA			("honda.com"),
	HERTZ			("hertzcarsales.com"),
	COPART			("copart.com"),
	GM				("gm.com"),
	CARMAX			("carmax.com"),
	PAACO			("paaco.com"),
	CLICK_MOTIVE	("clickmotive.com"),
	PINTEREST		("pinterest.com"),
	SMART			("smart.com");
	
	public final String definition;
	private NoCrawlDomain(String definition) {
		this.definition = definition;
	}
}