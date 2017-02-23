package datadefinitions;

import java.util.LinkedHashSet;
import java.util.Set;


public enum HomepagePath implements StringMatch{
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
	DEFAULT7	("/Default.aspx"),
	WEB_PAGE	("/WebPage.aspx"),
	HOME1		("/home"),
	HOME2		("/home/"),
	HOME3		("/home.html"),
	HOME4		("/Home.aspx"),
	FRENCH		("/fr/", true),
	FRENCH2		("/fr", true),
	FRENCH3		("/fr-CA/accueil.aspx", true),
	FRENCH4		("/fr-CA/accueil", true),
	ENGLISH		("/en/", true),
	ENGLISH2	("/en", true),
	ENGLISH3	("/eng/", true),
	ENGLISH4	("/en-CA/home.aspx", true),
	ENGLISH5	("/en-CA/home", true),
	ENGLISH6	("/en/index.spy", true),
	ENGLISH7	("/en-US", true),
	
	;
	
	public final String definition;
	public final boolean language;
	private static final Set<HomepagePath> langPaths = new LinkedHashSet<HomepagePath>();
	static {
		for(HomepagePath match : values()){
			if(match.language){
				langPaths.add(match);
			}
		}
	}
	
	private HomepagePath(String definition) {
		this.definition = definition;
		language = false;
	}
	private HomepagePath(String definition, boolean language) {
		this.definition = definition;
		this.language = language;
	}
	
	public static Set<HomepagePath> langValues() {
		Set<HomepagePath> returned = new LinkedHashSet<HomepagePath>();
		returned.addAll(langPaths);
		return returned;
	}
	@Override
	public String getDescription() {
		return "This is a valid homepage path";
	}
	@Override
	public String getDefinition() {
		return definition;
	}
	@Override
	public String getNotes() {
		return "This is a valid homepage path";
	}
	
	public boolean isLanguagePath() {
		return language;
	}
}