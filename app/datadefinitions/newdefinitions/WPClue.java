package datadefinitions.newdefinitions;

public enum WPClue {

	COBALT_NITRA			("assets.cobaltnitra.com", WebProvider.CDK_COBALT),
	DEALER_DOT_COM			("program: 'DealerDotCom'", WebProvider.DEALER_COM);
	

	private final String description;
	private final String definition;
	private final String notes;
	private final WebProvider wp;
	
	private WPClue(String definition, WebProvider wp) {
		this.definition = definition;
		this.description = "";
		this.notes = "";
		this.wp = wp;
	}
	
	private WPClue(String definition, WebProvider wp, String description, String notes) {
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
