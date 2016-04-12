package datadefinitions;

import java.util.LinkedHashSet;
import java.util.Set;


public enum ValidPathMatch {
	BLANK		(""),
	SLASH		("/"),
	INDEX1		("/index.html"),
	INDEX2		("/index.htm"),
	INDEX3		("/index.cfm"),
	INDEX4		("/index.asp"),
	INDEX5		("/index.php"),
	INDEX6		("/index.shtml"),
	DEFAULT1	("/default.htm"),
	DEFAULT2	("/default.html"),
	DEFAULT3	("/default.asp"),
	DEFAULT4	("/default.aspx"),
	DEFAULT5	("/default.cfm"),
	DEFAULT6	("/default.php"),
	HOME1		("/home"),
	HOME2		("/home/"),
	HOME3		("/home.html"),
	HOME4		("/Home.aspx"),
	FRENCH		("/fr/", true),
	FRENCH2		("/fr", true),
	ENGLISH		("/en/", true),
	ENGLISH2	("/en", true),
	ENGLISH3	("/eng/", true);
	
	public final String definition;
	public final boolean language;
	private static final Set<ValidPathMatch> langPaths = new LinkedHashSet<ValidPathMatch>();
	static {
		for(ValidPathMatch match : values()){
			if(match.language){
				langPaths.add(match);
			}
		}
	}
	
	private ValidPathMatch(String definition) {
		this.definition = definition;
		language = false;
	}
	private ValidPathMatch(String definition, boolean language) {
		this.definition = definition;
		this.language = language;
	}
	
	public static Set<ValidPathMatch> langValues() {
		Set<ValidPathMatch> returned = new LinkedHashSet<ValidPathMatch>();
		returned.addAll(langPaths);
		return returned;
	}
}