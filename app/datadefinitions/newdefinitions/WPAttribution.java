package datadefinitions.newdefinitions;

public enum WPAttribution {
	
	DEALER_COM				("Powered by Dealer.com"),
	VIN_SOLUTIONS			("Powered by Vinsolutions.com"),
	VIN_SOLUTIONS2			("Website Powered By <a href=\"http://www.vinsolutions.com"),
	CDK_GLOBAL				("&copy 2015 CDK Global", "", "Not usually visible except through source code");
	

	private final String description;
	private final String definition;
	private final String notes;
	
	private WPAttribution(String definition) {
		this.definition = definition;
		this.description = "";
		this.notes = "";
	}
	
	private WPAttribution(String definition, String description, String notes) {
		this.definition = definition;
		this.description = description;
		this.notes = notes;
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
	
	
}
