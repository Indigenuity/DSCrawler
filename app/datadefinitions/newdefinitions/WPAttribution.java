package datadefinitions.newdefinitions;

public enum WPAttribution {
	
	DEALER_COM				("Powered by Dealer.com", WebProvider.DEALER_COM),
	DEALER_COM2				("Website by Dealer.com", WebProvider.DEALER_COM),
	VIN_SOLUTIONS			("Powered by Vinsolutions.com", WebProvider.VIN_SOLUTIONS),
	VIN_SOLUTIONS2			("Website Powered By <a href=\"http://www.vinsolutions.com", WebProvider.VIN_SOLUTIONS),
	CDK_GLOBAL				("&copy 2015 CDK Global", WebProvider.CDK_COBALT, "", "Not usually visible except through source code"),
	AUTO_123_FR				("Membre d'Auto123.com", WebProvider.EVOLIO),
	EVOLIO_FR				("Site conçu et hébergé par <a href=\"http://www.evolio.ca/fr/\" target=\"_blank\">EVOLIO", WebProvider.EVOLIO);
	

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
