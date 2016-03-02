package datadefinitions.newdefinitions;

public enum WPAttribution {
	
	DEALER_COM				("Powered by Dealer.com", WebProvider.DEALER_COM),
	DEALER_COM2				("Website by Dealer.com", WebProvider.DEALER_COM),
	DEALER_COM3				("Web site by Dealer.com", WebProvider.DEALER_COM),
	VIN_SOLUTIONS			("Powered by Vinsolutions.com", WebProvider.VIN_SOLUTIONS),
	VIN_SOLUTIONS2			("Website Powered By <a href=\"http://www.vinsolutions.com", WebProvider.VIN_SOLUTIONS),
	CDK_GLOBAL				("&copy 2015 CDK Global", WebProvider.CDK_COBALT, "", "Not usually visible except through source code"),
	AUTO_123_FR				("Membre d'Auto123.com", WebProvider.EVOLIO),
	AUTO_123				("Member of Auto123.com", WebProvider.EVOLIO),
	EVOLIO_FR				("Site conçu et hébergé par <a href=\"http://www.evolio.ca/fr/\" target=\"_blank\">EVOLIO", WebProvider.EVOLIO),
	EVOLIO_FR_FULL			("Site conÃ§u et hÃ©bergÃ© par <a href=\"http://www.evolio.ca/fr/\" target=\"_blank\">EVOLIO</a>.", WebProvider.EVOLIO),
	EVOLIO					("Website created and hosted by <a href=\"http://www.evolio.ca/en/\"  target=\"_blank\">EVOLIO", WebProvider.EVOLIO),
	EZ_RESULTS				("Powered By: <a href=\"http://www.ez-results.ca", WebProvider.EZ_RESULTS),
	DEALER_DIRECT_2016		("&copy; 2016 Dealer Direct LLC", WebProvider.DEALER_DIRECT),
	DEALER_ON_2016			("2016 by&nbsp;</span><span class=\"copyrightProvider\"> <a href=\"http://www.dealeron.com", WebProvider.DEALER_ON),
	DEALER_FIRE				("<a href=\"http://www.dealerfire.com\" target=\"_blank\">Responsive Dealer Website by", WebProvider.DEALER_FIRE),
	AUTO_TRADER_CA			("http://www.autotrader.ca/ \" target=\"_blank\">autoTrader.ca", WebProvider.AUTO_TRADER_CA),
	DEALER_ZOOM_LOGO		("<img src=\"/img/dealerzoom_logo.jpg", WebProvider.DEALER_ZOOM)
	
	
	;
	

	private final String description;
	private final String definition;
	private final String notes;
	private final WebProvider wp;
	
	private WPAttribution(String definition, WebProvider wp) {
		this.definition = definition;
		this.description = "";
		this.notes = "";
		this.wp = wp;
	}
	
	private WPAttribution(String definition, WebProvider wp, String description, String notes) {
		this.definition = definition;
		this.description = description;
		this.notes = notes;
		this.wp = wp;
	}

	public String getDescription() {
		return description;
	}

	public String getDefinition() {
		return definition;
	}

	public String getNotes() {
		return notes;
	}
	
	public WebProvider getWp() {
		return wp;
	}
}
