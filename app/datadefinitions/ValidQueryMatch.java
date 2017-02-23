package datadefinitions;

import java.util.LinkedHashSet;
import java.util.Set;


public enum ValidQueryMatch implements StringMatch{
	SUBARU				(".*WebPage.aspx\\?WebSiteID=[0-9]+"),
	GENERIC_LANG		(".*?lng=1", true),
	GENERIC_LANG2		(".*?lng=2", true),
	GENERIC_LANG3		(".*?lng=3", true),
	GENERIC_LANG4		(".*?lng=4", true),
	GENERIC_LANG5		(".*?lng=5", true),
	ENGLISH1			(".*?lang=en", true),
	ENGLISH2			(".*?locale=en_CA", true),
	
	;
	
	public final String definition;
	public final boolean language;
	private static final Set<ValidQueryMatch> langQueries = new LinkedHashSet<ValidQueryMatch>();
	static {
		for(ValidQueryMatch match : values()){
			if(match.language){
				langQueries.add(match);
			}
		}
	}
	
	private ValidQueryMatch(String definition) {
		this.definition = definition;
		language = false;
	}
	private ValidQueryMatch(String definition, boolean language) {
		this.definition = definition;
		this.language = language;
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