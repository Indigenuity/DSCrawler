package datadefinitions;

import java.util.regex.Pattern;

public enum InvalidDomain {
	
	// .*//b makes sure the domain only matches on these exact domains.  E.g. .*\\bhonda.com.* won't match http://www.arrowheadhonda.com 
	
	CHEVRON			("chevronwithtechron.com"),
	CHEVY_DEALER	("chevydealerlocator.com"),
	DRIVE_TIME		("drivetime.com"),
	HONDA			("honda.com"),
	CLICK_MOTIVE	("clickmotive.com"),
	GM				("gm.com");
	
	private final Pattern pattern;
	
	public final String raw;
	public final String definition;
	private InvalidDomain(String raw) {
		this.raw = raw;
		this.definition = ".*\\b" + raw + ".*";
		this.pattern = Pattern.compile(this.definition);
	}
	public Pattern getPattern() {
		return pattern;
	}
	
	
}
