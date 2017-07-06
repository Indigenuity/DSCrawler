package datadefinitions;

import java.util.LinkedHashSet;
import java.util.Set;


public enum ValidQueryMatch implements StringMatch{
	SUBARU				(".*WebPage.aspx\\?WebSiteID=[0-9]+"),
	GENERIC_LANG		("(.*)(\\?lng=1)", "$1"),
	GENERIC_LANG2		("(.*)(\\?lng=2)", "$1"),
	GENERIC_LANG3		("(.*)(\\?lng=3)", "$1"),
	GENERIC_LANG4		("(.*)(\\?lng=4)", "$1"),
	GENERIC_LANG5		("(.*)(\\?lng=5)", "$1"),
	ENGLISH1			("(.*)(\\?lang=en)", "$1"),
	ENGLISH2			("(.*)(\\?locale=en_CA)", "$1"),
	
	;
	
	public final String definition;
	public final boolean language;
	public final String replacementString;
	private static final Set<ValidQueryMatch> langQueries = new LinkedHashSet<ValidQueryMatch>();
	static {
		for(ValidQueryMatch match : values()){
			if(match.language){
				langQueries.add(match);
			}
		}
	}
	
	private ValidQueryMatch(String definition) {
		this(definition, null);
	}
	private ValidQueryMatch(String definition, String replacementString) {
		this.definition = definition;
		this.replacementString = replacementString;
		if(replacementString != null){
			this.language = true;
		} else {
			this.language = false;
		}
	}
	
	public static Set<ValidQueryMatch> langValues() {
		Set<ValidQueryMatch> returned = new LinkedHashSet<ValidQueryMatch>();
		returned.addAll(langQueries);
		return returned;
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
	
	public boolean isLanguageQuery() {
		return language;
	}
}