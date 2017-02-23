package datadefinitions.newdefinitions;

import datadefinitions.StringMatch;

public enum AutoRemoveQuery implements StringMatch{
	CS_E			("cs:e=.*"),
	CS_PRO			("cs:pro=.*"),
	GCLID			("gclid=.*"),
	GWS_RD			("gws_rd=.*"),
	PAID_SEARCH		("pdsrch=.*"),
	UTM_REFERRER	("utm_referrer=.*"),
	UTM_SOURCE		("utm_source=.*"),
	GA				("_ga=.*"),
	MB_RTE			("mb=rte.*"),
	CLEAR_ALL		("ClearAll=.*"),
	
	;
	
	public final String definition;
	public final boolean language;
	
	private AutoRemoveQuery(String definition) {
		this.definition = definition;
		language = false;
	}
	
	@Override
	public String getDescription() {
		return "Indicates a valid query";
	}
	@Override
	public String getDefinition() {
		return definition;
	}
	@Override
	public String getNotes() {
		return "Indicates a valid query";
	}
	
}