package datadefinitions.newdefinitions;

import java.util.regex.Pattern;

import datadefinitions.StringMatch;

public enum DefunctPath implements StringMatch {
	UNUSED_DOMAINS			("/UnusedDomains.htm");
	
	public final String definition;
	private DefunctPath(String definition) {
		this.definition = definition;
	}
	@Override
	public String getDefinition() {
		return definition;
	}
	@Override
	public String getDescription() {
		return "This signifies an expired domain name";
	}
	@Override
	public String getNotes() {
		return "This signifies an expired domain name";
	}
}
