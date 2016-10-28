package datadefinitions.newdefinitions;

import datadefinitions.StringMatch;

public enum WPAttribution implements StringMatch{
	
	AUTO_123				("Member of Auto123.com", WebProvider.EVOLIO),
	AUTO_123_FR				("Membre d'Auto123.com", WebProvider.EVOLIO),
	AUTO_TRADER_CA			("http://www.autotrader.ca/ \" target=\"_blank\">autoTrader.ca", WebProvider.AUTO_TRADER_CA),
	CDK_GLOBAL				("&copy 2015 CDK Global", WebProvider.CDK_COBALT, "", "Not usually visible except through source code"),
	CONVERTUS				("convertus-logo", WebProvider.CONVERTUS),
	CONVERTUS2				("by Convertus", WebProvider.CONVERTUS),
	DEALER_COM				("Powered by Dealer.com", WebProvider.DEALER_COM),
	DEALER_COM2				("Website by Dealer.com", WebProvider.DEALER_COM),
	DEALER_COM3				("Web site by Dealer.com", WebProvider.DEALER_COM),
	DEALER_DIRECT_2016		("&copy; 2016 Dealer Direct LLC", WebProvider.DEALER_DIRECT),
	DEALER_DNA				("Powered by <a href=\"http://www.dealerdna.com", WebProvider.DEALER_DNA),
	DEALER_FIRE				("<a href=\"http://www.dealerfire.com\" target=\"_blank\">Responsive Dealer Website by", WebProvider.DEALER_FIRE),
	DEALER_INSPIRE			("Websites by <a href=\"http://www.dealerinspire.com", WebProvider.DEALER_INSPIRE),
	DEALER_ON_2016			("2016 by&nbsp;</span><span class=\"copyrightProvider\"> <a href=\"http://www.dealeron.com", WebProvider.DEALER_ON),
	DEALER_ZOOM_LOGO		("<img src=\"/img/dealerzoom_logo.jpg", WebProvider.DEALER_ZOOM),
	DLD						("Website by DLDWebsites.com", WebProvider.DLD),
	DMT						("DMT_Powered", WebProvider.DMT),
	E_DEALER_CA				("http://websites.edealer.ca/assets/logos/eDealerlogo1.png", WebProvider.E_DEALER_CA),
	E_DEALER_CA2			("edealer-new.png", WebProvider.E_DEALER_CA),
	E_DEALER_CA3			("Powered and Designed By Edealer", WebProvider.E_DEALER_CA),
	E_DEALER_CA4			("eDealerlogo2.png", WebProvider.E_DEALER_CA),
	EVOLIO					("Website created and hosted by <a href=\"http://www.evolio.ca/en/\"  target=\"_blank\">EVOLIO", WebProvider.EVOLIO),
	EVOLIO_FR				("Site conçu et hébergé par <a href=\"http://www.evolio.ca/fr/\" target=\"_blank\">EVOLIO", WebProvider.EVOLIO),
	EVOLIO_FR_FULL			("Site conÃ§u et hÃ©bergÃ© par <a href=\"http://www.evolio.ca/fr/\" target=\"_blank\">EVOLIO</a>.", WebProvider.EVOLIO),
	EZ_RESULTS				("Powered By: <a href=\"http://www.ez-results.ca", WebProvider.EZ_RESULTS),
	GLOVEBOX				("Site By <strong>Glovebox", WebProvider.GLOVEBOX),
	STRATHCOM				("Powered by <a href=\"http://www.strathcom", WebProvider.STRATHCOM),
	STRATHCOM2				("Powered by Strathcom Media", WebProvider.STRATHCOM),
	STRATHCOM3				("Designed and powered by <a target=\"_blank\" href=\"http://strathcom", WebProvider.STRATHCOM),
	STRATHCOM4				("Designed by <a target=\"_blank\" href=\"http://www.strathcom", WebProvider.STRATHCOM),
	STRATHCOM_GENERAL		("href=\"http://www.strathcom", WebProvider.STRATHCOM),
	VIN_SOLUTIONS			("Powered by Vinsolutions.com", WebProvider.VIN_SOLUTIONS),
	VIN_SOLUTIONS2			("Powered By <a href=\"http://www.vinsolutions.com", WebProvider.VIN_SOLUTIONS)
	
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
