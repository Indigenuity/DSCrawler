package datadefinitions;

import java.util.HashSet;
import java.util.Set;


public enum ValidQueryMatch {
	AUTO_SHOPPER(".*(autoshopper.com/dealers/?dealedrid).*");
	
	public final String definition;
	public final boolean language;
	private static final Set<ValidQueryMatch> langQueries = new HashSet<ValidQueryMatch>();
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
		Set<ValidQueryMatch> returned = new HashSet<ValidQueryMatch>();
		returned.addAll(langQueries);
		return returned;
	}
}